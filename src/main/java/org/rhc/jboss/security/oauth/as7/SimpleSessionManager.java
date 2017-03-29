package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.*;

import java.util.Set;

/**
 * Created by ajurcenk on 2/27/17.
 */
public class SimpleSessionManager implements SessionManager {

    @Override
    public String getDeploymentName() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Session createSession(HttpServerExchange httpServerExchange, SessionConfig sessionConfig) {
        return null;
    }

    @Override
    public Session getSession(HttpServerExchange httpServerExchange, SessionConfig sessionConfig) {
        return null;
    }

    @Override
    public Session getSession(String s) {
        return null;
    }

    @Override
    public void registerSessionListener(SessionListener sessionListener) {

    }

    @Override
    public void removeSessionListener(SessionListener sessionListener) {

    }

    @Override
    public void setDefaultSessionTimeout(int i) {

    }

    @Override
    public Set<String> getTransientSessions() {
        return null;
    }

    @Override
    public Set<String> getActiveSessions() {
        return null;
    }

    @Override
    public Set<String> getAllSessions() {
        return null;
    }

    @Override
    public SessionManagerStatistics getStatistics() {
        return null;
    }
}