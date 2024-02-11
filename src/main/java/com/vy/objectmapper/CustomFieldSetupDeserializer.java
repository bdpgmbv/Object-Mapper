package com.vy.objectmapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vy.objectmapper.dto.FieldSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomFieldSetupDeserializer extends StdDeserializer<FieldSetup> {

    public CustomFieldSetupDeserializer() {
        this(null);
    }
    public CustomFieldSetupDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public FieldSetup deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JacksonException {
        final Logger logger = LoggerFactory.getLogger(getClass());

        final FieldSetup fieldSetup = new FieldSetup();

        final ObjectCodec codec = p.getCodec();
        final JsonNode jsonNode = codec.readTree(p);

        try {
            final JsonNode ignoreFieldPathNode = jsonNode.get("ignoreFieldPath");
            final String ignoreFieldPathAsString = ignoreFieldPathNode.asText() + "=======";
            fieldSetup.setIgnoreFieldPath(ignoreFieldPathAsString);
        } catch (final Exception e) {
            logger.debug("101_parse_exeption: unknown json.");
        }

        return fieldSetup;
    }
}
