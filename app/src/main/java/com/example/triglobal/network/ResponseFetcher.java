package com.example.triglobal.network;

import com.example.triglobal.exceptions.FetchingException;
import com.example.triglobal.exceptions.NoInternetException;

public interface ResponseFetcher<A, B> {
    A fetchResponse(B arg) throws FetchingException, NoInternetException;
}
