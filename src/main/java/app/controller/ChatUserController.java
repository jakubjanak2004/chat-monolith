package app.controller;

import app.dto.ChatUserDTO;
import app.dto.ChatUserUpdateDTO;
import app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody ChatUserUpdateDTO chatUserUpdateDTO, Principal principal) {
        chatUserService.updateUserWithUsername(chatUserUpdateDTO, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ChatUserDTO>> getUsersNotMePageable(@RequestParam(required = false) String query, Principal principal, Pageable pageable) {
        return ResponseEntity.ok(chatUserService.getUsersNotUsernamePageable(query, principal.getName(), pageable));
    }
}
