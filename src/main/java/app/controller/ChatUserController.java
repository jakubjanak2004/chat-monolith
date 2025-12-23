package app.controller;

import app.dto.ChatUserDTO;
import app.dto.ChatUserUpdateDTO;
import app.dto.PictureDTO;
import app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.UUID;

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

    @PutMapping("/me/profile-picture")
    public ResponseEntity<Void> updateMyProfilePicture(@RequestPart("file") MultipartFile file, Principal principal) {
        chatUserService.updateUserProfilePicture(file, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<Resource> getUsersProfilePicture(@PathVariable String username) {
        PictureDTO pic = chatUserService.getProfilePictureForUsername(username);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pic.contentType()))
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(pic.stream()));
    }
}
