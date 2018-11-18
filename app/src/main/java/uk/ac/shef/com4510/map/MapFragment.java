package uk.ac.shef.com4510.map;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.details.DetailsActivity;

public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private MapViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    private void setMapMarkers() {
        viewModel.getImages().observe(this, images -> {
            map.clear();

            if (images == null) return;

            for (Image img : images) {
                LatLng pos = new LatLng(img.getLatitude(), img.getLongitude());
                MarkerOptions options = new MarkerOptions().position(pos).title(img.getTitle());
                Marker marker = map.addMarker(options);
                marker.setTag(img.getPath());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        setMapMarkers();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Context context = requireActivity().getApplicationContext();
        Intent startActivityIntent = new Intent(context, DetailsActivity.class);
        String path = (String) marker.getTag();
        startActivityIntent.putExtra("imagePath", path);
        context.startActivity(startActivityIntent);

        return true;
    }
}
