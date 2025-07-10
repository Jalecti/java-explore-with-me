package ru.practicum.ewm.exception;

public class ConflictParticipationRequestLimitException extends RuntimeException {
    public ConflictParticipationRequestLimitException(String message) {
        super(message);
    }
}
