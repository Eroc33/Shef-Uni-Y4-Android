package uk.ac.shef.com4510.details;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import uk.ac.shef.com4510.data.Image;

public class EditDetailsViewModel extends DetailsViewModel {
    final public MutableLiveData<String> title = new MutableLiveData<>();
    final public MutableLiveData<String> description = new MutableLiveData<>();

    public EditDetailsViewModel(@NonNull Application application) {
        super(application);
        Image image = getImage().getValue();
        title.setValue(image.getTitle());
        description.setValue(image.getDescription());
    }

    public String getTitle() { return title.getValue(); }
    public String getDescription() { return description.getValue(); }
}
