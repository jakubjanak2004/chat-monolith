package app.util;

import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Message;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import net.datafaker.Faker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Generator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("sk"));
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;

    public void generateChatUser(String username, String password) {
        ChatUser chatUser = constructNewUser(username, password);
        chatUserRepository.save(chatUser);
    }

    private ChatUser constructNewUser(String password) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String username = ensureUniqueUsername(String.format("%s %s", firstName, lastName));
        String email = faker.internet().emailAddress();
        String encodedPassword = passwordEncoder.encode(password);

        return ChatUser.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(encodedPassword)
                .build();
    }

    private ChatUser constructNewUser(String username, String password) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String encodedPassword = passwordEncoder.encode(password);
        String uniqueUsername = ensureUniqueUsername(username);

        return ChatUser.builder()
                .username(uniqueUsername)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(encodedPassword)
                .build();
    }

    public List<ChatUser> generateChatUsers(int count, String password) {
        List<ChatUser> chatUserList = IntStream.rangeClosed(1, count)
                .mapToObj(i -> constructNewUser(password))
                .toList();
        return chatUserRepository.saveAll(chatUserList);
    }

    private String ensureUniqueUsername(String base) {
        String candidate = base;
        AtomicInteger suffix = new AtomicInteger(2);
        while (chatUserRepository.existsByUsername(candidate)) {
            candidate = base + " " + suffix.getAndIncrement();
        }
        return candidate;
    }

    public void generateMessagesForChat(Chat chat, int wordCountFrom, int wordCountTo) {
        int wordCount = ThreadLocalRandom.current().nextInt(wordCountFrom, wordCountTo + 1);
        List<ChatUser> chatUsers = chat.getChatMemberships().stream()
                .map(ChatMembership::getChatUser)
                .toList();
        ChatUser chatUser = chatUsers.get(ThreadLocalRandom.current().nextInt(chatUsers.size()));
        Message responseTo = null;
        if (ThreadLocalRandom.current().nextBoolean()) {
            Page<Message> previousMessages =
                    messageRepository.findAllByChat(chat, PageRequest.of(0, 10));

            if (previousMessages.hasContent()) {
                List<Message> content = previousMessages.getContent();
                int idx = ThreadLocalRandom.current().nextInt(content.size());
                responseTo = content.get(idx);
            }
        }
        messageRepository.save(
                Message.builder()
                        .content(faker.lorem().sentence(wordCount))
                        .responseTo(responseTo)
                        .chat(chat)
                        .chatUser(chatUser)
                        .build()
        );
    }
}
