package org.rhc.jboss.security.oauth.as7;

/**
 * Expiring entity
 */
public class ExpiringEntity<T> {


    /**
     * Put operation name
     */
    private long putTime;

    /**
     * State value
     */
    private T value;


    public ExpiringEntity(long putTime, T value) {

        this.putTime = putTime;
        this.value = value;
    }

    public long getPutTime() {

        return putTime;
    }

    public T getValue() {

        return value;
    }

    @Override
    public String toString() {
        return "ExpiringEntity{" +
                "putTime=" + putTime +
                ", value=" + value +
                '}';
    }
}