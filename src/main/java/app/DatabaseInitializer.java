package app;

import app.config.props.AdminSeedProperties;
import app.config.props.UsersSeedProperties;
import app.entity.Chat;
import app.entity.ChatUser;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.util.Generator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private final Generator chatUserGenerator;
    private final ChatRepository chatRepository;

    @Override
    public void run(String... args) {
        LOGGER.info("Seeding database...");
        List<ChatUser> chatUsers = createUsers();
        ChatUser firstChatUser = chatUsers.getFirst();
        LOGGER.info("firstChatUser: {}", firstChatUser);
        createAdmin(firstChatUser);
        LOGGER.info("creating testing users");
        createChatUsers();
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
                Chat.createChatWithOwnerAndMembers("test chat 2", adminChatUser, List.of(chatUser)),
                Chat.createChatWithOwnerAndMembers("test chat 3", adminChatUser, List.of(chatUser)),
                Chat.createChatWithOwnerAndMembers("test chat 4", adminChatUser, List.of(chatUser)),
                // in these chats the user will only be invited
                Chat.createChatWithOwnerAndInvitees("test chat 5", chatUser, List.of(adminChatUser)),
                Chat.createChatWithOwnerAndInvitees("test chat 6", chatUser, List.of(adminChatUser)),
                Chat.createChatWithOwnerAndInvitees("test chat 7", chatUser, List.of(adminChatUser)),
                Chat.createChatWithOwnerAndInvitees("test chat 8", chatUser, List.of(adminChatUser))

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

        // todo add profile pic to users
        return chatUserGenerator.generateChatUsers(usersSeedProperties.count(), usersSeedProperties.password());
    }

    private void createChatUsers() {
        createChatUsers(usersSeedProperties.count(), usersSeedProperties.chatCount(), usersSeedProperties.messageCount());
    }

    private void createChatUsers(int count, int chatCount, int messagesCount) {
        if (!usersSeedProperties.enabled()) return;

        List<ChatUser> users = IntStream.rangeClosed(1, count)
                .mapToObj(i -> getOrCreateTestUser(String.format("test%d", i)))
                .toList();

        if (users.size() < 2) {
            LOGGER.info("Skipping test chat creation, need at least 2 users. users={}", users.size());
            return;
        }

        ChatUser owner = users.getFirst();
        List<ChatUser> others = users.subList(1, users.size());

        for (int c = 1; c <= chatCount; c++) {
            for (int i = 0; i < others.size(); i++) {
                ChatUser other = others.get(i);
                String chatName = String.format("test chat %d-%d", c, i + 1);
                Chat chat = Chat.createChatWithOwnerAndMembers(chatName, owner, List.of(other));
                Chat savedChat = chatRepository.save(chat);

                IntStream.rangeClosed(1, messagesCount).forEach(n ->
                        chatUserGenerator.generateMessagesForChat(savedChat, 3, 12)
                );
            }
        }
    }

    private ChatUser getOrCreateTestUser(String username) {
        Optional<ChatUser> existing = chatUserRepository.findByUsername(username);
        return existing.orElseGet(() -> chatUserGenerator.generateChatUser(username, "testing"));
    }
}
