package uk.ac.shef.com4510;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Provides a one-time location update. Requires GPS and permission to access fine location.
 */
public class SingleShotLocationProvider {
    public enum LocationReason {NO_FINE_LOCATION, NO_GPS, NO_LAST_KNOWN}

    public interface LocationCallback {
        void onLocationAvailable(@NonNull Location location);
        void onLocationUnavailable(LocationReason reason);
    }

    /**
     * Request a single location update.
     * @param context The application context.
     * @param callback A callback to receive the location or a reason why it was unavailable.
     * @param timeout How long to wait before falling back to last known location.
     */
    public static void requestSingleUpdate(Context context, LocationCallback callback, int timeout) {
        int fineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Criteria criteria = new Criteria();
                Looper myLooper = Looper.myLooper();
                Handler myHandler = new Handler(myLooper);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onLocationAvailable(location);
                        locationManager.removeUpdates(this);
                        // Stop the timeout once we have a location
                        myHandler.removeCallbacksAndMessages(null);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                    @Override
                    public void onProviderEnabled(String provider) { }

                    @Override
                    public void onProviderDisabled(String provider) { }
                };

                Runnable timeoutRunnable = () -> {
                    locationManager.removeUpdates(locationListener);

                    // Use last known location once we've ran out of time
                    String bestProvider = locationManager.getBestProvider(criteria,false);
                    Location locationLastKnown = locationManager.getLastKnownLocation(bestProvider);

                    if (locationLastKnown == null) {
                        // Last known location might not exist
                        callback.onLocationUnavailable(LocationReason.NO_LAST_KNOWN);
                    } else {
                        callback.onLocationAvailable(locationLastKnown);
                    }
                };

                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, locationListener, myLooper);
                myHandler.postDelayed(timeoutRunnable, timeout);
            } else {
                callback.onLocationUnavailable(LocationReason.NO_GPS);
            }
        } else {
            callback.onLocationUnavailable(LocationReason.NO_FINE_LOCATION);
        }
    }
}