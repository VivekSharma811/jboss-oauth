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

    public CustomSessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {

        super(deploymentName, 10000, expireOldestUnusedSessionOnMax);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {

            System.out.println("Scheduling: " + System.nanoTime());

            final Set<String> sessions = this.getAllSessions();

            for (String seesionId : sessions
                    ) {

                System.out.println("Active session: " + seesionId);
            }

            return;
        };

        int initialDelay = 0;
        int period = 3;
        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    }

    @Override
    public Session getSession(String sessionId) {

        System.out.println("******** CustomSessionManager");

        return super.getSession(sessionId);
    }


}
