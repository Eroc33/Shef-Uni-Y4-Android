package uk.ac.shef.com4510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageRepository {
    private static final String TAG = "ImageRepository";
    private static final Set<Uri> storageUris;

    static {
        storageUris = new HashSet<>();
        storageUris.add(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        storageUris.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private final MutableLiveData<List<DiskImage>> externalImages;
    private final MutableLiveData<List<DiskImage>> internalImages;
    private final ContentResolver contentResolver;
    private final ContentObserver contentObserver;


    public ImageRepository(Application app) {
        externalImages = new MutableLiveData<>();
        internalImages = new MutableLiveData<>();
        contentResolver = app.getContentResolver();
        contentObserver = new ImageContentObserver(this);
        setupContentObservers();
        for (Uri uri : storageUris) {
            query(uri);
        }
    }

    private void query(Uri uri) {
        if (!storageUris.contains(uri)) {
            Log.w(TAG, String.format("Spurious refresh for uri: %s", uri));
            return;
        }
        //TODO: pass CancellationSignal to query
        try (Cursor cursor = contentResolver.query(uri, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE}, null, null)) {
            List<DiskImage> newImages = new ArrayList<>(cursor.getCount());
            cursor.moveToNext();
            while (!cursor.isAfterLast()) {
                newImages.add(new DiskImage(cursor.getString(0), cursor.getString(1)));
                cursor.moveToNext();
            }
            if (uri == MediaStore.Images.Media.INTERNAL_CONTENT_URI) {
                internalImages.postValue(newImages);
            } else {
                externalImages.postValue(newImages);
            }
        }
    }

    private void setupContentObservers() {
        for (Uri uri : storageUris) {
            contentResolver.registerContentObserver(uri, true, contentObserver);
        }
    }

    public LiveData<List<DiskImage>> getInternalImages() {
        return internalImages;
    }

    public LiveData<List<DiskImage>> getExternalImages() {
        return externalImages;
    }

    //TODO: figure out how to sensibly merge internalImages and externalImages

    private void refresh(Uri uri) {
        query(uri);
    }

    private static class ImageContentObserver extends ContentObserver {
        WeakReference<ImageRepository> imageRepo;

        public ImageContentObserver(ImageRepository imageRepo) {
            super(null);
            this.imageRepo = new WeakReference<>(imageRepo);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            ImageRepository repo = this.imageRepo.get();
            if (repo != null) {
                repo.refresh(uri);
            }
        }
    }
}
