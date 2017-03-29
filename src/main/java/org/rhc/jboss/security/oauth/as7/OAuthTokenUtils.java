package org.rhc.jboss.security.oauth.as7;

/**
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenUtils {

    static String[] splitToken(String token) throws OAuthTokenDecodeException {

        String[] parts = token.split("\\.");

        if (parts.length == 2 && token.endsWith(".")) {

            parts = new String[]{parts[0], parts[1], ""};
        }
        if (parts.length != 3) {

            throw new OAuthTokenDecodeException(String.format("The token was expected to have 3 parts, but got %s.", parts.length));
        }



        return parts;
    }
}
