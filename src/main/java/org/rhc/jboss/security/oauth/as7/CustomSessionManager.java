package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import org.rhc.securityapi.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajurcenk on 4/5/17.
 */
public class CustomSessionManager extends InMemorySessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSessionManager.class);

    public static final String SSO_SESSION_ATTRIBUTE_JWT_TOKEN = "jwt";

    public CustomSessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {

        super(deploymentName, 10000, expireOldestUnusedSessionOnMax);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {

            LOG.debug("Scheduling: " + System.nanoTime());

            final Set<String> sessions = this.getActiveSessions();

            for (String seesionId : sessions
                    ) {


                System.out.println("Active session: " + seesionId);

                // Get Session
                final Session session = this.getSession(seesionId);
                if (session != null) {

                    if (session.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN) != null) {

                        final String jwtToken = session.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN).toString();

                        // Validate token
                        final TokenValidationResult result = this.validateJwtToken(jwtToken);

                        // Check token validation result
                        if (!result.isValid()) {
                            LOG.debug("Invalidating session");
                            session.invalidate(null);
                            LOG.debug("Session is invalidated.");
                        }

                    }
                }

            }

            return;
        };

        int initialDelay = 0;
        int period = 3;

        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    }


    /**
     * Validates token
     *
     * @param jwtTokenStr
     * @return
     */
    private TokenValidationResult validateJwtToken(final String jwtTokenStr) {

        LOG.debug("Entering to validateJwtToken(). validateJwtToken: ");

        // Create configuration
        // TODO move to constructor

        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final ValidateTokenRequest request = new ValidateTokenRequest();
        request.setToken(jwtTokenStr);

        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Get token
        final TokenValidationResult tokenValidationResult = apiClient.validateToken(request);

        LOG.debug("Exiting from validateJwtToken().  token; {}", tokenValidationResult);

        return tokenValidationResult;
    }


}
