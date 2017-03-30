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
        final SecurityApiClientConfig cfg = new SecurityApiClientConfig(baseUrl, authToken, apiVersion);


        final String code = "AAAAAAAAAAAAAAAAAAAAAA.ZZNUBYt31AjKGmIo26shtcZHgZM.nJqlX-5JpKR-1PLDhyAX61gltYu0730K11RSmypGLwM1UZVVMPs7rXvFp5TWQMow8S-cM2DEFmcG0qYf_6UpwX-7rA-B54TAIWI1bSbKezsrWCDs3yVWCQT4SVmfRoqaef2clzd8GQfnrvt9vUtk16OvUYTfnty_5ekLwsm1fwkDAVYk5WaP-LZnR6UG5xHEOOhhU4g69Ds6IiIh7UpSHHoE-xkcwiF4F88fG7buwHEZQtkPuQdPBgrogpl3CTzopJARhBx4_WGK_ZLAKBP3gKHYUtUjovc-zbIluvDqM7kmGcZtOeXWkE9HfUZXCSrhJieW41ArMtzkHLRkbxJLVQ";
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

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
