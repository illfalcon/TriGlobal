package com.example.triglobal.ui.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.Lead;
import com.example.triglobal.ui.MainActivity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeadDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = LeadDetailsFragment.class.getSimpleName();
    private static final String LEADSTRING = "leadString";
    private Lead mLead;

    private TextView mMovingDate, mVolumeM3, mVolumeFt3, mContents, mAssembly, mStorage, mAddedDate;
    private CheckBox mBusiness, mPacking;

    private TextView mCompanyName, mName, mTelephone1, mTelephone2, mEmail;

    private TextView mStreetFrom, mZipcodeFrom, mCityFrom, mCountryFrom;

    private TextView mStreetTo, mZipcodeTo, mCityTo, mCountryTo;

    private NetworkChangeReceiver networkChangeReceiver;

    public LeadDetailsFragment() {
        // Required empty public constructor
    }

    public static LeadDetailsFragment newInstance(Lead lead) {
        LeadDetailsFragment fragment = new LeadDetailsFragment();
        ObjectMapper om = new ObjectMapper();
        String leadString = "";
        try {
            leadString = om.writeValueAsString(lead);
        } catch (Exception e) {
            Log.e(TAG, "newInstance: error when serializing object");
        }
        Bundle bundle = new Bundle();
        bundle.putString(LEADSTRING, leadString);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.onConnectionAction = this::endWaitingForNetwork;
    }

    private void findViews() {
        mMovingDate = getView().findViewById(R.id.moving_date);
        mVolumeM3 = getView().findViewById(R.id.volume_m3);
        mVolumeFt3 = getView().findViewById(R.id.volume_ft3);
//        mContents = getView().findViewById(R.id.contents);
        mAssembly = getView().findViewById(R.id.assembly);
        mStorage = getView().findViewById(R.id.storage);
        mBusiness = getView().findViewById(R.id.business);
        mPacking = getView().findViewById(R.id.packing);
        mCompanyName = getView().findViewById(R.id.company_name);
        mName = getView().findViewById(R.id.name);
        mTelephone1 = getView().findViewById(R.id.telephone_1);
        mTelephone2 = getView().findViewById(R.id.telephone_2);
        mEmail = getView().findViewById(R.id.email);
        mStreetFrom = getView().findViewById(R.id.street_from);
        mZipcodeFrom = getView().findViewById(R.id.zipcode_from);
        mCityFrom = getView().findViewById(R.id.city_from);
        mCountryFrom = getView().findViewById(R.id.country_from);
        mStreetTo = getView().findViewById(R.id.street_to);
        mZipcodeTo = getView().findViewById(R.id.zipcode_to);
        mCityTo = getView().findViewById(R.id.city_to);
        mCountryTo = getView().findViewById(R.id.country_to);
        mAddedDate = getView().findViewById(R.id.lead_added_date);
    }

    private void setViewValues(Lead lead) {
        mMovingDate.setText(lead.getMovingDate());
        mVolumeM3.setText(String.valueOf(lead.getVolumeMeters()));
        mVolumeFt3.setText(String.valueOf(lead.getVolumeFeet()));
//        mContents.setText(lead.getContents());
        mAssembly.setText(String.valueOf(lead.isAssembly()));
        mStorage.setText(String.valueOf(lead.isStorage()));
        if (mBusiness.isChecked()) {
            mBusiness.toggle();
        }
        if (lead.isBusiness() == 1) {
            mBusiness.toggle();
        }
        if (mPacking.isChecked()) {
            mPacking.toggle();
        }
        if (lead.isPacking() == 1) {
            mPacking.toggle();
        }
        mCompanyName.setText("");
        mName.setText(lead.getFullName());
        mTelephone1.setText(lead.getTelephone1());
        mTelephone2.setText(lead.getTelephone2());
        mEmail.setText(lead.getEmail());
        mStreetFrom.setText(lead.getStreetFrom());
        mZipcodeFrom.setText(lead.getZipcodeFrom());
        mCityFrom.setText(lead.getCityFrom());
        mCountryFrom.setText(lead.getCoCodeFrom());
        mStreetTo.setText(lead.getStreetTo());
        mZipcodeTo.setText(lead.getZipcodeTo());
        mCityTo.setText(lead.getCityTo());
        mCountryTo.setText(lead.getCoCodeTo());
        mAddedDate.setText(lead.getAddedTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lead_details, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findViews();
        if (getArguments().containsKey(LEADSTRING) && !getArguments().getString(LEADSTRING).equals("")) {
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                mLead = om.readValue(getArguments().getString(LEADSTRING), Lead.class);
            } catch (Exception e) {
                Log.e(TAG, "onCreateView: exception when deserializing lead");
                mLead = new Lead(); //TODO: think about how to best handle this
            }
            setViewValues(mLead);
            mEmail.setOnClickListener(this);
            mTelephone1.setOnClickListener(this);
            mTelephone2.setOnClickListener(this);
        }
    }

    //TODO: rewrite with different onclicklisteners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email:
                if (!mEmail.getText().toString().equals("")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", mEmail.getText().toString(), null));
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
                break;
            case R.id.telephone_1:
                if (!mTelephone1.getText().toString().trim().equals("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            "tel", mTelephone1.getText().toString().trim(), null));
                    startActivity(Intent.createChooser(dialIntent, "Call..."));
                }
                break;
            case R.id.telephone_2:
                if (!mTelephone2.getText().toString().trim().equals("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                            "tel", mTelephone2.getText().toString().trim(), null));
                    startActivity(Intent.createChooser(dialIntent, "Call..."));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(networkChangeReceiver);
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
}
