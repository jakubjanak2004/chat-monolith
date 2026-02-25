package app.controller.rest;

import app.dto.request.LoginDTO;
import app.dto.request.SignUpDTO;
import app.dto.response.AuthResponseDTO;
import app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDTO> logIn(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponseDTO = authService.login(loginDTO);
        LOGGER.info("User {} logged in.", loginDTO.getUsername());
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping(value = "signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody SignUpDTO signUpDTO) {
        AuthResponseDTO authResponseDTO = authService.signUp(signUpDTO);
        LOGGER.info("User {} signed up.", signUpDTO.getUsername());
        return ResponseEntity.ok(authResponseDTO);
    }
}
