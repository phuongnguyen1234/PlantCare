package com.example.plantcare.ui.history;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.ui.main.BaseFragment;

public class HistoryFragment extends BaseFragment {

    private HistoryViewModel mViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_history;
    }

    @Override
    protected String getToolbarTitle() {
        return "Lịch sử chăm sóc";
    }
}