package app.service;

import app.dto.ChatUserDTO;
import app.dto.ChatUserUpdateDTO;
import app.entity.ChatUser;
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

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;
    private final ChatUserMapper chatUserMapper;

    // todo add preauthorize
    public void updateMe(@Valid ChatUserUpdateDTO chatUserUpdateDTO, Principal principal) {
        chatUserRepository.findByUsername(principal.getName())
                .ifPresent(chatUser -> chatUserMapper.updateFromDto(chatUserUpdateDTO, chatUser));
    }

    public Page<ChatUserDTO> getUsersNotMePageable(String query, Principal principal, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatUserRepository.searchByNameNormNotMe(queryNormalized, principal.getName(), pageable)
                .map(chatUserMapper::toDto);
    }
}
