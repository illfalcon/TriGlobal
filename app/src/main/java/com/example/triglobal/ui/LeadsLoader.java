package com.example.triglobal.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.exceptions.NoInternetException;
import com.example.triglobal.models.Lead;
import com.example.triglobal.network.ListFetcher;
import com.example.triglobal.network.LeadsFetcher;
import com.example.triglobal.network.OnNoInternetCallback;

import java.util.List;
import java.util.logging.Handler;

public class LeadsLoader extends AsyncTaskLoader<List<Lead>> {
     private static final String TAG = LeadsLoader.class.getSimpleName();

     private OnNoInternetCallback onNoInternetCallback;

     private ListFetcher listFetcher;
     private List<Lead> mCachedLeads;

     public LeadsLoader(Context context, OnNoInternetCallback onNoInternetCallback) {
         super(context);
         listFetcher = new LeadsFetcher();
         this.onNoInternetCallback = onNoInternetCallback;
     }

    @Override
    protected void onStartLoading() {
         if (mCachedLeads == null)
            forceLoad();
         else
             deliverResult(mCachedLeads);
    }

    @Nullable
    @Override
    public List<Lead> loadInBackground() {
         try {
             List<Lead> leadsData = listFetcher.fetchList();
             Log.d(TAG, "loadInBackground: leadsData == null " + (leadsData == null));
             return leadsData;
         } catch (NoInternetException nie) {
             Log.e(TAG, "loadInBackground: no internet");
             onNoInternetCallback.onNoInternet();
         } catch (Exception e) {
             Log.d(TAG, "loadInBackground: " + e.getMessage());
         }
         return null;
    }

    @Override
    public void deliverResult(@Nullable List<Lead> data) {
        super.deliverResult(data);
        mCachedLeads = data;
    }
}
