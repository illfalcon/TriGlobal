package com.example.triglobal.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String TAG = NetworkChangeReceiver.class.getSimpleName();

    public interface OnConnectionAction {
        void onConnection();
    }

    public OnConnectionAction onConnectionAction;

    public NetworkChangeReceiver() {
        onConnectionAction = () -> {};
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Connected");
        if (checkInternet(context))
            onConnectionAction.onConnection();
    }

    boolean checkInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
