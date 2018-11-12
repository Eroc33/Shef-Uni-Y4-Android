package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.SetImageViewSourceTask;
import uk.ac.shef.com4510.databinding.ActivityDetailsBinding;
import uk.ac.shef.com4510.support.CoordinateFormatter;

public class DetailsActivity extends AppCompatActivity {
    private DetailsViewModel viewModel;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setLifecycleOwner(this);

        imageView = this.findViewById(R.id.thumbnail);

        //TODO: handle case that path is not set
        String path = getIntent().getStringExtra("imagePath");
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        viewModel.setPath(path);
        viewModel.getImage().observe(this, image -> {
            binding.setImage(image);
            binding.setCoordinateFormatter(new CoordinateFormatter(getResources()));
            binding.executePendingBindings();
            //TODO: find a way to set image source from databinding, but allow cancellation
            new SetImageViewSourceTask().execute(
                    new SetImageViewSourceTask.Parameters(
                            imageView,
                            image.getPath())
            );
        });
    }
}
