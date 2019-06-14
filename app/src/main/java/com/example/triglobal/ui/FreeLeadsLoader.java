package com.example.triglobal.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.models.FreeLead;
import com.example.triglobal.models.Lead;
import com.example.triglobal.network.DataFetcher;
import com.example.triglobal.network.FreeLeadsFetcher;

import java.util.List;

public class FreeLeadsLoader extends AsyncTaskLoader<List<FreeLead>> {
    private static final String TAG = FreeLeadsLoader.class.getSimpleName();

    private DataFetcher dataFetcher;

    public FreeLeadsLoader(@NonNull Context context) {
        super(context);
        dataFetcher = new FreeLeadsFetcher();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<FreeLead> loadInBackground() {
        try {
            List<FreeLead> freeLeadsData = dataFetcher.fetchData();
            Log.d(TAG, "loadInBackground: freeLeadsData == null " + (freeLeadsData == null));
            return freeLeadsData;
        } catch (Exception e) {
            Log.d(TAG, "loadInBackground: " + e.getMessage());
        }
        return null;
    }
}
