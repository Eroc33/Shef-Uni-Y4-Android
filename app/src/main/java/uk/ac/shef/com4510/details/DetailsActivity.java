package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.ActivityDetailsBinding;
import uk.ac.shef.com4510.support.CoordinateFormatter;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setCoordinateFormatter(new CoordinateFormatter(getResources()));
        binding.setLifecycleOwner(this);

        //TODO: handle case that path is not set
        String path = getIntent().getStringExtra("imagePath");
        DetailsViewModel viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        viewModel.setPath(path);
        viewModel.getImage().observe(this, image -> {
            binding.setImage(image);
            binding.executePendingBindings();
        });
    }
}
