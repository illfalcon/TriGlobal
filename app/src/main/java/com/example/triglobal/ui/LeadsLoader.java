package com.example.triglobal.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.triglobal.exceptions.NoInternetException;
import com.example.triglobal.models.Lead;
import com.example.triglobal.network.ListFetcher;
import com.example.triglobal.network.LeadsFetcher;

import java.util.List;

public class LeadsLoader extends AsyncTaskLoader<List<Lead>> {
     private static final String TAG = LeadsLoader.class.getSimpleName();
     public static final int MSG_NO_INTERNET = 0;

     private Handler mHandler;

     private ListFetcher listFetcher;
     private List<Lead> mCachedLeads;

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

     public LeadsLoader(Context context, Handler handler) {
         super(context);
         listFetcher = new LeadsFetcher();
         mHandler = handler;
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
             publishMessage("no internet");
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
