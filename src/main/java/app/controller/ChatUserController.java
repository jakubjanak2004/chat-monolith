package app.controller;

import app.dto.ChatUserUpdateDTO;
import app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody ChatUserUpdateDTO chatUserUpdateDTO, Principal principal) {
        chatUserService.updateMe(chatUserUpdateDTO, principal);
        return ResponseEntity.ok().build();
    }
}
