package org.rhc.jboss.security.oauth.as7;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajurcenk on 2/27/17.
 */

public class HttpSessionWrapper implements Session {


    private HttpSession httpSession;
    private SessionManager sessionManager;

    public HttpSessionWrapper(final HttpSession httpSession) {

        this.httpSession = httpSession;
        this.sessionManager = new SimpleSessionManager();

    }

    @Override
    public String getId() {

        return httpSession.getId();
    }

    @Override
    public void requestDone(HttpServerExchange httpServerExchange) {

    }

    @Override
    public long getCreationTime() {

        return httpSession.getCreationTime();
    }

    @Override
    public long getLastAccessedTime() {

        return httpSession.getLastAccessedTime();
    }

    @Override
    public void setMaxInactiveInterval(int time) {

        httpSession.setMaxInactiveInterval(time);
    }

    @Override
    public int getMaxInactiveInterval() {

        return httpSession.getMaxInactiveInterval();
    }

    @Override
    public Object getAttribute(String s) {

        return httpSession.getAttribute(s);
    }

    @Override
    public Set<String> getAttributeNames() {


        final Set<String> atts = new HashSet<>();

        Enumeration<String> enumer = httpSession.getAttributeNames();

        while (enumer.hasMoreElements()) {

            final String attrName = enumer.nextElement();
            if (attrName != null) {

                atts.add(attrName);
            }
        }

        return atts;
    }

    @Override
    public Object setAttribute(String s, Object o) {

        httpSession.setAttribute(s, o);

        return o;
    }

    @Override
    public Object removeAttribute(String s) {


        httpSession.removeAttribute(s);

        return null;

    }

    @Override
    public void invalidate(HttpServerExchange httpServerExchange) {

        httpSession.invalidate();
    }

    @Override
    public SessionManager getSessionManager() {

        return this.sessionManager;
    }

    @Override
    public String changeSessionId(HttpServerExchange httpServerExchange, SessionConfig sessionConfig) {

        throw new IllegalStateException("Method not supported");
    }
}