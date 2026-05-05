package app.entity;

import app.util.TextNormalize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static app.config.props.ValidationConstraints.USERNAME_MAX_LENGTH;
import static app.config.props.ValidationConstraints.USERNAME_MIN_LENGTH;

@Entity
@Table(indexes = {
        @Index(name = "idx_chat_user_name_normalized", columnList = "nameNormalized")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    private String username;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasProfilePicture = false;

    @Column(nullable = false)
    @NotBlank
    private String firstName;

    @Column(nullable = false)
    @NotBlank
    private String lastName;

    @Column(nullable = false)
    private String nameNormalized;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "chatUser")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatUser")
    private List<ChatMembership> chatMemberships = new ArrayList<>();

    @PrePersist
    @PreUpdate
    void normalizeNames() {
        this.nameNormalized = TextNormalize.normalize(String.format("%s %s", firstName, lastName));
    }

    @Override
    public String toString() {
        return String.format("ChatUser{username=%s}", username);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
