package app.util;

import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Message;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import net.datafaker.Faker;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ChatUserGenerator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("sk"));
    private static final Random RANDOM = new Random();
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;

    public ChatUser generateChatUser(String password) {
        ChatUser chatUser = constructNewUser(password);
        return chatUserRepository.save(chatUser);
    }

    private ChatUser constructNewUser(String password) {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String username = String.format("%s %s", firstName, lastName);
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

    public List<ChatUser> generateChatUsers(int count, String password) {
        List<ChatUser> chatUserList = IntStream.rangeClosed(1, count)
                .parallel()
                .mapToObj(i -> generateChatUser(password))
                .toList();
        return chatUserRepository.saveAll(chatUserList);
    }

    public Message generateMessagesForChat(Chat chat, int wordCountFrom, int wordCountTo) {
        int wordCount = RANDOM.nextInt(wordCountFrom, wordCountTo + 1);
        List<ChatUser> chatUsers = chat.getChatMemberships().stream()
                .map(ChatMembership::getChatUser)
                .toList();
        ChatUser chatUser = chatUsers.get(RANDOM.nextInt(chatUsers.size()));
        return messageRepository.save(
                Message.builder()
                        .content(faker.lorem().sentence(wordCount))
                        .chat(chat)
                        .chatUser(chatUser)
                        .build()
        );
    }
}
