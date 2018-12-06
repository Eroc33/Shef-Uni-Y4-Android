package uk.ac.shef.com4510.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.database.Cursor;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Image {
    public static final String[] FIELDS = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.MINI_THUMB_MAGIC,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DESCRIPTION,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
    };
    private static final String TAG = "Image";
    @PrimaryKey
    @NonNull
    private final String path;
    private final String thumbnailPath;
    private final String title;
    private final double latitude;
    private final double longitude;
    private final String description;
    @TypeConverters(Converters.class)
    private final Calendar date;
    private final int iso;
    private final double aperture;
    private final double focalLength;

    public Image(@NonNull String path, String thumbnailPath, String title, double latitude, double longitude, String description, Calendar date) {
        this.path = path;
        this.thumbnailPath = thumbnailPath;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.date = date;
        int iso = 0;
        double aperture = 0.0;
        double focalLength = 0.0;
        try {
            ExifInterface exif = new ExifInterface(path);
            iso = exif.getAttributeInt(ExifInterface.TAG_ISO, 0);
            aperture = exif.getAttributeDouble(ExifInterface.TAG_APERTURE, 0.0);
            focalLength = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0);
        }catch (IOException e){
            Log.w(TAG,String.format("Couldn't get exif for file '%s'. It may have been deleted.",path));
        }
        this.iso = iso;
        this.aperture = aperture;
        this.focalLength = focalLength;
    }

    public static Image fromCursor(Cursor cursor, String thumbnailPath) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(5) * 1000L);
        return new Image(
                cursor.getString(0),
                thumbnailPath,
                cursor.getString(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getString(5),
                cal
        );
    }

    public int getIso() {
        return iso;
    }

    public double getAperture() {
        return aperture;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public double getFstop(){
        return focalLength/aperture;
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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public String getBestThumbnailPath() {
        if(thumbnailPath!=null){
            return thumbnailPath;
        }else{
            return path;
        }
    }
}
