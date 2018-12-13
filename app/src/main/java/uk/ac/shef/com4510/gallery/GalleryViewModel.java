package uk.ac.shef.com4510.gallery;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.search.Search;

public class GalleryViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;

    private LiveData<List<Image>> images;

    public final MutableLiveData<Boolean> scanningImages = new MutableLiveData<>();

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
        images = imageRepository.getAllImages();
    }

    public LiveData<List<Image>> getImages() {
        return images;
    }

    public void applySearch(Search search) {
        images = imageRepository.search(search);
    }

    public void withExactImages(List<String> paths){ images = imageRepository.findExact(paths); }
}
