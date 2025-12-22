package app.service;

import app.dto.ChatUserDTO;
import app.dto.ChatUserUpdateDTO;
import app.mapper.ChatUserMapper;
import app.repository.ChatUserRepository;
import app.util.TextNormalize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;
    private final ChatUserMapper chatUserMapper;

    // todo add preauthorize
    public void updateUserWithUsername(@Valid ChatUserUpdateDTO chatUserUpdateDTO, String username) {
        chatUserRepository.findByUsername(username)
                .ifPresent(chatUser -> chatUserMapper.updateFromDto(chatUserUpdateDTO, chatUser));
    }

    public Page<ChatUserDTO> getUsersNotUsernamePageable(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatUserRepository.findByNameNormNotUsername(queryNormalized, username, pageable)
                .map(chatUserMapper::toDto);
    }
}
