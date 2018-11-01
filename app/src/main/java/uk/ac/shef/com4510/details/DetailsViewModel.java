package uk.ac.shef.com4510.details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import uk.ac.shef.com4510.DiskImage;
import uk.ac.shef.com4510.ImageRepository;

public class DetailsViewModel extends AndroidViewModel {
    private Uri storageUri = null;
    private int id = -1;
    private ImageRepository imageRepository;
    private MutableLiveData<DiskImage> image;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
        image = new MutableLiveData<>();
    }

    public void setStorageUri(Uri uri) {
        this.storageUri = uri;
        if (storageUri != null && id != -1) {
            image.setValue(imageRepository.getImage(storageUri, id));
        }
    }

    public void setId(int id) {
        this.id = id;
        if (storageUri != null && id != -1) {
            image.setValue(imageRepository.getImage(storageUri, id));
        }
    }

    public LiveData<DiskImage> getImage() {
        return image;
    }
}
