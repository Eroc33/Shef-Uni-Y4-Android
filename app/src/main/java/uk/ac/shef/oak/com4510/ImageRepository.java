package uk.ac.shef.oak.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.ac.shef.oak.com4510.data.CalendarConverters;
import uk.ac.shef.oak.com4510.data.Image;
import uk.ac.shef.oak.com4510.data.ImageDao;
import uk.ac.shef.oak.com4510.search.Search;

public class ImageRepository {
    private static final String TAG = "ImageRepository";
    private final uk.ac.shef.oak.com4510.Application app;

    public void removeImage(Image image){
        List<Image> images = new ArrayList<>();
        images.add(image);
        removeImages(images);
    }

    public void removeImages(List<Image> images){
        //noinspection unchecked
        new RemoveImagesTask(app.getImageDb().imageDao()).execute(images);
    }

    private LiveData<List<Image>> filterAndQueueDeletions(LiveData<List<Image>> imageLiveData){
        MediatorLiveData<List<Image>> filteredLiveData = new MediatorLiveData<>();
        filteredLiveData.addSource(imageLiveData,(images)->{
            List<Image> deleted = new ArrayList<>();
            for (Image image : images){
                if (!new File(image.getPath()).exists()){
                    deleted.add(image);
                }
            }
            images.removeAll(deleted);
            removeImages(deleted);
            filteredLiveData.setValue(images);
        });
        return filteredLiveData;
    }


    public ImageRepository(Application app) {
        this.app = (uk.ac.shef.oak.com4510.Application) app;
    }

    public LiveData<List<Image>> getAllImages() {
        return filterAndQueueDeletions(app.getImageDb().imageDao().allImages());
    }

    public LiveData<Image> getImage(long id) {
        return app.getImageDb().imageDao().getImage(id);
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
        return filterAndQueueDeletions(app.getImageDb().imageDao().search(title, description, CalendarConverters.calendarToUnixTimestamp(startDate), CalendarConverters.calendarToUnixTimestamp(endDate)));
    }

    public void update(Image image) {
        AsyncTask.execute(()-> app.getImageDb().imageDao().updateSync(image));
    }

    public LiveData<List<Image>> findExact(long[] ids) {
        return filterAndQueueDeletions(app.getImageDb().imageDao().findAll(ids));
    }

    private static class RemoveImagesTask extends AsyncTask<List<Image>, Void, Void> {
        private ImageDao dao;

        RemoveImagesTask(ImageDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final List<Image>... params) {
            dao.removeSync(params[0]);
            return null;
        }
    }
}
