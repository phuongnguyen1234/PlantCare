package com.example.plantcare.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.databinding.FragmentTaskBinding;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TaskFragment extends Fragment implements TaskAdapter.OnItemMenuClickListener {

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

        setupRecyclerView();
        setupNavigation();

        binding.fabLayout.fab.setVisibility(View.VISIBLE);
        binding.fabLayout.fab.setOnClickListener(v -> viewModel.onFabClicked());
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter();
        adapter.setOnItemMenuClickListener(this);
        binding.rvTasks.setAdapter(adapter);

        viewModel.getAllTasksWithPlants().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                adapter.submitList(tasks);
            }
        });
    }

    private void setupNavigation() {
        viewModel.navigateToAddTask.observe(getViewLifecycleOwner(), navigate -> {
            if (navigate) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TaskDetailFragment())
                        .addToBackStack(null)
                        .commit();
                viewModel.onNavigatedToAddTask();
            }
        });

        viewModel.navigateToEditTask.observe(getViewLifecycleOwner(), taskId -> {
            if (taskId != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, TaskDetailFragment.newInstance(taskId))
                        .addToBackStack(null)
                        .commit();
                viewModel.onNavigatedToEditTask();
            }
        });
    }

    @Override
    public void onEditClick(int taskId) {
        viewModel.onEditTask(taskId);
    }

    @Override
    public void onDeleteClick(TaskWithPlants taskWithPlants) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            viewModel.deleteTask(taskWithPlants.task);
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }

    @Override
    public void onCompleteClick(TaskWithPlants taskWithPlants) {
        viewModel.processTask(taskWithPlants.task, true);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
