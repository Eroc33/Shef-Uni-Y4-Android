package uk.ac.shef.com4510.details;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.SingleShotLocationProvider;
import uk.ac.shef.com4510.databinding.EditDetailsFragmentBinding;

/**
 * Activity allowing the user to edit various details of an image
 */
public class EditDetailsActivity extends AppCompatActivity implements EditDetailsActions,
        ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final int LOCATION_TIMEOUT = 10000;
    private EditDetailsViewModel viewModel;
    private EditDetailsFragmentBinding binding;
    private Marker marker;
    private GoogleMap gmap;

    class PermissionRequestCode {
        static final int ACCESS_FINE_LOCATION = 201;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.edit_details_fragment);

        viewModel = ViewModelProviders.of(this).get(EditDetailsViewModel.class);
        viewModel.setId(getIntent().getLongExtra("id", -1));

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        binding.setActions(this);
        binding.executePendingBindings();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gmap = map;
        gmap.setOnMapLongClickListener(this);
        gmap.getUiSettings().setMapToolbarEnabled(false);

        viewModel.getImage().observe(this, image -> {
            double lat = image.getLatitude();
            double lng = image.getLongitude();

            if (lat != 0.0 || lng != 0.0) {
                // Don't draw the marker if the location is unknown
                LatLng pos = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions().position(pos);
                gmap.addMarker(options);
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
            }
        });
    }

    public void setNewLocation(LatLng pos) {
        viewModel.latitude.setValue(pos.latitude);
        viewModel.longitude.setValue(pos.longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));

        if (marker == null) {
            MarkerOptions options = new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            marker = gmap.addMarker(options);
        } else {
            marker.setPosition(pos);
        }
    }

    @Override
    public void onMapLongClick(LatLng pos) {
        setNewLocation(pos);
    }

    @Override
    public void commitEdit() {
        viewModel.commitEdit();
        finish();
    }

    @Override
    public void getLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.getting_location, Toast.LENGTH_SHORT).show();
            SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override public void onLocationAvailable(@NonNull Location location) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            LatLng pos = new LatLng(lat, lng);

                            setNewLocation(pos);
                        }

                        @Override public void onLocationUnavailable(
                                SingleShotLocationProvider.LocationReason reason) {
                            switch (reason) {
                                case NO_FINE_LOCATION: {
                                    Toast.makeText(getApplicationContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                case NO_GPS: {
                                    Toast.makeText(getApplicationContext(), R.string.no_gps, Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                case NO_LAST_KNOWN: {
                                    Toast.makeText(getApplicationContext(), R.string.no_last_known, Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }, LOCATION_TIMEOUT);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionRequestCode.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }
}
