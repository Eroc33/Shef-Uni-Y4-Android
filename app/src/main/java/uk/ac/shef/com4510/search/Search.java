package uk.ac.shef.com4510.search;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import uk.ac.shef.com4510.data.Converters;

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
    private Calendar date;

    public Search(String title, String description, Calendar date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    protected Search(Parcel in) {
        title = in.readString();
        description = in.readString();
        if (in.readByte() == 1) {
            date = Converters.calendarFromUnixTimestamp(in.readLong());
        } else {
            date = null;
        }
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

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        Long timeStamp = Converters.calendarToUnixTimestamp(date);
        if (timeStamp != null) {
            dest.writeByte((byte) 1);
            dest.writeLong(timeStamp);
        } else {
            dest.writeByte((byte) 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
