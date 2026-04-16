// src/main/java/com/revworkforce/exception/AuthorizationException.java
package com.revworkforce.exceptions;


public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}