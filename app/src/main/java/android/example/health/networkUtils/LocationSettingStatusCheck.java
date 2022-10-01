package android.example.health.networkUtils;

import android.content.Context;
import android.location.LocationManager;

public class LocationSettingStatusCheck {
    public static boolean isLocationEnable(Context mContext) {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }
}
