package ru.practicum.shareit.error;

public class UserMissMatchException extends RuntimeException {

    public UserMissMatchException(String message) {
        super(message);
    }
}
