package app.mapper;

import app.dto.AuthResponseDTO;
import app.entity.ChatUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuthResponseMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "token", target = "token")
    AuthResponseDTO toDto(ChatUser user, String token);
}
