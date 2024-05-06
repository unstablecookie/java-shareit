package ru.practicum.shareit.error;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException() {
        super();
    }

    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
