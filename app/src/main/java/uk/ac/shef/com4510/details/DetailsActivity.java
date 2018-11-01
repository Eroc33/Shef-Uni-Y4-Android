package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.SetImageViewSourceTask;

public class DetailsActivity extends AppCompatActivity {
    DetailsViewModel viewModel;
    private TextView titleView;
    private TextView coordsView;
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        titleView = this.findViewById(R.id.title);
        imageView = this.findViewById(R.id.thumbnail);
        coordsView = this.findViewById(R.id.coordinates);

        Uri uri = getIntent().getParcelableExtra("imageUri");
        long id = getIntent().getLongExtra("imageId", -1);
        //TODO: handle case that id is not set (i.e. -1)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        viewModel.setStorageUri(uri);
        viewModel.setId(id);
        viewModel.getImage().observe(this, diskImage -> {
            titleView.setText(diskImage.getTitle());
            new SetImageViewSourceTask().execute(
                    new SetImageViewSourceTask.Parameters(
                            imageView,
                            bitmap,
                            diskImage.getPath(),
                            this::setBitmap)
            );
            coordsView.setText(String.format("%f,%f", diskImage.getLatitude(), diskImage.getLongitude()));
        });
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
