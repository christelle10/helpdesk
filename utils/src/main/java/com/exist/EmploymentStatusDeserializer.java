package com.exist;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;


public class EmploymentStatusDeserializer extends JsonDeserializer<EmploymentStatus> {
    @Override
    public EmploymentStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();
        if (value.isEmpty()) {
            return null; // Convert empty string to null
        }
        return EmploymentStatus.valueOf(value.toUpperCase().replace(" ", "_")); // Handle enum case sensitivity
    }
}



