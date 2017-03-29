package org.rhc.jboss.security.oauth.as7;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Simple token service
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenServiceImpl implements OAuthTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenServiceImpl.class);

    public OAuthToken decodeToken(String tokenStr) {


        LOG.debug("Entering to decodeToken()");


        final String[] parts = OAuthTokenUtils.splitToken(tokenStr);

        final OAuthTokenDecoder decoder = new OAuthTokenDecoder();

        String payloadJson;

        try {

            // Decode token
            payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));

            LOG.debug("payloadJson: {}", payloadJson);

        } catch (NullPointerException e) {

            LOG.warn("The UTF-8 Charset isn't initialized.", e);

            throw new OAuthTokenDecodeException("The UTF-8 Charset isn't initialized.", e);
        }

        // Parse token json
        final OAuthToken token = decoder.parseToken(payloadJson);

        LOG.debug("Exiting from decodeToken()");

        return token;

    }


}
