package com.vy.objectmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vy.objectmapper.dto.FieldSetup;
import com.vy.objectmapper.dto.Request;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObjectMapperBuilderUnitTest {

    ObjectMapper objectMapper = new ObjectMapperBuilder()
            .enableIndentation()
            .dateFormat()
            .preserveOrder(true)
            .build();

    final String JSON_FIELDSETUP = "{\"identityFieldPaths\": [\"doerId\"], \"outputFieldPaths\": [\"doerId\", \"doerDetails.doerName\"], \"ignoreFieldPath\": \"doerAddress.*\", \"ignore\": true, \"numberTolerance\": 0.00}";

    FieldSetup fieldSetupObj = new FieldSetup(List.of("doerId"), List.of("doerId", "doerDetails.doerName"), "doerAddress.*", true, 0.00);

    @Test
    public void whenReadValueFieldSetup_thenReturnFieldSetupObjectCorrectly() throws JsonProcessingException {
        FieldSetup fieldSetup = objectMapper.readValue(JSON_FIELDSETUP, FieldSetup.class);

        assertEquals(fieldSetup.getOutputFieldPaths().get(1), "doerDetails.doerName");
        assertEquals(fieldSetup.getIgnoreFieldPath(), "doerAddress.*");
    }

    @Test
    public void whenWriteValueAsString_thenReturnJSONCorrectly() throws JsonProcessingException {
        Request request = new Request(fieldSetupObj, new Date(1684909857000L));

        String requestString = objectMapper.writeValueAsString(request);
        //System.out.println(requestString);

        String expected = "{\n" +
                "  \"fieldSetup\" : {\n" +
                "    \"identityFieldPaths\" : [ \"doerId\" ],\n" +
                "    \"outputFieldPaths\" : [ \"doerId\", \"doerDetails.doerName\" ],\n" +
                "    \"ignoreFieldPath\" : \"doerAddress.*\",\n" +
                "    \"ignore\" : true,\n" +
                "    \"numberTolerance\" : 0.0\n" +
                "  },\n" +
                "  \"datePurchased\" : \"2023-05-24 02:30 AM EDT\"\n" +
                "}";
        assertEquals(expected, requestString);
    }
}
