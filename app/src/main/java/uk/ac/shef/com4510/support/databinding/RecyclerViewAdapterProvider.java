package uk.ac.shef.com4510.support.databinding;

import android.support.v7.widget.RecyclerView;

/**
 * Provides a facade for fragments or activities to provide a @link{RecyclerView.Adapter} as
 * it is generally accepted that viewmodels should not contain views (which RecyclerView.Adapter does)
 * and databinding views should not know about the type of their parent.
 * */
public interface RecyclerViewAdapterProvider {
    RecyclerView.Adapter<?> getAdapter();
}
