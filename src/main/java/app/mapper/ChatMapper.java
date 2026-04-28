package app.mapper;

import app.dto.response.ChatDTO;
import app.entity.Chat;
import app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ChatUserMapper.class, MessageMapper.class}
)
public interface ChatMapper {
    @Mapping(source = "chat.id", target = "id")
    ChatDTO toDTO(Chat chat);
}
