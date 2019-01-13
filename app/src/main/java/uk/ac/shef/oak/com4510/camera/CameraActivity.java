package uk.ac.shef.oak.com4510.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.ac.shef.oak.com4510.MainActivity;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.SingleShotLocationProvider;
import uk.ac.shef.oak.com4510.support.MediaStoreHelper;

/**
 * Handles the taking of new images using the device's camera.
 */

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    // LOCATION_TIMEOUT is small here, since we don't want to wait ages to get a location before saving the image.
    private static final int LOCATION_TIMEOUT = 2500;
    private File currentCaptureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryOpenCamera();
    }

    /**
     * Tries to open the camera. Handles missing permissions and devices lacking a camera.
     */
    private void tryOpenCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                        PermissionRequestCode.CAMERA_STORAGE_LOCATION);
            } else {
                openCamera();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_camera, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Opens the camera, assuming the relevant permissions etc.
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), newFilename());
            Uri photoURI = FileProvider.getUriForFile(this, "uk.ac.shef.oak.com4510", photoFile);

            currentCaptureFile = photoFile;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }

        startActivityForResult(intent, ActivityResultCode.CAMERA);
    }

    private String newFilename() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "IMG_" + timeStamp + ".jpg";

    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionRequestCode.CAMERA_STORAGE_LOCATION: {
                if (hasAllPermissionsGranted(grantResults)) {
                    openCamera();
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), R.string.grant_permissions, Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private void savePhoto(File file) {
        savePhoto(file,0.0,0.0);
    }

    /**
     * Saves a photo with a location.
     * @param file The file to save the image to.
     * @param lat Latitude.
     * @param lng Longitude.
     */
    private void savePhoto(File file, double lat, double lng) {
        Log.i(TAG,"save photo with latlng");
        try {
            MediaStoreHelper.insertImageWithLocation(getContentResolver(), file.getCanonicalPath(), file.getName(), file.getName(), lat,lng);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.cannot_save_photo, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivityResultCode.CAMERA: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, String.format("Will load_one %s", currentCaptureFile.toString()));
                    if (currentCaptureFile == null) {
                        throw new RuntimeException("currentCaptureFile is null when camera completes. this shouldn't be possible");
                    }
                    File file = currentCaptureFile;
                    currentCaptureFile = null;

                    // Attempt to get a location for this image...
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                                new SingleShotLocationProvider.LocationCallback() {
                                    @Override
                                    public void onLocationAvailable(@NonNull Location location) {
                                        savePhoto(file, location.getLatitude(), location.getLongitude());
                                    }

                                    @Override
                                    public void onLocationUnavailable(SingleShotLocationProvider.LocationReason reason) {
                                        savePhoto(file);
                                    }
                                }, LOCATION_TIMEOUT);
                    } else {
                        savePhoto(file);
                    }
                }

                finish();
            }
        }
    }

    class PermissionRequestCode {
        static final int CAMERA_STORAGE_LOCATION = 102;
    }

    class ActivityResultCode {
        static final int CAMERA = 201;
    }
}
