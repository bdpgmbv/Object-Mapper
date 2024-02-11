package com.vy.objectmapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vy.objectmapper.dto.FieldSetup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

public class ReadWriteJsonUnitTest {
    List<String> identityFieldPaths = new ArrayList<>(List.of("doerId"));
    List<String> outputFieldPaths = new ArrayList<>(List.of("doerId", "doerDetails.doerName"));
    final String EXAMPLE_FIELD_SETUP_JSON = "{\"identityFieldPaths\": [\"doerId\"],\"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"],\"ignoreFieldPath\": \"doerAddress.*\",\"ignore\": true,\"numberTolerance\": 0.00}";
    final String LOCAL_FIELD_SETUP_JSON = "[{\"identityFieldPaths\": [\"doerId\"],\"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"],\"ignoreFieldPath\": \"doerAddress.*\",\"ignore\": true,\"numberTolerance\": 0.00},{\"identityFieldPaths\": [\"doerId\"],\"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"],\"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true, \"numberTolerance\": 0.00}]";
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void whenWriteValueAsString_thenCorrect() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FieldSetup fieldSetup = new FieldSetup(identityFieldPaths, outputFieldPaths, "doerAddress.*", true, 0.00);

        String result = objectMapper.writeValueAsString(fieldSetup);

        assertThat(result, containsString("doerId"));
        assertThat(result, containsString("doerDetails"));

    }

    @Test
    public void whenWriteValueToAndReadValueFromTemporaryFile_thenCorrect() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File fieldSetupFile = temporaryFolder.newFile("FieldSetup.json");
        final FieldSetup fieldSetup = new FieldSetup(identityFieldPaths, outputFieldPaths, "doerAddress.*", true, 0.00);

        objectMapper.writeValue(fieldSetupFile, fieldSetup);

        FieldSetup readFromFile = objectMapper.readValue(fieldSetupFile, FieldSetup.class);

        assertEquals(fieldSetup.getIgnoreFieldPath(), readFromFile.getIgnoreFieldPath());
    }

    @Test
    public void whenReadValueFromJSONString_thenCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FieldSetup fieldSetup = objectMapper.readValue(EXAMPLE_FIELD_SETUP_JSON, FieldSetup.class);
        assertNotNull(fieldSetup);
        assertTrue(fieldSetup.ignore);
    }

    @Test
    public void whenReadTree_thenCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(EXAMPLE_FIELD_SETUP_JSON);
        assertNotNull(jsonNode);
        assertThat(jsonNode.get("identityFieldPaths").get(0).asText(), containsString("doerId"));
    }

    @Test
    public void whenReadValueFromJSONStringCreateList_thenCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<FieldSetup> fieldSetups = objectMapper.readValue(LOCAL_FIELD_SETUP_JSON, new TypeReference<List<FieldSetup>>() {
        });

        assertThat(fieldSetups, hasSize(2));
        for(FieldSetup fieldSetup : fieldSetups) {
            assertNotNull(fieldSetup);
            assertThat(fieldSetup.getOutputFieldPaths().get(0), containsString("doerId"));
        }
    }
    
    @Test
    public void whenReadValueFromJSONStringCreateMap_thenCorrect() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> stringObjectMap = objectMapper.readValue(EXAMPLE_FIELD_SETUP_JSON, new TypeReference<Map<String, Object>>() {
        });

        assertThat(stringObjectMap.keySet(), hasSize(5));
        for(Object value : stringObjectMap.values()) {
            assertNotNull(value);
        }
    }

    @Test
    public void whenReadValueFromFile_thenCorrect() throws IOException {
        File file = new File("src/test/resources/json_fieldSetup.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        FieldSetup fieldSetup = objectMapper.readValue(file, FieldSetup.class);

        assertEquals(fieldSetup.getOutputFieldPaths().get(0), "doerId");
    }

    @Test
    public void whenReadValueFromUrl_thenCorrect() throws IOException {
        URL url = new URL("file:src/test/resources/json_fieldSetup.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        FieldSetup fieldSetup = objectMapper.readValue(url, FieldSetup.class);

        assertEquals(fieldSetup.getOutputFieldPaths().get(0), "doerId");
    }
}
