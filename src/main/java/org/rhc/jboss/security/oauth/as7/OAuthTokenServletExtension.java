package org.rhc.jboss.security.oauth.as7;


import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * Registers custom authenticator
 * <p>
 * Created by ajurcenk on 2/14/17.
 */
public class OAuthTokenServletExtension implements ServletExtension {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenServletExtension.class);

    private static final String AUTH_SCHEMA_NAME = "CUST_TOKEN";


    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {

        LOG.debug("handleDeployment(). Registering custom authenticator with name: {} ", AUTH_SCHEMA_NAME);

        deploymentInfo.addAuthenticationMechanism(AUTH_SCHEMA_NAME, new OAuthTokenAuthenticationMechanism.OAuthTokenAuthenticationMechanismFactory());

        LOG.debug("handleDeployment(). Custom authenticator is registered with name: {}", AUTH_SCHEMA_NAME);
    }
}
