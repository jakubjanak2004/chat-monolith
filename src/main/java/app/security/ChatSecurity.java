package app.security;

import app.entity.ActiveMembership;
import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Message;
import app.enumeration.MembershipType;
import app.repository.ActiveMembershipRepository;
import app.repository.ChatMembershipRepository;
import app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component("chatSecurity")
@RequiredArgsConstructor
public class ChatSecurity {
    private final ChatMembershipRepository chatMembershipRepository;
    private final MessageRepository messageRepository;
    private final ActiveMembershipRepository activeMembershipRepository;



    public boolean canManageChatWithId(UUID chatId, Authentication authentication) {
        return chatMembershipRepository.existsByChat_IdAndChatUser_Username(chatId, authentication.getName());
    }

    public boolean canManageChatWithIdWhenMembershipIs(UUID chatId, Set<MembershipType> membershipTypes, Authentication authentication) {
        return activeMembershipRepository.findFirstByChat_IdAndChatUser_Username(chatId, authentication.getName())
                .map(ActiveMembership::getMembershipType)
                .map(membershipTypes::contains)
                .orElseThrow();
    }

    public boolean canManageMessageWithId(UUID messageId, Authentication authentication) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        Chat chat = message.getChat();
        return canManageChatWithId(chat.getId(), authentication);
    }

    public boolean canManageMembershipWithId(UUID membershipId, Authentication authentication) {
        return chatMembershipRepository.findById(membershipId)
                .map(ChatMembership::getChatUser)
                .map(ChatUser::getUsername)
                .map(username -> Objects.equals(username, authentication.getName()))
                .orElseThrow();
    }
}
