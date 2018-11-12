package uk.ac.shef.com4510;

import android.Manifest;
import android.app.IntentService;
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

public class ImageScannerService extends IntentService {

    public static final String ACTION_SCAN_ALL = "uk.ac.shef.com4501.ACTION_SCAN_ALL";
    public static final String ACTION_LOAD_ONE = "uk.ac.shef.com4501.ACTION_LOAD_ONE";
    public static final String LOAD_URI = "uk.ac.shef.com4501.LOAD_URI";

    private static final Set<Uri> STORAGE_URIS;
    private static final String TAG = ImageScannerService.class.getCanonicalName();

    static {
        STORAGE_URIS = new HashSet<>();
        STORAGE_URIS.add(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        STORAGE_URIS.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private ContentResolver contentResolver;

    public ImageScannerService() {
        super("Image Scanner Service");
    }

    public static void scan_all(Context context) {
        Intent serviceIntent = new Intent(context, ImageScannerService.class);
        serviceIntent.setAction(ACTION_SCAN_ALL);
        context.startService(serviceIntent);
    }

    public static void load_one(Context context,Uri uri) {
        Log.i(TAG, String.format("Loading single image from uri: %s",uri.toString()));
        Intent serviceIntent = new Intent(context, ImageScannerService.class);
        serviceIntent.setAction(ACTION_LOAD_ONE);
        serviceIntent.putExtra(LOAD_URI,uri);
        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentResolver = this.getContentResolver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null){
            return;
        }
        String action = intent.getAction();
        if(action == null){
            return;
        }
        if(action.equals(ACTION_SCAN_ALL)){
            ContentObserver contentObserver = new ImageContentObserver(this);
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
        }else if(action.equals(ACTION_LOAD_ONE)){
            Uri uri = intent.getParcelableExtra(LOAD_URI);
            if(uri == null){
                throw new IllegalArgumentException("LOAD_URI must not be null");
            }
            changed(uri);
        }
    }

    private ImageDao getImageDao() {
        return ((Application) getApplication()).getImageDb().imageDao();
    }

    private void changed(Uri uri) {
        Log.i(TAG, String.format("Uri %s changed", uri.toString()));
        try (Cursor cursor = contentResolver.query(uri, Image.FIELDS, null, null, null, null)) {
            ImageDao imageDatabase = getImageDao();
            cursor.moveToNext();
            int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (pathIndex < 0) throw new AssertionError();
            if (!cursor.isAfterLast()) {
                if (!imageDatabase.containsSync(cursor.getString(pathIndex))) {
                    imageDatabase.insertSync(Image.fromCursor(cursor));
                }
            }
        }
    }

    private static class ImageContentObserver extends ContentObserver {
        private final ImageScannerService scannerService;

        ImageContentObserver(ImageScannerService scannerService) {
            super(null);
            this.scannerService = scannerService;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.i(TAG,String.format("Uri change observed: %s",uri.toString()));
            scannerService.changed(uri);
        }
    }
}