package app;

import app.config.AdminSeedConfig;
import app.entity.ChatUser;
import app.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional
public class DatabaseInitializer implements CommandLineRunner {
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedConfig adminSeedConfig;

    @Override
    public void run(String... args) {
        if (!adminSeedConfig.enabled()) return;

        if (chatUserRepository.existsByUsername(adminSeedConfig.username())) return;

        ChatUser adminChatUser = ChatUser.builder()
                .username(adminSeedConfig.username())
                .password(passwordEncoder.encode(adminSeedConfig.password()))
                .email(adminSeedConfig.email())
                .firstName(adminSeedConfig.firstName())
                .lastName(adminSeedConfig.lastName())
                .build();

        chatUserRepository.save(adminChatUser);
    }
}
