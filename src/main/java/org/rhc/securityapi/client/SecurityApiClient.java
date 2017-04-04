package org.rhc.securityapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Security API client
 */
public class SecurityApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityApiClient.class);

    public static final int HTTP_STATUS_OK = 200;
    public static final String URL_SECURITY_API_TOKEN_GET_TOKEN = "Security/api/Token/GetToken";
    public static final String URL_SECURITY_API_TOKEN_VALIDATE = "Security/api/Token/Validate";

    /**
     * Configuration object
     */
    private SecurityApiClientConfig cfg = null;

    /**
     * Constructor
     * @param cfg - configuration daya
     */
    public SecurityApiClient(SecurityApiClientConfig cfg) {

        this.cfg = cfg;
    }


    /**
     * Gets JWT token
     * @param req - contains access token and keys
     * @return
     */
    public AdfsToken getToken(final GetTokenRequest req) {

        LOG.debug("Entering to getToken() Request: {}", req);

        // Build url
        final StringBuilder urlBuilder = new StringBuilder(this.cfg.getBaseUrl());
        urlBuilder.append(cfg.getApiVersion()).append("/");
        urlBuilder.append(URL_SECURITY_API_TOKEN_GET_TOKEN);

        final String serviceUrl = urlBuilder.toString();

        // Create invocation object
        WebRequestInvocationBuilder invocationBuilder = WebRequestInvocationBuilder.create(serviceUrl);
        invocationBuilder.addApiKey(req.getApiKey());
        invocationBuilder.addAppKey(req.getAppKey());
        invocationBuilder.addAuthToken(this.cfg.getAuthToken());

        invocationBuilder.addEntity(Entity.entity(req, MediaType.APPLICATION_JSON));

        LOG.debug("Sending POST request to url: {}", serviceUrl);

        Response response = null;
        AdfsToken token = null;

        // Send POST request
        try {

            response = invocationBuilder.buildPost().invoke();

            LOG.debug("Request is completed. Status: {}", response.getStatus());

            if (response.getStatus() != HTTP_STATUS_OK) {

                LOG.warn("Error occurred getting token. Status code: {} Response: {}", response.getStatus(), response.readEntity(String.class));

                throw new SecurityApiException("Error occurred getting token. See error log for details.");
            }

            // Get token
            token = response.readEntity(AdfsToken.class);


        } finally {

            if (response != null) {

                response.close();
            }

        }

        LOG.debug("Exiting from getToken() Token: {}", token);

        return token;
    }


    /**
     * Validates JWT token
     *
     * @param req - contains token to validate
     * @return
     */
    public TokenValidationResult validateToken(final ValidateTokenRequest req) {

        LOG.debug("Entering to validateToken() Request: {}", req);

        // Build url
        final StringBuilder urlBuilder = new StringBuilder(this.cfg.getBaseUrl());
        urlBuilder.append(cfg.getApiVersion()).append("/");
        urlBuilder.append(URL_SECURITY_API_TOKEN_VALIDATE);

        final String serviceUrl = urlBuilder.toString();

        // Create invocation object
        WebRequestInvocationBuilder invocationBuilder = WebRequestInvocationBuilder.create(serviceUrl);

        invocationBuilder.addAuthToken(req.getToken());

        // Not needed
        invocationBuilder.addApiKey(req.getApiKey());
        invocationBuilder.addAppKey(req.getAppKey());

        invocationBuilder.addEntity(Entity.entity(req, MediaType.APPLICATION_JSON));

        LOG.debug("Sending POST request to url: {}", serviceUrl);

        Response response = null;
        TokenValidationResult tokenValidationResult = null;

        // Send POST request
        try {

            response = invocationBuilder.buildPost().invoke();

            LOG.debug("Request is completed. Status: {}", response.getStatus());

            if (response.getStatus() != HTTP_STATUS_OK) {

                LOG.warn("Error occurred getting token. Status code: {} Response: {}", response.getStatus(), response.readEntity(String.class));

                throw new SecurityApiException("Error occurred getting token. See error log for details.");
            }

            // Get token
            tokenValidationResult = response.readEntity(TokenValidationResult.class);


        } finally {

            if (response != null) {

                response.close();
            }

        }


        LOG.debug("Exiting from validateToken() Response: {}", tokenValidationResult);

        return tokenValidationResult;
    }
}
