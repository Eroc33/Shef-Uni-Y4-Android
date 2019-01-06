package uk.ac.shef.com4510.widgets;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

@BindingMethods({@BindingMethod(type = TextInputDatePicker.class, attribute = "date", method = "setDate"),
        @BindingMethod(type = TextInputDatePicker.class, attribute = "onDateChanged", method = "setDateChangeListener")})
@InverseBindingMethods(@InverseBindingMethod(type = TextInputDatePicker.class, attribute = "date", event = "onDateChanged", method = "getDate"))
public class TextInputDatePicker extends TextInputEditText implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener {

    private DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    private Calendar date;
    private DateChangeListener dateChangeListener;

    public TextInputDatePicker(Context context) {
        super(context);
        setInputType(EditorInfo.TYPE_NULL);
    }

    public TextInputDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(EditorInfo.TYPE_NULL);
    }

    public TextInputDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInputType(EditorInfo.TYPE_NULL);
    }

    @BindingAdapter({"onDateChanged"})
    public static void setDateChangeListener(TextInputDatePicker view, InverseBindingListener onDateChanged) {
        view.setDateChangeListener((date) -> onDateChanged.onChange());
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        if (this.date != null && this.date.equals(date)) {
            return;
        }
        this.date = date;
        if (date == null) {
            setText("");
        } else {
            setText(format.format(date.getTime()));
        }
        if (dateChangeListener != null) {
            dateChangeListener.onDateChanged(date);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setDate(calendar);
        clearFocus();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused) {
            return;
        }
        Calendar defaultDate = date;
        if (defaultDate == null) {
            defaultDate = Calendar.getInstance();
        }
        DatePickerDialog dialog =
                new DatePickerDialog(getContext(), this, defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));
        dialog.setOnCancelListener(this);
        dialog.show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        setDate(null);
        clearFocus();
    }

    public void setDateChangeListener(DateChangeListener dateChangeListener) {
        this.dateChangeListener = dateChangeListener;
    }

    public interface DateChangeListener {
        void onDateChanged(@Nullable Calendar date);
    }
}
