package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import uk.ac.shef.com4510.ImageScannerService;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.GalleryFragmentBinding;
import uk.ac.shef.com4510.support.databinding.RecyclerViewAdapterProvider;

public class GalleryFragment
        extends Fragment
        implements ActivityCompat.OnRequestPermissionsResultCallback, RecyclerViewAdapterProvider, GalleryActions {

    private static final String TAG = "GalleryFragment";

    private GalleryFragmentBinding binding;

    @Override
    public void openCamera() {
        Navigation.findNavController(this.getView()).navigate(R.id.action_galleryFragment_to_cameraActivity);
    }

    class PermissionRequestCode {
        static final int READ_EXTERNAL_STORAGE = 101;
    }

    private GalleryViewModel viewModel;

    @Override
    public RecyclerView.Adapter<?> getAdapter() {
        return new GalleryRecyclerViewAdapter(getContext(), viewModel, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GalleryFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setAdapterProvider(this);
        binding.setActions(this);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionRequestCode.READ_EXTERNAL_STORAGE);
        } else {
            continueSetup();
        }
        return binding.getRoot();
    }

    private void continueSetup() {
        ImageScannerService.scan_all(requireActivity().getApplicationContext());
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
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
