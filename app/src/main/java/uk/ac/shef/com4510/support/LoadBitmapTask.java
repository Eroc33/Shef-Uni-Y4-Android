package uk.ac.shef.com4510.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Used to load a @{Bitmap} on a background thread to avoid freezing the ui.
 */
public class LoadBitmapTask extends AsyncTask<LoadBitmapTask.Parameters, Void, LoadBitmapTask.Parameters[]> {

    private boolean complete;

    //TODO: Arguably these should scale with screen density.
    private static final int THUMBNAIL_WIDTH = 512;
    private static final int THUMBNAIL_HEIGHT = 512;

    /**
     * Calculates per image downsampling
     * @param path the file location for the image of which to calculate the sample size
     * @return
     */
    private static int calculateDownsampleSize(String path) {
        //get image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        // calculate a power of two sample size that makes the image approximately
        // THUMBNAIL_HEIGHTxTHUMBNAIL_WIDTH in size
        while (height > THUMBNAIL_HEIGHT
                && width > THUMBNAIL_WIDTH) {
            inSampleSize *= 2;
            height /= 2;
            width /= 2;
        }

        return inSampleSize;
    }

    @Override
    protected LoadBitmapTask.Parameters[] doInBackground(Parameters... params) {
        for (Parameters param : params) {
            if(param.downsample) {
                BitmapFactory.Options options = new BitmapFactory.Options();
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
