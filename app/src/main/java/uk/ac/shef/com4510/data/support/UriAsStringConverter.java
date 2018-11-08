package uk.ac.shef.com4510.data.support;

import android.arch.persistence.room.TypeConverter;
import android.net.Uri;

public class UriAsStringConverter {
    @TypeConverter
    public static Uri fromString(String string) {
        return Uri.parse(string);
    }

    @TypeConverter
    public static String uriToString(Uri uri) {
        return uri.toString();
    }
}
