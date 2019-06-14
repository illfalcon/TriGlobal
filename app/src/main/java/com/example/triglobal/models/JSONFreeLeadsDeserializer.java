package com.example.triglobal.models;

import android.util.Log;

import com.example.triglobal.exceptions.SerializationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONFreeLeadsDeserializer implements ListDeserializer {
    private static final String LOG_TAG = JSONLeadsDeserializer.class.getSimpleName();

    private ObjectMapper objectMapper;

    public JSONFreeLeadsDeserializer() {
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Log.d(LOG_TAG, "JSONFreeLeadsDeserializer created");
    }

    public List<FreeLead> Deserialize(String string) throws SerializationException {
        try {
            TypeReference<HashMap<String, FreeLead>> typeRef
                    = new TypeReference<HashMap<String, FreeLead>>() {};
            Map<String, FreeLead> map = objectMapper.readValue(string, typeRef);
            List<FreeLead> leads = new ArrayList<>();
            leads.addAll(map.values());
            return leads;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception thrown: " + e.getMessage());
            throw new SerializationException(e.getMessage());
        }
    }
}
