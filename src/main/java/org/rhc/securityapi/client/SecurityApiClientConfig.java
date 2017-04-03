package org.rhc.securityapi.client;

/**
 * Security API client configuration
 */
public class SecurityApiClientConfig {

    private String baseUrl;
    private String authToken;
    private String apiVersion;
    private String redirectUri;

    public String getBaseUrl() { return baseUrl; }

    public String getAuthToken() { return authToken; }

    public String getApiVersion() { return apiVersion; }

    public String getRedirectUri() { return redirectUri; }

    public SecurityApiClientConfig(String baseUrl, String authToken, String apiVersion, String redirectUri) {

        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.apiVersion = apiVersion;
        this.redirectUri = redirectUri;

    }

}
