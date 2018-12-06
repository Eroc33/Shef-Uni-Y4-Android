package uk.ac.shef.com4510.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.ExifInterface;
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

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.SingleShotLocationProvider;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private File currentCaptureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tryOpenCamera();
    }

    private void tryOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "IMG_" + timeStamp + ".jpg";

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
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
        switch (requestCode) {
            case ActivityResultCode.CAMERA: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, String.format("Will load_one %s", currentCaptureFile.toString()));
                    if (currentCaptureFile == null) {
                        throw new RuntimeException("currentCaptureFile is null when camera completes. this shouldn't  be possible");
                    }
                    File file = currentCaptureFile;
                    currentCaptureFile = null;

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                                new SingleShotLocationProvider.LocationCallback() {
                                    @Override
                                    public void onLocationAvailable(Location location) {
                                        try {
                                            double lat = location.getLatitude();
                                            double lng = location.getLongitude();
                                            String lat_s = Double.toString(lat);
                                            String lng_s = Double.toString(lng);
                                            ExifInterface exif = new ExifInterface(file.getName());
                                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat_s);
                                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lng_s);
                                            exif.saveAttributes();
                                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getCanonicalPath(), file.getName(), file.getName());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), R.string.cannot_save_photo, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onLocationUnavailable(SingleShotLocationProvider.LocationReason reason) {
                                        try {
                                            MediaStore.Images.Media.insertImage(getContentResolver(), file.getCanonicalPath(), file.getName(), file.getName());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), R.string.cannot_save_photo, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    finish();
                } else {
                    finish();
                }
            }
        }
    }

    class PermissionRequestCode {
        static final int CAMERA = 102;
    }

    class ActivityResultCode {
        static final int CAMERA = 201;
    }
}
