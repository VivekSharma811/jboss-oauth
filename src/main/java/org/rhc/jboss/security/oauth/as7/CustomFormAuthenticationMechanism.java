package org.rhc.jboss.security.oauth.as7;

import io.undertow.UndertowMessages;
import io.undertow.security.api.*;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.SingleSignOn;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.session.Session;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.handlers.security.ServletFormAuthenticationMechanism;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;


import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.RedirectBuilder;
import org.jboss.security.SimplePrincipal;
import org.rhc.securityapi.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.extension.undertow.security.AccountImpl;
import org.wildfly.extension.undertow.security.sso.SingleSignOnManager;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * Created by ajurcenk on 2/27/17.
 */
public class CustomFormAuthenticationMechanism extends ServletFormAuthenticationMechanism {

    private static final Logger LOG = LoggerFactory.getLogger(CustomFormAuthenticationMechanism.class);

    public static final String POST_LOCATION = "custom_security_check";
    public static final String REQ_PARAM_CODE = "code";
    public static final String REQ_PARAM_STATE = "state";
    public static final String UTF_8 = "UTF-8";
    public static final String SSO_SESSION_ATTRIBUTE = "SSOID";
    public static final String SSO_SESSION_ATTRIBUTE_JWT_TOKEN = "jwt";



    private String mechanismName;

    private String cookieName;

    /**
     * Token authorization url (Configuration)
     */
    private String tokenAuthorizeUrl;

    /**
     * Default page (Configuration)
     */
    private String defaultPage;

    /**
     * Client ID (Configuration)
     */
    private String clientId;

    /**
     * Check token url (Configuration)
     */
    private String checkTokenUrl;


    /**
     * Resource name (Configuration)
     */
    private String resource;

    /**
     * Use state parameter (Configuration)
     */
    private boolean useStateParam = false;

    /**
     * State expiration in seconds (Configuration)
     */
    private int stateExpirationTimeInSeconds;

    /**
     * Session manager
     */
    private SingleSignOnManager ssoSessionManager;


    /**
     * States storage
     */
    private ExpiringInMemoryStorage<String> statesStorage;


    /**
     * Token groups mapper
     */
    private final TokenGroupsMapper groupsMapper;


    public CustomFormAuthenticationMechanism(String name, String loginPage, String errorPage, SingleSignOnManager ssoManager,
                                             final String defaultPage,
                                             final String checkTokenUrl,
                                             final String resource,
                                             final String clientId,
                                             final boolean useStateParam,
                                             final int stateExpirationTimeInSeconds,
                                             final String tokenAuthorizeUrl,
                                             final TokenGroupsMapper groupsMapper) {

        super(FormParserFactory.builder().build(), name, loginPage, errorPage);

        // defaultPage=jboss-helloworld&checkTokenUrl=jboss-helloworld/token&resource=http://osboxes/jboss-helloworld&clientId=bc&useStateParam=true&stateExpirationTimeInSeconds=60

        this.cookieName = "JSESSIONID";

        /*
        this.tokenAuthorizeUrl = "http://localhost:8080/adfs/oauth2/authorize";
        this.checkTokenUrl = "token";
        this.resource= "http://osboxes/jboss-helloworld";
        this.clientId = "bc";
        this.useStateParam = true;
        this.stateExpirationTimeInSeconds = 60;
        this.mechanismName = "FROM";
        this.defaultPage="business-central";*/


        this.mechanismName = name;
        this.defaultPage = defaultPage;
        this.checkTokenUrl = checkTokenUrl;
        this.resource = resource;
        this.clientId = clientId;
        this.useStateParam = useStateParam;
        this.stateExpirationTimeInSeconds  = stateExpirationTimeInSeconds;
        this.tokenAuthorizeUrl = tokenAuthorizeUrl;

        this.ssoSessionManager = ssoManager;
        this.statesStorage = new ExpiringInMemoryStorage<>(stateExpirationTimeInSeconds);

        this.groupsMapper = groupsMapper;

        // Start SSO session manager
        this.ssoSessionManager.start();
    }

    @Override
    public AuthenticationMechanismOutcome authenticate(final HttpServerExchange exchange, final SecurityContext securityContext) {

        LOG.debug("Entering to authenticate. Request URI : {}", exchange.getRequestURI());

     if (exchange.getRequestURI().endsWith(this.checkTokenUrl) &&
                exchange.getRequestMethod().equals(Methods.GET)) {

            LOG.debug("Token verification url is detected");

            final AuthenticationMechanism.AuthenticationMechanismOutcome tokenValidationReqResult = handleTokenValidationRequest(exchange, securityContext);

            if (tokenValidationReqResult == AuthenticationMechanismOutcome.AUTHENTICATED) {

                // Redirect to main page
                String loc = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + "/" + this.defaultPage;

                LOG.debug("Redirecting to url {}", loc);

                this.handleRedirectBack(exchange, loc);
            } // if

            exchange.endExchange();

            return tokenValidationReqResult;

        } else if (exchange.getRequestURI().endsWith(POST_LOCATION) && exchange.getRequestMethod().equals(Methods.POST)) {

            ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);

            LOG.debug("Exiting from authenticate. Custom security check url is detected.");

            // Remove old session
            final HttpSession currentSession = servletRequestContext.getCurrentServletContext().getSession(exchange, false);
            if (currentSession != null) {

                // Invalidate session
                currentSession.invalidate();

                // Redirect to main page
                String loc = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + "/" + this.defaultPage;

                LOG.debug("Redirecting to url {}", loc);

                this.handleRedirectBack(exchange, loc);


                // Redirect to default page.
                exchange.endExchange();
            }

            return runFormAuth(exchange, securityContext);

        } else {

            // Check session
            LOG.debug("Checking authentication cookie");

            Cookie cookie = (Cookie) exchange.getRequestCookies().get(this.cookieName);

            if (cookie == null) {

                LOG.debug("Authentication cookie is not found.  Redirecting to authentication page...");

                try {

                    final String tokenAuthUrl = buildTokenAuthUrl(exchange);

                    handleRedirectBack(exchange, tokenAuthUrl);

                    exchange.endExchange();

                    LOG.debug("Authentication cookie is not found. User redirected to url: {}", tokenAuthUrl);

                    return AuthenticationMechanismOutcome.NOT_ATTEMPTED;

                } catch (UnsupportedEncodingException exEnc) {

                    throw new RuntimeException(exEnc);
                }

            } // Session is not set


            // Session is valid. Validate current session
            final AuthenticationMechanism.AuthenticationMechanismOutcome sessionValidationResult = validateSession(exchange, securityContext);

            //exchange.endExchange();

            return sessionValidationResult;
        }

    }



    /**
     * Handles redirection to authorization server.
     *
     * @param exchange
     */

    protected void handleRedirectBack(HttpServerExchange exchange, final String location) {

        exchange.addDefaultResponseListener((httpExchange) -> {

            sendRedirect(exchange, location);

            httpExchange.setStatusCode(302);

            httpExchange.endExchange();

            return true;
        });


    }

    /**
     * Builds token authorization url
     *
     * @param exchange
     * @return
     * @throws UnsupportedEncodingException
     */
    private String buildTokenAuthUrl(HttpServerExchange exchange) throws UnsupportedEncodingException {

        final StringBuilder tokenVerificationRelativeUrl = new StringBuilder(this.checkTokenUrl);

        final String tokenVerificationUrl = RedirectBuilder.redirect(exchange, tokenVerificationRelativeUrl.toString(), false);

        LOG.debug("Token verification callback url: {}", tokenVerificationUrl);


        final StringBuilder tokenAuthUrlBuilder = new StringBuilder(this.tokenAuthorizeUrl);
        tokenAuthUrlBuilder.append("?");
        // Response type
        tokenAuthUrlBuilder.append("response_type").append("=").append(java.net.URLEncoder.encode("code", UTF_8));

        // Client ID
        tokenAuthUrlBuilder.append("&");
        tokenAuthUrlBuilder.append("client_id").append("=").append(java.net.URLEncoder.encode(this.clientId, UTF_8));

        // Resource
        tokenAuthUrlBuilder.append("&");
        tokenAuthUrlBuilder.append("resource").append("=").append(java.net.URLEncoder.encode(resource, UTF_8));

        // State parameter
        // An random string. It is used to protect against cross-site request forgery attacks.

        if (useStateParam) {

            final String stateValue = this.generateStateValue();

            // Add to state storage
            this.statesStorage.put(stateValue, stateValue);

            tokenAuthUrlBuilder.append("&");
            tokenAuthUrlBuilder.append(REQ_PARAM_STATE).append("=").append(java.net.URLEncoder.encode(stateValue, UTF_8));

        } // if state


        // Redirect url
        tokenAuthUrlBuilder.append("&");
        tokenAuthUrlBuilder.append("redirect_uri").append("=").append(tokenVerificationUrl);

        return tokenAuthUrlBuilder.toString();
    }


    /**
     * Handles token verification request
     *
     * @param exchange
     * @param securityContext
     * @return
     */
    protected AuthenticationMechanism.AuthenticationMechanismOutcome handleTokenValidationRequest(HttpServerExchange exchange,
                                                                                                  SecurityContext securityContext) {

        LOG.debug("Entering to handleTokenValidationRequest()");


        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);


        // Remove old session
        final HttpSession currentSession = servletRequestContext.getCurrentServletContext().getSession(exchange, false);
        if (currentSession != null) {

            currentSession.invalidate();
        }


        LOG.debug("Start parsing authentication");

        OAuthToken token = null;

        //  Check state
        if (useStateParam) {

            LOG.debug("Staring state parameter checking ");


            if (!exchange.getQueryParameters().containsKey(REQ_PARAM_STATE)) {

                LOG.warn("State parameter is not found in the request");

                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }

            // Get state from request

            final String stateParamValue = exchange.getQueryParameters()
                    .getOrDefault(REQ_PARAM_STATE, new ArrayDeque<>()).getFirst();


            final String stateVal = this.statesStorage.get(stateParamValue);

            if (stateVal == null) {

                LOG.warn("State parameter is not found in the state store.");

                return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }


            LOG.debug("Staring state parameter checking is completed. State parameter {} ", stateParamValue);


        } // if useStateParam

        // Get token from request
        final String tokenStr = exchange.getQueryParameters()
                .getOrDefault(REQ_PARAM_CODE, new ArrayDeque<>()).getFirst();


        try {

            LOG.debug("Extracting token parameter from request.");

            // Get JWT token from access token
            final AdfsToken jwtToken = getJwtToken(tokenStr);

            final OAuthTokenService tokenSvc = OAuthTokenServiceFactory.getFactory().getService();

            // Decode access token
            token = tokenSvc.decodeToken(jwtToken.getAccessToken());

            LOG.debug("Token is extracted and parsed from from request.");

        } catch (OAuthTokenDecodeException decodeEx) {


            LOG.warn("Error occurred during token decoding", decodeEx);

            LOG.debug("Exiting from handleTokenValidationRequest()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }

        LOG.debug("Auth. token is created.");

        // Convert user groups to string representation
        String rolesStr =  "";
        final List<String> tokenGroups = token.getGroups();

        if (this.groupsMapper != null
                && tokenGroups != null) {

            LOG.debug("Mapping groups using groups mapper.");

            // Groups mapper is not set
            final List<String> newGroups = this.groupsMapper.mapGroups(tokenGroups);

            rolesStr =  String.join("^", newGroups);
        }

        else {

            rolesStr =  tokenGroups == null ? "" : String.join("^", tokenGroups);
        }


        LOG.debug("User roles/groups: {}",rolesStr );

        LOG.debug("Verify account");

        SimplePrincipal principal = new SimplePrincipal(token.getWinAccountName());

        IdentityManager identityManager = securityContext.getIdentityManager();

        Account account = identityManager.verify(new AccountImpl(principal, Collections.<String>emptySet(), rolesStr));

        if (account == null) {

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }

        securityContext.authenticationComplete(account, mechanismName, true);

        LOG.debug("Account is valid.");


        // Create new session
        final HttpSession session = servletRequestContext.getCurrentServletContext().getSession(exchange, true);
        final HttpSessionWrapper httpSessionWrapper = new HttpSessionWrapper(session);


        // Create sso account holder
        final SingleSignOn sso = this.ssoSessionManager.createSingleSignOn(account, this.mechanismName);

        // Register sso session
        this.registerSessionIfRequired(sso, httpSessionWrapper, tokenStr);


        LOG.debug("Exiting from handleTokenValidationRequest()");

        return AuthenticationMechanismOutcome.AUTHENTICATED;
    }


    /**
     * Registers SSO session
     *
     * @param sso
     * @param session
     */
    private void registerSessionIfRequired(SingleSignOn sso, Session session, final String jwtToken) {


        LOG.debug("Entering to registerSessionIfRequired()");

        if (!sso.contains(session)) {

            sso.add(session);

            LOG.debug("Session is added to session holder.");
        }

        if (session.getAttribute(SSO_SESSION_ATTRIBUTE) == null) {

            LOG.debug("SSO Session attribute is added to session.");

            session.setAttribute(SSO_SESSION_ATTRIBUTE, sso.getId());
        }

        if (session.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN) == null) {

            LOG.debug("JWT Session attribute is added to session.");

            session.setAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN, jwtToken);
        }

        LOG.debug("Exiting from registerSessionIfRequired()");

    }


    /**
     * Validates http session
     *
     * @param exchange
     * @param securityContext
     * @return
     */
    protected AuthenticationMechanism.AuthenticationMechanismOutcome validateSession(HttpServerExchange exchange,
                                                                                     SecurityContext securityContext) {

        LOG.debug("Entering to validateSession()");

        ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);

        // Get user session
        final HttpSession currentSession = servletRequestContext.getCurrentServletContext().getSession(exchange, false);

        if (currentSession == null) {

            LOG.debug("Session is not found. User is not authenticated");

            LOG.debug("Exiting from validateSession()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }

        final Object ssoIdAttr = currentSession.getAttribute(SSO_SESSION_ATTRIBUTE);
        if (ssoIdAttr == null) {

            LOG.debug("SSO ID attribute is not found in the session. User is not authenticated");

            LOG.debug("Exiting from validateSession()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }


        final String ssoId = ssoIdAttr.toString();
        final SingleSignOn sso = this.ssoSessionManager.findSingleSignOn(ssoId);
        if (sso == null) {

            LOG.debug("SSO session is not found. User is not authenticated.");

            LOG.debug("Exiting from validateSession()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }


        if (currentSession.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN) == null) {

            LOG.debug("JWT token session attribute is not set.");

            LOG.debug("Exiting from validateSession()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;

        }

        final String jwtToken = currentSession.getAttribute(SSO_SESSION_ATTRIBUTE_JWT_TOKEN).toString();

        LOG.debug("Validating JWT token");

        final TokenValidationResult tokenValidationRes = this.validateJwtToken(jwtToken);

        LOG.debug("JWT token validation result {}", tokenValidationRes);


        if (!tokenValidationRes.isValid()) {

            LOG.warn("JWT tokrn is not valid");
            LOG.debug("Exiting from validateSession()");

            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
        }


        // Complete authentication
        securityContext.authenticationComplete(sso.getAccount(), mechanismName, true);

        LOG.debug("SSO session is found. Sso session id: {} Account name: {}", sso.getId(), sso.getAccount().getPrincipal());


        LOG.debug("Exiting from validateSession()");


        return AuthenticationMechanismOutcome.NOT_ATTEMPTED;

    }

    /**
     * Generate unique state value;
     *
     * @return
     */
    private String generateStateValue() {

        // TODO is thread save ?
        final String uuid = java.util.UUID.randomUUID().toString();

        return uuid;
    }


    /**x
     * Sends redirect request
     * @param exchange
     * @param location
     */
    private void sendRedirect(HttpServerExchange exchange, String location) {

        exchange.getResponseHeaders().put(Headers.LOCATION, location);
    }

    /**
     * Gets JWT token
     * @param accessTokenStr - access token

     */
    private AdfsToken getJwtToken(final String accessTokenStr) {

        LOG.debug("Entering to getJwtToken(). accessTokenStr: ", accessTokenStr);

        // Create configuration
        // TODO move to constructor
        final SecurityApiClientConfig cfg = SecurityApiClientConfigFactory.getInstance().createConfig();
        final SecurityApiClient apiClient = new SecurityApiClient(cfg);

        // Create request
        final GetTokenRequest request = new GetTokenRequest();
        request.setCode(accessTokenStr);
        request.setClientId(this.clientId);

        request.setRedirectUrl(cfg.getRedirectUrl());
        request.setApiKey("apiKey");
        request.setAppKey("appKey");

        // Get token
        final AdfsToken token =  apiClient.getToken(request);

        LOG.debug("Exiting from getJwtToken(). JWT token; {}", token);

        return token;
    }

    /**
     * Validates token
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
        final TokenValidationResult tokenValidationResult =  apiClient.validateToken(request);

        LOG.debug("Exiting from validateJwtToken().  token; {}", tokenValidationResult);

        return tokenValidationResult;
    }

    /**
     * Factory class
     */
    public static final class CustomFormAuthenticationMechanismFactory implements AuthenticationMechanismFactory {

        private final SingleSignOnManager ssoManager;

        public CustomFormAuthenticationMechanismFactory(SingleSignOnManager ssoManager) {

            this.ssoManager = ssoManager;
        }


        @Override
        public AuthenticationMechanism create(String mechanismName, FormParserFactory formParserFactory, Map<String, String> properties) {

            LOG.debug("Entering create() Configuration properties: {}.", properties);


            // Get configuration parameter
            final String defaultPage = this.getCfgParam("defaultPage", properties, mechanismName);
            final String checkTokenUrl = this.getCfgParam("checkTokenUrl", properties, mechanismName);
            final String resource = this.getCfgParam("resource", properties, mechanismName);
            final String clientId = this.getCfgParam("clientId", properties, mechanismName);
            final Boolean useStateParam = Boolean.parseBoolean(this.getCfgParam("useStateParam", properties, mechanismName));
            final int stateExpirationTimeInSeconds = Integer.parseInt(this.getCfgParam("stateExpirationTimeInSeconds", properties, mechanismName));
            final String tokenAuthorizeUrl = this.getCfgParam("tokenAuthorizeUrl", properties, mechanismName);

            // Groups mapping
            final String groupsMappingCfg = this.getCfgParam("groupsMapping", properties, mechanismName);

            TokenGroupsMapper groupsMapper = null;

            // Create groups mapper
            if (groupsMappingCfg != null
                    && !groupsMappingCfg.isEmpty()) {

                LOG.debug("Creating groups mapper");

                groupsMapper = TokenGroupsMapper.TokenGroupsMapperFactory.create(groupsMappingCfg);

                LOG.debug("Groups mapper is created");
            }

           final AuthenticationMechanism authenticationMechanism = new CustomFormAuthenticationMechanism(mechanismName,
                   properties.get(LOGIN_PAGE),
                   properties.get(ERROR_PAGE),
                   this.ssoManager,
                   defaultPage,
                   checkTokenUrl,
                   resource,
                   clientId,
                   useStateParam,
                   stateExpirationTimeInSeconds,
                   tokenAuthorizeUrl,
                   groupsMapper);

            LOG.debug("Exiting from create. Authentication mechanism: {} is created.", mechanismName);

            return authenticationMechanism;
        }

        /**
         * Returns configuration parameter value
         *
         * @param paramName
         * @param properties
         * @param mechanismName
         * @return
         */
        private String getCfgParam(final String paramName, Map<String, String> properties, String mechanismName) {

            String paramVal = (String) properties.get(paramName);

            if (paramVal == null || paramVal.isEmpty()) {

                throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, paramName);
            }

            return paramVal;
        }
    }

}
