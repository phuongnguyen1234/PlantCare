package com.example.plantcare.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.plantcare.data.enums.DisplayableEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DropdownUtils {

    /**
     * Sets up an AutoCompleteTextView with values from any enum that implements DisplayableEnum.
     * @param autoCompleteTextView The view to set up.
     * @param enumClass The .class of the enum to use for the dropdown items.
     * @param <T> An enum type that implements DisplayableEnum.
     */
    public static <T extends Enum<T> & DisplayableEnum> void setupEnumDropdown(AutoCompleteTextView autoCompleteTextView, Class<T> enumClass) {
        if (autoCompleteTextView == null || enumClass == null) {
            return;
        }
        Context context = autoCompleteTextView.getContext();

        T[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            return;
        }

        List<String> displayNames = Arrays.stream(enumConstants)
                .map(DisplayableEnum::getDisplayName)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, displayNames);

        autoCompleteTextView.setAdapter(adapter);
    }
}
