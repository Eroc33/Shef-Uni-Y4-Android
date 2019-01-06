package uk.ac.shef.com4510.support.databinding;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import uk.ac.shef.com4510.LoadBitmapTask;

public class AsyncImageBindingAdapter {
    private static WeakHashMap<ImageView, WeakReference<LoadBitmapTask>> tasks = new WeakHashMap<>();

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

    @BindingAdapter("app:imageAsync")
    public static void setImageAsync(ImageView view, Bitmap bm) {
        //recycle old bitmaps
        recycleViewBitmap(view);

        //set new image
        view.setImageBitmap(bm);

        //ensure bitmaps are recycled on view detach
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
}
