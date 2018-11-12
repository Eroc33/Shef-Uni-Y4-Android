package uk.ac.shef.com4510.support;

import android.content.res.Resources;

import uk.ac.shef.com4510.R;

public class CoordinateFormatter {
    private final Resources resources;

    public CoordinateFormatter(Resources resources) {
        this.resources = resources;
    }

    public String format(double latitude, double longitude) {
        return resources.getString(R.string.coordinate_format_string, latitude, longitude);
    }
}
