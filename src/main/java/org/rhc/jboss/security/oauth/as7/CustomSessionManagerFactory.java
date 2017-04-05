package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;

/**
 * Created by ajurcenk on 4/5/17.
 */
public class CustomSessionManagerFactory implements SessionManagerFactory {

    @Override
    public SessionManager createSessionManager(Deployment deployment) {

        CustomSessionManager sessionManager = new CustomSessionManager(deployment.getDeploymentInfo().getDeploymentName(),
                1000, true);

        return sessionManager;
    }
}
