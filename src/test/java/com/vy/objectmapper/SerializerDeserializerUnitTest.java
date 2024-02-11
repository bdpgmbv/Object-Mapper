package com.vy.objectmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vy.objectmapper.CustomFieldSetupDeserializer;
import com.vy.objectmapper.CustomFieldSetupSerializer;
import com.vy.objectmapper.dto.FieldSetup;
import com.vy.objectmapper.dto.Request;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class SerializerDeserializerUnitTest {

    final String JSON_FIELDSETUP = "{\"identityFieldPaths\": [\"doerId\"], \"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"], \"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true}";
    final String JSONDESERIALIZE_FIELDSETUP = "{\"identityFieldPaths\": [\"doerId\"], \"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"], \"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true, \"numberTolerance\": 0.00}";
    final String USE_JAVA_ARRAY_FOR_JSON_ARRAY_FIELDSETUP = "[{\"identityFieldPaths\": [\"doerId\"], \"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"], \"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true, \"numberTolerance\": 0.00}, {\"identityFieldPaths\": [\"doerId\"], \"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"], \"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true, \"numberTolerance\": 0.00}]";

    @Test
    public void whenFAIL_ON_UNKNOWN_PROPERTIES_thenJSONReadCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final FieldSetup fieldSetup = objectMapper.readValue(JSON_FIELDSETUP, FieldSetup.class);
        final JsonNode jsonNode = objectMapper.readTree(JSON_FIELDSETUP);
        final JsonNode jsonNode_identityFieldPaths = jsonNode.get("identityFieldPaths");
        final String identityFieldPaths =  jsonNode_identityFieldPaths.get(0).asText();

        assertNotNull(fieldSetup);
        assertThat(fieldSetup.getIgnoreFieldPath(), equalTo("doerAddress.*"));
        assertThat(identityFieldPaths, containsString("doerId"));
    }


    @Test
    public void whenCustomSerializer_thenWriteCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final SimpleModule serializeModule = new SimpleModule("CustomSerializer", new Version(1,0,0,null,null,null));
        serializeModule.addSerializer(FieldSetup.class, new CustomFieldSetupSerializer());

        objectMapper.registerModule(serializeModule);

        final FieldSetup fieldSetup = new FieldSetup(Arrays.asList("doerId"), Arrays.asList("doerId", "doerDetails.doerName"), "doerAddress.*", true, 0.00);
        String s = objectMapper.writeValueAsString(fieldSetup);

        assertThat(s, containsString("FieldPathsToBeIgnore"));
    }


    @Test
    public void whenCustomDeserialize_thenReadCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final SimpleModule deserializeModule = new SimpleModule("CustomDeserializer", new Version(1,0,0,null,null,null));
        deserializeModule.addDeserializer(FieldSetup.class, new CustomFieldSetupDeserializer());

        objectMapper.registerModule(deserializeModule);

        final FieldSetup fieldSetupDeserialized = objectMapper.readValue(JSONDESERIALIZE_FIELDSETUP, FieldSetup.class);

        assertNotNull(fieldSetupDeserialized);
        assertEquals(fieldSetupDeserialized.getIgnoreFieldPath(), "doerAddress.*=======");
    }


    @Test
    public void whenSetDateFormat_thenSerializedAsExpected() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final FieldSetup fieldSetup = new FieldSetup(Arrays.asList("doerId"), Arrays.asList("doerId", "doerDetails.doerName"), "doerAddress.*", true, 0.00);
        final Request request = new Request(fieldSetup, new Date());

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
        objectMapper.setDateFormat(df);

        final String fieldSetupAsString = objectMapper.writeValueAsString(request);

        assertNotNull(fieldSetupAsString);
        assertThat(fieldSetupAsString, containsString("datePurchased"));
    }

    @Test
    public void whenConfigureUSE_JAVA_ARRAY_FOR_JSON_ARRAY_thenJsonReadAsArray() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);

        final FieldSetup[] fieldSetups = objectMapper.readValue(USE_JAVA_ARRAY_FOR_JSON_ARRAY_FIELDSETUP, FieldSetup[].class);

        for (final FieldSetup fieldSetup : fieldSetups) {
            assertNotNull(fieldSetup);
            assertThat(fieldSetup.getIgnoreFieldPath(), containsString("doerAddress"));
        }
    }
}