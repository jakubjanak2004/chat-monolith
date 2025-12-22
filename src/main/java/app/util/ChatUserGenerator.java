package app.util;

import app.entity.ChatUser;
import app.repository.ChatUserRepository;
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
}
