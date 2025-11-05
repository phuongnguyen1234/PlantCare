package com.example.plantcare.data.enums;

public enum FrequencyUnit implements DisplayableEnum{
    HOUR("Giờ/lần"),
    DAY("Ngày/lần"),
    WEEK("Tuần/lần"),
    MONTH("Tháng/lần"),
    YEAR("Năm/lần");

    private final String displayName;

    FrequencyUnit(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
