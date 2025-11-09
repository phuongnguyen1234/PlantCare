package com.example.plantcare.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.databinding.FragmentTaskBinding;
import com.example.plantcare.ui.main.ToolbarAndNavControl;

public class TaskFragment extends Fragment {

    private TaskViewModel viewModel;
    private FragmentTaskBinding binding;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        adapter = new TaskAdapter();
        binding.rvTasks.setAdapter(adapter);

        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                adapter.submitList(tasks);
            }
        });

        viewModel.navigateToTaskDetail.observe(getViewLifecycleOwner(), navigate -> {
            if (navigate) {
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TaskDetailFragment())
                    .addToBackStack(null) // Allows user to press back to return to the list
                    .commit();
                viewModel.onNavigatedToTaskDetail();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show the main toolbar and navigation when this fragment is visible
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
