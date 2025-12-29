package app.mapper;

import app.dto.ChatDTO;
import app.entity.Chat;
import app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ChatUserMapper.class, MessageMapper.class}
)
public interface ChatMapper {
    @Mapping(source = "lastMessage", target = "lastMessage")
    @Mapping(source = "chat.id", target = "id")
    @Mapping(source = "chat.chatMemberships", target = "chatUsers")
    ChatDTO toDTO(Chat chat, Message lastMessage);
}
