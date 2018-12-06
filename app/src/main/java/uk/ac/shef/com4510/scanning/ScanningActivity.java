package uk.ac.shef.com4510.scanning;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import androidx.navigation.Navigation;
import uk.ac.shef.com4510.ImageScannerService;
import uk.ac.shef.com4510.MainActivity;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.GalleryFragmentBinding;
import uk.ac.shef.com4510.databinding.ScanningActivityBinding;
import uk.ac.shef.com4510.search.Search;
import uk.ac.shef.com4510.support.databinding.RecyclerViewAdapterProvider;

public class ScanningActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "ScanningActivity";

    private ScanningActivityBinding binding;

    class PermissionRequestCode {
        static final int READ_EXTERNAL_STORAGE = 101;
    }

    private ScanningViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.scanning_activity);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(ScanningViewModel.class);
        viewModel.scanningImages.observe(this, scanning -> {
            Log.d(TAG,String.format("Image scanning state changed to %b",scanning));
            if (!scanning) {
                navigateToMainActivity();
            }
        });
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionRequestCode.READ_EXTERNAL_STORAGE);
        } else {
            continueSetup();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    private void continueSetup() {
        viewModel.scanningImages.postValue(true);
        Log.d(TAG,"Starting to scan images");
        ImageScannerService.scan_all(getApplicationContext(),new ScanCompleteReciever(viewModel));
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

    private static class ScanCompleteReciever extends ResultReceiver {
        private WeakReference<ScanningViewModel> viewModelWeakReference;
        ScanCompleteReciever(ScanningViewModel viewModel) {
            super(null);
            viewModelWeakReference = new WeakReference<>(viewModel);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG,"handleMessage");
            ViewModel vm = viewModelWeakReference.get();
            if(vm!=null){
                ((ScanningViewModel) vm).scanningImages.postValue(false);
            }else{
                Log.w(TAG,"viewmodel is null!");
            }
        }
    }
}
