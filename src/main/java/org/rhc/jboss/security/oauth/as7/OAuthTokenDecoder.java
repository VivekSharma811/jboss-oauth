package org.rhc.jboss.security.oauth.as7;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;


/**
 * OAuth token decoder
 *
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenDecoder {


    private ObjectMapper mapper;

    public OAuthTokenDecoder() {
        this(getDefaultObjectMapper());
    }

    OAuthTokenDecoder(ObjectMapper mapper) {
        addDeserializers(mapper);
        this.mapper = mapper;
    }


    /**
     * Parses token
     *
     * @param json
     * @return
     * @throws OAuthTokenDecodeException
     */
    public OAuthToken parseToken(String json) throws OAuthTokenDecodeException {

        return convertFromJSON(json, OAuthToken.class);
    }


    private void addDeserializers(ObjectMapper mapper) {

        SimpleModule module = new SimpleModule();

        module.addDeserializer(OAuthToken.class, new OAuthTokenDeserializer());

        mapper.registerModule(module);
    }

    static ObjectMapper getDefaultObjectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return mapper;
    }

    <T> T convertFromJSON(String json, Class<T> tClazz) throws OAuthTokenDecodeException {

        OAuthTokenDecodeException exception = new OAuthTokenDecodeException(String.format("The string '%s' doesn't have a valid JSON format.", json));

        if (json == null || !json.startsWith("{") || !json.endsWith("}")) {
            throw exception;
        }

        try {
            return mapper.readValue(json, tClazz);

        } catch (IOException e) {

            throw exception;
        }
    }
}
