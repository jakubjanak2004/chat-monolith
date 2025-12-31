package app;

import app.config.props.AdminSeedProperties;
import app.config.props.UsersSeedProperties;
import app.entity.Chat;
import app.entity.ChatUser;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.util.ChatUserGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Transactional
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties adminSeedProperties;
    private final UsersSeedProperties usersSeedProperties;
    private final ChatUserGenerator chatUserGenerator;
    private final ChatRepository chatRepository;

    @Override
    public void run(String... args) {
        LOGGER.info("Seeding database...");
        List<ChatUser> chatUsers = createUsers();
        createAdmin(chatUsers.getFirst());
        LOGGER.info("Seeding finished");
    }

    private void createAdmin(ChatUser chatUser) {
        if (!adminSeedProperties.enabled()) return;

        if (chatUserRepository.existsByUsername(adminSeedProperties.username())) return;

        ChatUser adminChatUser = ChatUser.builder()
                .username(adminSeedProperties.username())
                .password(passwordEncoder.encode(adminSeedProperties.password()))
                .email(adminSeedProperties.email())
                .firstName(adminSeedProperties.firstName())
                .lastName(adminSeedProperties.lastName())
                .build();

        chatUserRepository.save(adminChatUser);

        // todo add check for chat users creation
        // todo add more seeded data
        // creating and saving chat for admin
        List<Chat> chats = List.of(
                Chat.createChatWithOwnerAndMembers("test chat", adminChatUser, List.of(chatUser)),
                Chat.createChatWithOwnerAndMembers("test chat 2", adminChatUser, List.of(chatUser))
        );

        chatRepository.saveAll(chats);

        IntStream.rangeClosed(1, adminSeedProperties.numOfMessages())
                .forEach(i -> chatUserGenerator.generateMessagesForChat(
                        chats.getFirst(),
                        adminSeedProperties.messageWordCountFrom(),
                        adminSeedProperties.messageWordCountTo())
                );
    }

    private List<ChatUser> createUsers() {
        if (!usersSeedProperties.enabled()) return List.of();

        return chatUserGenerator.generateChatUsers(usersSeedProperties.count(), usersSeedProperties.password());
    }
}
