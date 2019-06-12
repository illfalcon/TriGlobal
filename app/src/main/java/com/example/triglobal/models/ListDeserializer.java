package com.example.triglobal.models;

import com.example.triglobal.exceptions.SerializationException;

import java.util.List;

public interface ListDeserializer {
    <T> List<T> Deserialize(String jsonData) throws SerializationException;
}
