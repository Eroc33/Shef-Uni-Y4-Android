package uk.ac.shef.com4510.details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;

public class DetailsViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;
    private MediatorLiveData<Image> image = new MediatorLiveData<Image>();
    private LiveData<Image> imageSource;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public LiveData<Image> getImage() {
        return image;
    }

    public void setPath(String path) {
        if (imageSource != null) {
            image.removeSource(imageSource);
        }
        imageSource = imageRepository.getImage(path);
        image.addSource(imageSource, newImage -> {
            if (newImage == null) {
                return;
            }
            image.setValue(newImage);
        });
    }
}
