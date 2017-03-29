package org.rhc.jboss.security.oauth.as7;

/**
 * Created by ajurcenk on 2/15/17.
 */
public interface OAuthTokenFields {

    //Header
    String ALGORITHM = "alg";
    String CONTENT_TYPE = "cty";
    String TYPE = "typ";
    String KEY_ID = "kid";

    //Payload
    String ISSUER = "iss";
    String SUBJECT = "sub";
    String EXPIRES_AT = "exp";
    String NOT_BEFORE = "nbf";
    String ISSUED_AT = "iat";
    String JWT_ID = "jti";
    String AUDIENCE = "aud";

    // Custom
    String WIN_ACCOUNT_NAME = "winaccountname";
    String WIN_ACCOUNT_GROUPS = "group";

}
