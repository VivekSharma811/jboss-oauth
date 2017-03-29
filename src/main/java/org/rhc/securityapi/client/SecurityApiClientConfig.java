package org.rhc.securityapi.client;

/**
 * Security API client configuration
 */
public class SecurityApiClientConfig {

    private String baseUrl;
    private String authToken;
    private String apiVersion;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public SecurityApiClientConfig(String baseUrl, String authToken, String apiVersion) {

        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.apiVersion = apiVersion;
    }

}
