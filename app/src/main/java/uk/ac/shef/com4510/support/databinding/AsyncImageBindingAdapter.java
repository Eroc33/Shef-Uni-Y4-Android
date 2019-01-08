package uk.ac.shef.com4510.support.databinding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
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
     * Like ("app:imageStatic") (@link{setImageStatic}) but will recycle bitmaps when the view is detached
     * useful for recyclerviews
     * @param view The view to add the bitmap to
     * @param bm The bitmap
     */
    @BindingAdapter("app:imageAsync")
    public static void setImageAsync(ImageView view, Bitmap bm) {
        //same as setImageStatic
        setImageStatic(view,bm);

        //but also ensure bitmaps are recycled on view detach
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //recycle old bitmaps
                recycleViewBitmap(view);
            }
        });
    }

    /**
     * Attaches a bitmap to an ImageView with npe protection
     * @param view The view to add the bitmap to
     * @param bm The bitmap
     */
    @BindingAdapter("app:imageStatic")
    public static void setImageStatic(ImageView view, Bitmap bm) {
        //recycle old bitmaps
        recycleViewBitmap(view);

        //set new image
        view.setImageBitmap(bm);
    }
}
