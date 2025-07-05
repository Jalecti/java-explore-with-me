package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Getter
public class ErrorResponse {
    private final HttpStatus status;
    private final String reason;
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

}