package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajurcenk on 4/5/17.
 */
public class CustomSessionManager extends InMemorySessionManager {

    public static final String SSO_SESSION_ATTRIBUTE_JWT_TOKEN = "jwt";

    public CustomSessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {

        super(deploymentName, 10000, expireOldestUnusedSessionOnMax);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {

            System.out.println("Scheduling: " + System.nanoTime());

            final Set<String> sessions = this.getAllSessions();

            for (String seesionId : sessions
                    ) {


                System.out.println("Active session: " + seesionId);

                // Get Session
                final Session session = this.getSession(seesionId);
                if (session != null) {

                    if (session.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN) != null) {

                        final String jwtToken = session.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN).toString();

                        System.out.println("jwtToken: " + jwtToken);

                    }
                }

            }

            return;
        };

        int initialDelay = 0;
        int period = 3;

        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    }


}
