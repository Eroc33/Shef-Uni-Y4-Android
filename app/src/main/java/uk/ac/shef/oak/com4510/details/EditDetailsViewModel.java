package uk.ac.shef.oak.com4510.details;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.Calendar;

import uk.ac.shef.oak.com4510.data.CalendarConverters;
import uk.ac.shef.oak.com4510.data.Image;
import uk.ac.shef.oak.com4510.support.ObserverUtils;

/**
 * Provides mutable livedata for all the metadata properties the user can edit on an image
 */
public class EditDetailsViewModel extends DetailsViewModel {
    final public MutableLiveData<String> title = new MutableLiveData<>();
    final public MutableLiveData<String> description = new MutableLiveData<>();
    final public MutableLiveData<Double> latitude = new MutableLiveData<>();
    final public MutableLiveData<Double> longitude = new MutableLiveData<>();
    final public MutableLiveData<Calendar> date = new MutableLiveData<>();

    public EditDetailsViewModel(@NonNull Application application) {
        super(application);
        ObserverUtils.observeOneshot(getImage(),(image)->{
            title.setValue(image.getTitle());
            description.setValue(image.getDescription());
            latitude.setValue(image.getLatitude());
            longitude.setValue(image.getLongitude());
            date.setValue(image.getDate());
        });
    }

    public void commitEdit() {
        Image image = getImage().getValue();
        if (image == null) {
            //TODO: handle this here, or catch it where commitEdit is called, though it should likely never occur
            throw new NullPointerException("image is null in commitEdit");
        }

        imageRepository.update(image.withBasicInfo(
                title.getValue(),
                description.getValue(),
                latitude.getValue(),
                longitude.getValue(),
                CalendarConverters.calendarToUnixTimestamp(date.getValue())));
    }
}
