package uk.ac.shef.com4510;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageRepository imageRepository;
    private GalleryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GalleryRecyclerViewAdapter(getApplicationContext());
        imageRepository = new ImageRepository(getApplication());
        imageRepository.getExternalImages().observe(this, new Observer<List<DiskImage>>() {
            @Override
            public void onChanged(@Nullable List<DiskImage> diskImages) {
                Context context = getApplicationContext();
                adapter.setImages(diskImages);
            }
        });
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
    }
}
