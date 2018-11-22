package uk.ac.shef.com4510.map;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.io.File;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.details.DetailsActivity;

public class MapFragment extends Fragment
        implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback,
            ClusterManager.OnClusterItemClickListener<MapFragment.Cluster> {

    @Override
    public boolean onClusterItemClick(Cluster cluster) {
        Context context = requireActivity().getApplicationContext();
        Intent startActivityIntent = new Intent(context, DetailsActivity.class);
        String path = cluster.getImage().getPath();

        startActivityIntent.putExtra("imagePath", path);
        context.startActivity(startActivityIntent);

        return true;
    }

    class PermissionRequestCode {
        static final int ACCESS_FINE_LOCATION = 201;
    }

    private GoogleMap map;
    private MapViewModel viewModel;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    private ClusterManager<Cluster> clusterManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        viewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        FloatingActionButton fabLocation = view.findViewById(R.id.fab_location);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationMarker();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        startLocationUpdates();
    }

    private void setMapMarkers() {
        viewModel.getImages().observe(this, images -> {
            clusterManager.clearItems();

            if (images == null) return;

            for (Image img : images) {
                clusterManager.addItem(new Cluster(img));
            }
            //recalculate clusters
            clusterManager.cluster();
        });
    }

    private void setLocationMarker() {
        if (currentLocation != null) {
            double lat = currentLocation.getLatitude();
            double lng = currentLocation.getLongitude();

            LatLng pos = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            map.addMarker(options);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionRequestCode.ACCESS_FINE_LOCATION);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    currentLocation = locationResult.getLastLocation();
                }
            }, null);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        clusterManager = new ClusterManager<>(requireContext(),map);
        clusterManager.setOnClusterItemClickListener(this);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        setMapMarkers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }

                break;
            }
        }
    }

    public static class Cluster implements ClusterItem{

        private final Image image;
        private LatLng position;

        public Cluster(Image image) {
            position = new LatLng(image.getLatitude(),image.getLongitude());
            this.image = image;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String getSnippet() {
            return null;
        }

        public Image getImage() {
            return image;
        }
    }
}
