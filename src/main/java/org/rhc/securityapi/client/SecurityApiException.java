package org.rhc.securityapi.client;

/**
 * Security API exception
 */
public class SecurityApiException extends RuntimeException {

    public SecurityApiException(String message) {
        this(message, null);
    }

    public SecurityApiException(String message, Throwable cause) {

        super(message, cause);
    }
}
