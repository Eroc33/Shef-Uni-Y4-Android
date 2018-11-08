package uk.ac.shef.com4510;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.data.ImageDao;

public class ImageScannerService extends Service {

    private static final Set<Uri> STORAGE_URIS;
    private static final String TAG = ImageScannerService.class.getCanonicalName();

    static {
        STORAGE_URIS = new HashSet<>();
        STORAGE_URIS.add(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        STORAGE_URIS.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private ContentResolver contentResolver;
    private ContentObserver contentObserver;

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ImageScannerService.class);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentResolver = this.getContentResolver();
        contentObserver = new ImageContentObserver(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            for (Uri uri : STORAGE_URIS) {
                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI &&
                        getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    continue;
                }
                try (Cursor cursor = contentResolver.query(uri, Image.FIELDS, null, null, null, null)) {
                    int pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    ImageDao imageDatabase = getImageDao();
                    cursor.moveToNext();
                    while (!cursor.isAfterLast()) {
                        if (!imageDatabase.containsSync(cursor.getString(pathIndex))) {
                            imageDatabase.insertSync(Image.fromCursor(cursor));
                        }
                        cursor.moveToNext();
                    }
                }
                contentResolver.registerContentObserver(uri, true, contentObserver);
            }
        }).start();
        return START_STICKY;
    }

    private ImageDao getImageDao() {
        return ((Application) getApplication()).imageDb().imageDao();
    }

    private void changed(Uri uri) {
        Log.i(TAG, String.format("Uri %s changed", uri.toString()));
        try (Cursor cursor = contentResolver.query(uri, Image.FIELDS, null, null, null, null)) {
            int pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            ImageDao imageDatabase = getImageDao();
            cursor.moveToNext();
            if (!cursor.isAfterLast()) {
                if (!imageDatabase.containsSync(cursor.getString(pathIndex))) {
                    imageDatabase.insertSync(Image.fromCursor(cursor));
                }
            }
        }
    }

    private static class ImageContentObserver extends ContentObserver {
        WeakReference<ImageScannerService> scannerService;

        ImageContentObserver(ImageScannerService scannerService) {
            super(null);
            this.scannerService = new WeakReference<>(scannerService);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            ImageScannerService scannerService = this.scannerService.get();
            if (scannerService != null) {
                scannerService.changed(uri);
            }
        }
    }
}
