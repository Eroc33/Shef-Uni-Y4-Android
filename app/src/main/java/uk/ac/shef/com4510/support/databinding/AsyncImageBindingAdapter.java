package uk.ac.shef.com4510.support.databinding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Contains @link{BindingAdapter} implementation for attaching @{Bitmap}s to @link{ImageView}s
 * @see {BindingAdapter}
 * @see {Bitmap}
 * @see {ImageView}
 */
public class AsyncImageBindingAdapter {

    /**
     * Attaches a bitmap to an ImageView with npe protection
     * @param view The view to add the bitmap to
     * @param bm The bitmap
     */
    @BindingAdapter("app:imageAsync")
    public static void setImageAsync(ImageView view, Bitmap bm) {
        Drawable viewDrawable = view.getDrawable();
        Bitmap oldBm = null;
        //maybe get old bitmap
        if(viewDrawable instanceof  BitmapDrawable) {
            oldBm = ((BitmapDrawable)viewDrawable).getBitmap();
        }

        //set new bitmap
        view.setImageBitmap(bm);

        //try recycle old bitmap
        if (oldBm != null && !oldBm.isRecycled()) {
            oldBm.recycle();
        }
    }
}
