package uk.ac.shef.com4510.support.databinding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import uk.ac.shef.com4510.SetImageViewSourceTask;

public class AsyncImageBindingAdapter {
    private static WeakHashMap<ImageView, WeakReference<SetImageViewSourceTask>> tasks = new WeakHashMap<>();

    @BindingAdapter("app:imageAsync")
    public static void setImageAsync(ImageView view, String path) {
        //cancel any old tasks for this image
        WeakReference<SetImageViewSourceTask> taskRef = tasks.get(view);
        if (taskRef != null) {
            SetImageViewSourceTask task = taskRef.get();
            if (task != null && !task.isComplete()) {
                task.cancel(true);
            }
        }
        //clear old image
        view.setImageDrawable(null);
        //start new task
        SetImageViewSourceTask task = new SetImageViewSourceTask();
        tasks.put(view, new WeakReference<>(task));
        task.execute(new SetImageViewSourceTask.Parameters(
                view::setImageBitmap,
                path
        ));
    }
}
