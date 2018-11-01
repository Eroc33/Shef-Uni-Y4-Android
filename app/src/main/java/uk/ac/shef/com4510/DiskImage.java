package uk.ac.shef.com4510;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Arrays;
import java.util.List;

public class DiskImage {
    public static final String[] FIELDS = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
    };
    private final long id;
    private final String path;
    private final String title;
    private final double latitude;
    private final double longitude;
    private final Uri storageUri;

    DiskImage(Cursor cursor, Uri storageUri) {
        this(
                cursor.getLong(field(MediaStore.Images.Media._ID)),
                cursor.getString(field(MediaStore.Images.Media.DATA)),
                cursor.getString(field(MediaStore.Images.Media.DISPLAY_NAME)),
                cursor.getDouble(field(MediaStore.Images.Media.LATITUDE)),
                cursor.getDouble(field(MediaStore.Images.Media.LONGITUDE)),
                storageUri
        );
    }

    private DiskImage(long id, String path, String title, double latitude, double longitude, Uri storageUri) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storageUri = storageUri;
    }

    private static int field(String fieldName) {
        List<String> fields = Arrays.asList(FIELDS);
        return fields.indexOf(fieldName);
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public Uri getStorageUri() {
        return storageUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
