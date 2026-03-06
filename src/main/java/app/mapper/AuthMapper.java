package app.mapper;

import app.dto.request.LoginDTO;
import org.mapstruct.Mapper;
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
}
