package uk.ac.shef.com4510;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.shef.com4510.data.Image;
import uk.ac.shef.com4510.data.ImageDao;

public class ImageScannerService extends IntentService {

    public static final String ACTION_SCAN_ALL = "uk.ac.shef.com4501.ACTION_SCAN_ALL";
    public static final String ACTION_LOAD_ONE = "uk.ac.shef.com4501.ACTION_LOAD_ONE";
    public static final String LOAD_URI = "uk.ac.shef.com4501.LOAD_URI";

    private static final Set<Uri> STORAGE_URIS;
    private static final String TAG = ImageScannerService.class.getCanonicalName();
    public static final int BATCH_SIZE = 100;

    static {
        STORAGE_URIS = new HashSet<>();
        STORAGE_URIS.add(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        STORAGE_URIS.add(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private ContentResolver contentResolver;

    public ImageScannerService() {
        super("Image Scanner Service");
    }

    public static void scan_all(Context context, ResultReceiver receiver) {
        Intent serviceIntent = new Intent(context, ImageScannerService.class);
        serviceIntent.setAction(ACTION_SCAN_ALL);
        serviceIntent.putExtra("scanStatusUpdate",receiver);
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
            ResultReceiver scanStatusUpdate = null;
            if(intent.hasExtra("scanStatusUpdate")){
                scanStatusUpdate = intent.getParcelableExtra("scanStatusUpdate");
            }else{
                Log.w(TAG,"No onScanComplete callback");
            }
            ContentObserver contentObserver = new ImageContentObserver(this);
            for (Uri uri : STORAGE_URIS) {
                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI &&
                        getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    continue;
                }
                boolean isExternal = uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                try (Cursor cursor = contentResolver.query(uri, Image.FIELDS, null, null, null, null)) {
                    long total =  cursor.getCount();
                    long count = 0;
                    int pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    ImageDao imageDatabase = getImageDao();
                    cursor.moveToNext();

                    //we batch image insertions to avoid rooms using really high amounts of memory
                    List<Image> batch = new ArrayList<>(BATCH_SIZE);

                    //this won't insert anything, just send a progress update to the ui thread
                    saveImageBatch(scanStatusUpdate, total, count, imageDatabase, batch, isExternal);

                    while (!cursor.isAfterLast()) {
                        if (!imageDatabase.containsSync(cursor.getString(pathIndex))) {
                            batch.add(Image.fromCursor(cursor,thumbnailFromCursor(cursor,isExternal)));
                        }
                        count += 1;
                        if (batch.size() == BATCH_SIZE){
                            saveImageBatch(scanStatusUpdate, total, count, imageDatabase, batch, isExternal);
                        }
                        cursor.moveToNext();
                    }
                    saveImageBatch(scanStatusUpdate, total, count, imageDatabase, batch, isExternal);
                }
                contentResolver.registerContentObserver(uri, true, contentObserver);
            }
            Log.d(TAG,"Completed request ACTION_SCAN_ALL");
        }else if(action.equals(ACTION_LOAD_ONE)){
            Uri uri = intent.getParcelableExtra(LOAD_URI);
            if(uri == null){
                throw new IllegalArgumentException("LOAD_URI must not be null");
            }
            changed(uri);
        }
    }

    private void saveImageBatch(ResultReceiver scanStatusUpdate, long total, long count, ImageDao imageDatabase, List<Image> batch, boolean isExternal) {
        imageDatabase.insertSync(batch);
        batch.clear();
        if( scanStatusUpdate != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("total",total);
            bundle.putLong("count",count);
            bundle.putInt("stage",isExternal?R.string.external:R.string.internal);
            scanStatusUpdate.send(0, bundle);
        }else{
            Log.d(TAG,"No status callback");
        }
    }

    private String thumbnailFromCursor(Cursor cursor, boolean isExternalImage) {
        String imageId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
        Uri uri = MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI;
        if(isExternalImage){
            uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        }
        try(Cursor c2 = contentResolver.query(
                uri,
                new String[]{MediaStore.Images.Thumbnails.DATA},
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{imageId},
                null,
                null
        )){
            c2.moveToFirst();
            if(c2.isAfterLast() || c2.getColumnCount() <= 0){
                //TODO: If no thumbnail create one
                return null;
            }
            return c2.getString(0);
        }
    }

    private ImageDao getImageDao() {
        return ((Application) getApplication()).getImageDb().imageDao();
    }

    private void changed(Uri uri) {
        Log.i(TAG, String.format("Uri %s changed", uri.toString()));
        try (Cursor cursor = contentResolver.query(uri, Image.FIELDS, null, null, null, null)) {
            ImageDao imageDatabase = getImageDao();
            cursor.moveToFirst();
            int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            if (pathIndex < 0) throw new AssertionError();
            if (!cursor.isAfterLast()) {
                boolean isExternal = false;
                if (!imageDatabase.containsSync(cursor.getString(pathIndex))) {
                    imageDatabase.insertSync(Image.fromCursor(cursor,thumbnailFromCursor(cursor,isExternal)));
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
