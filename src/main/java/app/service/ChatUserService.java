package app.service;

import app.config.props.MinioProperties;
import app.dto.response.ChatUserDTO;
import app.dto.request.ChatUserUpdateDTO;
import app.dto.response.PictureDTO;
import app.entity.ChatUser;
import app.mapper.ChatUserMapper;
import app.repository.ChatUserRepository;
import app.util.TextNormalize;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;
    private final ChatUserMapper chatUserMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void updateUserWithUsername(@Valid ChatUserUpdateDTO chatUserUpdateDTO, String username) {
        chatUserRepository.findByUsername(username)
                .ifPresent(chatUser -> chatUserMapper.updateFromDto(chatUserUpdateDTO, chatUser));
    }

    public Page<ChatUserDTO> getUsersNotUsername(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatUserRepository.findByNameNormNotUsername(queryNormalized, username, pageable)
                .map(chatUserMapper::toChatUserDTO);
    }

    // todo determine if sneakythrows is the right way of handling exceptions here
    @SneakyThrows
    public PictureDTO getProfilePictureForUsername(String username) {
        ChatUser chatUser = chatUserRepository.findByUsername(username).orElseThrow();

        if (!chatUser.getHasProfilePicture()) {
            throw new NoSuchElementException("User does not have a profile picture");
        }

        String objectKey = getPictureObjectKeyForUser(chatUser);

        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .build()
        );

        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .build()
        );

        String contentType = stat.contentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return new PictureDTO(stream, contentType);
    }

    @SneakyThrows
    public void updateUserProfilePicture(MultipartFile file, String username) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image/* content types are allowed");
        }

        ChatUser chatUser = chatUserRepository.findByUsername(username).orElseThrow();

        String objectKey = getPictureObjectKeyForUser(chatUser);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.bucket())
                        .object(objectKey)
                        .contentType(contentType)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .build()
        );

        // setting the user has profile picture flag to true
        chatUser.setHasProfilePicture(Boolean.TRUE);
        chatUserRepository.save(chatUser);
    }

    private String getPictureObjectKeyForUser(ChatUser chatUser) {
        return String.format("users/%s/profile-pic", chatUser.getUsername());
    }
}
