package app.dto;
import java.io.InputStream;

public record PictureDTO(InputStream stream, String contentType) {}
