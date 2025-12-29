package app.controller;

import app.dto.response.ChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.response.MessageDTO;
import app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/me")
    public ResponseEntity<Page<ChatDTO>> getChatsForMe(@RequestParam(required = false) String query, Principal principal, Pageable pageable) {
        return ResponseEntity.ok(chatService.getChatsForUsernamePageable(query, principal.getName(), pageable));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<MessageDTO>> getMessagesForChat(@PathVariable UUID chatId, Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessagesForChatPageable(chatId ,pageable));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable UUID chatId, @RequestBody CreateMessageDTO messageDTO, Principal principal) {
        return ResponseEntity.ok(chatService.createMessageForChat(chatId, messageDTO, principal.getName()));
    }
}
