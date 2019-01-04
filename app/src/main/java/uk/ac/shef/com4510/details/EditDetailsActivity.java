package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.EditDetailsFragmentBinding;

public class EditDetailsActivity extends AppCompatActivity implements EditDetailsActions,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private EditDetailsViewModel viewModel;
    private EditDetailsFragmentBinding binding;
    private Marker marker;
    private GoogleMap gmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.edit_details_fragment);

        viewModel = ViewModelProviders.of(this).get(EditDetailsViewModel.class);
        viewModel.setPath(getIntent().getStringExtra("imagePath"));

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

    @Override
    public void onMapLongClick(LatLng pos) {
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
    public void commitEdit() {
        viewModel.commitEdit();
        finish();
    }
}
