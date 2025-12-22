package app.mapper;

import app.dto.ChatDTO;
import app.dto.MessageDTO;
import app.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ChatUserMapper.class, MessageMapper.class}
)
public abstract class ChatMapper {
    @Autowired
    protected MessageMapper messageMapper;

    @Mapping(target = "lastMessage", expression = "java(getLastMessage(chat))")
    @Mapping(source = "chatMemberships", target = "chatUsers")
    public abstract ChatDTO toDTO(Chat chat);

    protected MessageDTO getLastMessage(Chat chat) {
        if (chat.getMessages().isEmpty()) {
            return null;
        }
        return messageMapper.toDTO(chat.getMessages().getLast());
    }
}
