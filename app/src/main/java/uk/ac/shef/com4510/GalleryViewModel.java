package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

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
