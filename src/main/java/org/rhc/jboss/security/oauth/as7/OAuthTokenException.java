package org.rhc.jboss.security.oauth.as7;

/**
 * Generic token exception
 *
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenException extends RuntimeException {

    public OAuthTokenException(String message) {
        this(message, null);
    }

    public OAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }

}



