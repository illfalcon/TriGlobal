package com.example.triglobal.network;

import com.example.triglobal.exceptions.FetchingException;
import com.example.triglobal.exceptions.SerializationException;

import java.util.List;

public interface DataFetcher {
    <T> List<T> fetchData() throws FetchingException, SerializationException;
}
