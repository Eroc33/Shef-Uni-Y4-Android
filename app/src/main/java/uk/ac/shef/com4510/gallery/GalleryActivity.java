package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import uk.ac.shef.com4510.ImageScannerService;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.map.MapActivity;

public class GalleryActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "GalleryActivity";

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
    private File currentCaptureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionRequestCode.READ_EXTERNAL_STORAGE);
        } else {
            continueSetup();
        }

        FloatingActionButton fabCamera = findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryOpenCamera();
            }
        });

        FloatingActionButton fabMap = findViewById(R.id.fab_map);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Intent startActivityIntent = new Intent(context, MapActivity.class);
                context.startActivity(startActivityIntent);
            }
        });
    }

    private void continueSetup() {
        Context context = getApplicationContext();
        ImageScannerService.scan_all(context);
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        adapter = new GalleryRecyclerViewAdapter(context);
        viewModel.getImages().observe(this, allImages -> adapter.setImages(allImages));
        recyclerView = findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
    }

    private void tryOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionRequestCode.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newFilename());
            Uri photoURI = FileProvider.getUriForFile(this,
                    "uk.ac.shef.com4510",
                    photoFile);
            currentCaptureFile = photoFile;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        startActivityForResult(intent, ActivityResultCode.CAMERA);
    }

    private String newFilename() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMG_" + timeStamp + ".jpg";

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.READ_EXTERNAL_STORAGE: {
                continueSetup();
                break;
            }

            case PermissionRequestCode.CAMERA: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
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
                    Log.i(TAG,String.format("Will load_one %s",currentCaptureFile.toString()));
                    if(currentCaptureFile == null){
                        throw new RuntimeException("currentCaptureFile is null when camera completes. this shouldn't  be possible");
                    }
                    File file = currentCaptureFile;
                    currentCaptureFile = null;

                    try {
                        //TODO: this only adds basic information to the entry, need to extend this
                        // to add, for example, location data
                        MediaStore.Images.Media.insertImage(getContentResolver(),file.getCanonicalPath(),file.getName(),file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Error while saving photo",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
