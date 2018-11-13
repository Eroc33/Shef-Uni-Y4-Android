package uk.ac.shef.com4510.gallery;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.databinding.GalleryItemBinding;
import uk.ac.shef.com4510.details.DetailsActivity;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = GalleryRecyclerViewAdapter.class.getCanonicalName();
    private final Context context;
    private final GalleryViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private List<Image> images = new ArrayList<>();

    public GalleryRecyclerViewAdapter(Context context, GalleryViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel.getImages().observe(lifecycleOwner, this::setImages);
    }

    private void setImages(final List<Image> newImages) {
        //TODO: if performance is poor consider using DiffUtil here
        images = newImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GalleryItemBinding binding = GalleryItemBinding.inflate(inflater, parent, false);
        binding.setLifecycleOwner(lifecycleOwner);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);
        holder.rebind(image, () -> this.showDetailView(image.getPath()));
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryItemBinding binding;

        ViewHolder(GalleryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void rebind(Image image, Runnable onSelected) {
            binding.setImage(image);
            binding.setSelectedListener(onSelected);
            binding.executePendingBindings();
        }
    }
}
