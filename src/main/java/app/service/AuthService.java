package app.service;

import app.dto.response.AuthResponseDTO;
import app.dto.request.LoginDTO;
import app.dto.request.SignUpDTO;
import app.entity.ChatUser;
import app.event.UserCreatedEvent;
import app.exception.UsernameAlreadyExistsException;
import app.mapper.ChatUserMapper;
import app.repository.ChatUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatUserMapper chatUserMapper;

    @Value("${app.jwt.issuer}")
    private String jwtIssuer;
    @Value("${app.jwt.access-ttl}")
    private Duration jwtAccessTTL;

    public AuthResponseDTO login(@Valid LoginDTO loginDTO) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        return Optional.ofNullable(auth.getPrincipal())
                .map(obj -> (ChatUser) obj)
                .map(chatUser -> {
                    String token = generateToken(chatUser);
                    return chatUserMapper.toAuthResponseDTO(chatUser, token);
                }).orElseThrow();
    }

    public AuthResponseDTO signUp(@Valid SignUpDTO dto) {
        ensureUsernameAvailable(dto.getUsername());

        ChatUser user = chatUserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        ChatUser saved = chatUserRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(saved.getEmail(), saved.getUsername()));

        String token = generateToken(saved);
        return chatUserMapper.toAuthResponseDTO(saved, token);
    }

    private void ensureUsernameAvailable(String username) {
        if (chatUserRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }
    }


    private String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(jwtIssuer).issuedAt(now).expiresAt(now.plus(jwtAccessTTL)).subject(userDetails.getUsername()).build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }
}
