package uk.ac.shef.com4510.gallery;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.navigation.Navigation;
import uk.ac.shef.com4510.R;
import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.databinding.GalleryItemBinding;

public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GalleryRecyclerViewAdapter";
    private final GalleryViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private AsyncListDiffer<Image> differ = new AsyncListDiffer<>(this,DIFF_CALLBACK);

    private static final DiffUtil.ItemCallback<Image> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<Image>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Image oldImage, @NonNull Image newImage) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldImage.getPath().equals(newImage.getPath());
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull Image oldImage, @NonNull Image newImage) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldImage.equals(newImage);
        }
    };

    public GalleryRecyclerViewAdapter(Context context, GalleryViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel.getImages().observe(lifecycleOwner, this::setImages);
    }

    private void setImages(final List<Image> newImages) {
        differ.submitList(newImages);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, lifecycleOwner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = differ.getCurrentList().get(position);
        holder.rebind(image, (view) -> this.showDetailView(view, image.getPath()));
    }

    private void showDetailView(View view, String path) {
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", path);
        Navigation.findNavController(view).navigate(R.id.action_galleryFragment_to_detailsActivity, bundle);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryItemBinding binding;

        ViewHolder(LayoutInflater inflater, ViewGroup parent, LifecycleOwner lifecycleOwner) {
            this(GalleryItemBinding.inflate(inflater, parent, false),lifecycleOwner);
        }

        ViewHolder(GalleryItemBinding binding, LifecycleOwner lifecycleOwner) {
            super(binding.getRoot());
            binding.setLifecycleOwner(lifecycleOwner);
            this.binding = binding;
        }

        void rebind(Image image, View.OnClickListener onSelected) {
            binding.setImage(image);
            binding.setSelectedListener(onSelected);
            binding.executePendingBindings();
        }
    }
}
