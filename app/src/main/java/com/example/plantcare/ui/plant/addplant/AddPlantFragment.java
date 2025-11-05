package com.example.plantcare.ui.plant.addplant;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantcare.R;
import com.example.plantcare.ui.main.ToolbarAndNavControl;

public class AddPlantFragment extends Fragment {

    private AddPlantViewModel mViewModel;

    public static AddPlantFragment newInstance() {
        return new AddPlantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_plant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddPlantViewModel.class);
        // TODO: Use the ViewModel
    }


    // 2. Ghi đè onResume để ẨN Bottom Nav
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            // "Báo" cho Activity biết: "Tôi đang hiển thị, hãy ẩn Bottom Nav đi"
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(false);
        }
    }

    // 3. Ghi đè onPause để HIỆN LẠI Bottom Nav
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof ToolbarAndNavControl) {
            // "Báo" cho Activity biết: "Tôi sắp bị che khuất/hủy, hãy hiện lại Bottom Nav"
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

}