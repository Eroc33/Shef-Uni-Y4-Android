package uk.ac.shef.com4510.support;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;

import uk.ac.shef.com4510.LoadBitmapTask;

public class BitmapLoader {
    private LoadBitmapTask task;
    private String path;

    public LiveData<Bitmap> bitmap = new MutableLiveData<>();

    public synchronized void setSourcePath(String path){
        if (this.path == null && path == null){
            return;
        }
        if (this.path != null && this.path.equals(path)){
            return;//just let our existing task keep loading
        }
        this.path = path;
        if (task != null) {
            task.cancel(true);
        }
        if (path == null) {
            outputBitmap(null);
            return;
        }
        task = new LoadBitmapTask();
        task.execute(new LoadBitmapTask.Parameters(this::outputBitmap,path));
    }

    public synchronized void cancel(){
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        outputBitmap(null);
    }

    private synchronized void outputBitmap(Bitmap bitmap) {
        ((MutableLiveData<Bitmap>)this.bitmap).postValue(bitmap);
    }
}
