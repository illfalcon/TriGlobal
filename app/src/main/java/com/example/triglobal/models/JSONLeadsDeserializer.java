package com.example.triglobal.models;

import android.util.Log;

import com.example.triglobal.exceptions.SerializationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class JSONLeadsDeserializer implements ListDeserializer {
    private static final String LOG_TAG = JSONLeadsDeserializer.class.getSimpleName();

    private ObjectMapper objectMapper;

    public JSONLeadsDeserializer() {
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Log.d(LOG_TAG, "JSONLeadsDeserializer created");
    }

    public List<Lead> Deserialize(String string) throws SerializationException {
        try {
            List<Lead> leads = Arrays.asList(objectMapper.readValue(string, Lead[].class));
            return leads;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception thrown: " + e.getMessage());
            throw new SerializationException(e.getMessage());
        }
    }
}
