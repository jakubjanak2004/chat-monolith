package app.service;

import app.dto.response.ChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.response.MessageDTO;
import app.entity.Chat;
import app.entity.ChatUser;
import app.entity.Message;
import app.mapper.ChatMapper;
import app.mapper.MessageMapper;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import app.util.TextNormalize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatUserRepository chatUserRepository;

    public Page<ChatDTO> getChatsForUsernamePageable(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatRepository.findByNameNormAndUsername(queryNormalized, username, pageable)
                .map(chat -> {
                    Message lastMessage = messageRepository.findAllByChat(
                                    chat,
                                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                            ).stream()
                            .findFirst()
                            .orElse(null);
                    return chatMapper.toDTO(chat, lastMessage);
                });
    }

    public Page<MessageDTO> getMessagesForChatPageable(UUID chatId, Pageable pageable) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        return messageRepository.findAllByChat(chat, pageable)
                .map(messageMapper::toDTO);
    }

    public MessageDTO createMessageForChat(UUID chatId, @Valid CreateMessageDTO messageDTO, String username) {
        ChatUser chatUser = chatUserRepository.findByUsername(username).orElseThrow();
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        return Optional.of(messageDTO)
                .map(dto -> messageMapper.toEntity(dto, chat, chatUser, Instant.now()))
                .map(messageRepository::save)
                .map(messageMapper::toDTO)
                .orElseThrow();
    }
}
