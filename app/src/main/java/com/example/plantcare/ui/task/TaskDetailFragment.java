package com.example.plantcare.ui.task;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.databinding.FragmentTaskDetailBinding;
import com.example.plantcare.ui.main.BaseFragment;

public class TaskDetailFragment extends BaseFragment {

    private TaskDetailViewModel viewModel;
    private FragmentTaskDetailBinding binding;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_task_detail;
    }

    @Override
    protected String getToolbarTitle() {
        // This title can be dynamic based on whether we are adding or editing a task
        return "Thêm công việc";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout contentContainer = view.findViewById(R.id.secondary_content_container);
        if (contentContainer.getChildCount() > 0) {
            binding = FragmentTaskDetailBinding.bind(contentContainer.getChildAt(0));
        }

        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);

        if (binding != null) {
            binding.setViewModel(viewModel);
            binding.setLifecycleOwner(getViewLifecycleOwner());
        }

        // Handle save button click, etc.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
