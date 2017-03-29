package org.rhc.jboss.security.oauth.as7;

import junit.framework.TestCase;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.rhc.jboss.security.oauth.as7.OAuthToken;
import org.rhc.jboss.security.oauth.as7.OAuthTokenDecodeException;
import org.rhc.jboss.security.oauth.as7.OAuthTokenService;
import org.rhc.jboss.security.oauth.as7.OAuthTokenServiceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenServiceTest extends TestCase {

    @Test
    public void testDecodeTokenWithGroups() throws Exception {

        final String encodeToken = readTokenFile("token.txt");


        final OAuthTokenService tokenSvc = OAuthTokenServiceFactory.getFactory().getService();

        final OAuthToken token = tokenSvc.decodeToken(encodeToken);

        assertNotNull(token);
        assertNotNull(token.getWinAccountName());
        assertNotNull(token.getGroups());

        assertTrue(token.getGroups().size() > 0);

    }

    @Test
    public void testDecodeTokenInvalidFormat() throws Exception {

        final String encodeToken = readTokenFile("token_invalid_encoding.txt");

        final OAuthTokenService tokenSvc = OAuthTokenServiceFactory.getFactory().getService();

        try {
            final OAuthToken token = tokenSvc.decodeToken(encodeToken);

            assertTrue("OAuthTokenDecodeException",false);
        }
        catch (OAuthTokenDecodeException tokenEx) {

            assertTrue("OAuthTokenDecodeException",true);

        }
    }

    // TODO
    public void testDecodeTokenNoUserGroups() throws Exception {

    }


    @Test
    public void testUserImpl() {

        Collection<String> roles = null;

        //roles.add(null);
                //Collections.emptyList();
        Collections.unmodifiableSet(new HashSet<String>(roles));

    }

    /**
     * Reads token file
     *
     * @param token file name.
     * @return
     * @throws Exception
     */
    private  String readTokenFile(final String fileName) throws Exception {

        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {

            final String encodedToken = org.apache.commons.io.IOUtils.toString(stream);

            return  encodedToken;
        }
    }


}
