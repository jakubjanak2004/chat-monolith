package app.exception;

import app.dto.response.ErrorMessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static String fullPath(HttpServletRequest req) {
        return req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }

    // 403
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAuthorizationDeniedException(
            AuthorizationDeniedException e,
            HttpServletRequest req
    ) {
        LOGGER.warn("403 FORBIDDEN {} {} -> {}", req.getMethod(), fullPath(req), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessageDTO(e.getMessage()));
    }

    // 404
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoSuchElementException(
            NoSuchElementException e,
            HttpServletRequest req
    ) {
        LOGGER.warn("404 NOT_FOUND {} {} -> {}", req.getMethod(), fullPath(req), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDTO(e.getMessage()));
    }

    // 409 (your custom example)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> handleUsernameAlreadyExistsException(
            UsernameAlreadyExistsException e,
            HttpServletRequest req
    ) {
        LOGGER.warn("409 CONFLICT {} {} -> {}", req.getMethod(), fullPath(req), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessageDTO(e.getMessage()));
    }

    // 400 - bean validation on params, etc.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageDTO> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest req
    ) {
        LOGGER.warn("400 BAD_REQUEST {} {} -> {}", req.getMethod(), fullPath(req), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(e.getMessage()));
    }

    // 400 - @Valid DTO body validation (common in REST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest req
    ) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");

        LOGGER.warn("400 BAD_REQUEST {} {} -> {}", req.getMethod(), fullPath(req), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(msg));
    }

    // 400 - THIS IS THE ONE FOR "chatId=null/undefined" UUID conversion
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest req
    ) {
        // Example message: "Parameter 'chatId' must be a valid UUID."
        String param = e.getName();
        String expected = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "required type";
        String value = String.valueOf(e.getValue());

        String msg = "Invalid value for parameter '" + param + "': '" + value + "'. Expected " + expected + ".";

        LOGGER.warn("400 BAD_REQUEST {} {} -> {}", req.getMethod(), fullPath(req), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDTO(msg));
    }
}
