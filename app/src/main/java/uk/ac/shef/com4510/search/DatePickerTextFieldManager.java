package uk.ac.shef.com4510.search;

import android.app.DatePickerDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerTextFieldManager implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener {
    private View datepickerSourceView;
    final private Context context;
    final private MutableLiveData<Calendar> date;
    final private MutableLiveData<String> dateString;

    public DatePickerTextFieldManager(Context context,MutableLiveData<Calendar> date, MutableLiveData<String> dateString) {
        this.context = context;
        this.date = date;
        this.dateString = dateString;
    }

    public void showDatePicker(View sourceView, boolean show) {
        if (!show) {
            return;
        }
        datepickerSourceView = sourceView;
        Calendar calendar = date.getValue();
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        DatePickerDialog dialog =
                new DatePickerDialog(context, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setOnCancelListener(this);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        date.postValue(calendar);
        dateString.postValue(format.format(calendar.getTime()));
        if (datepickerSourceView != null) {
            datepickerSourceView.clearFocus();
            datepickerSourceView = null;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        date.postValue(null);
        dateString.postValue("");
        if (datepickerSourceView != null) {
            datepickerSourceView.clearFocus();
            datepickerSourceView = null;
        }
    }
}