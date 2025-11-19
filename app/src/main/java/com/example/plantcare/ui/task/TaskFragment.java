package com.example.plantcare.ui.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.databinding.FragmentTaskBinding;
import com.example.plantcare.ui.dialog.ConfirmDialog;
import com.example.plantcare.ui.main.ToolbarAndNavControl;

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
                Log.d("TASK", "submitList size=" + (tasks == null ? 0 : tasks.size()) + " hash=" + System.identityHashCode(tasks));
            } else {
                adapter.submitList(null);
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
        new ConfirmDialog.Builder()
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này không?")
                .setPositiveButton("Xóa", () -> viewModel.deleteTask(taskWithPlants.task))
                .setNegativeButton("Hủy", null) // No action needed for cancel
                .show(getParentFragmentManager(), "ConfirmDeleteTaskDialog");
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

    private final BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int taskId = intent.getIntExtra("taskId", -1);
            if (taskId != -1) {
                adapter.getCurrentList().stream()
                        .filter(t -> t.task.getTaskId() == taskId)
                        .findFirst().ifPresent(twp -> viewModel.processTask(twp.task, true));
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
