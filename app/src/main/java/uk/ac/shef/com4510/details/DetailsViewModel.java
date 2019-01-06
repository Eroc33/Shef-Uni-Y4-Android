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

/**
 * Provides an image and bitmap from the database id (the path).
 */
public class DetailsViewModel extends AndroidViewModel {
    protected final ImageRepository imageRepository;
    private MediatorLiveData<Image> image = new MediatorLiveData<>();
    private LiveData<Image> imageSource;
    //Used to provide the bitmap
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
            //if image is null, or unchanged don't reload stuff
            if (newImage == null || newImage.equals(image.getValue())) {
                return;
            }
            //start loading the bimap
            bitmapLoader.setSourcePath(newImage.getPath());
            //ensure exif is loaded
            if(!newImage.hasExif()){
                newImage = newImage.withExif();
                imageRepository.update(newImage);
            }
            image.setValue(newImage);
        });
    }
}
