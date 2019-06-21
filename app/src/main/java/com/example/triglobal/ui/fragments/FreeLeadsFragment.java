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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.FreeLead;
import com.example.triglobal.ui.BuyResponseAsyncTask;
import com.example.triglobal.ui.FreeLeadsLoader;
import com.example.triglobal.ui.recycler.FreeLeadsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreeLeadsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = FreeLeadsFragment.class.getSimpleName();
    private static final int FREE_LEADS_LOADER_ID = 0;

    private LoaderManager.LoaderCallbacks<List<FreeLead>> freeLeadsLoader =
            new LoaderManager.LoaderCallbacks<List<FreeLead>>() {
                @NonNull
                @Override
                public Loader<List<FreeLead>> onCreateLoader(int i, @Nullable Bundle bundle) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mFreeLeadsError.setVisibility(View.GONE);
                    return new FreeLeadsLoader(getContext(),
                            () -> getActivity().runOnUiThread(
                                    () -> Toast.makeText(getContext(), "NoInternet", Toast.LENGTH_SHORT).show()
                            )
                    );
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<FreeLead>> loader, List<FreeLead> freeLeads) {
                    mFreeLeadsError.setVisibility(View.GONE);
                    if (freeLeads != null && freeLeads != mFreeLeads) {
                        Collections.sort(freeLeads, (FreeLead o1, FreeLead o2) ->
                                o1.getTimeLeft().compareTo(o2.getTimeLeft()));
                        mFreeLeads = freeLeads;
                        Log.d(TAG, "onLoadFinished: freeLeadsEmpty? " + mFreeLeads.isEmpty());
                        loaded = true;
                        mAdapter.updateData(mFreeLeads);
                    } else {
                        if (!loaded) {
                            mFreeLeadsError.setVisibility(View.VISIBLE);
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<FreeLead>> loader) {
                }
            };

//    private Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
//    private Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
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
        FreeLeadsFragment freeLeadsFragment = new FreeLeadsFragment();
        return freeLeadsFragment;
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
        onFreeLeadPurchase = (FreeLead freeLead) -> new BuyResponseAsyncTask(s -> {
            if (s != null) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                if (s.equals("success")) {
                    loaded = false;
                    getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
                }
            } else {
                Toast.makeText(getContext(), "An error ocurred ", Toast.LENGTH_SHORT).show();
            }
        }).execute(freeLead.getId());
        mAdapter = new FreeLeadsAdapter(this.getContext(), mFreeLeads, onFreeLeadClickListener, onFreeLeadPurchase);
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = () ->
        {
            if (!loaded) {
                mFreeLeadsError.setVisibility(View.GONE);
                Log.d(TAG, "onCreate: startLoader");
                getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
            }
        };
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
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeReceiver, filter);
        getLoaderManager().initLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
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
        getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
    }

}
