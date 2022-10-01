package android.example.health.networkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class CheckNetworkConnection {
    public static boolean isNetworkAvailable(Context context) {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        for (Network mNetwork : networks) {
            NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(mNetwork);
            if (capabilities != null
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                isConnected = true;
                break;
            } else if (capabilities != null
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                isConnected = true;
                break;
            } else if (capabilities != null
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                isConnected = true;
                break;
            }
        }

        return isConnected;


    }

    public static String getInternetConnectivityStatus(Context context) {
        String outcome = "Offline";
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = cm.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        outcome = "Online";
                        break;
                    }
                }
            } else {
                NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
                for (NetworkInfo tempNetworkInfo : networkInfos) {
                    if (tempNetworkInfo.isConnected()) {
                        outcome = "Online";
                        break;
                    }
                }
            }
        }

        return outcome;
    }

}
