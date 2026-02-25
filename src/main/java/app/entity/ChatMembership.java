package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "chat_membership",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "chat_user_id"})
)
public abstract class ChatMembership {
    @Id
    @GeneratedValue
    protected UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_user_id", nullable = false)
    protected ChatUser chatUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    protected Chat chat;
}
