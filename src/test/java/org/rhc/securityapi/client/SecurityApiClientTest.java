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

        String baseUrl = "https://dev.servsmartapi.com/";
        String apiVersion = "V4";
        String authToken = "aothTOken";
        String redirectUrl = "";
        final SecurityApiClientConfig cfg = new SecurityApiClientConfig(baseUrl, authToken, apiVersion, redirectUrl);


        final String code = "AAAAAAAAAAAAAAAAAAAAAA.lWHBdsTS1AilAZVR8ylH5-Q_JKI.c37ao8gY8_lnzrjogz59b1Lax78vj5Y94Lt2OLwm1K0nuMXvAzykITjrpP8PT5Om0x1QzS1t5fBKJFXzThnfynPJffqPeizT24Qxxnw9JjE6J70hY7t7ThnmWHwneSNesWUcFxwAdTfhEqSOYfOaAAOlxnkVCOlAMKsTkDCTSUdkEnH_QkkIrY0f-Nd5pyYn-AswJxrM51mSYjBDRvwfLiof8U6JUtsJSsQZ5AKR6LsV925vcGCVkSUXFt7o-k042nS1qLOOL-OSbMjaRQZnroWjaoZ6rajHEjePhkczYYceSuFpVO67O5H9qCF0rQwrOANu5YemavMpTU7kwxUwtw&state=e42f43d4-603f-4e60-b189-a25e3449574a";
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final GetTokenRequest request = new GetTokenRequest();
        request.setCode(code);
        request.setClientId("business_central_dev");
        request.setRedirectUrl("https://business-central-dev.intranet.com:8443/business-central/token");
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

        final String code = "AAAAAAAAAAAAAAAAAAAAAA.UB6f-Ix31AjSGjuCANYPn7sEs1I.YJ5ErdyWJSSVg7X31EAHVvHX_7sSPSMhzSYaMMafjmCmjeWWZcP8AKg6lOCkWrqQeUp4K636XURPnNI1eTCP57eNmdyKNL_u64I7Vi40R6vZIO7QCHrjnq_kfgieox2dD16st81msqwME8qOnKgnnbNag2rXcxqwnGwhWLLQ2ggypUOSG542fYEWMVPqt6SONCZ6qXiPsScwfQKVvpEG0AMycCu9_XV_Q__aWYYOt_Dd_oL0lPdiZFA23tLE0M9ZBdbtUfbMHo1cwRpWdGgg0msAJjr--Hz6PTJ6IrYU7oqvMtee5MBbY3RQm-kE9QFuwzdb_5560psvEUfTsdcW2g";

        // Create request
        final GetTokenRequest request = new GetTokenRequest();
        request.setCode(code);
        request.setClientId("business_central");
        request.setRedirectUrl("http://10.52.36.20:8080/business-central");
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Get token
        final AdfsToken token =  apiClient.getToken(request);



        assertNotNull(token);

        System.out.println("Token: " + token);
    }

    @Test
    public void testValidateTokenOk() throws Exception{

        final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkF1ZHBIa0tnS2lhZ3Q2U0pxajdVcjFRX3M5USJ9.eyJhdWQiOiJodHRwOi8vMTAuNTIuMzYuMjA6ODA4MC9idXNpbmVzcy1jZW50cmFsIiwiaXNzIjoiaHR0cDovL2FkZnMuc2VydmljZW1hc3Rlci5jb20vYWRmcy9zZXJ2aWNlcy90cnVzdCIsImlhdCI6MTQ5MDg5MTAzMywiZXhwIjoxNDkwODk0NjMzLCJ3aW5hY2NvdW50bmFtZSI6ImFqdXJjZW5rIiwiZ3JvdXAiOlsiRG9tYWluIFVzZXJzIiwiQXBwRHluYW1pY3NfQ3VzdG9tX0Rhc2hib2FyZF9WaWV3ZXIiLCJBcHBEeW5hbWljc19EQl9Nb25pdG9yaW5nX1VzZXIiLCJBcHBEeW5hbWljc19TZXJ2ZXJfTW9uaXRvcmluZ19Vc2VyIiwiQXBwRHluYW1pY3NfUmVhZF9Pbmx5X1VzZXIiXSwiYXV0aF90aW1lIjoiMjAxNy0wMy0zMFQxNjoyMzo1My4wODlaIiwiYXV0aG1ldGhvZCI6InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphYzpjbGFzc2VzOlBhc3N3b3JkUHJvdGVjdGVkVHJhbnNwb3J0IiwidmVyIjoiMS4wIiwiYXBwaWQiOiJidXNpbmVzc19jZW50cmFsIn0.JkebdsNaoCo1ZXep3ncO704c2spc1MK33aDEimcAugBOWW0ODmK2aZvlfUi9aAzjZ-0b2jwrPyG5c-f_qldw9tTffQyULqRMBS0AlsIz9LzKaDNWyLJr51SmQyAXnExOQUpevfTVpf5rqfXjsIyxQugmJRr5rwBkWWD7tn36aQpaxMXqydPhrvdWh-3-KQwuW7AmRkCu3orWLILwY6GXv8EeT9DKqDZ7amaDvE-Dk-RLthKozjEoaT_K5pui-PLq0NXYF2SMq6ICYVwlvwllPPTCfomJFjCEQ_dCps0UKU-GmjAPZRoRyXas55IOew-ow3KOqzQczyYg2FHkwBKFEQ";

        // Create configuration
        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final ValidateTokenRequest request = new ValidateTokenRequest();

        request.setToken(token);
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Validate token
        final TokenValidationResult tokenValidationResult =  apiClient.validateToken(request);

        assertNotNull(tokenValidationResult);

        System.out.println("Validation result: " + tokenValidationResult);

    }

}
