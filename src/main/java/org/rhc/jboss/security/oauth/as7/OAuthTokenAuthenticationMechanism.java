package org.rhc.jboss.security.oauth.as7;

import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;

import io.undertow.util.HttpString;
import org.apache.commons.codec.binary.StringUtils;
import org.jboss.security.SimplePrincipal;
import org.rhc.securityapi.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.security.api.AuthenticationMechanism;
import org.wildfly.extension.undertow.security.AccountImpl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * OAuth token authentication - checks OAuth tokens.
 *
 * Created by ajurcenk on 2/13/17.
 */
public class OAuthTokenAuthenticationMechanism implements AuthenticationMechanism {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenAuthenticationMechanism.class);

    private final String mechanismName;
    private final List<HttpString> identityHeaders;

    private final SecurityApiClient apiClient;


    private OAuthTokenAuthenticationMechanism(String mechanismName, List<HttpString> identityHeaders) {


        LOG.debug("Entering to OAuthTokenAuthenticationMechanism()");


        this.mechanismName = mechanismName;
        this.identityHeaders = identityHeaders;


        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        apiClient = new SecurityApiClient(cfg);

        LOG.debug("Exiting from OAuthTokenAuthenticationMechanism()");

    }


    public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {

        LOG.debug("Entering to authenticate()");


        LOG.debug("Getting authentication header");

        final String authHeader = this.getAuthHeader(exchange);

        if (authHeader == null) {

            LOG.warn("Authentication header is not found in the request");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }


        // Validate token
        LOG.debug("Authentication header is found in the request. Validating token");

        final TokenValidationResult tokenValidationResult = this.validateToken(authHeader);

        LOG.debug("Token validation result:" + tokenValidationResult);


        if (!tokenValidationResult.isValid()) {

            LOG.warn("Token is not valid.");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }


        LOG.debug("Authentication header is found in the request");
        LOG.debug("Start parsing authentication");

        OAuthToken token = null;

        try {

            final OAuthTokenService tokenSvc = OAuthTokenServiceFactory.getFactory().getService();
            token = tokenSvc.decodeToken(authHeader);
        } catch (OAuthTokenDecodeException decodeEx) {


            LOG.warn("Error occurred during token decoding", decodeEx);

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }

        LOG.debug("Auth. token is created.");

        // Convert user groups to string representation
        String rolesStr = token.getGroups() == null ? "" : String.join("^", token.getGroups());

        LOG.debug("Verify account");

        SimplePrincipal principal = new SimplePrincipal(token.getWinAccountName());

        IdentityManager identityManager = securityContext.getIdentityManager();

        Account account = identityManager.verify(new AccountImpl(principal, Collections.<String>emptySet(), rolesStr));

        if (account == null) {

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }

        securityContext.authenticationComplete(account, mechanismName, true);

        LOG.debug("Account is valid.");


        LOG.debug("Exiting from authenticate");

        return AuthenticationMechanismOutcome.AUTHENTICATED;

    }


    public ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {

        return new ChallengeResult(true, HttpServletResponse.SC_UNAUTHORIZED);
    }


    public static final class OAuthTokenAuthenticationMechanismFactory implements AuthenticationMechanismFactory {

        private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenAuthenticationMechanismFactory.class);


        public static final String IDENTITY_HEADER = "identity-header";


        public AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {


            LOG.debug("Entering create().");

            // Get parameters

            String identityHeaderNames = (String) properties.get(IDENTITY_HEADER);

            if (identityHeaderNames == null || identityHeaderNames.isEmpty()) {

                throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, IDENTITY_HEADER);
            } else {

                LOG.debug("Configuration parameter: " + IDENTITY_HEADER + " = " + identityHeaderNames);

                // Parse configuration parameters
                List<HttpString> identityHttpStr = new ArrayList<>();

                String[] headers = identityHeaderNames.split(",");

                for (String header : headers) {

                    identityHttpStr.add(new HttpString(header));
                }


                LOG.debug("Exiting create()");

                return new OAuthTokenAuthenticationMechanism(mechanismName, identityHttpStr);
            }


        }
    }


    /**
     * Returns authentication header value from HTTP header
     *
     * @param exchange
     * @return
     */
    private String getAuthHeader(HttpServerExchange exchange) {

        Iterator iter = this.identityHeaders.iterator();

        String res;
        do {

            if (!iter.hasNext()) {

                return null;
            }

            HttpString header = (HttpString) iter.next();

            res = exchange.getRequestHeaders().getFirst(header);

        } while (res == null);


        return res;
    }


    /**
     * Validates token
     *
     * @param token
     * @return
     */
    private TokenValidationResult validateToken(final String token) {


        LOG.debug("Entering validateToken().");

        // Create request
        final ValidateTokenRequest request = new ValidateTokenRequest();

        request.setToken(token);
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Validate token
        final TokenValidationResult tokenValidationResult = apiClient.validateToken(request);


        LOG.debug("Exiting from validateToken().");

        return tokenValidationResult;

    }


}
