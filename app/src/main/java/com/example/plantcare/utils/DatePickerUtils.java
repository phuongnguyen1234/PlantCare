package com.example.plantcare.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatePickerUtils {

    // Functional Interface for callbacks
    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public interface OnDateTimeSelectedListener {
        void onDateTimeSelected(LocalDateTime dateTime);
    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(LocalTime time);
    }

    /**
     * Shows a DatePickerDialog and returns the selected date via a callback.
     * @param context Context to display the dialog in.
     * @param initialDate The date to initially show in the picker.
     * @param listener Callback to be invoked with the selected date.
     */
    public static void showDatePickerDialog(Context context, LocalDate initialDate, OnDateSelectedListener listener) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            // month is 0-indexed, so we add 1
            listener.onDateSelected(LocalDate.of(year, month + 1, dayOfMonth));
        };

        new DatePickerDialog(context, dateSetListener,
                initialDate.getYear(),
                initialDate.getMonthValue() - 1, // month is 0-indexed
                initialDate.getDayOfMonth()).show();
    }

    /**
     * Shows a DatePickerDialog followed by a TimePickerDialog and returns the selected date-time via a callback.
     * @param context Context to display the dialog in.
     * @param initialDateTime The date-time to initially show in the pickers.
     * @param listener Callback to be invoked with the selected date-time.
     */
    public static void showDateTimePickerDialog(Context context, LocalDateTime initialDateTime, OnDateTimeSelectedListener listener) {
        // 1. Date picker listener
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            // 2. After date is picked, create and show time picker
            TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hourOfDay, minute) -> {
                // 3. When time is picked, create the final LocalDateTime object and invoke the callback
                LocalDateTime selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute);
                listener.onDateTimeSelected(selectedDateTime);
            };

            new TimePickerDialog(context, timeSetListener, initialDateTime.getHour(), initialDateTime.getMinute(), true).show();
        };

        // 1. Show date picker first
        new DatePickerDialog(context, dateSetListener,
                initialDateTime.getYear(),
                initialDateTime.getMonthValue() - 1,
                initialDateTime.getDayOfMonth()).show();
    }

    /**
     * Shows a TimePickerDialog and returns the selected time via a callback.
     * @param context Context to display the dialog in.
     * @param initialTime The time to initially show in the picker.
     * @param listener Callback to be invoked with the selected time.
     */
    public static void showTimePickerDialog(Context context, LocalTime initialTime, OnTimeSelectedListener listener) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            listener.onTimeSelected(LocalTime.of(hourOfDay, minute));
        };

        new TimePickerDialog(context, timeSetListener,
                initialTime.getHour(),
                initialTime.getMinute(),
                true).show();
    }
}
