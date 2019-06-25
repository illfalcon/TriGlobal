package com.example.triglobal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.triglobal.R;
import com.example.triglobal.broadcast.NetworkChangeReceiver;
import com.example.triglobal.models.FreeLead;
import com.example.triglobal.models.Lead;
import com.example.triglobal.ui.fragments.FreeLeadsFragment;
import com.example.triglobal.ui.fragments.LeadDetailsFragment;
import com.example.triglobal.ui.fragments.LeadsFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MainActivity extends AppCompatActivity implements
        LeadsFragment.OnLeadFragmentInteractionListener, FreeLeadsFragment.OnFreeLeadFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_KEY = "curFragment";
    private static final String LEAD_KEY = "curLead";
    private static final String ROOT_TAG = "root";
    private static final String TITLE_KEY = "title";

    private boolean addNewFragment;

    private String mCurFragmentName;
    private Fragment mCurFragment;
    private Lead mDisplayedLead;

    private Fragment mLeadsFragment;
    private Fragment mFreeLeadsFragment;
    private Fragment mSirelloFragment;
    private FragmentManager mFragmentManager;
    private NetworkChangeReceiver networkChangeReceiver;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_free_leads:
                        popAllBackStack();
                        startFreeLeadsFragment();
                        Log.d(LOG_TAG, "Free leads clicked");
                        return true;
                    case R.id.navigation_leads:
                        popAllBackStack();
                        startLeadFragment();
                        Log.d(LOG_TAG, "Leads clicked");
                        return true;
                    case R.id.navigation_sirello:
                        Toast.makeText(MainActivity.this, "Sirello", Toast.LENGTH_SHORT)
                                .show();
                        Log.d(LOG_TAG, "Sirello clicked");
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.getMenu().findItem(R.id.navigation_leads).setChecked(true);
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null && savedInstanceState.getString(FRAGMENT_KEY) != null) {
            mCurFragmentName = savedInstanceState.getString(FRAGMENT_KEY);
        } else {
            startLeadFragment();
        }
        if (savedInstanceState != null)
            setActionBarTitle(savedInstanceState.getString(TITLE_KEY));
        mFragmentManager.addOnBackStackChangedListener(() -> {
            Log.i(LOG_TAG, "back stack changed ");
            Fragment fr = mFragmentManager.findFragmentById(R.id.fragment_container);
            if (fr != null) {
                mCurFragmentName = fr.getClass().getSimpleName();
                Log.d(LOG_TAG, "onCreate: " + mCurFragmentName);
                if (mCurFragmentName.equals(LeadsFragment.TAG))
                    navView.getMenu().findItem(R.id.navigation_leads).setChecked(true);
                else if (mCurFragmentName.equals(FreeLeadsFragment.TAG))
                    navView.getMenu().findItem(R.id.navigation_free_leads).setChecked(true);
            }
        });
    }

    private void popAllBackStack() {
        mFragmentManager.popBackStack(ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startLeadFragment() {
        if (mCurFragmentName == null || !mCurFragmentName.equals(LeadsFragment.TAG)) {
            if (mLeadsFragment == null)
                mLeadsFragment = LeadsFragment.newInstance();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mLeadsFragment);
            fragmentTransaction.addToBackStack(ROOT_TAG);
            fragmentTransaction.commit();
            mCurFragmentName = LeadsFragment.TAG;
            mCurFragment = mLeadsFragment;
        }
    }

    public void startLeadDetailsFragment(Lead lead) {
        if (!mCurFragmentName.equals(LeadDetailsFragment.TAG)) {
            LeadDetailsFragment detailsFragment = LeadDetailsFragment.newInstance(lead);
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, detailsFragment);
            ft.addToBackStack(null);
            ft.commit();
            mCurFragmentName = LeadDetailsFragment.TAG;
            mCurFragment = detailsFragment;
        }
    }

    public void startFreeLeadsFragment() {
        if (!mCurFragmentName.equals(FreeLeadsFragment.TAG)) {
            if (mFreeLeadsFragment == null)
                mFreeLeadsFragment = FreeLeadsFragment.newInstance();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mFreeLeadsFragment);
            fragmentTransaction.addToBackStack(ROOT_TAG);
            fragmentTransaction.commit();
            mCurFragmentName = FreeLeadsFragment.TAG;
            mCurFragment = mFreeLeadsFragment;
        }
    }

    @Override
    public void onLeadChoice(Lead lead) {
        mDisplayedLead = lead;
        startLeadDetailsFragment(lead);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String leadSerialized = "";
        if (mCurFragmentName.equals(LeadDetailsFragment.TAG)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                leadSerialized = objectMapper.writeValueAsString(mDisplayedLead);
            } catch (Exception e) {
                Log.e(LOG_TAG, "onSaveInstanceState: lead not serialized");
            }
        }
        outState.putString(FRAGMENT_KEY, mCurFragmentName);
        outState.putString(LEAD_KEY, leadSerialized);
        outState.putString(TITLE_KEY, (String) getSupportActionBar().getTitle());
    }

    @Override
    public void onFreeLeadChoice(FreeLead freeLead) {
        mDisplayedLead = freeLead;
        startLeadDetailsFragment(freeLead);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
