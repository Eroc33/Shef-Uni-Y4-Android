package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
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
        Calendar searchDate = search.getDate();
        Calendar startDate = null;
        Calendar endDate = null;
        if(searchDate != null){
            startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR,search.getDate().get(Calendar.YEAR));
            startDate.set(Calendar.MONTH,search.getDate().get(Calendar.MONTH));
            startDate.set(Calendar.DAY_OF_MONTH,search.getDate().get(Calendar.DAY_OF_MONTH));
            startDate.set(Calendar.HOUR_OF_DAY,0);
            startDate.set(Calendar.MINUTE,0);
            startDate.set(Calendar.SECOND,0);
            startDate.set(Calendar.MILLISECOND,0);
            endDate = (Calendar) startDate.clone();
            endDate.add(Calendar.HOUR,24);
        }
        return app.getImageDb().imageDao().search(search.getTitle(), search.getDescription(), startDate, endDate);
    }

    public void update(Image image) {
        AsyncTask.execute(()-> app.getImageDb().imageDao().updateSync(image));
    }

    public LiveData<List<Image>> findExact(List<String> paths) {
        return app.getImageDb().imageDao().findAll(paths);
    }
}
