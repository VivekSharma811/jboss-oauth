package org.rhc.jboss.security.oauth.as7;

/**
 *  OAuth token service factory
 *
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenServiceFactory {


    private static OAuthTokenServiceFactory INSTANCE = new OAuthTokenServiceFactory();

    /**
     * Default token service implementation
     */
    private OAuthTokenService tokenSvc = new OAuthTokenServiceImpl();


    public static OAuthTokenServiceFactory getFactory() {

        return INSTANCE;
    }

    /**
     * Returns token service implementation
     * @return
     */
    public  OAuthTokenService getService() {

        return tokenSvc;
    }

}
