package uk.ac.shef.com4510;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.security.Permission;

public class SingleShotLocationProvider {

    public interface LocationCallback {
        void onNewLocationAvailable(Location location);
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
                        callback.onNewLocationAvailable(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                    @Override
                    public void onProviderEnabled(String provider) { }

                    @Override
                    public void onProviderDisabled(String provider) { }

                }, null);
            } else {
                Toast.makeText(context, R.string.no_gps, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.no_fine_location, Toast.LENGTH_SHORT).show();
        }
    }
}