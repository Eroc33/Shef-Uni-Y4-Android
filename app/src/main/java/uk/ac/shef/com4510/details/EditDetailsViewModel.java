package uk.ac.shef.com4510.details;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.support.ObserverUtils;

public class EditDetailsViewModel extends DetailsViewModel {
    final public MutableLiveData<String> title = new MutableLiveData<>();
    final public MutableLiveData<String> description = new MutableLiveData<>();

    public EditDetailsViewModel(@NonNull Application application) {
        super(application);
        ObserverUtils.observeOneshot(getImage(),(image)->{
            title.setValue(image.getTitle());
            description.setValue(image.getDescription());
        });
    }

    public void commitEdit(){
        Image image = getImage().getValue();
        if (image == null) {
            //TODO: handle this here, or catch it where commitEdit is called, though it should likely never occur
            throw new NullPointerException("image is null in commitEdit");
        }
        imageRepository.update(image.withTitleAndDescription(title.getValue(),description.getValue()));
    }
}
