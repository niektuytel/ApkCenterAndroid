package com.pukkol.apkcenter.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class API {

    private static final String APK_VERSION = "1";// from shared preferences

    public static String sIpAddress = "http://192.168.2.7:51630/" + APK_VERSION + "/";
    //public static String sIpAddress = "http://85.214.165.155:51630/" + APK_VERSION;

    private static boolean LOCAL = true;
    public static String API_ADDRESS()
    {
        if(LOCAL)
        {
            return "http://192.168.2.7:51630/" + APK_VERSION;
        }
        else
        {
            return "http://85.214.165.155:51630";
        }
    }

    public static final String PATH_CATEGORIES = "/apkcenter/category/";
    public static final String PATH_CATEGORY = "/apkcenter/category";
    public static final String PATH_SECTION = "/apkcenter/";
    public static final String PATH_APP = "/apkcenter/app/";
    public static final String PATH_APK = "/apkcenter/";
    public static final String PATH_REQUEST = "/apkcenter/request/app/";
    public static final String PATH_SEARCH = "/apkcenter/search?value=";
    public static final String PATH_ERROR = "/apkcenter/error";


    public static boolean isNetworkAvailable(Context context)
    {
        Application application = (Application) context.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();

        if (nw == null)
        {
            return false;
        }

        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);

        if(actNw == null)
        {
            return false;
        }

        return (
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        );
    }

}
