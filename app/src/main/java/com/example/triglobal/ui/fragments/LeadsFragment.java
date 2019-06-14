package com.example.triglobal.ui.fragments;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.Lead;
import com.example.triglobal.ui.LeadsLoader;
import com.example.triglobal.ui.recycler.LeadsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class LeadsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Lead>>,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = LeadsFragment.class.getSimpleName();
    public static final String LOADED_KEY = "loaded";
    private static NetworkChangeReceiver networkChangeReceiver;

    private RecyclerView mRecyclerView;
    private LeadsAdapter mAdapter;
    private List<Lead> mLeads;
    private OnLeadFragmentInteractionListener mListener;
    private TextView mLeadsError;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean loaded;

    private LeadsAdapter.onItemClickListener onItemClickListener;

    public LeadsFragment() {
        // Required empty public constructor
    }

    public static LeadsFragment newInstance() {
        return new LeadsFragment();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: ");
        loaded = false;
        mLeads = new ArrayList<>();
        mAdapter.updateData(mLeads);
        getLoaderManager().restartLoader(0, null, this);
    }

    public interface OnLeadFragmentInteractionListener {
        void onLeadChoice(Lead lead);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        if (context instanceof OnLeadFragmentInteractionListener) {
            mListener = (OnLeadFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnLeadFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: loaded = " + loaded);
        Log.d(TAG, "onResume: mLeads empty? " + mLeads.isEmpty());
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = () -> {
            mLeadsError.setVisibility(View.GONE);
            if (!loaded)
                getLoaderManager().restartLoader(0, null, this);
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeReceiver, filter);
        if (!loaded) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = view.findViewById(R.id.leads_recyclerview);
        mLeadsError = view.findViewById(R.id.leads_error_message);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return view;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @NonNull
    @Override
    public Loader<List<Lead>> onCreateLoader(int i, @Nullable Bundle bundle) {
        mSwipeRefreshLayout.setRefreshing(true);
        mLeadsError.setVisibility(View.GONE);
        return new LeadsLoader(this.getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Lead>> loader, List<Lead> leads) {
        mLeadsError.setVisibility(View.GONE);
        Log.d(TAG, "onLoadFinished: leads == null: " + (leads == null));
        Log.d(TAG, "onLoadFinished: loaded: " + loaded);
        if (leads != null) {
            Collections.sort(leads, (Lead o1, Lead o2) -> o1.getMovingDate().compareTo(o2.getMovingDate()));
            mLeads = leads;
            loaded = true;
        } else {
            if (!loaded) {
                mLeadsError.setVisibility(View.VISIBLE);
            }
        }
        mAdapter.updateData(mLeads);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOADED_KEY, loaded);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Lead>> loader) {
        mSwipeRefreshLayout.setRefreshing(true);
        mLeadsError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        mLeads = new ArrayList<>();
        onItemClickListener = (Lead lead) -> mListener.onLeadChoice(lead);
        mAdapter = new LeadsAdapter(this.getContext(), mLeads, onItemClickListener);
    }
}
