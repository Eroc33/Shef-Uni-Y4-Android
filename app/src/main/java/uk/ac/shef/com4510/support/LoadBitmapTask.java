package uk.ac.shef.com4510.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Used to load a @{Bitmap} on a background thread to avoid freezing the ui.
 */
public class LoadBitmapTask extends AsyncTask<LoadBitmapTask.Parameters, Void, LoadBitmapTask.Parameters[]> {

    private boolean complete;

    @Override
    protected LoadBitmapTask.Parameters[] doInBackground(Parameters... params) {
        for (Parameters param : params) {
            param.bitmap = BitmapFactory.decodeFile(param.path);
        }
        return params;
    }

    @Override
    protected void onPostExecute(LoadBitmapTask.Parameters[] parameters) {
        for (LoadBitmapTask.Parameters parameter : parameters) {
            parameter.callback.accept(parameter.bitmap);
            parameter.bitmap = null;
        }
        complete = true;
    }

    /**
     * @return whether the task has finished running
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Used to receive a bitmap loaded by a @link{LoadBitmapTask}
     */
    public interface Callback{
        void accept(Bitmap bitmap);
    }

    public static class Parameters {
        private final Callback callback;
        private final String path;
        private Bitmap bitmap;

        public Parameters(Callback callback, String path) {
            this.callback = callback;
            this.path = path;
        }
    }
}
