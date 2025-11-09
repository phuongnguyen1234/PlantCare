package com.example.plantcare.utils;

import androidx.room.TypeConverter;

import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converters {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(formatter);
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value, formatter);
    }

    // Converter cho enum Status
    @TypeConverter
    public static String fromStatus(Status status) {
        return status == null ? null : status.name(); // lưu enum dưới dạng String
    }

    @TypeConverter
    public static Status toStatus(String value) {
        return value == null ? null : Status.valueOf(value); // convert ngược lại
    }

    // Converter cho enum TaskType
    @TypeConverter
    public static String fromTaskType(TaskType taskType) {
        return taskType == null ? null : taskType.name(); // lưu enum dưới dạng String
    }

    @TypeConverter
    public static TaskType toTaskType(String value) {
        return value == null ? null : TaskType.valueOf(value); // convert ngược lại
    }

    // Converter cho enum FrequencyUnit
    @TypeConverter
    public static String fromFrequencyUnit(FrequencyUnit frequencyUnit) {
        return frequencyUnit == null ? null : frequencyUnit.name(); // lưu enum dưới dạng String
    }

    @TypeConverter
    public static FrequencyUnit toFrequencyUnit(String value) {
        return value == null ? null : FrequencyUnit.valueOf(value); // convert ngược lại
    }
}
