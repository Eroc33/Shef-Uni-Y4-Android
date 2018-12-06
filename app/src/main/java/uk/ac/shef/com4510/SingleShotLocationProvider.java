package uk.ac.shef.com4510;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SingleShotLocationProvider {

    public enum LocationReason {NO_FINE_LOCATION, NO_GPS}

    public interface LocationCallback {
        void onLocationAvailable(Location location);
        void onLocationUnavailable(LocationReason reason);
    }

    public static void requestSingleUpdate(Context context, LocationCallback callback) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        int fineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if (fineLocation == PackageManager.PERMISSION_GRANTED) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onLocationAvailable(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                    @Override
                    public void onProviderEnabled(String provider) { }

                    @Override
                    public void onProviderDisabled(String provider) { }

                }, null);
            } else {
                callback.onLocationUnavailable(LocationReason.NO_FINE_LOCATION);
            }
        } else {
            callback.onLocationUnavailable(LocationReason.NO_GPS);
        }
    }
}