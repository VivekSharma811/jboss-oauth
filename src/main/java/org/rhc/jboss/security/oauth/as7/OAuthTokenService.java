package org.rhc.jboss.security.oauth.as7;

/**
 * Created by ajurcenk on 2/15/17.
 */
public interface OAuthTokenService {

    /**
     * Decodes token
     *
     * @param tokenStr -  token BASE64 encoded string
     * @return - decoded token
     */
    OAuthToken decodeToken(final String tokenStr);

}

