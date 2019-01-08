package uk.ac.shef.com4510.gallery;

import android.arch.lifecycle.LifecycleOwner;
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
import uk.ac.shef.com4510.support.BitmapLoader;

/**
 * Adapts a @link{GalleryViewModel} into a @link{RecyclerView} of the titles and thumbnails of the
 * images from the viewmodel.
 */
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
            return oldImage.getId() == newImage.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull Image oldImage, @NonNull Image newImage) {
            // for the gallery we only care about the path and title changing
            return oldImage.getPath().equals(newImage.getPath()) && oldImage.getTitle().equals(newImage.getTitle());
        }
    };

    public GalleryRecyclerViewAdapter(GalleryViewModel viewModel, LifecycleOwner lifecycleOwner) {
        setHasStableIds(true);
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel.getImages().observe(lifecycleOwner, this::setImages);
    }

    private void setImages(final List<Image> newImages) {
        //Here we diff the images for better performance over just replacing the list.
        differ.submitList(newImages);
    }

    @Override
    public long getItemId(int position) {
        return differ.getCurrentList().get(position).getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent, lifecycleOwner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = differ.getCurrentList().get(position);
        holder.rebind(image, (view) -> this.showDetailView(view, image.getId()));
    }

    private void showDetailView(View view, long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        Navigation.findNavController(view).navigate(R.id.action_galleryFragment_to_detailsActivity, bundle);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryItemBinding binding;
        private BitmapLoader bitmapLoader = new BitmapLoader();

        private final static String TAG = "ViewHolder";

        ViewHolder(LayoutInflater inflater, ViewGroup parent, LifecycleOwner lifecycleOwner) {
            this(GalleryItemBinding.inflate(inflater, parent, false),lifecycleOwner);
        }

        ViewHolder(GalleryItemBinding binding, LifecycleOwner lifecycleOwner) {
            super(binding.getRoot());
            binding.setLifecycleOwner(lifecycleOwner);
            this.bitmapLoader.setDownsample(true);
            this.bitmapLoader.bitmap.observe(lifecycleOwner,(bitmap)->{
                this.binding.setBitmap(bitmap);
                this.binding.executePendingBindings();
            });
            this.binding = binding;
        }

        void rebind(Image image, View.OnClickListener onSelected) {
            if(image == null){
                return;
            }
            if(binding.getImage() != null && image.getId() == binding.getImage().getId()){
                return;
            }
            bitmapLoader.cancel();
            binding.setBitmap(null);

            bitmapLoader.setSourcePath(image.getBestThumbnailPath());

            binding.setImage(image);
            binding.setSelectedListener(onSelected);
            binding.executePendingBindings();
        }

        void recycle() {
            bitmapLoader.cancel();
            binding.setBitmap(null);
            binding.executePendingBindings();
        }
    }
}
