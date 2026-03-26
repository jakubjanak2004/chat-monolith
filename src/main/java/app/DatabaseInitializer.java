package app;

import app.config.props.AdminSeedProperties;
import app.config.props.UsersSeedProperties;
import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Message;
import app.repository.ChatRepository;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import app.util.Generator;
import app.util.ParallelEntitySeedFactory;
import app.util.TextNormalize;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Transactional
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final Faker faker = new Faker(Locale.forLanguageTag("sk"));
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties adminSeedProperties;
    private final UsersSeedProperties usersSeedProperties;
    private final Generator chatUserGenerator;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Override
    public void run(@NotNull String... args) {
        LOGGER.info("Seeding database...");
        List<ChatUser> chatUsers = createSeedUsers();
        ChatUser firstChatUser = chatUsers.getFirst();
        createAdmin(firstChatUser);
        createTestUsers(usersSeedProperties.count(), usersSeedProperties.password(), usersSeedProperties.chatCount(), usersSeedProperties.messageCount());
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

    private List<ChatUser> createSeedUsers() {
        // todo add profile pic to users
        if (!usersSeedProperties.enabled()) return List.of();

        ParallelEntitySeedFactory<ChatUser> parallelEntitySeedFactory = new ParallelEntitySeedFactory<>(ChatUser.class, i -> {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress();
            String encodedPassword = passwordEncoder.encode(usersSeedProperties.password());
            String normalizedUsername = TextNormalize.normalize(String.format("%s%s", firstName, lastName));

            return ChatUser.builder()
                    .username(String.format("%s-%d", normalizedUsername, i))
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(encodedPassword)
                    .build();
        });
        return chatUserGenerator.generateChatUsers(usersSeedProperties.count(), parallelEntitySeedFactory);
    }

    private void createTestUsers(int count, String password, int chatCount, int messagesCount) {
        if (!usersSeedProperties.enabled()) return;

        ParallelEntitySeedFactory<ChatUser> parallelEntitySeedFactory = new ParallelEntitySeedFactory<>(ChatUser.class, i -> {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress();
            String encodedPassword = passwordEncoder.encode(password);

            return ChatUser.builder()
                    .username(String.format("test%d", i))
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(encodedPassword)
                    .build();
        });

        List<ChatUser> users = chatUserGenerator.generateChatUsers(count, parallelEntitySeedFactory);

        if (users.size() < 2) {
            LOGGER.info("Skipping test chat creation, need at least 2 users. users={}", users.size());
            return;
        }

        ChatUser owner = users.getFirst();
        List<ChatUser> others = users.subList(1, users.size());

        AtomicInteger atomicInteger = new AtomicInteger(0);
        ParallelEntitySeedFactory<Chat> parallelChatSeedFactory = new ParallelEntitySeedFactory<>(Chat.class, i -> {
            ChatUser otherUser = users.get(i % others.size());
            String chatName = String.format("test chat %d", atomicInteger.getAndIncrement());
            return Chat.createChatWithOwnerAndMembers(chatName, owner, List.of(otherUser));
        });
        List<Chat> chatList = parallelChatSeedFactory.createEntities((others.size()) - 1);
        List<Chat> savedChatList = chatRepository.saveAll(chatList);

        ParallelEntitySeedFactory<Message> parallelMessageSeedFactory = new ParallelEntitySeedFactory<>(Message.class, i -> {
            Chat chat = savedChatList.get(i % chatList.size());
            List<ChatUser> chatUsers = chat.getChatMemberships().stream()
                    .map(ChatMembership::getChatUser)
                    .toList();
            ChatUser chatUser = chatUsers.get(ThreadLocalRandom.current().nextInt(chatUsers.size()));

            return Message.builder()
                    // todo word cont should not be a magic constant
                    .content(faker.lorem().sentence(30))
                    // todo not setting response to right now
//                    .responseTo(responseTo)
                    .chat(chat)
                    .chatUser(chatUser)
                    .build();
        });

        List<Message> messageList = parallelMessageSeedFactory.createEntities(messagesCount);
        messageRepository.saveAll(messageList);
    }
}
