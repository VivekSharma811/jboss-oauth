package org.rhc.securityapi.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents Adfs token
 */
public class AdfsToken {

    private String accessToken = "";
    private String tokenTtype = "";
    private String expiresInMinutes = "";


    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("token_type")
    public String getTokenTtype() {
        return tokenTtype;
    }

    public void setTokenTtype(String tokenTtype) {
        this.tokenTtype = tokenTtype;
    }

    @JsonProperty("expires_in")
    public String getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setExpiresInMinutes(String expiresInMinutes) {
        this.expiresInMinutes = expiresInMinutes;
    }

    @Override
    public String toString() {
        return "AdfsToken{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenTtype='" + tokenTtype + '\'' +
                ", expiresInMinutes='" + expiresInMinutes + '\'' +
                '}';
    }

}
