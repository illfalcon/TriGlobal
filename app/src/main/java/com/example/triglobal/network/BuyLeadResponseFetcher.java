package com.example.triglobal.network;

import android.util.Log;

import com.example.triglobal.exceptions.FetchingException;
import com.example.triglobal.exceptions.NoInternetException;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class BuyLeadResponseFetcher implements ResponseFetcher<String, Integer> {
    private static String TAG = BuyLeadResponseFetcher.class.getSimpleName();

    public BuyLeadResponseFetcher() {

    }

    private String loadResponse(int reId) throws FetchingException, NoInternetException {
        if (!NetworkChecker.hasActiveInternetConnection())
            throw new NoInternetException("Error in loadResponse: no internet");
        Log.d(TAG, "loadData: started loading data");
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
            RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\nContent-Disposition: form-data; name=\"token\"\n\nCdcZ5TqsTS\n------WebKitFormBoundary7MA4YWxkTrZu0gW\nContent-Disposition: form-data; name=\"id\"\n\n1\n------WebKitFormBoundary7MA4YWxkTrZu0gW\nContent-Disposition: form-data; name=\"re_id\"\n\n" + reId + "\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
            Request request = new Request.Builder()
                    .url("https://public.triglobal-test-back.nl/api/buy_lead.php")
                    .post(body)
                    .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "8c5d32a6-7c4c-44fa-9a15-9bb97b36c04e")
                    .build();
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            Response response;
            response = client.newCall(request).execute();
            Log.d(TAG, "loadData: finished loading data");
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "Error in loadData: " + e.getMessage());
            throw new FetchingException(e.getMessage());
        }
    }

    @Override
    public String fetchResponse(Integer arg) throws FetchingException, NoInternetException {
        return loadResponse(arg);
    }
}
