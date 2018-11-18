package uk.ac.shef.com4510.data;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public final class Converters {

    //private as instantiating this makes no sense
    private Converters() {
    }

    @TypeConverter
    public static Calendar fromUnixTimestamp(Long seconds) {
        if (seconds == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(seconds * 1000L);
        return cal;
    }

    @TypeConverter
    public static Long calendarToUnixTimestamp(Calendar cal) {
        if (cal == null) {
            return null;
        } else {
            return cal.getTimeInMillis() / 1000L;
        }
    }
}