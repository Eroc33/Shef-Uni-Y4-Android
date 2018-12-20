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
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Locale;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.ActivityDetailsBinding;
import uk.ac.shef.com4510.support.CoordinateFormatter;
import uk.ac.shef.com4510.support.ObserverUtils;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, DetailViewActions {
    private DetailsViewModel viewModel;

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
        viewModel.getImage().observe(this, image -> {
            binding.setImage(image);
            binding.executePendingBindings();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        viewModel.getImage().observe(this, image -> {
            double lat = image.getLatitude();
            double lng = image.getLongitude();
            LatLng pos = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions().position(pos);

            googleMap.addMarker(options);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
        });
    }

    @Override
    public void edit() {
        ObserverUtils.observeOneshot(viewModel.getImage(),(image)->{
            Intent intent = new Intent(getApplicationContext(),EditDetailsActivity.class);
            intent.putExtra("imagePath",image.getPath());
            startActivity(intent);
        });
    }
}
