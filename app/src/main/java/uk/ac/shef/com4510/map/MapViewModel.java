package uk.ac.shef.com4510.map;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;

public class MapViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;

    private MediatorLiveData<List<Image>> filteredImages = new MediatorLiveData<>();

    public MapViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
        filteredImages.addSource(imageRepository.getAllImages(), images -> {
            if (images == null) {
                filteredImages.postValue(null);
                return;
            }
            //Unfortunately we can't use the streams api for filtering due to the minimum targeted android version
            List<Image> filtered = new ArrayList<>(images.size());
            for (Image image : images) {
                if (image.getLatitude() == 0.0 && image.getLongitude() == 0.0) {
                    continue;
                }
                filtered.add(image);
            }
            filteredImages.postValue(filtered);
        });
    }

    public LiveData<List<Image>> getImages() {
        return filteredImages;
    }
}
