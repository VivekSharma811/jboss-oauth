package org.rhc.jboss.security.oauth.as7;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.*;

/**
 * Created by ajurcenk on 2/15/17.
 */
public class OAuthTokenDeserializer  extends StdDeserializer<OAuthToken> {

    OAuthTokenDeserializer() {
        this(null);
    }

    private OAuthTokenDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public OAuthToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        Map<String, JsonNode> tree = p.getCodec().readValue(p, new TypeReference<Map<String, JsonNode>>() {
        });

        if (tree == null) {
            throw new OAuthTokenDecodeException("Parsing the Payload's JSON resulted on a Null map");
        }

        // Get windows account
        final String winAccName = getString(tree, OAuthTokenFields.WIN_ACCOUNT_NAME);

        final List<String> groups = getStringOrArray(tree, OAuthTokenFields.WIN_ACCOUNT_GROUPS);


        return new OAuthToken(winAccName, groups);
    }


    /**
     * Gets field value from map
     *
     * @param tree
     * @param fieldName
     * @return
     */
    String getString(Map<String, JsonNode> tree, String fieldName) {

        JsonNode node = tree.get(fieldName);

        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText(null);
    }

    /**
     * Gets string or array
     * @param tree
     * @param claimName
     * @return
     * @throws OAuthTokenDecodeException
     */
    List<String> getStringOrArray(Map<String, JsonNode> tree, String claimName) throws OAuthTokenDecodeException {

        JsonNode node = tree.get(claimName);

        if (node == null || node.isNull() || !(node.isArray() || node.isTextual())) {

            return null;
        }

        // Text node
        if (node.isTextual() && !node.asText().isEmpty()) {

            return Collections.singletonList(node.asText());
        }

        ObjectMapper mapper = new ObjectMapper();

        List<String> list = new ArrayList<>(node.size());

        for (int i = 0; i < node.size(); i++) {

            try {

                list.add(mapper.treeToValue(node.get(i), String.class));

            } catch (JsonProcessingException e) {

                throw new OAuthTokenDecodeException("Couldn't map the Claim's array contents to String", e);
            }
        }

        return list;
    }

}
