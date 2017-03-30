package org.rhc.securityapi.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Headers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.util.Arrays;
import java.util.Map;

/**
 * Request builder
 */
public class WebRequestInvocationBuilder {

    private final String url;
    private String apiKey = "";
    private String appKey = "";
    private String authToken = "";
    private Entity entity;



    private WebRequestInvocationBuilder(final String url) {

        this.url = url;
    }

    /**
     * Creates builder
     *
     * @param url
     * @return
     */
    public static WebRequestInvocationBuilder create(final String url) {

        final WebRequestInvocationBuilder retVal = new WebRequestInvocationBuilder(url);

        return  retVal;
    }





    public WebRequestInvocationBuilder addEntity(final Entity entity) {

        this.entity = entity;

        return this;
    }

    public WebRequestInvocationBuilder addAppKey(final String appKey) {

        this.appKey = appKey;

        return this;
    }

    public WebRequestInvocationBuilder addApiKey(final String apiKey) {

        this.apiKey = apiKey;

        return this;
    }

    public WebRequestInvocationBuilder addAuthToken(final String authToken) {

        this.authToken = authToken;

        return this;
    }


    /**
     * Builds POST invocation
     *
     * @return
     */
    public Invocation buildPost() {

        final ResteasyClient client = new ResteasyClientBuilder().build();
        final ResteasyWebTarget target  = client.target(this.url);

        final MultivaluedMap<String, Object>  headers = new Headers();

        // api_key
        if (this.apiKey != null ) {
            // headers.put("api_key", Arrays.asList(new String[]{this.apiKey}));
        }

        // app_key
        if (this.appKey != null ) {

            //headers.put("app_key", Arrays.asList(new String[]{this.appKey}));
        }

        // Authorization
        if (this.authToken != null ) {

            //headers.put("Authorization", Arrays.asList(new String[]{this.authToken}));
        }


        return target.request().headers(headers).buildPost(this.entity);


    }
}
