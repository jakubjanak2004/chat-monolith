package app.mapper;

import app.dto.AuthResponseDTO;
import app.dto.ChatUserUpdateDTO;
import app.dto.SignUpDTO;
import app.entity.ChatUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ChatUserMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "token", target = "token")
    AuthResponseDTO toDto(ChatUser user, String token);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateFromDto(ChatUserUpdateDTO chatUserUpdateDTO, @MappingTarget ChatUser user);

    @Mapping(target="id", ignore = true)
    ChatUser toEntity(SignUpDTO signUpDTO);
}
