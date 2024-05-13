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
}
