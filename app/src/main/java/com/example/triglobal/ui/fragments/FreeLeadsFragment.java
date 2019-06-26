package com.example.triglobal.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.FreeLead;
import com.example.triglobal.network.NetworkChecker;
import com.example.triglobal.ui.BuyResponseAsyncTask;
import com.example.triglobal.ui.FreeLeadsLoader;
import com.example.triglobal.ui.MainActivity;
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
    private static final String LOADED_KEY = "loaded";

    private LoaderManager.LoaderCallbacks<List<FreeLead>> freeLeadsLoader =
            new LoaderManager.LoaderCallbacks<List<FreeLead>>() {
                @NonNull
                @Override
                public Loader<List<FreeLead>> onCreateLoader(int i, @Nullable Bundle bundle) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mFreeLeadsError.setVisibility(View.GONE);
                    return new FreeLeadsLoader(getContext(), mHandler);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<FreeLead>> loader, List<FreeLead> freeLeads) {
                    mFreeLeadsError.setVisibility(View.GONE);
                    if (freeLeads != null) {
                        Collections.sort(freeLeads, (FreeLead o1, FreeLead o2) ->
                                o1.getTimeLeft().compareTo(o2.getTimeLeft()));
                        mFreeLeads = freeLeads;
                        if (!loaded)
                            runLayoutAnimation(mRecyclerView, mFreeLeads);
                        else
                            mAdapter.updateData(freeLeads);
                        loaded = true;
                    } else {
                        if (!loaded && NetworkChecker.isNetworkAvailable(getContext())) {
                            mFreeLeadsError.setVisibility(View.VISIBLE);
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<FreeLead>> loader) {
                }
            };

    private Handler mHandler;
    private RecyclerView mRecyclerView;
    private FreeLeadsAdapter mAdapter;
    private List<FreeLead> mFreeLeads;
    private FreeLeadsAdapter.OnFreeLeadClickListener onFreeLeadClickListener;
    private FreeLeadsAdapter.OnFreeLeadPurchase onFreeLeadPurchase;
    private TextView mFreeLeadsError;
    //private TextView mFreeLeadsWaitingForNetwork;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean loaded;
    private OnFreeLeadFragmentInteractionListener mListener;
    private NetworkChangeReceiver networkChangeReceiver;
    private Context mContext;
    private CoordinatorLayout mCoordinator;

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
        mContext = context;
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
        if (savedInstanceState == null)
            mFreeLeads = new ArrayList<>();
        else
            loaded = savedInstanceState.getBoolean(LOADED_KEY);
        onFreeLeadClickListener = (FreeLead freeLead) -> mListener.onFreeLeadChoice(freeLead);
        onFreeLeadPurchase = (FreeLead freeLead) ->
                new AlertDialog.Builder(getContext()).setTitle("Free Lead Purchase")
                .setMessage("Are you sure you want to buy this free lead for " + freeLead.getCost())
                .setPositiveButton("Yes", (dialog, which) ->
                        new BuyResponseAsyncTask(s -> {
                            if (s != null) {
                                if (s.equals("success")) {
                                    Snackbar.make(mCoordinator, "You have succesfully purchased " +
                                            freeLead.getFullName() + "!", Snackbar.LENGTH_LONG).show();
                                    loaded = false;
                                    getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
                                } else {
                                    Snackbar.make(mCoordinator, "You cannot purchase " +
                                            freeLead.getFullName() + ".", Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                if (NetworkChecker.isNetworkAvailable(getContext()))
                                    Snackbar.make(mCoordinator, "An error occurred, please, check your internet connection and try again", Snackbar.LENGTH_LONG)
                                            .show();
                                else {
                                    Snackbar.make(mCoordinator, "Need network to purchase a lead", Snackbar.LENGTH_LONG)
                                            .show();
                                    waitForNetwork();
                                }
                            }
                }).execute(freeLead.getId()))
                .setNegativeButton("No", null)
                .show();
        mAdapter = new FreeLeadsAdapter(this.getContext(), mFreeLeads, onFreeLeadClickListener, onFreeLeadPurchase);
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = () ->
        {
            //mFreeLeadsWaitingForNetwork.setVisibility(View.GONE);
            endWaitingForNetwork();
            if (!loaded) {
                Log.d(TAG, "onCreate: startLoader");
                getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FreeLeadsLoader.MSG_NO_INTERNET) {
                    Log.d(TAG, "handleMessage: NetworkChecker.isNetworkAvailable(mContext) = " +
                            NetworkChecker.isNetworkAvailable(mContext));
                    if (!NetworkChecker.isNetworkAvailable(mContext)) {
                        waitForNetwork();
                    }
                        //mFreeLeadsWaitingForNetwork.setVisibility(View.VISIBLE);
                    else
                        Snackbar.make(mCoordinator, "Unable to connect to the internet, please check your network configuration", Snackbar.LENGTH_LONG)
                                .show();
                }
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
        mCoordinator = view.findViewById(R.id.coordinator_free_leads);
//        mFreeLeadsWaitingForNetwork = view.findViewById(R.id.freeleads_no_internet_message);
//        if (savedInstanceState != null)
//            mFreeLeadsWaitingForNetwork.setVisibility(savedInstanceState.getInt(WAITING_VISIBILITY));
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
        if (NetworkChecker.isNetworkAvailable(getContext())) {
            loaded = false;
//            mFreeLeads = new ArrayList<>();
//            mAdapter.updateData(mFreeLeads);
            getLoaderManager().restartLoader(FREE_LEADS_LOADER_ID, null, freeLeadsLoader);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar.make(mCoordinator, "Need network to refresh", Snackbar.LENGTH_SHORT).show();
            waitForNetwork();
        }

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

    private void runLayoutAnimation(final RecyclerView recyclerView, List<FreeLead> freeLeads) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        mAdapter.updateData(freeLeads);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOADED_KEY, loaded);
    }
}