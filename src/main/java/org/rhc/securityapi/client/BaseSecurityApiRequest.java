package org.rhc.securityapi.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for all Security API requests.
 */
public abstract class BaseSecurityApiRequest {

    private String apiKey="";
    private String appKey="";

    public String getApiKey() {
        return apiKey;
    }

    @JsonIgnore
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    @JsonIgnore
    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
