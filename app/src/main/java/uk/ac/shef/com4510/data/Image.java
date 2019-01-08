package uk.ac.shef.com4510.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Holds path and cached metadata for an image
 */
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
    private final long timestamp;
    private final int iso;
    private final double fstop;
    private final double focalLength;
    private final double shutterSpeed;
    private final boolean hasExif;

    public Image(@NonNull String path, String thumbnailPath, String title, double latitude, double longitude, String description, long timestamp, int iso, double fstop
    , double focalLength, double shutterSpeed, boolean hasExif) {
        this.path = path;
        this.thumbnailPath = thumbnailPath;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = timestamp;
        this.iso = iso;
        this.fstop = fstop;
        this.focalLength = focalLength;
        this.shutterSpeed = shutterSpeed;
        this.hasExif = hasExif;
    }

    /**
     * Loads an image from a mediastore cursor and a thumbnail path
     * @param cursor A media store cursor. Should use @link{FIELDS} as the column
     * @param thumbnailPath
     */
    public Image(Cursor cursor, String thumbnailPath) {
        this(
                cursor.getString(0),
                thumbnailPath,
                cursor.getString(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getString(5),
                cursor.getLong(6),
                0,
                0,
                0,
                0,
                false
        );
    }

    /**
     * Loads exif for this @link{Image} and returns a new instance of @link{Image} with the exif added.
     * @return An image with available exif loaded
     */
    public Image withExif() {
        try {
            ExifInterface exif = new ExifInterface(path);
            // If the Exif has location data included, it replaces that which is currently stored for the Image.
            double exifLatitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, latitude);
            double exifLongitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, longitude);
            int iso = exif.getAttributeInt(ExifInterface.TAG_ISO_SPEED, 0);
            //According to the exif standard ApertureValue is the fstop value
            double fstop = exif.getAttributeDouble(ExifInterface.TAG_APERTURE_VALUE, 0.0);
            double focalLength = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0);
            double shutterSpeed = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME,0);
            return new Image(
                    path,
                    thumbnailPath,
                    title,
                    exifLatitude,
                    exifLongitude,
                    description,
                    timestamp,
                    iso,
                    fstop,
                    focalLength,
                    shutterSpeed,
                    true);
        } catch (IOException e){
            Log.w(TAG,String.format("Couldn't get exif for file '%s'. It may have been deleted.",path));
            return null;
        }
    }

    public Image withBasicInfo(String title, String description, Double latitude, Double longitude, long timestamp){
        return new Image(
                path,
                thumbnailPath,
                title,
                latitude,
                longitude,
                description,
                timestamp,
                iso,
                fstop,
                focalLength,
                shutterSpeed,
                true);
    }

    public int getIso() {
        return iso;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public double getFstop(){
        return fstop;
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

    public long getTimestamp() {
        return timestamp;
    }

    public Calendar getDate() {
        return CalendarConverters.calendarFromUnixTimestamp(timestamp);
    }

    public String getDateString(DateFormat formatter){
        Calendar date = getDate();
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

    public double getShutterSpeed() {
        return shutterSpeed;
    }

    public boolean hasExif() {
        return hasExif;
    }

    /**
     * Fstop formatted as a string in the conventional format
     */
    public String formatFstop(){
        if(fstop != 0){
            return String.format(Locale.getDefault(),"f/%.2f",fstop);
        }else{
            return null;
        }
    }

    /**
     * Iso formatted as a string in the conventional format
     */
    public String formatISO(){
        if(iso != 0){
            return String.format(Locale.getDefault(),"ISO%d",iso);
        }else{
            return null;
        }
    }

    /**
     * Focal Length formatted as a string in the conventional format
     */
    public String formatFocalLength(){
        if(focalLength != 0){
            return String.format(Locale.getDefault(),"%.2fmm",focalLength);
        }else{
            return null;
        }
    }

    /**
     * Shutter Speed formatted as a string in the conventional format
     */
    public String formatShutterSpeed(){
        if(shutterSpeed == 0.0){
            return null;
        }
        if(shutterSpeed >= 1){
            return String.format(Locale.getDefault(),"%ds",(long)shutterSpeed);
        }else{
            long numer = 1;
            long denom = (long)(1/shutterSpeed);
            return String.format(Locale.getDefault(),"%d/%ds",numer,denom);
        }
    }
}
