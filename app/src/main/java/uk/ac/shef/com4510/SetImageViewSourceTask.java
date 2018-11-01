package uk.ac.shef.com4510;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class SetImageViewSourceTask extends AsyncTask<SetImageViewSourceTask.Parameters, Void, SetImageViewSourceTask.Parameters[]> {

    @Override
    protected SetImageViewSourceTask.Parameters[] doInBackground(Parameters... params) {
        for (Parameters param : params) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            if (param.bitmap != null) {
                opts.inBitmap = param.bitmap;
            }
            param.bitmap = BitmapFactory.decodeFile(param.path, opts);
        }
        return params;
    }

    @Override
    protected void onPostExecute(SetImageViewSourceTask.Parameters[] parameters) {
        for (SetImageViewSourceTask.Parameters parameter : parameters) {
            parameter.imageView.setImageBitmap(parameter.bitmap);
            parameter.postBitmap.consume(parameter.bitmap);
        }
    }

    public interface BitmapConsumer {
        void consume(Bitmap bitmap);
    }

    public static class Parameters {
        private ImageView imageView;
        private Bitmap bitmap;
        private String path;
        private BitmapConsumer postBitmap;

        public Parameters(ImageView imageView, Bitmap bitmap, String path, BitmapConsumer postBitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.path = path;
            this.postBitmap = postBitmap;
        }
    }
}
