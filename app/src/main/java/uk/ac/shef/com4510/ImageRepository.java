package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import uk.ac.shef.com4510.data.CalendarConverters;
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
        String title = search.getTitle();
        String description = search.getDescription();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        if (title != null){
            title = String.format("%%%s%%",title);
        }

        if (description != null){
            description = String.format("%%%s%%",description);
        }

        startDate.set(Calendar.YEAR, search.getStartDate().get(Calendar.YEAR));
        startDate.set(Calendar.MONTH, search.getStartDate().get(Calendar.MONTH));
        startDate.set(Calendar.DAY_OF_MONTH, search.getStartDate().get(Calendar.DAY_OF_MONTH));
        startDate.set(Calendar.HOUR_OF_DAY,0);
        startDate.set(Calendar.MINUTE,0);
        startDate.set(Calendar.SECOND,0);
        startDate.set(Calendar.MILLISECOND,0);

        endDate.set(Calendar.YEAR, search.getEndDate().get(Calendar.YEAR));
        endDate.set(Calendar.MONTH, search.getEndDate().get(Calendar.MONTH));
        endDate.set(Calendar.DAY_OF_MONTH, search.getEndDate().get(Calendar.DAY_OF_MONTH));
        endDate.set(Calendar.HOUR_OF_DAY,0);
        endDate.set(Calendar.MINUTE,0);
        endDate.set(Calendar.SECOND,0);
        endDate.set(Calendar.MILLISECOND,0);

        Log.d("ImageRepository", String.format("searching with title: `%s` and description: `%s`",title,description));
        return app.getImageDb().imageDao().search(title, description, CalendarConverters.calendarToUnixTimestamp(startDate), CalendarConverters.calendarToUnixTimestamp(endDate));
    }

    public void update(Image image) {
        AsyncTask.execute(()-> app.getImageDb().imageDao().updateSync(image));
    }

    public LiveData<List<Image>> findExact(List<String> paths) {
        return app.getImageDb().imageDao().findAll(paths);
    }
}
