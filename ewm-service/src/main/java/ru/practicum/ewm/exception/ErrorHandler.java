package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Error with the input parameter.",
                e.getMessage()
                );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidation(final ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Error with the input parameter.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "Error with the specified ID.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictEmail(final ConflictEmailException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified email address.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictCategoryName(final ConflictCategoryNameException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified category name.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictEventDate(final ConflictEventDateException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified event date.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictStateAction(final ConflictStateActionException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified state action.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictEventState(final ConflictEventStateException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified event state.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictInitiator(final ConflictInitiatorException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the specified event initiator.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictParticipationRequest(final ConflictParticipationRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the participation request.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictParticipationRequestLimit(final ConflictParticipationRequestLimitException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the participation request limit.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictLinkedEventsCategory(final ConflictLinkedEventsCategoryException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the linked events with category",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictAuthorComment(final ConflictAuthorCommentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Error with the author comment",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleForbidden(final ForbiddenException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access is denied.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing servlet request parameter error.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleOther(final Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server error.",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}