package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.search.Search;

public class ImageRepository {
    private static final String TAG = "ImageRepository";
    private final uk.ac.shef.com4510.Application app;


    public ImageRepository(Application app) {
        this.app = (uk.ac.shef.com4510.Application) app;
    }

    public LiveData<List<Image>> getAllImages() {
        return app.getImageDb().imageDao().allImages();
    }

    public LiveData<Image> getImage(String path) {
        return app.getImageDb().imageDao().getImage(path);
    }

    public LiveData<List<Image>> search(Search search) {
        return app.getImageDb().imageDao().search(search.getTitle(), search.getDescription(), search.getDate());
    }
}
