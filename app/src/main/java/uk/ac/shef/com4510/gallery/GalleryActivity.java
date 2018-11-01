package uk.ac.shef.com4510.gallery;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import uk.ac.shef.com4510.R;

public class GalleryActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private RecyclerView recyclerView;
    private GalleryViewModel viewModel;
    private GalleryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else {
            completeCreation();
        }
    }

    private void completeCreation() {
        viewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        adapter = new GalleryRecyclerViewAdapter(getApplicationContext());
        viewModel.getImages().observe(this, allImages -> adapter.setImages(allImages));
        recyclerView = findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults){
        completeCreation();
    }
}
