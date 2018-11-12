package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import uk.ac.shef.com4510.ImageScannerService;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.camera.CameraActivity;
import uk.ac.shef.com4510.databinding.ActivityGalleryBinding;
import uk.ac.shef.com4510.map.MapActivity;
import uk.ac.shef.com4510.support.databinding.RecyclerViewAdapterProvider;

public class GalleryActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, RecyclerViewAdapterProvider {

    private static final String TAG = "GalleryActivity";

    private ActivityGalleryBinding binding;

    class PermissionRequestCode {
        static final int READ_EXTERNAL_STORAGE = 101;
    }

    private GalleryViewModel viewModel;

    @Override
    public RecyclerView.Adapter<?> getAdapter() {
        return new GalleryRecyclerViewAdapter(this, viewModel, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        binding.setLifecycleOwner(this);
        binding.setAdapterProvider(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionRequestCode.READ_EXTERNAL_STORAGE);
        } else {
            continueSetup();
        }
    }

    private void continueSetup() {
        ImageScannerService.scan_all(getApplicationContext());
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        viewModel.setOnOpenCamera(() -> startActivity(new Intent(this, CameraActivity.class)));
        viewModel.setOnOpenMap(() -> startActivity(new Intent(this, MapActivity.class)));
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.READ_EXTERNAL_STORAGE: {
                continueSetup();
                break;
            }
        }
    }
}
