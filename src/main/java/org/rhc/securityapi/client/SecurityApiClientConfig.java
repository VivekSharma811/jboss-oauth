package org.rhc.securityapi.client;

/**
 * Security API client configuration
 */
public class SecurityApiClientConfig {

    private String baseUrl;
    private String authToken;
    private String apiVersion;
    private String redirectUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }


    public SecurityApiClientConfig(String baseUrl, String authToken, String apiVersion, String redirectUrl) {

        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.apiVersion = apiVersion;
        this.redirectUrl = redirectUrl;
    }

}
