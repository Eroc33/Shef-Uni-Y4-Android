package uk.ac.shef.com4510.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.SetImageViewSourceTask;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.details.DetailsActivity;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = GalleryRecyclerViewAdapter.class.getCanonicalName();
    private final Context context;
    private List<Image> images = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public GalleryRecyclerViewAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setImages(final List<Image> newImages) {
        //TODO: if performance is poor consider using DiffUtil here
        images = newImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //TODO: cancel image load task if we rebind a holder while it's loading
        Image image = images.get(position);
        if(holder.loadTask != null){
            Log.d(TAG, "Canceling load task");
            holder.loadTask.cancel(true);
        }
        holder.textView.setText(image.getTitle());
        holder.imageView.setImageDrawable(null);
        holder.itemView.setOnClickListener(view -> {
            this.showDetailView(image.getPath());
        });
        holder.loadTask = new SetImageViewSourceTask();
        holder.loadTask.execute(
                new SetImageViewSourceTask.Parameters(
                        holder.getImageView(),
                        holder.getBitmap(),
                        image.getPath(),
                        holder::setBitmap)
        );
    }

    private void showDetailView(String path) {
        Intent startActivityIntent = new Intent(context, DetailsActivity.class);
        startActivityIntent.putExtra("imagePath", path);
        context.startActivity(startActivityIntent);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void holderBitmapReady(ViewHolder viewHolder) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SetImageViewSourceTask loadTask;
        ImageView imageView;
        TextView textView;
        Bitmap bitmap;

        ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail);
            this.textView = view.findViewById(R.id.title);
            this.bitmap = null;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public ImageView getImageView() {
            return this.imageView;
        }
    }
}
