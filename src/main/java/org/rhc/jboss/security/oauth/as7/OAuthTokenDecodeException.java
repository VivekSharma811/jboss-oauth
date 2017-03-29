package org.rhc.jboss.security.oauth.as7;

/**
 * Token decode exception
 * <p>
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenDecodeException extends OAuthTokenException {

    public OAuthTokenDecodeException(String message) {
        this(message, null);
    }

    public OAuthTokenDecodeException(String message, Throwable cause) {

        super(message, cause);
    }

}
