package app.mapper;

import app.dto.request.LoginDTO;
import app.dto.response.AuthResponseDTO;
import app.entity.ChatUser;
import app.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthMapper {
    default UsernamePasswordAuthenticationToken toUsernamePasswordAuthenticationToken(LoginDTO loginDTO) {
        return new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password());
    }

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "refreshToken.token", target = "refreshToken")
    AuthResponseDTO toAuthResponseDTO(ChatUser user, String accessToken, RefreshToken refreshToken);
}
