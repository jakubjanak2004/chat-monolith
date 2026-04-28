package app.service;

import app.dto.request.ActiveMembershipUpdateDTO;
import app.dto.request.CreateChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.request.GiveUpAdminDTO;
import app.dto.response.ChatDTO;
import app.dto.response.ActiveMembershipDTO;
import app.dto.response.InvitationDTO;
import app.dto.response.UserInvitationsDTO;
import app.dto.response.MessageDTO;
import app.entity.ActiveMembership;
import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Invitation;
import app.entity.Message;
import app.enumeration.MembershipType;
import app.event.MessageCreatedEvent;
import app.mapper.ActiveMembershipMapper;
import app.mapper.ChatMapper;
import app.mapper.InvitationMapper;
import app.mapper.MessageMapper;
import app.repository.ActiveMembershipRepository;
import app.repository.ChatMembershipRepository;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.repository.InvitationRepository;
import app.repository.MessageRepository;
import app.util.TextNormalize;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final ActiveMembershipRepository activeMembershipRepository;
    private final ActiveMembershipMapper activeMembershipMapper;
    private final ChatMembershipRepository chatMembershipRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationMapper invitationMapper;

    @PreAuthorize("@chatUserSecurity.hasUsername(#username, authentication)")
    public Page<ChatDTO> getChatsForUsername(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatRepository.findChatsForUsername(username, queryNormalized, pageable)
                .map(chatMapper::toDTO);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public Page<MessageDTO> getMessagesForChat(String query, UUID chatId, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return messageRepository.findMessages(chatId, queryNormalized, pageable)
                .map(messageMapper::toDTO);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public MessageDTO createMessageForChat(UUID chatId, @Valid CreateMessageDTO messageDTO, String username) {
        ChatUser chatUser = chatUserRepository.findByUsername(username).orElseThrow();

        Chat chat = chatRepository.findById(chatId).orElseThrow();

        Message message = messageMapper.toEntity(messageDTO, chat, chatUser, Instant.now());

        if (messageDTO.replyToId() != null) {
            Message replyTo = messageRepository.findById(messageDTO.replyToId()).orElseThrow();
            message.setResponseTo(replyTo);
        }

        Message saved = messageRepository.save(message);
        MessageCreatedEvent messageCreatedEvent = messageMapper.toMessageCreatedEvent(saved);
        eventPublisher.publishEvent(messageCreatedEvent);

        return messageMapper.toDTO(saved);
    }

    @PreAuthorize("@chatUserSecurity.hasUsername(#ownerUsername, authentication)")
    public ChatDTO getChatIdOfChatWithPerson(String otherUsername, String ownerUsername) {
        ChatUser chatUser = chatUserRepository.findByUsername(ownerUsername).orElseThrow();
        ChatUser otherUser = chatUserRepository.findByUsername(otherUsername).orElseThrow();
        return chatRepository.findChatsWithExactlyParticipants(List.of(chatUser.getUsername(), otherUser.getUsername()))
                .stream()
                .map(chatMapper::toDTO)
                .toList().getFirst();
    }

    @PreAuthorize("@chatUserSecurity.hasUsername(#ownerUsername, authentication)")
    public ChatDTO createChatForUser(@Valid CreateChatDTO dto, String ownerUsername) {
        ChatUser owner = chatUserRepository.findByUsername(ownerUsername)
                .orElseThrow();

        // normalize list: trim, remove blanks, distinct, and don't include owner
        List<String> usernames = dto.membersList().stream()
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

        Chat chat = Chat.createChatWithOwnerAndMembers(dto.name(), owner, members);

        Chat saved = chatRepository.save(chat);
        return chatMapper.toDTO(saved);
    }

    @PreAuthorize("@chatSecurity.canManageMessageWithId(#messageId, authentication)")
    public MessageDTO getMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(messageMapper::toDTO)
                .orElseThrow();
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public ChatDTO getChatById(UUID chatId) {
        return chatRepository.findById(chatId)
                .map(chatMapper::toDTO)
                .orElseThrow();
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public List<ActiveMembershipDTO> getActiveMembershipsForChat(UUID chatId) {
        return activeMembershipRepository.findAllByChat_Id(chatId)
                .stream()
                .map(activeMembershipMapper::toDTO)
                .toList();
    }

    @PreAuthorize("@chatSecurity.canManageChatWithIdWhenMembershipIs(#chatId, 'ADMIN', authentication)")
    public void updateMembershipRole(UUID chatId, String username, @Valid ActiveMembershipUpdateDTO activeMembershipUpdateDTO) {
        activeMembershipRepository.findFirstByChat_IdAndChatUser_Username(chatId, username)
                .ifPresent(activeMembership -> activeMembershipMapper.updateFromDTO(activeMembershipUpdateDTO, activeMembership));
    }

    @PreAuthorize("@chatSecurity.canManageChatWithIdWhenMembershipIs(#chatId, 'ADMIN', authentication)")
    public void giveUpAdminMembership(UUID chatId, String username, @Valid GiveUpAdminDTO giveUpAdminDTO) {
        ActiveMembership me = activeMembershipRepository
                .findFirstByChat_IdAndChatUser_Username(chatId, username)
                .orElseThrow(() -> new NoSuchElementException("Membership not found"));

        if (me.getMembershipType() != MembershipType.ADMIN) {
            throw new IllegalStateException("Only admin can give up admin role");
        }

        long adminCount = activeMembershipRepository.countByChat_IdAndMembershipType(chatId, MembershipType.ADMIN);

        // If there are other admins, just demote
        if (adminCount >= 2) {
            me.setMembershipType(MembershipType.EDITOR);
            return;
        }

        String successorUsername = Optional.ofNullable(giveUpAdminDTO.successorUsername())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Successor is required when you are the only ADMIN"
                ));

        if (successorUsername.equals(username)) {
            throw new IllegalArgumentException("Successor cannot be yourself");
        }

        ActiveMembership successor = activeMembershipRepository
                .findFirstByChat_IdAndChatUser_Username(chatId, successorUsername)
                .orElseThrow(() -> new NoSuchElementException("Successor not in chat"));

        successor.setMembershipType(MembershipType.ADMIN);
        me.setMembershipType(MembershipType.EDITOR);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithIdWhenMembershipIs(#chatId, 'ADMIN', authentication)")
    public void deleteMembershipFromChat(UUID chatId, String username) {
        ChatMembership chatMembership = chatMembershipRepository.findByChat_IdAndChatUser_Username(chatId, username)
                .orElseThrow();
        chatMembershipRepository.delete(chatMembership);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithIdWhenMembershipIs(#chatId, 'ADMIN', authentication)")
    public void inviteChatUser(UUID chatId, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        ChatUser user = chatUserRepository.findByUsername(username).orElseThrow();

        boolean alreadyMember = chatMembershipRepository
                .existsByChat_IdAndChatUser_Username(chatId, username);
        if (alreadyMember) {
            throw new IllegalStateException("User is already a member of this chat");
        }

        Invitation invitation = Invitation.builder()
                .chat(chat)
                .chatUser(user)
                .build();

        invitationRepository.save(invitation);
    }

    @PreAuthorize("@chatUserSecurity.hasUsername(#username, authentication)")
    public List<UserInvitationsDTO> getInvitationsForMe(String username) {
        return invitationRepository.findAllByChatUser_Username(username)
                .stream()
                .map(invitationMapper::toUserInvitationsDTO)
                .toList();
    }

    @PreAuthorize("@chatSecurity.canManageMembershipWithId(#invitationId, authentication)")
    public void deleteInvitationWithId(UUID invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow();
        invitationRepository.delete(invitation);
    }

    @PreAuthorize("@chatSecurity.canManageMembershipWithId(#invitationId, authentication)")
    public void acceptInvitationWithId(UUID invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow();
        invitationRepository.delete(invitation);
        invitationRepository.flush();
        ActiveMembership activeMembership = ActiveMembership.builder()
                .chat(invitation.getChat())
                .chatUser(invitation.getChatUser())
                .membershipType(MembershipType.MEMBER)
                .build();
        activeMembershipRepository.save(activeMembership);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public List<InvitationDTO> getInvitationsForChat(UUID chatId) {
        return invitationRepository.findAllByChat_Id(chatId).stream()
                .map(invitationMapper::toInvitationDTO)
                .toList();
    }

    @PreAuthorize("@chatSecurity.canManageChatWithIdWhenMembershipIs(#chatId, 'ADMIN', authentication)")
    public void deleteInvitationForChatWithUser(UUID chatId, String username) {
        Invitation invitation = invitationRepository.findFirstByChat_IdAndChatUser_Username(chatId, username).orElseThrow();
        invitationRepository.delete(invitation);
    }

    @PreAuthorize("@chatSecurity.canManageChatWithId(#chatId, authentication)")
    public Integer getMessagesCountForChat(UUID chatId) {
        return messageRepository.countByChat_Id(chatId);
    }
}
