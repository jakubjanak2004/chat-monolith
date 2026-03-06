package app.controller;

import app.dto.request.ChatUserUpdateDTO;
import app.dto.response.ChatUserDTO;
import app.dto.response.PictureDTO;
import app.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
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

    @PutMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateMe(@RequestBody ChatUserUpdateDTO chatUserUpdateDTO, Principal principal) {
        LOGGER.info("PUT /users/me");
        chatUserService.updateUserWithUsername(chatUserUpdateDTO, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChatUserDTO>> getUsersNotMe(@RequestParam(required = false) String query, Principal principal, @ParameterObject Pageable pageable) {
        LOGGER.info("GET /users?query={}&page={}&size={}&sort={}", query, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(chatUserService.getUsersNotUsername(query, principal.getName(), pageable));
    }

    @PutMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateMyProfilePicture(@RequestPart(value = "file") MultipartFile file, Principal principal) {
        LOGGER.info("PUT /users/me/profile-picture");
        chatUserService.updateUserProfilePicture(file, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{username}/profile-picture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> getUsersProfilePicture(@PathVariable String username) {
        LOGGER.info("GET /users/{}/profile-picture", username);
        PictureDTO pic = chatUserService.getProfilePictureForUsername(username);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pic.contentType()))
                .cacheControl(CacheControl.noCache())
                .body(new InputStreamResource(pic.stream()));
    }
}
