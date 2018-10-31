package uk.ac.shef.com4510;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SetImageViewSourceTask extends AsyncTask<SetImageViewSourceTask.Parameters, Void, List<GalleryRecyclerViewAdapter.ViewHolder>> {

    @Override
    protected List<GalleryRecyclerViewAdapter.ViewHolder> doInBackground(Parameters... params) {
        List<GalleryRecyclerViewAdapter.ViewHolder> holders = new ArrayList<>(params.length);
        for (Parameters param : params) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            if (param.viewHolder.bitmap != null) {
                opts.inBitmap = param.viewHolder.bitmap;
            }
            param.viewHolder.bitmap = BitmapFactory.decodeFile(param.path, opts);
            holders.add(param.viewHolder);
        }
        return holders;
    }

    @Override
    protected void onPostExecute(List<GalleryRecyclerViewAdapter.ViewHolder> readyHolders) {
        for (GalleryRecyclerViewAdapter.ViewHolder holder : readyHolders) {
            holder.imageView.setImageBitmap(holder.bitmap);
        }
    }

    public static class Parameters {
        private GalleryRecyclerViewAdapter.ViewHolder viewHolder;
        private String path;
        private GalleryRecyclerViewAdapter adapter;

        public Parameters(GalleryRecyclerViewAdapter.ViewHolder viewHolder, String path, GalleryRecyclerViewAdapter adapter) {
            this.viewHolder = viewHolder;
            this.path = path;
            this.adapter = adapter;
        }
    }
}
