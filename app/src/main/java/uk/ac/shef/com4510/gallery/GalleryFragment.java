package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.navigation.Navigation;
import uk.ac.shef.com4510.ImageScannerService;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.GalleryFragmentBinding;
import uk.ac.shef.com4510.search.Search;
import uk.ac.shef.com4510.support.databinding.RecyclerViewAdapterProvider;

public class GalleryFragment
        extends Fragment
        implements ActivityCompat.OnRequestPermissionsResultCallback, RecyclerViewAdapterProvider, GalleryActions {

    private static final String TAG = "ScanningActivity";

    private GalleryFragmentBinding binding;
    private RecyclerView.Adapter<?> recyclerViewAdapter;

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
        return recyclerViewAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GalleryFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setAdapterProvider(this);
        binding.setActions(this);
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        recyclerViewAdapter = new GalleryRecyclerViewAdapter(getContext(), viewModel, this);
        Search search = getArguments().getParcelable("search");
        if (search != null) {
            viewModel.applySearch(search);
        }else{
            List<String> exactPaths = getArguments().getStringArrayList("showExact");
            if(exactPaths != null){
                viewModel.withExactImages(exactPaths);
            }
        }

        binding.setViewModel(viewModel);
        binding.executePendingBindings();

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
        ImageScannerService.scan_all(requireActivity().getApplicationContext(),new ScanResultReciever(viewModel));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueSetup();
                } else {
                    Toast.makeText(getContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private static class ScanResultReciever extends ResultReceiver {
        private WeakReference<GalleryViewModel> viewModelWeakReference;
        ScanResultReciever(GalleryViewModel viewModel) {
            super(null);
            viewModelWeakReference = new WeakReference<>(viewModel);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            GalleryViewModel vm = viewModelWeakReference.get();
            long count = resultData.getLong("count");
            long total = resultData.getLong("total");
            int stageString = resultData.getInt("stage");
            Log.i(TAG,String.format("Scanning status updated: %d/%d",count,total));
            if(vm!=null){
                Log.i(TAG,"model updated");
                vm.setProgress(count,total,stageString);
            }else{
                Log.w(TAG,"model NOT updated");
            }
        }
    }
}
