package uk.ac.shef.oak.com4510.search;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.databinding.SearchFragmentBinding;

/**
 * Allows the user to input search parameters, and then starts a fragment to display them
 */
public class SearchFragment extends Fragment implements SearchActions {
    private static final String TAG = "SearchFragment";
    private SearchViewModel viewModel;
    private SearchFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setActions(this);
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        binding.setViewmodel(viewModel);
        binding.executePendingBindings();
        return binding.getRoot();
    }

    @Override
    public void doSearch() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("search", viewModel.makeSearch());
        Navigation.findNavController(getView()).navigate(R.id.action_searchFragment_to_galleryFragment, bundle);
    }
}
