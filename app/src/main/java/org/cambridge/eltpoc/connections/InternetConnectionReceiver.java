package org.cambridge.eltpoc.connections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import org.cambridge.eltpoc.ELTApplication;

/**
 * Created by etorres on 6/24/15.
 */
public class InternetConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) &&
                !intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) &&
                !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            return;

        ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));

        if (connectivityManager == null)
            ELTApplication.getInstance().getWebModel()
                    .setHasInternetConnection(false);

        if(connectivityManager.getActiveNetworkInfo() == null)
            ELTApplication.getInstance().getWebModel()
                    .setHasInternetConnection(false);

        if (connectivityManager.getActiveNetworkInfo() != null) {
            ELTApplication.getInstance().getWebModel()
                    .setHasInternetConnection(connectivityManager.getActiveNetworkInfo()
                            .isConnected());
        }
        ELTApplication.getInstance().getWebModel().notifyObservers();
    }
}
