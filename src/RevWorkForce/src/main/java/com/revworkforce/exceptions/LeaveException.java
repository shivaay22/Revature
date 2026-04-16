package com.revworkforce.exceptions;



public class LeaveException extends RuntimeException {
    public LeaveException(String message) {
        super(message);
    }

    public LeaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
