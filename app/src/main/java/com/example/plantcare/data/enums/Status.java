package com.example.plantcare.data.enums;

public enum Status implements DisplayableEnum{
    SCHEDULED("Lên lịch"),

    READY("Sẵn sàng"),
    COMPLETED("Hoàn thành"),
    MISSED("Quá hạn");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
