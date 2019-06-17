package com.example.triglobal.ui;

import android.os.AsyncTask;
import android.util.Log;

import com.example.triglobal.network.BuyLeadResponseFetcher;
import com.example.triglobal.network.ResponseFetcher;

public class BuyResponseAsyncTask extends AsyncTask<Integer, Void, String> {
    private static final String TAG = BuyResponseAsyncTask.class.getSimpleName();

    private ResponseFetcher responseFetcher;
    public interface PostExecuteAction {
        void AfterExecution(String s);
    }
    private PostExecuteAction postExecuteAction;

    public BuyResponseAsyncTask(PostExecuteAction postExecuteAction) {
        responseFetcher = new BuyLeadResponseFetcher();
        this.postExecuteAction = postExecuteAction;
    }


    @Override
    protected String doInBackground(Integer... integers) {
        try {
            return (String) responseFetcher.fetchResponse(integers[0]);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Exception ocurred: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        postExecuteAction.AfterExecution(s);
    }
}
