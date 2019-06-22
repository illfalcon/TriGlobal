package com.example.triglobal.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.exceptions.NoInternetException;
import com.example.triglobal.models.FreeLead;
import com.example.triglobal.network.ListFetcher;
import com.example.triglobal.network.FreeLeadsFetcher;
import com.example.triglobal.network.OnNoInternetCallback;

import java.util.List;

public class FreeLeadsLoader extends AsyncTaskLoader<List<FreeLead>> {
    private static final String TAG = FreeLeadsLoader.class.getSimpleName();
    public static final int MSG_NO_INTERNET = 0;

    private ListFetcher listFetcher;
    private List<FreeLead> mCachedFreeLeads;
    private Handler mHandler;

    private void publishMessage(String message) {
        if (mHandler != null) {
            Bundle data = new Bundle();
            data.putString("message", message);
            Message msg = new Message();
            msg.setData(data);
            msg.what = MSG_NO_INTERNET;
            mHandler.sendMessage(msg);
        }
    }

    public FreeLeadsLoader(@NonNull Context context, Handler handler) {
        super(context);
        listFetcher = new FreeLeadsFetcher();
        mHandler = handler;
    }

    @Override
    protected void onStartLoading() {
        if (mCachedFreeLeads == null)
            forceLoad();
        else
            deliverResult(mCachedFreeLeads);
    }

    @Nullable
    @Override
    public List<FreeLead> loadInBackground() {
        try {
            List<FreeLead> freeLeadsData = listFetcher.fetchList();
            Log.d(TAG, "loadInBackground: freeLeadsData == null " + (freeLeadsData == null));
            return freeLeadsData;
        } catch (NoInternetException nie) {
            Log.e(TAG, "loadInBackground: no internet");
            publishMessage("no internet");
        } catch (Exception e) {
            Log.d(TAG, "loadInBackground: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deliverResult(@Nullable List<FreeLead> data) {
        super.deliverResult(data);
        mCachedFreeLeads = data;
    }
}
