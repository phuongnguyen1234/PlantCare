package com.example.plantcare.data.enums;

public class EnumUtils {
    // Generic method: Lấy enum từ display name
    public static <T extends Enum<T> & DisplayableEnum> T fromDisplayName(Class<T> enumClass, String displayName) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getDisplayName().equalsIgnoreCase(displayName)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Không có enum với display name: " + displayName);
    }
}
