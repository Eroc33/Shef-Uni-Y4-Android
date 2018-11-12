package uk.ac.shef.com4510.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

@Entity
public class Image {
    public static final String[] FIELDS = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
    };
    @PrimaryKey
    @NonNull
    private final String path;
    private final String title;
    private final double latitude;
    private final double longitude;

    public Image(@NonNull String path, String title, double latitude, double longitude) {
        this.path = path;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Image fromCursor(Cursor cursor) {
        return new Image(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getDouble(2),
                cursor.getDouble(3)
        );
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
