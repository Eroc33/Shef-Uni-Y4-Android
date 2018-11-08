package uk.ac.shef.com4510.gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;

public class GalleryViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;

    public LiveData<List<Image>> getImages() {
        return imageRepository.getAllImages();
    }
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }
}
