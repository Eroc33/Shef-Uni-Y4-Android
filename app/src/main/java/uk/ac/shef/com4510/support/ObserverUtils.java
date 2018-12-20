package uk.ac.shef.com4510.support;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public class ObserverUtils {
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
