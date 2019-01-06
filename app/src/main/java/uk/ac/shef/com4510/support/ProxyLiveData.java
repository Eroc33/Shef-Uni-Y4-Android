package uk.ac.shef.com4510.support;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProxyLiveData<T> extends MutableLiveData<T> {
    private LiveData<T> source;

    /**
     * Sets the proxied livedata
     * @param source
     */
    @MainThread
    public void setSource(@NonNull LiveData<T> source) {
        removeSource();
        this.source = source;
        if (hasActiveObservers()) {
            this.source.observeForever(this::changed);
        }
    }

    /**
     * Removes the current proxied source
     */
    @MainThread
    public void removeSource() {
        if (source != null) {
            source.removeObserver(this::changed);
        }
        source = null;
    }

    @CallSuper
    @Override
    protected void onActive() {
        if(source != null) {
            source.observeForever(this::changed);
        }
    }

    @CallSuper
    @Override
    protected void onInactive() {
        if(source != null) {
            source.removeObserver(this::changed);
        }
    }

    private void changed(@Nullable T val){
        this.setValue(val);
    }
}
