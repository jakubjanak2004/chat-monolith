package app.service;

import app.dto.AuthResponseDTO;
import app.dto.LoginDTO;
import app.entity.ChatUser;
import app.mapper.AuthResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthResponseMapper authResponseMapper;
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
                    return authResponseMapper.toDto(chatUser, token);
                }).orElseThrow();
    }

    private String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(jwtIssuer).issuedAt(now).expiresAt(now.plus(jwtAccessTTL)).subject(userDetails.getUsername()).build();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }
}
