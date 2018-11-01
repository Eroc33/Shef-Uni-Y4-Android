package uk.ac.shef.com4510.gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.DiskImage;
import uk.ac.shef.com4510.ImageRepository;

public class GalleryViewModel extends AndroidViewModel {
    ImageRepository imageRepository;
    public LiveData<List<DiskImage>> getImages(){
        return imageRepository.getAllImages();
    }
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }
}
