package app.service;

import app.config.props.JwtProperties;
import app.dto.request.LoginDTO;
import app.dto.request.RefreshRequestDTO;
import app.dto.request.SignUpDTO;
import app.dto.response.AuthResponseDTO;
import app.entity.ChatUser;
import app.entity.RefreshToken;
import app.event.UserCreatedEvent;
import app.exception.UsernameAlreadyExistsException;
import app.mapper.AuthMapper;
import app.mapper.ChatUserMapper;
import app.repository.ChatUserRepository;
import app.repository.RefreshTokenRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
public class AuthService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final ChatUserRepository chatUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatUserMapper chatUserMapper;
    private final AuthMapper authMapper;
    private final MacAlgorithm macAlgorithm;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponseDTO login(@Valid LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = authMapper.toUsernamePasswordAuthenticationToken(loginDTO);
        Authentication auth = authManager.authenticate(usernamePasswordAuthenticationToken);

        return Optional.ofNullable(auth.getPrincipal())
                .map(obj -> (ChatUser) obj)
                .map(chatUser -> {
                    String accessToken = generateAccessToken(chatUser);

                    RefreshToken refreshToken = generateRefreshToken(chatUser);

                    RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);

                    return authMapper.toAuthResponseDTO(chatUser, accessToken, savedRefreshToken);
                }).orElseThrow();
    }

    public AuthResponseDTO signUp(@Valid SignUpDTO dto) {
        ensureUsernameAvailable(dto.username());

        ChatUser user = chatUserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        ChatUser saved = chatUserRepository.save(user);

        UserCreatedEvent userCreatedEvent = chatUserMapper.toUserCreatedEvent(saved);
        eventPublisher.publishEvent(userCreatedEvent);

        String accessToken = generateAccessToken(saved);

        RefreshToken savedRefreshToken = refreshTokenRepository.save(generateRefreshToken(saved));

        return authMapper.toAuthResponseDTO(saved, accessToken, savedRefreshToken);
    }

    private void ensureUsernameAvailable(String username) {
        if (chatUserRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }
    }

    public AuthResponseDTO refresh(@Valid RefreshRequestDTO refreshRequestDTO) {
        RefreshToken newRefreshToken = rotateToken(refreshRequestDTO.refreshToken());

        ChatUser chatUser = newRefreshToken.getUser();

        String newAccessToken = generateAccessToken(chatUser);

        return authMapper.toAuthResponseDTO(chatUser, newAccessToken, newRefreshToken);
    }

    private String generateAccessToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(now.plus(jwtProperties.accessTTL()))
                .subject(userDetails.getUsername())
                .build();

        JwsHeader headers = JwsHeader.with(macAlgorithm).build();
        return jwtEncoder
                .encode(JwtEncoderParameters.from(headers, claims))
                .getTokenValue();
    }

    private RefreshToken generateRefreshToken(ChatUser chatUser) {
        return RefreshToken.builder()
                .token(generateRefreshTokenValue())
                .user(chatUser)
                .expiresAt(Instant.now().plus(jwtProperties.refreshTTL()))
                .build();
    }

    private RefreshToken rotateToken(String refreshToken) {
        RefreshToken oldRefreshToken = refreshTokenRepository
                .findByTokenAndExpiresAtAfter(refreshToken, Instant.now())
                .orElseThrow();

        ChatUser chatUser = oldRefreshToken.getUser();

        refreshTokenRepository.delete(oldRefreshToken);

        RefreshToken newRefreshToken = generateRefreshToken(chatUser);

        return refreshTokenRepository.save(newRefreshToken);
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
