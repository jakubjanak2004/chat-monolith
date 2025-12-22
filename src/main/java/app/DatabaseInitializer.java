package app;

import app.config.AdminSeedConfig;
import app.config.UsersSeedConfig;
import app.entity.ChatUser;
import app.repository.ChatUserRepository;
import app.util.ChatUserGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Transactional
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedConfig adminSeedConfig;
    private final UsersSeedConfig usersSeedConfig;
    private final ChatUserGenerator chatUserGenerator;

    @Override
    public void run(String... args) {
        LOGGER.info("Seeding database...");
        createAdmin();
        createUsers();
        LOGGER.info("Seeding finished");
    }

    private void createAdmin() {
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

    private void createUsers() {
        if (!usersSeedConfig.enabled()) return;

        chatUserGenerator.generateChatUsers(usersSeedConfig.count(), usersSeedConfig.password());
    }
}
