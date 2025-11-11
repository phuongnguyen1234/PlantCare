package com.example.plantcare.ui.plant.addeditplant;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddEditPlantViewModel extends ViewModel {
    private final MutableLiveData<Uri> plantImageUri = new MutableLiveData<>();

    public LiveData<Uri> getPlantImageUri() {
        return plantImageUri;
    }

    public void setPlantImageUri(Uri uri) {
        plantImageUri.setValue(uri);
    }
}
