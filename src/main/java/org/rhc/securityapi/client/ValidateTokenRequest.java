package org.rhc.securityapi.client;

/**
 * Validate token request
 */
public class ValidateTokenRequest extends BaseSecurityApiRequest {

    private String token = "";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ValidateTokenRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}

