package org.rhc.securityapi.client;

import junit.framework.TestCase;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by ajurcenk on 3/6/17.
 */
public class TestSimpleClient extends TestCase {


    @Test
    public void testGetToken() throws Exception {

        // Create client
        final ResteasyClient client = new ResteasyClientBuilder().build();

        final ResteasyWebTarget target  = client.target("http://localhost:8080/V3/Security/api/Token/GetToken");


        // Request data
        GetTokenRequest req = new GetTokenRequest();
        req.setClientId("test");
        req.setCode("sdsfsfdf");
        req.setRedirectUrl("http://www.test.org");

        WebRequestInvocationBuilder invocationBuilder =  WebRequestInvocationBuilder.create("http://localhost:8080/V3/Security/api/Token/GetToken") ;
        invocationBuilder.addApiKey("apiKey");
        invocationBuilder.addAppKey("appKey");
        invocationBuilder.addAuthToken("auttoken");
        invocationBuilder.addEntity(Entity.entity(req, MediaType.APPLICATION_JSON));

        // Send posr
        final Response resp = invocationBuilder.buildPost().invoke();


        System.out.println("Response status: " + resp.getStatus());
        System.out.println("Response body: " + resp.readEntity(String.class));
    }



}
