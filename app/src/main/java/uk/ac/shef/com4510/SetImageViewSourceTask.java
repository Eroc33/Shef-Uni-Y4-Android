package uk.ac.shef.com4510;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class SetImageViewSourceTask extends AsyncTask<SetImageViewSourceTask.Parameters, Void, SetImageViewSourceTask.Parameters[]> {

    private boolean complete;

    @Override
    protected SetImageViewSourceTask.Parameters[] doInBackground(Parameters... params) {
        for (Parameters param : params) {
            param.bitmap = BitmapFactory.decodeFile(param.path);
        }
        return params;
    }

    @Override
    protected void onPostExecute(SetImageViewSourceTask.Parameters[] parameters) {
        for (SetImageViewSourceTask.Parameters parameter : parameters) {
            parameter.imageView.setImageBitmap(parameter.bitmap);
        }
        complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public static class Parameters {
        private final ImageView imageView;
        private final String path;
        private Bitmap bitmap;

        public Parameters(ImageView imageView, String path) {
            this.imageView = imageView;
            this.path = path;
        }
    }
}
