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

    //recycle old bitmaps
    private static synchronized void recycleViewBitmap(ImageView view){
        Drawable viewDrawable = view.getDrawable();
        if(viewDrawable instanceof  BitmapDrawable) {
            Bitmap old_bm = ((BitmapDrawable)viewDrawable).getBitmap();
            view.setImageBitmap(null);
            if (old_bm != null && !old_bm.isRecycled()) {
                old_bm.recycle();
            }
        }
    }

    /**
     * Attaches a bitmap to an ImageView with npe protection
     * @param view The view to add the bitmap to
     * @param bm The bitmap
     */
    @BindingAdapter("app:imageAsync")
    public static void setImageAsync(ImageView view, Bitmap bm) {
        //recycle old bitmaps
        recycleViewBitmap(view);

        //set new image
        view.setImageBitmap(bm);
    }
}
