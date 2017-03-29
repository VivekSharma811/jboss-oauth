package org.rhc.jboss.security.oauth.as7;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.acl.Group;

import javax.security.auth.callback.CallbackHandler;

import java.util.Map;

/**
 * Created by ajurcenk on 2/9/17.
 */


public class OAuthManageResourceLoginModule extends UsernamePasswordLoginModule {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthManageResourceLoginModule.class);


    // User roles
    private String rolesStr = "";


    public OAuthManageResourceLoginModule() {

    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {

        super.initialize(subject, callbackHandler, sharedState, options);

        this.rolesStr = this.getRoleFromCallback();

    }

    @Override
    protected boolean validatePassword(String inputPassword,
                                       String expectedPassword) {
        return true;
    }


    @Override
    protected String getUsersPassword() throws LoginException {

        return "bpmsuite1!";
    }


    /**
     * Returns user roles sets
     *
     * @return
     * @throws LoginException
     */
    protected Group[] getRoleSets() throws LoginException {

        LOG.debug("Entering to getRoleSets()");

        SimpleGroup roles = new SimpleGroup("Roles");

        // Parse roles string
        LOG.debug("Parsing roles string {}", this.rolesStr);


        String[] tokens = rolesStr.split("\\^");

        for (String token : tokens) {

            String roleName = token != null ? token.trim() : "";

            LOG.debug("Adding role: [{}] to account", roleName);

            roles.addMember(new SimplePrincipal(roleName));
        }

        final Group[] roleSets = {roles};

        LOG.debug("Exiting from getRoleSets(). Account has {} roles", roles.toString());

        return roleSets;
    }


    /**
     * Returns roles from password callback
     *
     * @return
     */
    private String getRoleFromCallback() {


        LOG.debug("Entering to getRoleFromCallback())");

        PasswordCallback pc = new PasswordCallback("password", false);

        Callback[] callbacks = {pc};

        try {

            callbackHandler.handle(callbacks);

        } catch (IOException e) {

            LOG.warn("IOException");

            return "";

        } catch (UnsupportedCallbackException e) {

            LOG.warn("Could not resolve callback:");

            return "";
        }
        char[] tmpPassword = pc.getPassword();
        char[] credential;

        if (tmpPassword != null) {

            credential = new char[tmpPassword.length];

            System.arraycopy(tmpPassword, 0, credential, 0, tmpPassword.length);

            pc.clearPassword();

            LOG.debug("Role from callback: {}", new String(credential));

            return new String(credential);
        } else {

            LOG.warn("null password from callback!");

            return "";
        }

    }


}
