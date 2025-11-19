package com.example.plantcare.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.plantcare.databinding.DialogConfirmComponentBinding;

public class ConfirmDialog extends DialogFragment {

    private DialogConfirmComponentBinding binding;

    private String title, message, positiveButtonText, negativeButtonText;
    private OnClickListener positiveButtonListener, negativeButtonListener;

    public interface OnClickListener {
        void onClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogConfirmComponentBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setTitle(title);
        binding.setMessage(message);
        binding.setPositiveButtonText(positiveButtonText);
        binding.setNegativeButtonText(negativeButtonText);

        binding.btnPositive.setOnClickListener(v -> {
            if (positiveButtonListener != null) {
                positiveButtonListener.onClick();
            }
            dismiss();
        });

        binding.btnNegative.setOnClickListener(v -> {
            if (negativeButtonListener != null) {
                negativeButtonListener.onClick();
            }
            dismiss();
        });
    }

    public static class Builder {
        private final ConfirmDialog dialog;

        public Builder() {
            dialog = new ConfirmDialog();
        }

        public Builder setTitle(String title) {
            dialog.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            dialog.message = message;
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener listener) {
            dialog.positiveButtonText = text;
            dialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener listener) {
            dialog.negativeButtonText = text;
            dialog.negativeButtonListener = listener;
            return this;
        }

        public void show(FragmentManager fragmentManager, String tag) {
            dialog.show(fragmentManager, tag);
        }
    }
}
