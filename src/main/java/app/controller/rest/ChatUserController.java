package app.controller.rest;

import app.dto.response.ChatUserDTO;
import app.dto.request.ChatUserUpdateDTO;
import app.dto.response.PictureDTO;
import app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ChatUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatUserController.class);
    private final ChatUserService chatUserService;

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody ChatUserUpdateDTO chatUserUpdateDTO, Principal principal) {
        LOGGER.info("PUT /users/me");
        chatUserService.updateUserWithUsername(chatUserUpdateDTO, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ChatUserDTO>> getUsersNotMePageable(@RequestParam(required = false) String query, Principal principal, Pageable pageable) {
        LOGGER.info("GET /users?query={}&page={}&size={}&sort={}", query, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(chatUserService.getUsersNotUsernamePageable(query, principal.getName(), pageable));
    }

    @PutMapping("/me/profile-picture")
    public ResponseEntity<Void> updateMyProfilePicture(@RequestPart("file") MultipartFile file, Principal principal) {
        LOGGER.info("PUT /users/me/profile-picture");
        chatUserService.updateUserProfilePicture(file, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/profile-picture")
    public ResponseEntity<Resource> getUsersProfilePicture(@PathVariable String username) {
        LOGGER.info("GET /users/{}/profile-picture", username);
        PictureDTO pic = chatUserService.getProfilePictureForUsername(username);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pic.contentType()))
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(pic.stream()));
    }
}
