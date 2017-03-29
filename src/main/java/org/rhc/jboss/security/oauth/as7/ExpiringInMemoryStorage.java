package org.rhc.jboss.security.oauth.as7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Expiring in memory storage
 */
public class ExpiringInMemoryStorage<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ExpiringInMemoryStorage.class);


    /**
     * Expiration time period
     */
    private long expirationTimeInSeconds;

    private Map<String, ExpiringEntity<T>> storageMap;

    public ExpiringInMemoryStorage(final long expirationTimeInSeconds) {

        this.storageMap = new ConcurrentHashMap<>();
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }

    /**
     * Adds object to storage
     *
     * @param key
     * @param objectToStore
     */
    public void put(final String key, T objectToStore) {

        LOG.debug("Entering to put()");

        final ExpiringEntity<T> entity = new ExpiringEntity<>(System.currentTimeMillis(), objectToStore);


        LOG.debug("Exiting from put(). Entity {} is added.", entity);

        this.storageMap.put(key, entity);
    }

    /**
     * Returns value
     *
     * @param key
     * @return
     */
    public T get(final String key) {

        LOG.debug("Entering to get(). key: {}", key);

        final long currentTime = System.currentTimeMillis();

        final ExpiringEntity<T> entity = this.storageMap.get(key);

        if (entity != null) {

            LOG.debug("Entity is found. {} for key {}", entity, key);

            // Check entity expiration
            long diffInSeconds = (currentTime - entity.getPutTime()) / 1000;

            boolean isEntityExpired = diffInSeconds > this.expirationTimeInSeconds;

            LOG.debug("Exiting from get(). Is entity is expired: {} Difference in seconds {} expirationTimeInSeconds {}",
                    isEntityExpired, diffInSeconds, expirationTimeInSeconds);

            return (isEntityExpired ? null : entity.getValue());
        }

        LOG.debug("Exiting from get(). Enttity is not found by key: {}", key);

        return null;
    }
}