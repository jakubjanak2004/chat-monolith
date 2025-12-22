package app.entity;

import app.enumeration.MembershipType;
import app.util.TextNormalize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chat {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nameNormalized;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ChatMembership> chatMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    @OrderBy("created ASC")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    @PreUpdate
    void normalizeNames() {
        this.nameNormalized = TextNormalize.normalize(name);
    }

    public static Chat createChatWithOwnerAndMembers(String chatName, ChatUser owner, List<ChatUser> members) {
        Chat chat = Chat.builder()
                .name(chatName)
                .build();

        ChatMembership ownerMembership = ActiveMembership.builder()
                .membershipType(MembershipType.OWNER)
                .chatUser(owner)
                .chat(chat)
                .build();

        List<ChatMembership> memberships = members.stream().map(member ->
                (ChatMembership) ActiveMembership.builder()
                        .membershipType(MembershipType.MEMBER)
                        .chatUser(member)
                        .chat(chat)
                        .build()
        ).toList();

        // adding all memberships to the Chat entity
        // when saved it will cascade the persist onto memberships
        chat.getChatMemberships().add(ownerMembership);
        chat.getChatMemberships().addAll(memberships);

        return chat;
    }
}
