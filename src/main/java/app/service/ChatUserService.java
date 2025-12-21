package app.service;

import app.dto.ChatUserUpdateDTO;
import app.mapper.ChatUserMapper;
import app.repository.ChatUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
