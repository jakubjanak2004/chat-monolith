package app.util;

import app.entity.Chat;
import app.entity.ChatMembership;
import app.entity.ChatUser;
import app.entity.Message;
import app.repository.ChatUserRepository;
import app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
// TODO: do not use repository.save() when list of objects is created, use saveAll() always
public class Generator {
    private static final Faker faker = new Faker(Locale.forLanguageTag("sk"));
    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
    private final ChatUserRepository chatUserRepository;
    private final MessageRepository messageRepository;

    public List<ChatUser> generateChatUsers(int count, ParallelEntitySeedFactory<ChatUser> parallelEntitySeedFactory) {
        List<ChatUser> chatUserList = parallelEntitySeedFactory.createEntities(count);
        LOGGER.info("Saving ChatUser instances...");
        List<ChatUser> chatUsersSavedList = chatUserRepository.saveAll(chatUserList);
        LOGGER.info("ChatUser instances saved");
        return chatUsersSavedList;
    }

    public Message generateMessagesForChat(Chat chat, int wordCountFrom, int wordCountTo) {
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

        return messageRepository.save(
                Message.builder()
                        .content(faker.lorem().sentence(wordCount))
                        .responseTo(responseTo)
                        .chat(chat)
                        .chatUser(chatUser)
                        .build()
        );
    }

    public long getNumberOfUsers() {
        return chatUserRepository.count();
    }
}
