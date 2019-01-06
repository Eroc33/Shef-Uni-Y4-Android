package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Locale;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.ActivityDetailsBinding;
import uk.ac.shef.com4510.support.CoordinateFormatter;
import uk.ac.shef.com4510.support.ObserverUtils;

/**
 * Activity showing fullscreen image, location on map, and exif data
 */
public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, DetailViewActions {
    private DetailsViewModel viewModel;
    private Marker locationMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setCoordinateFormatter(new CoordinateFormatter(getResources()));
        binding.setDateFormatter(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()));
        binding.setActions(this);
        binding.setLifecycleOwner(this);

        //TODO: handle case that path is not set
        String path = getIntent().getStringExtra("imagePath");
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        viewModel.setPath(path);
        binding.setViewmodel(viewModel);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        viewModel.getImage().observe(this, image -> {
            double lat = image.getLatitude();
            double lng = image.getLongitude();

            map.getUiSettings().setScrollGesturesEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setMapToolbarEnabled(false);

            if (lat != 0.0 || lng != 0.0) {
                // Don't draw the marker if the location is unknown
                LatLng pos = new LatLng(lat, lng);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));

                if (locationMarker == null) {
                    MarkerOptions options = new MarkerOptions().position(pos);
                    locationMarker = map.addMarker(options);
                } else {
                    locationMarker.setPosition(pos);
                }
            }
        });
    }

    /**
     * Open the edit details view
     */
    @Override
    public void edit() {
        ObserverUtils.observeOneshot(viewModel.getImage(),(image)->{
            Intent intent = new Intent(getApplicationContext(),EditDetailsActivity.class);
            intent.putExtra("imagePath",image.getPath());
            startActivity(intent);
        });
    }
}
