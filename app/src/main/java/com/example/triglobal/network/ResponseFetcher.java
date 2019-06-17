package com.example.triglobal.network;

import com.example.triglobal.exceptions.FetchingException;

public interface ResponseFetcher<A, B> {
    A fetchResponse(B arg) throws FetchingException;
}
