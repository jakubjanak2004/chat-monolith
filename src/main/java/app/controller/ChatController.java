package app.controller;

import app.dto.ChatDTO;
import app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/me")
    public ResponseEntity<Page<ChatDTO>> getChatsForMe(@RequestParam(required = false) String query, Principal principal, Pageable pageable) {
        return ResponseEntity.ok(chatService.getChatsForUsernamePageable(query, principal.getName(), pageable));
    }
}
