package uk.ac.shef.com4510.details;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import uk.ac.shef.com4510.ImageRepository;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.support.BitmapLoader;

public class DetailsViewModel extends AndroidViewModel {
    protected final ImageRepository imageRepository;
    private MediatorLiveData<Image> image = new MediatorLiveData<>();
    private LiveData<Image> imageSource;
    private BitmapLoader bitmapLoader = new BitmapLoader();

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public LiveData<Image> getImage() {
        return image;
    }
    public LiveData<Bitmap> getBitmap() {
        return bitmapLoader.bitmap;
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
            bitmapLoader.setSourcePath(newImage.getPath());
            if(!newImage.hasExif()){
                newImage = newImage.withExif();
                imageRepository.update(newImage);
            }
            image.setValue(newImage);
        });
    }
}
