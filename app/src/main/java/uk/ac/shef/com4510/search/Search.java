package uk.ac.shef.com4510.search;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import uk.ac.shef.com4510.data.CalendarConverters;

/**
 * Data holder for search data when launching the gallery fragment to display results from another fragment.
 */
public class Search implements Parcelable {
    public static final Creator<Search> CREATOR = new Creator<Search>() {
        @Override
        public Search createFromParcel(Parcel in) {
            return new Search(in);
        }

        @Override
        public Search[] newArray(int size) {
            return new Search[size];
        }
    };

    private String title;
    private String description;
    private Calendar startDate;
    private Calendar endDate;

    public Search(String title, String description, Calendar startDate, Calendar endDate) {
        this.title = title;
        this.description = description;

        if (startDate != null) {
            this.startDate = startDate;
        } else {
            // If no start date is given then we use the beginning of (Unix) time...
            startDate = Calendar.getInstance();
            startDate.setTime(new Date(0L));
            this.startDate = startDate;
        }

        if (endDate != null) {
            this.endDate = endDate;
        } else {
            // Visa versa when no end date is given.
            endDate = Calendar.getInstance();
            endDate.setTime(new Date(Long.MAX_VALUE));
            this.endDate = endDate;
        }
    }

    protected Search(Parcel in) {
        title = in.readString();
        description = in.readString();
        startDate = CalendarConverters.calendarFromUnixTimestamp(in.readLong());
        endDate = CalendarConverters.calendarFromUnixTimestamp(in.readLong());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() { return endDate; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(CalendarConverters.calendarToUnixTimestamp(startDate));
        dest.writeLong(CalendarConverters.calendarToUnixTimestamp(endDate));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
