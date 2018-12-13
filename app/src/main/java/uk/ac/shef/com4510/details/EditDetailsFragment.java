package uk.ac.shef.com4510.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.databinding.EditDetailsFragmentBinding;

public class EditDetailsFragment extends Fragment implements EditDetailsActions {

    private EditDetailsViewModel viewModel;
    private EditDetailsFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(EditDetailsViewModel.class);
        viewModel.setPath(requireActivity().getIntent().getStringExtra("imagePath"));

        binding = EditDetailsFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setActions(this);
        binding.setViewmodel(viewModel);
        binding.executePendingBindings();

        return binding.getRoot();
    }

    @Override
    public void editDetails() {
        // TODO: Finish this!
        LiveData<Image> img = viewModel.getImage();
        img.observe(this, image -> {
            if (image != null) {
                Image newImage = new Image(
                        image.getPath(),
                        image.getThumbnailPath(),
                        viewModel.getTitle(),
                        image.getLatitude(),
                        image.getLongitude(),
                        viewModel.getDescription(),
                        image.getDate(),
                        image.getIso(),
                        image.getFstop(),
                        image.getFocalLength(),
                        image.hasExif()
                );
            }
        });
    }

}
