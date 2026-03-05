package app.controller;

import app.dto.request.ActiveMembershipUpdateDTO;
import app.dto.request.CreateChatDTO;
import app.dto.request.CreateMessageDTO;
import app.dto.request.GiveUpAdminDTO;
import app.dto.response.ChatDTO;
import app.dto.response.ActiveMembershipDTO;
import app.dto.response.InvitationDTO;
import app.dto.response.UserInvitationsDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
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

    @GetMapping(value = "/me/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserInvitationsDTO>> getInvitationsForMe(Principal principal) {
        LOGGER.info("GET /chats/me/invitations?{}", principal.getName());
        return ResponseEntity.ok(chatService.getInvitationsForMe(principal.getName()));
    }

    @DeleteMapping(value = "/me/invitations/{invitationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteInvitation(@PathVariable UUID invitationId) {
        LOGGER.info("DELETE /chats/me/invitations/{}", invitationId);
        chatService.deleteInvitationWithId(invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/me/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable UUID invitationId) {
        LOGGER.info("ACCEPT /chats/me/invitations/{}", invitationId);
        chatService.acceptInvitationWithId(invitationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value="/{chatId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatDTO> getChat(@PathVariable UUID chatId) {
        LOGGER.info("GET /chats/{}", chatId);
        return ResponseEntity.ok(chatService.getChatById(chatId));
    }

    @PostMapping(value="/{chatId}/admin/transfer")
    public ResponseEntity<Void> giveUpAdminMembership(@PathVariable UUID chatId, Principal principal, @RequestBody GiveUpAdminDTO giveUpAdminDTO) {
        chatService.giveUpAdminMembership(chatId, principal.getName(), giveUpAdminDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{chatId}/memberships", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActiveMembershipDTO>> getMembershipsForChat(@PathVariable UUID chatId) {
        LOGGER.info("GET /chats/{}/memberships", chatId);
        return ResponseEntity.ok(chatService.getActiveMembershipsForChat(chatId));
    }

    @PostMapping(value = "/{chatId}/memberships/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> inviteChatUser(@PathVariable UUID chatId, @PathVariable String username) {
        chatService.inviteChatUser(chatId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{chatId}/memberships/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteMembership(@PathVariable UUID chatId, @PathVariable String username) {
        chatService.deleteMembershipFromChat(chatId, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{chatId}/memberships/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMembershipRole(@PathVariable UUID chatId, @PathVariable String username, @RequestBody ActiveMembershipUpdateDTO activeMembershipUpdateDTO) {
        chatService.updateMembershipRole(chatId, username, activeMembershipUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value="/{chatId}/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvitationDTO>> getInvitationsForChat(@PathVariable UUID chatId) {
        LOGGER.info("GET /chats/invitations?{}", chatId);
        return ResponseEntity.ok(chatService.getInvitationsForChat(chatId));
    }

    @DeleteMapping(value = "/{chatId}/invitations/{username}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable UUID chatId, @PathVariable String username) {
        LOGGER.info("DELETE /chats/invitations/{}?{}", chatId, username);
        chatService.deleteInvitationForChatWithUser(chatId, username);
        return ResponseEntity.ok().build();
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

    @GetMapping(value = "/me/person/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatDTO> getChatIdOfChatWithPerson(@PathVariable String username, Principal principal) {
        LOGGER.info("GET /me/person/{}/id", username);
        return ResponseEntity.ok(chatService.getChatIdOfChatWithPerson(username, principal.getName()));
    }
}
