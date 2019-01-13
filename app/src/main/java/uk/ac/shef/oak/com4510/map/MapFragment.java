package uk.ac.shef.oak.com4510.map;

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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import androidx.navigation.Navigation;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.SingleShotLocationProvider;
import uk.ac.shef.oak.com4510.data.Image;
import uk.ac.shef.oak.com4510.details.DetailsActivity;

/**
 * Displays image locations on map view, clustering if necessary, and navigates to a details view,
 * or list view when an image or cluster is selected.
 */
public class MapFragment extends Fragment
        implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback,
            ClusterManager.OnClusterItemClickListener<MapFragment.Cluster>, ClusterManager.OnClusterClickListener<MapFragment.Cluster> {

    public static final String TAG = "MapFragment";
    private static final int LOCATION_TIMEOUT = 10000;
    private GoogleMap map;
    private MapViewModel viewModel;
    private Marker locationMarker;
    private ClusterManager<Cluster> clusterManager;

    /**
     * When an individual unclustered item from a cluster is clicked
     * @return Whether we handled it or not
     */
    @Override
    public boolean onClusterItemClick(Cluster cluster) {
        //we need a context for activity start
        Context context = requireActivity().getApplicationContext();

        //db id of what was clicked
        long id = cluster.getImage().getId();

        //launch details activity
        Intent startActivityIntent = new Intent(context, DetailsActivity.class);
        startActivityIntent.putExtra("id", id);
        context.startActivity(startActivityIntent);

        //we always handle it
        return true;
    }

    /**
     * When a cluster is clicked
     * @return Whether we handled it or not
     */
    @Override
    public boolean onClusterClick(com.google.maps.android.clustering.Cluster<Cluster> cluster) {
        Bundle bundle = new Bundle();

        //collect everything that was clicked
        //Note: Ideally we would use streams here, but the minimum  specified android version does
        // not seem to support streams.
        ArrayList<Cluster> clusters = new ArrayList<>(cluster.getItems());
        long[] ids =  new long[cluster.getSize()];
        for(int i = 0; i< clusters.size(); i++){
            ids[i] = clusters.get(i).image.getId();
        }

        Log.d(TAG,"navigating to gallery fragment with showExact");
        //launch gallery view with that set of images so the user can choose the one they want.
        bundle.putLongArray("showExact", ids);
        Navigation.findNavController(getView()).navigate(R.id.action_mapFragment_to_galleryFragment, bundle);

        //we always handle it
        return true;
    }

    class PermissionRequestCode {
        static final int ACCESS_FINE_LOCATION = 201;
    }

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
        fabLocation.setOnClickListener(v -> setLocationMarker());

        return view;
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
        Context context = requireContext();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), R.string.getting_location, Toast.LENGTH_SHORT).show();
            SingleShotLocationProvider.requestSingleUpdate(context,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override public void onLocationAvailable(@NonNull Location location) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            LatLng pos = new LatLng(lat, lng);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));

                            if (locationMarker == null) {
                                MarkerOptions options = new MarkerOptions()
                                        .position(pos)
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                locationMarker = map.addMarker(options);
                            } else {
                                locationMarker.setPosition(pos);
                            }

                        }

                        @Override public void onLocationUnavailable(
                                SingleShotLocationProvider.LocationReason reason) {
                            if (getContext() != null) {
                                switch (reason) {
                                    case NO_FINE_LOCATION: {
                                        Toast.makeText(getContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                    case NO_GPS: {
                                        Toast.makeText(getContext(), R.string.no_gps, Toast.LENGTH_SHORT).show();
                                        break;
                                    }

                                    case NO_LAST_KNOWN: {
                                        Toast.makeText(getContext(), R.string.no_last_known, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        clusterManager = new ClusterManager<>(requireContext(),map);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterClickListener(this);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.getUiSettings().setMapToolbarEnabled(false);
        setMapMarkers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationMarker();
                } else {
                    Toast.makeText(getContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    /**
     * Cluster implementation which just forwards to the image used to construct it.
     */
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
