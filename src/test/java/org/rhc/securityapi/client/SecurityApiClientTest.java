package org.rhc.securityapi.client;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by ajurcenk on 3/6/17.
 */
public class SecurityApiClientTest extends TestCase {

    @Test
    public void testGetTokenOk() throws Exception{

        // Create configuration

        String baseUrl = "http://localhost:8080/";
        String apiVersion = "V3";
        String authToken = "authtoken";

        final SecurityApiClientConfig cfg = new SecurityApiClientConfig(baseUrl, authToken, apiVersion);
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final GetTokenRequest request = new GetTokenRequest();
        request.setCode("code");
        request.setClientId("clientId");
        request.setRedirectUrl("http://localahost:8080/test");
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Get token
       final AdfsToken token =  apiClient.getToken(request);

        assertNotNull(token);

        System.out.println("Token: " + token);

    }


    @Test
    public void testCfgFactory() throws Exception{

        // Create configuration
        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final GetTokenRequest request = new GetTokenRequest();
        request.setCode("code");
        request.setClientId("clientId");
        request.setRedirectUrl("http://localahost:8080/test");
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Get token
        final AdfsToken token =  apiClient.getToken(request);

        assertNotNull(token);

        System.out.println("Token: " + token);
    }

    @Test
    public void testValidateTokenOk() throws Exception{

        // Create configuration
        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final ValidateTokenRequest request = new ValidateTokenRequest();

        request.setToken("token to validate");
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Validate token
        final TokenValidationResult tokenValidationResult =  apiClient.validateToken(request);

        assertNotNull(tokenValidationResult);

        System.out.println("Validation result: " + tokenValidationResult);

    }

}
