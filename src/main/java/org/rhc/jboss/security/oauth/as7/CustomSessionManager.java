package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.session.InMemorySessionManager;

/**
 * Created by ajurcenk on 4/5/17.
 */
public class CustomSessionManager extends InMemorySessionManager {

    public CustomSessionManager(String deploymentName, int maxSessions, boolean expireOldestUnusedSessionOnMax) {

        super(deploymentName, 10000, expireOldestUnusedSessionOnMax);
    }
}
