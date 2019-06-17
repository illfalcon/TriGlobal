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
import android.widget.Toast;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.FreeLead;
import com.example.triglobal.ui.FreeLeadsLoader;
import com.example.triglobal.ui.recycler.FreeLeadsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreeLeadsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<List<FreeLead>> {

    public static final String TAG = FreeLeadsFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private FreeLeadsAdapter mAdapter;
    private List<FreeLead> mFreeLeads;
    private FreeLeadsAdapter.OnFreeLeadClickListener onFreeLeadClickListener;
    private FreeLeadsAdapter.OnFreeLeadPurchase onFreeLeadPurchase;
    private TextView mFreeLeadsError;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean loaded;
    private OnFreeLeadFragmentInteractionListener mListener;
    private NetworkChangeReceiver networkChangeReceiver;

    public FreeLeadsFragment() {
        // Required empty public constructor
    }

    public static FreeLeadsFragment newInstance() {
        return new FreeLeadsFragment();
    }

    public interface OnFreeLeadFragmentInteractionListener {
        void onFreeLeadChoice(FreeLead freeLead);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        if (context instanceof OnFreeLeadFragmentInteractionListener) {
            mListener = (OnFreeLeadFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnLeadFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFreeLeads = new ArrayList<>();
        onFreeLeadClickListener = (FreeLead freeLead) -> mListener.onFreeLeadChoice(freeLead);
        onFreeLeadPurchase = (FreeLead freeLead) -> {
            Toast.makeText(this.getContext(), "Buy clicked", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: buy button clicked");
        };
        mAdapter = new FreeLeadsAdapter(this.getContext(), mFreeLeads, onFreeLeadClickListener, onFreeLeadPurchase);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_leads, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.freeleads_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = view.findViewById(R.id.freeleads_recyclerview);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mFreeLeadsError = view.findViewById(R.id.freeleads_error_message);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = () ->
        {
            mFreeLeadsError.setVisibility(View.GONE);
            if (!loaded) {
                getLoaderManager().restartLoader(0, null, this);
            }
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
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onRefresh() {
        loaded = false;
        mFreeLeads = new ArrayList<>();
        mAdapter.updateData(mFreeLeads);
        getLoaderManager().restartLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<List<FreeLead>> onCreateLoader(int i, @Nullable Bundle bundle) {
        mSwipeRefreshLayout.setRefreshing(true);
        mFreeLeadsError.setVisibility(View.GONE);
        return new FreeLeadsLoader(this.getContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<FreeLead>> loader, List<FreeLead> freeLeads) {
        mFreeLeadsError.setVisibility(View.GONE);
        if (freeLeads != null) {
            Collections.sort(freeLeads, (FreeLead o1, FreeLead o2) ->
                    o1.getTimeLeft().compareTo(o2.getTimeLeft()));
            mFreeLeads = freeLeads;
            Log.d(TAG, "onLoadFinished: freeLeadsEmpty? " + mFreeLeads.isEmpty());
            loaded = true;
        } else {
            if (!loaded) {
                mFreeLeadsError.setVisibility(View.VISIBLE);
            }
        }
        mAdapter.updateData(mFreeLeads);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<FreeLead>> loader) {
        mSwipeRefreshLayout.setRefreshing(true);
        mFreeLeadsError.setVisibility(View.GONE);
    }

}
