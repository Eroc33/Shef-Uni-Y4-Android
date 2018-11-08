package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import uk.ac.shef.com4510.R;

public class GalleryActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    class PermissionRequestCode {
        static final int READ_EXTERNAL_STORAGE = 101;
        static final int CAMERA = 102;
    }

    class ActivityResultCode {
        static final int CAMERA = 201;
    }

    private RecyclerView recyclerView;
    private GalleryViewModel viewModel;
    private GalleryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionRequestCode.READ_EXTERNAL_STORAGE);
        } else {
            continueSetup();
        }

        FloatingActionButton fab = findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryOpenCamera();
            }
        });
    }

    private void continueSetup() {
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        adapter = new GalleryRecyclerViewAdapter(getApplicationContext());
        viewModel.getImages().observe(this, allImages -> adapter.setImages(allImages));
        recyclerView = findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
    }

    private void tryOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA},
                    PermissionRequestCode.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, PermissionRequestCode.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case PermissionRequestCode.READ_EXTERNAL_STORAGE: {
                continueSetup();

                break;
            }

            case PermissionRequestCode.CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }

                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ActivityResultCode.CAMERA: {
                if(resultCode == Activity.RESULT_OK){
                    // Photo received
                }
            }
        }
    }
}
