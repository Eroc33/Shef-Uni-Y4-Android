package uk.ac.shef.com4510.details;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.databinding.EditDetailsFragmentBinding;

public class EditDetailsActivity extends AppCompatActivity implements EditDetailsActions {

    private EditDetailsViewModel viewModel;
    private EditDetailsFragmentBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.edit_details_fragment);

        viewModel = ViewModelProviders.of(this).get(EditDetailsViewModel.class);
        viewModel.setPath(getIntent().getStringExtra("imagePath"));

        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        binding.setActions(this);
        binding.executePendingBindings();
    }


    @Override
    public void commitEdit() {
        viewModel.commitEdit();
        finish();
    }
}
