package uk.ac.shef.com4510.search;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import uk.ac.shef.com4510.databinding.ActivitySearchBinding;

import uk.ac.shef.com4510.R;

public class SearchActivity extends AppCompatActivity implements SearchActions{
    private static final String TAG = "SearchActivity";
    private DatePickerTextFieldManager datePickerTextFieldManager;
    private SearchViewModel viewModel;
    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setLifecycleOwner(this);
        binding.setActions(this);
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        datePickerTextFieldManager = new DatePickerTextFieldManager(this,viewModel.date,viewModel.datestring);
        binding.setViewmodel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void showDatePicker(View sourceView, boolean show) {
        datePickerTextFieldManager.showDatePicker(sourceView, show);
    }

    @Override
    public void doSearch() {
        //TODO
    }
}
