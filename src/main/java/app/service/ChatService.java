package app.service;

import app.dto.ChatDTO;
import app.mapper.ChatMapper;
import app.repository.ChatMembershipRepository;
import app.repository.ChatRepository;
import app.util.TextNormalize;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMembershipRepository chatMembershipRepository;
    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;

    public Page<ChatDTO> getChatsForUsernamePageable(String query, String username, Pageable pageable) {
        String queryNormalized = TextNormalize.normalize(query);
        return chatRepository.findByNameNormAndUsername(queryNormalized, username, pageable)
                .map(chatMapper::toDTO);
    }
}
