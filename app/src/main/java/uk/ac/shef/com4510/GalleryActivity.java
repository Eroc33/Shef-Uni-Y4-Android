package uk.ac.shef.com4510;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private RecyclerView recyclerView;
    private ImageRepository imageRepository;
    private GalleryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        completeCreation();
    }

    private void completeCreation() {
        adapter = new GalleryRecyclerViewAdapter(getApplicationContext());
        imageRepository = new ImageRepository(getApplication());
        imageRepository.getAllImages().observe(this, new Observer<List<DiskImage>>() {
            @Override
            public void onChanged(@Nullable List<DiskImage> allImages) {
                adapter.setImages(allImages);
            }
        });
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults){
        completeCreation();
    }
}
