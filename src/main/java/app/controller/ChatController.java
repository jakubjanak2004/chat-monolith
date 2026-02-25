package app.controller;

import app.dto.request.CreateChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.response.ChatDTO;
import app.dto.response.MessageDTO;
import app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChatDTO>> getChatsForMe(@RequestParam(required = false) String query, Principal principal, @ParameterObject Pageable pageable) {
        LOGGER.info("GET /chats/me?query={}", query);
        return ResponseEntity.ok(chatService.getChatsForUsernamePageable(query, principal.getName(), pageable));
    }

    @PostMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatDTO> createChat(@RequestBody CreateChatDTO createChatDTO, Principal principal) {
        return ResponseEntity.ok(chatService.createChatForUser(createChatDTO, principal.getName()));
    }

    @GetMapping(value = "/{chatId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MessageDTO>> getMessagesForChat(@PathVariable UUID chatId, @ParameterObject Pageable pageable) {
        LOGGER.info("GET /chats/{}/messages?page={}&size={}&sort={}",
                chatId, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(chatService.getMessagesForChatPageable(chatId, pageable));
    }

    @PostMapping(value = "/{chatId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO> createMessage(@PathVariable UUID chatId, @RequestBody CreateMessageDTO createMessageDTO, Principal principal) {
        LOGGER.info("POST /chats/{}/messages", chatId);
        return ResponseEntity.ok(chatService.createMessageForChat(chatId, createMessageDTO, principal.getName()));
    }

    @GetMapping(value = "/messages/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO> getMessage(@PathVariable UUID messageId) {
        LOGGER.info("GET /chats/messages/{}", messageId);
        return ResponseEntity.ok(chatService.getMessage(messageId));
    }

    // todo use to check if chat is present
    @GetMapping(value = "/me/person/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatDTO> getChatIdOfChatWithPerson(@PathVariable String username, Principal principal) {
        LOGGER.info("GET /me/person/{}/id", username);
        return ResponseEntity.ok(chatService.getChatIdOfChatWithPerson(username, principal.getName()));
    }
}
