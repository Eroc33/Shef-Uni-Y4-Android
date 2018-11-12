package uk.ac.shef.com4510.map;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;

public class MapViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;

    public LiveData<List<Image>> getImages() {
        return imageRepository.getAllImages();
    }

    public MapViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }
}
