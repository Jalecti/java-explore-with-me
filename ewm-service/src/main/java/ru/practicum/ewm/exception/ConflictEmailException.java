package ru.practicum.ewm.exception;

public class ConflictEmailException extends RuntimeException {
    public ConflictEmailException(String message) {
        super(message);
    }
}
