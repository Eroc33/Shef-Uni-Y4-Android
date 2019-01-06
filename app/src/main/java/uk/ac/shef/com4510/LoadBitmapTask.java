package uk.ac.shef.com4510;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

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

    public boolean isComplete() {
        return complete;
    }

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