package uk.ac.shef.com4510.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Image {
    public static final String[] FIELDS = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DESCRIPTION,
            MediaStore.Images.Media.DATE_ADDED
    };
    @PrimaryKey
    @NonNull
    private final String path;
    private final String title;
    private final double latitude;
    private final double longitude;
    private final String description;
    @TypeConverters(Converters.class)
    private final Calendar date;

    public Image(@NonNull String path, String title, double latitude, double longitude, String description, Calendar date) {
        this.path = path;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.date = date;
    }

    public static Image fromCursor(Cursor cursor) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(5) * 1000L);
        return new Image(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getDouble(2),
                cursor.getDouble(3),
                cursor.getString(4),
                cal
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

    public String getDescription() {
        return description;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDateString(DateFormat formatter){
        if(date == null){
            return null;
        }
        Date d = date.getTime();
        if(d == null){
            return null;
        }
        return formatter.format(d);
    }
}
