package com.example.plantcare.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class BaseViewModel extends AndroidViewModel {

    // For showing toast messages
    protected final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public final LiveData<String> toastMessage = _toastMessage;

    // For navigating back
    protected final MutableLiveData<Boolean> _navigateBack = new MutableLiveData<>(false);
    public final LiveData<Boolean> navigateBack = _navigateBack;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Call this after the toast message has been shown.
     */
    public void onToastMessageShown() {
        _toastMessage.setValue(null);
    }

    /**
     * Call this after the navigation event has been handled.
     */
    public void onNavigatedBack() {
        _navigateBack.setValue(false);
    }
}
