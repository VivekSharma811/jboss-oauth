package org.rhc.securityapi.client;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token validation result
 */
public class TokenValidationResult {

    private boolean valid = false;
    private String info = "";


    @JsonProperty("Valid")
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @JsonProperty("Info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "TokenValidationResult{" +
                "valid=" + valid +
                ", info='" + info + '\'' +
                '}';
    }
}
