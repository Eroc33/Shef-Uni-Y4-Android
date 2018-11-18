package uk.ac.shef.com4510.gallery;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.databinding.GalleryItemBinding;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = GalleryRecyclerViewAdapter.class.getCanonicalName();
    private final GalleryViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private List<Image> images = new ArrayList<>();

    public GalleryRecyclerViewAdapter(Context context, GalleryViewModel viewModel, LifecycleOwner lifecycleOwner) {
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
        holder.rebind(image, (view) -> this.showDetailView(view, image.getPath()));
    }

    private void showDetailView(View view, String path) {
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", path);
        Navigation.findNavController(view).navigate(R.id.action_galleryFragment_to_detailsActivity, bundle);
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

        void rebind(Image image, View.OnClickListener onSelected) {
            binding.setImage(image);
            binding.setSelectedListener(onSelected);
            binding.executePendingBindings();
        }
    }
}
