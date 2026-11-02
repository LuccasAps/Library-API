package com.luccasaps.jpa.exceptions;

public class ObjectOptimisticLockingFailureException extends RuntimeException {
    public ObjectOptimisticLockingFailureException(String message) {
        super(message);
    }
}
