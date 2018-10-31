package uk.ac.shef.com4510;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private List<DiskImage> images = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public GalleryRecyclerViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setImages(final List<DiskImage> newImages) {
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
        DiskImage image = images.get(position);
        holder.textView.setText(image.getTitle());
        holder.imageView.setImageDrawable(null);
        new SetImageViewSourceTask().execute(new SetImageViewSourceTask.Parameters(holder, image.getPath(), this));
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void holderBitmapReady(ViewHolder viewHolder) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        Bitmap bitmap;

        ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail);
            this.textView = view.findViewById(R.id.title);
            this.bitmap = null;
        }
    }
}
