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
    private final double fstop;
    private final double focalLength;
    private final double shutterSpeed;
    private final boolean hasExif;

    public Image(@NonNull String path, String thumbnailPath, String title, double latitude, double longitude, String description, Calendar date, int iso, double fstop
    , double focalLength, double shutterSpeed, boolean hasExif) {
        this.path = path;
        this.thumbnailPath = thumbnailPath;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.date = date;
        this.iso = iso;
        this.fstop = fstop;
        this.focalLength = focalLength;
        this.shutterSpeed = shutterSpeed;
        this.hasExif = hasExif;
    }

    public static Image fromCursor(Cursor cursor, String thumbnailPath) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getLong(6) * 1000L);
        return new Image(
                cursor.getString(0),
                thumbnailPath,
                cursor.getString(2),
                cursor.getDouble(3),
                cursor.getDouble(4),
                cursor.getString(5),
                cal,
                0,
                0,
                0,
                0,
                false
        );
    }

    public Image withExif(){
        try {
            ExifInterface exif = new ExifInterface(path);
            int iso = exif.getAttributeInt(ExifInterface.TAG_ISO, 0);
            //According to the exif standard ApertureValue is the fstop value
            double fstop = exif.getAttributeDouble(ExifInterface.TAG_APERTURE, 0.0);
            double focalLength = exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0);
            double shutterSpeed = exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME,0);
            return new Image(
                    path,
                    thumbnailPath,
                    title,
                    latitude,
                    longitude,
                    description,
                    date,
                    iso,
                    fstop,
                    focalLength,
                    shutterSpeed,
                    true);
        }catch (IOException e){
            Log.w(TAG,String.format("Couldn't get exif for file '%s'. It may have been deleted.",path));
            return null;
        }
    }

    public Image withTitleAndDescription(String title, String description){
        return new Image(
                path,
                thumbnailPath,
                title,
                latitude,
                longitude,
                description,
                date,
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

    public double getShutterSpeed() {
        return shutterSpeed;
    }

    public boolean hasExif() {
        return hasExif;
    }

    public String formatFstop(){
        if(fstop != 0){
            return String.format("f/%f",fstop);
        }else{
            return "";
        }
    }

    public String formatISO(){
        if(iso != 0){
            return String.format("ISO%d",iso);
        }else{
            return "";
        }
    }

    public String formatFocalLength(){
        if(focalLength != 0){
            return String.format("%fmm",focalLength);
        }else{
            return "";
        }
    }

    public String formatShutterSpeed(){
        if(shutterSpeed == 0.0){
            return "";
        }
        if(shutterSpeed >= 1){
            return String.format("%ds",(long)shutterSpeed);
        }else{
            long numer = 1;
            long denom = (long)(1/shutterSpeed);
            return String.format("%d/%ds",numer,denom);
        }
    }
}
