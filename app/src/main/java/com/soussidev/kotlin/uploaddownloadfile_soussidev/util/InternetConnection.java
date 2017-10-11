package com.soussidev.kotlin.uploaddownloadfile_soussidev.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Soussi on 10/10/2017.
 */

public class InternetConnection {

    /**
     * @author Soussi
     *
     * @Fun checkConnection()
     *
     *
     *
     */

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
