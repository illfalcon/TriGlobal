package com.example.triglobal.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.models.Lead;
import com.example.triglobal.network.ListFetcher;
import com.example.triglobal.network.LeadsFetcher;

import java.util.List;

public class LeadsLoader extends AsyncTaskLoader<List<Lead>> {
    private static final String TAG = LeadsLoader.class.getSimpleName();

     private ListFetcher listFetcher;

     public LeadsLoader(Context context) {
         super(context);
         listFetcher = new LeadsFetcher();
     }

    @Override
    protected void onStartLoading() {
         forceLoad();
    }

    @Nullable
    @Override
    public List<Lead> loadInBackground() {
         try {
             List<Lead> leadsData = listFetcher.fetchList();
             Log.d(TAG, "loadInBackground: leadsData == null " + (leadsData == null));
             return leadsData;
         } catch (Exception e) {
             Log.d(TAG, "loadInBackground: " + e.getMessage());
         }
         return null;
    }
}
