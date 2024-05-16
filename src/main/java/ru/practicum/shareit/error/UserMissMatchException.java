package ru.practicum.shareit.error;

public class UserMissMatchException extends RuntimeException {
    public UserMissMatchException() {
        super();
    }

    public UserMissMatchException(String message) {
        super(message);
    }
}
