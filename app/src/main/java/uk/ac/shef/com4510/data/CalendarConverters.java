package uk.ac.shef.com4510.data;

import java.util.Calendar;

public final class CalendarConverters {

    //private as instantiating this makes no sense
    private CalendarConverters() {
    }

    public static Calendar calendarFromUnixTimestamp(long seconds) {
        if (seconds == 0) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(seconds * 1000L);
        return cal;
    }

    public static long calendarToUnixTimestamp(Calendar cal) {
        if (cal == null) {
            return 0;
        } else {
            return cal.getTimeInMillis() / 1000L;
        }
    }
}