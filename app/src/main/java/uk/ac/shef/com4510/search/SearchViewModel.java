package uk.ac.shef.com4510.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * SSearch parameters the user can input in @link{SearchFragment}
 */
public class SearchViewModel extends AndroidViewModel {

    final public MutableLiveData<String> title = new MutableLiveData<>();
    final public MutableLiveData<String> description = new MutableLiveData<>();
    final public MutableLiveData<Calendar> date = new MutableLiveData<>();

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public Search makeSearch() {
        return new Search(title.getValue(), description.getValue(), date.getValue());
    }
}
