package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
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

        String path = getIntent().getStringExtra("imagePath");
        //TODO: handle case that id is not set (i.e. -1)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        viewModel.setPath(path);
        viewModel.getImage().observe(this, image -> {
            titleView.setText(image.getTitle());
            new SetImageViewSourceTask().execute(
                    new SetImageViewSourceTask.Parameters(
                            imageView,
                            bitmap,
                            image.getPath(),
                            this::setBitmap)
            );
            coordsView.setText(String.format("%f,%f", image.getLatitude(), image.getLongitude()));
        });
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
