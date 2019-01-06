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
import uk.ac.shef.com4510.support.ProxyLiveData;

/**
 * Provides an image and bitmap from the database id (the path).
 */
public class DetailsViewModel extends AndroidViewModel {
    private final ImageRepository imageRepository;
    private final MediatorLiveData<Image> image = new MediatorLiveData<>();
    private final ProxyLiveData<Image> imageSource = new ProxyLiveData<>();
    //Used to provide the bitmap
    private final BitmapLoader bitmapLoader = new BitmapLoader();

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
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

    public LiveData<Image> getImage() {
        return image;
    }
    public LiveData<Bitmap> getBitmap() {
        return bitmapLoader.bitmap;
    }

    public void setPath(String path) {
        imageSource.setSource(imageRepository.getImage(path));
    }
}
