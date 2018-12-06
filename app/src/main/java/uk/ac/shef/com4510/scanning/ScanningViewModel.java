package uk.ac.shef.com4510.scanning;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.search.Search;

public class ScanningViewModel extends AndroidViewModel {
    public final MutableLiveData<Boolean> scanningImages = new MutableLiveData<>();

    public ScanningViewModel(@NonNull Application application) {
        super(application);
    }
}
