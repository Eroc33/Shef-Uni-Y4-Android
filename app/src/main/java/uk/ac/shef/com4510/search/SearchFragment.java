package uk.ac.shef.com4510.search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.shef.com4510.databinding.SearchFragmentBinding;

public class SearchFragment extends Fragment implements SearchActions {
    private static final String TAG = "SearchFragment";
    private DatePickerTextFieldManager datePickerTextFieldManager;
    private SearchViewModel viewModel;
    private SearchFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setActions(this);
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        datePickerTextFieldManager = new DatePickerTextFieldManager(requireContext(), viewModel.date, viewModel.datestring);
        binding.setViewmodel(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
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
