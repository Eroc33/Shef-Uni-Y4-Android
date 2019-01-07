package uk.ac.shef.com4510.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Used to load a @{Bitmap} on a background thread to avoid freezing the ui.
 */
public class LoadBitmapTask extends AsyncTask<LoadBitmapTask.Parameters, Void, LoadBitmapTask.Parameters[]> {

    private boolean complete;

    private static final int THUMBNAIL_WIDTH = 256;
    private static final int THUMBNAIL_HEIGHT = 256;

    private static int calculateDownsampleSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > THUMBNAIL_HEIGHT
                && (halfWidth / inSampleSize) > THUMBNAIL_WIDTH) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    @Override
    protected LoadBitmapTask.Parameters[] doInBackground(Parameters... params) {
        for (Parameters param : params) {
            if(param.downsample) {
                BitmapFactory.Options options =new BitmapFactory.Options();
                options.inSampleSize = calculateDownsampleSize(param.path);
                param.bitmap = BitmapFactory.decodeFile(param.path,options);
            }else {
                param.bitmap = BitmapFactory.decodeFile(param.path);
            }
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
        private boolean downsample;

        public Parameters(Callback callback, String path, boolean downsample) {
            this.callback = callback;
            this.path = path;
            this.downsample = downsample;
        }
    }
}
