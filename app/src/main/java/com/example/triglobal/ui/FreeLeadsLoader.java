package com.example.triglobal.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.models.FreeLead;
import com.example.triglobal.network.ListFetcher;
import com.example.triglobal.network.FreeLeadsFetcher;

import java.util.List;

public class FreeLeadsLoader extends AsyncTaskLoader<List<FreeLead>> {
    private static final String TAG = FreeLeadsLoader.class.getSimpleName();

    private ListFetcher listFetcher;
    private List<FreeLead> mCachedFreeLeads;

    public FreeLeadsLoader(@NonNull Context context) {
        super(context);
        listFetcher = new FreeLeadsFetcher();
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
