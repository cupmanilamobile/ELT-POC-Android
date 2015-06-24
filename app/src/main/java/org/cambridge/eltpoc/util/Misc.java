package org.cambridge.eltpoc.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class Misc {
    public static File getFileDirectory(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(connectivityManager == null)
            return false;
        if(connectivityManager.getActiveNetworkInfo() == null)
            return false;
        return connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
