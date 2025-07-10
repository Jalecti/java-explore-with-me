package ru.practicum.ewm.exception;

public class ConflictEventStateException extends RuntimeException {
    public ConflictEventStateException(String message) {
        super(message);
    }
}
