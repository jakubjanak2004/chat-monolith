package app.mapper;

import app.dto.MessageDTO;
import app.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MessageMapper {
    @Mapping(source = "responseTo.id", target = "responseToId")
    MessageDTO toDTO(Message message);
}
