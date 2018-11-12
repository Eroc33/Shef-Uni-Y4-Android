package uk.ac.shef.com4510.map;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setMapMarkers() {
        viewModel.getImages().observe(this, images -> {
            map.clear();
            if (images == null) {
                return;
            }
            for (Image img : images) {
                LatLng pos = new LatLng(img.getLatitude(), img.getLongitude());
                map.addMarker(new MarkerOptions().position(pos).title(img.getTitle()));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapMarkers();
    }
}
