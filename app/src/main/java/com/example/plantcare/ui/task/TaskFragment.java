package com.example.plantcare.ui.task;

import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.data.repository.TaskRepository;
import com.example.plantcare.databinding.FragmentTaskBinding;
import com.example.plantcare.notification.TaskActionReceiver;
import com.example.plantcare.notification.TaskAlarmScheduler;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDateTime;


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

        // Lắng nghe thông báo hoàn thành từ Notification
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(processTaskReceiver,
                new IntentFilter(TaskActionReceiver.ACTION_PROCESS_TASK_FROM_NOTIFICATION));
    }

    // Tạo một BroadcastReceiver mới để xử lý tác vụ từ notification
    private final BroadcastReceiver processTaskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int taskId = intent.getIntExtra("taskId", -1);
            if (taskId != -1) {
                // Tìm Task trong danh sách hiện tại của adapter
                adapter.getCurrentList().stream()
                        .filter(t -> t.task.getTaskId() == taskId)
                        .findFirst()
                        .ifPresent(taskWithPlants -> {
                            // Gọi viewModel để xử lý công việc
                            viewModel.processTask(taskWithPlants.task, true);
                        });
            }
        }
    };

    /** Khi hoàn thành từ thanh thông báo */
    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Khi notification action đã xử lý ở receiver thì UI chỉ cần refresh
            int taskId = intent.getIntExtra("taskId", -1);
            // (Bạn có thể show Snackbar/Toast nếu muốn)
            viewModel.reloadTasks();
        }
    };

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
        // Unregister receiver để tránh leak
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(processTaskReceiver);
        binding = null;
    }
}
