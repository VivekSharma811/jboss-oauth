package org.rhc.jboss.security.oauth.as7;

import io.undertow.security.impl.SingleSignOn;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionListener;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.extension.undertow.security.sso.InMemorySingleSignOnManagerFactory;
import org.wildfly.extension.undertow.security.sso.SingleSignOnManager;
import org.wildfly.extension.undertow.security.sso.SingleSignOnManagerFactory;

import javax.servlet.ServletContext;


/**
 * Created by ajurcenk on 2/20/17.
 */
public class OAuthTokenWithRedirectionAuthenticationMechanismServletExtension implements ServletExtension {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenWithRedirectionAuthenticationMechanismServletExtension.class);

    private static final String AUTH_SCHEMA_NAME = "FORM";


    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {

        LOG.debug("handleDeployment(). Registering custom authenticator with name: {} ", AUTH_SCHEMA_NAME);

        // Create SSO session manager
        final SingleSignOnManagerFactory ssoManagerFactory = new InMemorySingleSignOnManagerFactory();
        final SingleSignOnManager ssoManager = ssoManagerFactory.createSingleSignOnManager(null);

        // Add SSO HTTP session listener
        LOG.debug("Adding SSO session listener");

        deploymentInfo.addSessionListener(new SsoSessionListener(ssoManager));

        LOG.debug("SSO session listener is added");


        // Add custom form authentication
        deploymentInfo.addAuthenticationMechanism(AUTH_SCHEMA_NAME, new CustomFormAuthenticationMechanism.CustomFormAuthenticationMechanismFactory(ssoManager));


        LOG.debug("handleDeployment(). Custom authenticator is registered with name: {}", AUTH_SCHEMA_NAME);
    }


    /**
     * SSO session listener
     */
    private static class SsoSessionListener implements SessionListener {

        private static final Logger LOG = LoggerFactory.getLogger(SsoSessionListener.class);

        /**
         * SSO session manager
         */
        final SingleSignOnManager ssoManager;

        public SsoSessionListener(final SingleSignOnManager ssoManager ) {

            this.ssoManager = ssoManager;
        }

        @Override
        public void sessionCreated(Session session, HttpServerExchange httpServerExchange) {

            LOG.debug("New session is created");

        }

        @Override
        public void sessionDestroyed(Session session, HttpServerExchange httpServerExchange, SessionDestroyedReason sessionDestroyedReason) {

            LOG.debug("Session is destroyed");

            final Object ssoAttr = session.getAttribute(CustomFormAuthenticationMechanism.SSO_SESSION_ATTRIBUTE);

            LOG.debug("SSO attribute value: {}",ssoAttr );

            if (ssoAttr != null ) {

                LOG.debug("Removing SSO session for session Id: {}",ssoAttr );

                final SingleSignOn ssoSession =  this.ssoManager.findSingleSignOn(ssoAttr.toString());

                if (ssoSession != null) {

                    this.ssoManager.removeSingleSignOn(ssoSession);

                    LOG.debug("SSO session is removed. SSO Session Id: {}",ssoAttr );
                }

            } // if

        }

        @Override
        public void attributeAdded(Session session, String s, Object o) {

        }

        @Override
        public void attributeUpdated(Session session, String s, Object o, Object o1) {

        }

        @Override
        public void attributeRemoved(Session session, String s, Object o) {

        }

        @Override
        public void sessionIdChanged(Session session, String s) {

        }
    }
}
