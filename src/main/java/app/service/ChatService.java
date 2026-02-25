package app.service;

import app.dto.request.CreateChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.response.ChatDTO;
import app.dto.response.MessageDTO;
import app.entity.Chat;
import app.entity.ChatUser;
import app.entity.Message;
import app.event.MessageCreatedEvent;
import app.mapper.ChatMapper;
import app.mapper.MessageMapper;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import app.util.TextNormalize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatUserRepository chatUserRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Page<ChatDTO> getChatsForUsernamePageable(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        // todo find chats by ActiveMemberships so that no invitations are loaded
        return chatRepository.findByNameNormAndUsername(queryNormalized, username, pageable)
                .map(this::fromChatToDTO);
    }

    public Page<MessageDTO> getMessagesForChatPageable(UUID chatId, Pageable pageable) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        return messageRepository.findAllByChat(chat, pageable)
                .map(messageMapper::toDTO);
    }

    public MessageDTO createMessageForChat(UUID chatId, @Valid CreateMessageDTO messageDTO, String username) {
        ChatUser chatUser = chatUserRepository.findByUsername(username).orElseThrow();

        Chat chat = chatRepository.findById(chatId).orElseThrow();

        Message message = messageMapper.toEntity(messageDTO, chat, chatUser, Instant.now());

        if (messageDTO.getReplyToId() != null) {
            Message replyTo = messageRepository.findById(messageDTO.getReplyToId()).orElseThrow();
            message.setResponseTo(replyTo);
        }

        Message saved = messageRepository.save(message);
        eventPublisher.publishEvent(new MessageCreatedEvent(saved.getId(), chatId));

        return messageMapper.toDTO(saved);
    }

    public ChatDTO getChatIdOfChatWithPerson(String otherUsername, String ownerUsername) {
        ChatUser chatUser = chatUserRepository.findByUsername(ownerUsername).orElseThrow();
        ChatUser otherUser = chatUserRepository.findByUsername(otherUsername).orElseThrow();
        return chatRepository.findChatsWithExactlyParticipants(List.of(chatUser.getUsername(), otherUser.getUsername()))
                .stream()
                .map(this::fromChatToDTO)
                .toList().getFirst();
    }

    public ChatDTO createChatForUser(@Valid CreateChatDTO dto, String ownerUsername) {
        ChatUser owner = chatUserRepository.findByUsername(ownerUsername)
                .orElseThrow();

        // normalize list: trim, remove blanks, distinct, and don't include owner
        List<String> usernames = dto.getMembersList().stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .filter(u -> !u.equals(ownerUsername))
                .toList();

        // load ChatUser entities for members
        List<ChatUser> members = chatUserRepository.findAllByUsernameIn(usernames);

        // validate all exist
        if (members.size() != usernames.size()) {
            throw new IllegalArgumentException("Some usernames do not exist.");
        }

        Chat chat = Chat.createChatWithOwnerAndMembers(dto.getName(), owner, members);

        Chat saved = chatRepository.save(chat);
        return chatMapper.toDTO(saved, null);
    }

    public MessageDTO getMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toDTO)
                .orElseThrow();
    }

    private ChatDTO fromChatToDTO(Chat chat) {
        Message lastMessage = messageRepository.findAllByChat(
                        chat,
                        PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                ).stream()
                .findFirst()
                .orElse(null);
        return chatMapper.toDTO(chat, lastMessage);
    }
}
