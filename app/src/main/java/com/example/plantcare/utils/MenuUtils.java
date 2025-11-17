package com.example.plantcare.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.example.plantcare.R;

import java.lang.reflect.Field;

public class MenuUtils {

    public static void showCustomPopupMenu(View anchor, int menuRes, PopupMenu.OnMenuItemClickListener listener) {
        Context context = anchor.getContext();
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());

        // --- Start of Customization Logic ---

        // Menu items
        MenuItem deleteItem = popupMenu.getMenu().findItem(R.id.menu_delete);
        MenuItem editItem = popupMenu.getMenu().findItem(R.id.menu_edit);

        if (editItem != null) {
            // Scale icon
            int inset = 12;
            Drawable originalEditIcon = editItem.getIcon();
            if (originalEditIcon != null) {
                InsetDrawable smallerEditIcon = new InsetDrawable(originalEditIcon, inset, inset, inset, inset);
                editItem.setIcon(smallerEditIcon);
            }

            // Tint icon to match default text color
            int defaultTextColor = ContextCompat.getColor(context, R.color.white);
            editItem.setIconTintList(ColorStateList.valueOf(defaultTextColor));
        }

        if (deleteItem != null) {
            // Scale icon
            int inset = 12;
            Drawable originalDeleteIcon = deleteItem.getIcon();
            if (originalDeleteIcon != null) {
                InsetDrawable smallerDeleteIcon = new InsetDrawable(originalDeleteIcon, inset, inset, inset, inset);
                deleteItem.setIcon(smallerDeleteIcon);
            }

            // Tint icon and text
            int redPaleColor = ContextCompat.getColor(context, R.color.red_pale);
            SpannableString deleteTitle = new SpannableString(deleteItem.getTitle());
            deleteTitle.setSpan(new ForegroundColorSpan(redPaleColor), 0, deleteTitle.length(), 0);
            deleteItem.setTitle(deleteTitle);
            deleteItem.setIconTintList(ColorStateList.valueOf(redPaleColor));
        }

        // Force icons to show
        try {
            Field popup = popupMenu.getClass().getDeclaredField("mPopup");
            popup.setAccessible(true);
            Object menu = popup.get(popupMenu);
            menu.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- End of Customization Logic ---

        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.show();
    }
}
