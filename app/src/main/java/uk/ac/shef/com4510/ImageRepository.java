package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import uk.ac.shef.com4510.data.Image;

public class ImageRepository {
    private static final String TAG = "ImageRepository";
    private final Application app;


    public ImageRepository(Application app) {
        this.app = app;
    }

    public LiveData<List<Image>> getAllImages() {
        return ((uk.ac.shef.com4510.Application) app).getImageDb().imageDao().allImages();
    }

    public LiveData<Image> getImage(String path) {
        return ((uk.ac.shef.com4510.Application) app).getImageDb().imageDao().getImage(path);
    }
}
