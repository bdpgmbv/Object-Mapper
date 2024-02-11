package com.vy.objectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.vy.objectmapper.dto.FieldSetup;

import java.io.IOException;

public class CustomFieldSetupSerializer extends StdSerializer<FieldSetup> {
    private static final long serialVersionUID = 1396140685442227917L;
    public CustomFieldSetupSerializer() {
        this(null);
    }
    public CustomFieldSetupSerializer(final Class<FieldSetup> t) {
        super(t);
    }
    @Override
    public void serialize(FieldSetup value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("FieldPathsToBeIgnore", value.getIgnoreFieldPath());
        gen.writeEndObject();
    }
}
