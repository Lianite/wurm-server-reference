// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import org.fourthline.cling.model.ModelUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import java.util.logging.Logger;

public class NetworkUtils
{
    private static final Logger log;
    
    public static NetworkInfo getConnectedNetworkInfo(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        networkInfo = connectivityManager.getNetworkInfo(1);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        networkInfo = connectivityManager.getNetworkInfo(0);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        networkInfo = connectivityManager.getNetworkInfo(6);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        networkInfo = connectivityManager.getNetworkInfo(9);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }
        NetworkUtils.log.info("Could not find any connected network...");
        return null;
    }
    
    public static boolean isEthernet(final NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, 9);
    }
    
    public static boolean isWifi(final NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, 1) || ModelUtil.ANDROID_EMULATOR;
    }
    
    public static boolean isMobile(final NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, 0) || isNetworkType(networkInfo, 6);
    }
    
    public static boolean isNetworkType(final NetworkInfo networkInfo, final int type) {
        return networkInfo != null && networkInfo.getType() == type;
    }
    
    public static boolean isSSDPAwareNetwork(final NetworkInfo networkInfo) {
        return isWifi(networkInfo) || isEthernet(networkInfo);
    }
    
    static {
        log = Logger.getLogger(NetworkUtils.class.getName());
    }
}
