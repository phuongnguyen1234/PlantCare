package com.example.plantcare.data.enums;

public enum TaskType implements DisplayableEnum {
    WATER("Tưới nước"),
    FERTILIZE("Bón phân"),
    LIGHT("Tắm nắng"),
    OTHER("Khác");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
