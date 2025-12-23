package app;

import app.config.AdminSeedConfig;
import app.config.UsersSeedConfig;
import app.entity.Chat;
import app.entity.ChatUser;
import app.entity.Message;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import app.util.ChatUserGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Override
    public void run(String... args) {
        LOGGER.info("Seeding database...");
        List<ChatUser> chatUsers = createUsers();
        createAdmin(chatUsers.getFirst());
        LOGGER.info("Seeding finished");
    }

    private void createAdmin(ChatUser chatUser) {
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

        // todo add check for chat users creation
        // todo add more seeded data
        // creating and saving chat for admin
        List<Chat> chats = List.of(
                Chat.createChatWithOwnerAndMembers("test chat", adminChatUser, List.of(chatUser)),
                Chat.createChatWithOwnerAndMembers("test chat 2", adminChatUser, List.of(chatUser))
        );

        chatRepository.saveAll(chats);

        Message message = Message.builder()
                .content("test message content")
                .chat(chats.getFirst())
                .chatUser(chatUser)
                .build();

        messageRepository.save(message);
    }

    private List<ChatUser> createUsers() {
        if (!usersSeedConfig.enabled()) return List.of();

        return chatUserGenerator.generateChatUsers(usersSeedConfig.count(), usersSeedConfig.password());
    }
}
