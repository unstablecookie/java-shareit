package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnsupportedStatusException.class)
    protected ResponseEntity<ErrorResponse> handleUnsupportedStatusException(UnsupportedStatusException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, "Unknown state: UNSUPPORTED_STATUS"), status);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorResponse(status, "item bookings not found"), status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status, "entity not found"), status);
    }

    @ExceptionHandler(TimeOverlapException.class)
    protected ResponseEntity<ErrorResponse> handleTimeOverlapException(TimeOverlapException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status, ""), status);
    }

    @ExceptionHandler(UserMissMatchException.class)
    protected ResponseEntity<ErrorResponse> handleUserMissMatchException(UserMissMatchException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorResponse(status, ""), status);
    }
}
