package org.rhc.securityapi.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents get token request
 */
public class GetTokenRequest extends BaseSecurityApiRequest {

    private String code;
    private String clientId;
    private String redirectUrl;

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("cleint_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("redirect_url")
    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public String toString() {

        return "GetTokenRequest{" +
                "code='" + code + '\'' +
                ", clientId='" + clientId + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", apiKey='" + this.getApiKey() + '\'' +
                ", appKey='" + this.getAppKey() + '\'' +
                '}';
    }
}
