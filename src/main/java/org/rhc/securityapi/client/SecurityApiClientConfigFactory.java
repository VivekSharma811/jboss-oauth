package org.rhc.securityapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Security API client configuration
 */
public class SecurityApiClientConfigFactory {


    private static SecurityApiClientConfigFactory INSTANCE = new SecurityApiClientConfigFactory();

    // Configuration file location
    protected static final String DEFAULT_CFG_PROPERTY_VALUE = "classpath:/org.rhc.securityapi.client.config.properties";
    protected static final String SECURITY_API_CLIENT_PROP_NAME = "org.rhc.securityapi.client.config.location";

    private static final Logger LOG = LoggerFactory.getLogger(SecurityApiClientConfigFactory.class);
    public static final String PORP_BASE_URL = "baseUrl";
    public static final String PROP_AUTH_TOKEN = "authToken";
    public static final String PROP_API_VERSION = "apiVersion";
    public static final String PROP_REDIRECT_URL = "redirectUrl";



    private SecurityApiClientConfigFactory() {

    }

    public static SecurityApiClientConfigFactory getInstance() {
        return  INSTANCE;
    }


    /**
     * Reads configuration and sets configuration properties
     */
    public SecurityApiClientConfig createConfig() {

        final String propertiesLocation = System.getProperty(SECURITY_API_CLIENT_PROP_NAME);
        final Properties configProps = readConfigProperties(propertiesLocation, DEFAULT_CFG_PROPERTY_VALUE);

        // Set properties
        final String baseUrl = configProps.getProperty(PORP_BASE_URL);
        final String authToken = configProps.getProperty(PROP_AUTH_TOKEN);
        final String apiVersion = configProps.getProperty(PROP_API_VERSION);
        final String redirectUrl = configProps.getProperty(PROP_REDIRECT_URL);

        // TODO Add property values check

        SecurityApiClientConfig cfg = new SecurityApiClientConfig( baseUrl,  authToken,  apiVersion, redirectUrl);

        return cfg;
    }

    /**
     * Reads configuration properties
     *
     * @param propertiesLocation - properties location
     * @param defaultProperties  - default properties location
     * @return - properties
     */
    protected Properties readConfigProperties(String propertiesLocation, String defaultProperties) {

        LOG.debug("Entering to readConfigProperties()");

        Properties config = null;

        URL locationUrl = null;
        if (propertiesLocation == null) {
            propertiesLocation = defaultProperties;
        }

        LOG.debug("Configuration properties will be loaded from {}", propertiesLocation);
        if (propertiesLocation.startsWith("classpath:")) {
            String stripedLocation = propertiesLocation.replaceFirst("classpath:", "");
            locationUrl = this.getClass().getResource(stripedLocation);
            if (locationUrl == null) {
                locationUrl = Thread.currentThread().getContextClassLoader().getResource(stripedLocation);
            }
        } else {
            try {
                locationUrl = new URL(propertiesLocation);
            } catch (MalformedURLException e) {
                locationUrl = this.getClass().getResource(propertiesLocation);
                if (locationUrl == null) {
                    locationUrl = Thread.currentThread().getContextClassLoader().getResource(propertiesLocation);
                }
            }
        }

        if (locationUrl != null) {
            config = new Properties();
            try {
                config.load(locationUrl.openStream());
            } catch (IOException e) {
                LOG.error("Error when loading configuration properties r", e);
                config = null;
            }
        }

        if (config == null) {

            LOG.error("Error when loading configuration properties  from "
                    + propertiesLocation);

            throw new RuntimeException("Error when loading configuration properties  from "
                    + propertiesLocation);
        }

        LOG.debug("Exiting from readConfigProperties(). Config: {}", config);

        return config;
    }


}
