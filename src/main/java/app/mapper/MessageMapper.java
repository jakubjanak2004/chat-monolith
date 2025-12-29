package app.mapper;

import app.dto.request.CreateMessageDTO;
import app.dto.response.MessageDTO;
import app.entity.Chat;
import app.entity.ChatUser;
import app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageMapper {
    @Mapping(source = "responseTo.id", target = "responseToId")
    @Mapping(source = "chatUser", target = "sender")
    MessageDTO toDTO(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responseTo", ignore = true)
    @Mapping(target = "responses", ignore = true)
    Message toEntity(CreateMessageDTO createMessageDTO, Chat chat, ChatUser chatUser, Instant created);
}
