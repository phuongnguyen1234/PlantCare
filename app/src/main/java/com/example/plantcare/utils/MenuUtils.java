package com.example.plantcare.utils;

import android.content.Context;
import android.content.res.ColorStateList;
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

    public interface PopupMenuCustomizer {
        void customize(PopupMenu popupMenu);
    }

    public static void showCustomPopupMenu(View anchor, int menuRes, PopupMenuCustomizer customizer, PopupMenu.OnMenuItemClickListener listener) {
        Context context = anchor.getContext();
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());

        if (customizer != null) {
            customizer.customize(popupMenu);
        }

        // --- Start of Styling Logic ---
        int redDeleteColor = ContextCompat.getColor(context, R.color.red_delete);
        int greyColor = ContextCompat.getColor(context, R.color.grey_icon);

        styleMenuItem(popupMenu.getMenu().findItem(R.id.menu_edit), greyColor, false);
        styleMenuItem(popupMenu.getMenu().findItem(R.id.menu_delete), redDeleteColor, true);
        styleMenuItem(popupMenu.getMenu().findItem(R.id.action_delete_all), redDeleteColor, true);
        // --- End of Styling Logic ---

        // Force icons to show
        try {
            Field popup = popupMenu.getClass().getDeclaredField("mPopup");
            popup.setAccessible(true);
            Object menu = popup.get(popupMenu);
            menu.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.show();
    }

    public static void showCustomPopupMenu(View anchor, int menuRes, PopupMenu.OnMenuItemClickListener listener) {
        showCustomPopupMenu(anchor, menuRes, null, listener);
    }

    private static void styleMenuItem(MenuItem menuItem, int color, boolean tintText) {
        if (menuItem == null) {
            return;
        }

        if (menuItem.getIcon() != null) {
            // Scale icon
            int inset = 12;
            InsetDrawable smallerIcon = new InsetDrawable(menuItem.getIcon(), inset, inset, inset, inset);
            menuItem.setIcon(smallerIcon);

            // Tint icon
            menuItem.setIconTintList(ColorStateList.valueOf(color));
        }

        if (tintText) {
            SpannableString title = new SpannableString(menuItem.getTitle());
            title.setSpan(new ForegroundColorSpan(color), 0, title.length(), 0);
            menuItem.setTitle(title);
        }
    }
}
