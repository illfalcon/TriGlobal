package com.example.triglobal.ui.fragments;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.Lead;
import com.example.triglobal.network.NetworkChecker;
import com.example.triglobal.ui.LeadsLoader;
import com.example.triglobal.ui.MainActivity;
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
    private static NetworkChangeReceiver networkChangeReceiver;
    private static final String WAITING_VISIBILITY = "waiting";
    private static final String LOADED_KEY = "loaded";

    private RecyclerView mRecyclerView;
    private LeadsAdapter mAdapter;
    private List<Lead> mLeads;
    private OnLeadFragmentInteractionListener mListener;
    private TextView mLeadsError;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private TextView mWaitingForNetwork;
    private boolean loaded;
    private Handler mHandler;
    private Context mContext;
    private CoordinatorLayout mCoordinator;

    private LeadsAdapter.onItemClickListener onItemClickListener;

    public LeadsFragment() {
        // Required empty public constructor
    }

    public static LeadsFragment newInstance() {
        return new LeadsFragment();
    }

    @Override
    public void onRefresh() {
        if (NetworkChecker.isNetworkAvailable(getContext())) {
            loaded = false;
//            mLeads = new ArrayList<>();
//            mAdapter.updateData(mLeads);
            getLoaderManager().restartLoader(0, null, this);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mCoordinator, "Need network to refresh", Snackbar.LENGTH_SHORT).show();
            waitForNetwork();
        }
    }

    public interface OnLeadFragmentInteractionListener {
        void onLeadChoice(Lead lead);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeReceiver, filter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        getActivity().unregisterReceiver(networkChangeReceiver);
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(WAITING_VISIBILITY, mWaitingForNetwork.getVisibility());
//    }

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
        mCoordinator = view.findViewById(R.id.coordinator_leads);
//        mWaitingForNetwork = view.findViewById(R.id.leads_no_internet_message);
//        if (savedInstanceState != null)
//            mWaitingForNetwork.setVisibility(savedInstanceState.getInt(WAITING_VISIBILITY));
        return view;
    }

    @NonNull
    @Override
    public Loader<List<Lead>> onCreateLoader(int i, @Nullable Bundle bundle) {
        mSwipeRefreshLayout.setRefreshing(true);
        mLeadsError.setVisibility(View.GONE);
        return new LeadsLoader(this.getContext(), mHandler);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Lead>> loader, List<Lead> leads) {
        mLeadsError.setVisibility(View.GONE);
        if (leads != null) {
            Collections.sort(leads, (Lead o1, Lead o2) -> o1.getMovingDate().compareTo(o2.getMovingDate()));
            mLeads = leads;
            if (!loaded)
                runLayoutAnimation(mRecyclerView, mLeads);
            else
                mAdapter.updateData(leads);
            loaded = true;
        } else {
            if (!loaded && NetworkChecker.isNetworkAvailable(getContext())) {
                mLeadsError.setVisibility(View.VISIBLE);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Lead>> loader) {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");
        if (savedInstanceState == null) {
            mLeads = new ArrayList<>();
        } else {
            loaded = savedInstanceState.getBoolean(LOADED_KEY);
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LeadsLoader.MSG_NO_INTERNET) {
                    Log.d(TAG, "handleMessage: NetworkChecker.isNetworkAvailable(mContext) = " +
                            NetworkChecker.isNetworkAvailable(mContext));
                    if (!NetworkChecker.isNetworkAvailable(mContext)) {
                        waitForNetwork();
                    }
//                        mWaitingForNetwork.setVisibility(View.VISIBLE);
                    else
                        Snackbar.make(mCoordinator, "Unable to connect to the internet, please check your network configuration", Snackbar.LENGTH_LONG)
                        .show();
                }
            }
        };
        onItemClickListener = (Lead lead) -> mListener.onLeadChoice(lead);
        mAdapter = new LeadsAdapter(this.getContext(), mLeads, onItemClickListener);
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = () -> {
            endWaitingForNetwork();
            if (!loaded)
                getLoaderManager().restartLoader(0, null, this);
        };
    }

    private void waitForNetwork() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setActionBarTitle("Waiting for network...");
        }
    }

    private void endWaitingForNetwork() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setActionBarTitle("TriGlobal");
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, List<Lead> leads) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        mAdapter.updateData(leads);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOADED_KEY, loaded);
    }
}
