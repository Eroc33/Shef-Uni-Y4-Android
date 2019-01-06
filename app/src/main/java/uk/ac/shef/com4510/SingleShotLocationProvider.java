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

public class SingleShotLocationProvider {

    private static final long LOCATION_TIMEOUT = 10000;

    public enum LocationReason {NO_FINE_LOCATION, NO_GPS, NO_LAST_KNOWN}

    public interface LocationCallback {
        void onLocationAvailable(@NonNull Location location);
        void onLocationUnavailable(LocationReason reason);
    }

    public static void requestSingleUpdate(Context context, LocationCallback callback) {
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

                    String bestProvider = locationManager.getBestProvider(criteria,false);
                    Location locationLastKnown = locationManager.getLastKnownLocation(bestProvider);

                    if (locationLastKnown == null) {
                        callback.onLocationUnavailable(LocationReason.NO_LAST_KNOWN);
                    } else {
                        callback.onLocationAvailable(locationLastKnown);
                    }
                };

                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, locationListener, myLooper);
                myHandler.postDelayed(timeoutRunnable, LOCATION_TIMEOUT);
            } else {
                callback.onLocationUnavailable(LocationReason.NO_GPS);
            }
        } else {
            callback.onLocationUnavailable(LocationReason.NO_FINE_LOCATION);
        }
    }
}