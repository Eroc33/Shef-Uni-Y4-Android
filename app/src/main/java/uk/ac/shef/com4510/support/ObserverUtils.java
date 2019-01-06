package uk.ac.shef.com4510.support;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public class ObserverUtils {
    /**
     * Attaches an @link{Observer} (without a limited lifetime) to a @link{Livedata} that only triggers once.
     * @param livedata The livedata to attach the observer to
     * @param observer The observer to attach
     * @param <T> The type produced by the livedata and consumed by the observer.
     */
    public static <T> void observeOneshot(LiveData<T> livedata, Observer<T> observer){
        livedata.observeForever(new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                livedata.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}
