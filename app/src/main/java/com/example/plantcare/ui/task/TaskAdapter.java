package com.example.plantcare.ui.task;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.databinding.ItemTaskBinding;
import com.example.plantcare.utils.MenuUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskAdapter extends ListAdapter<TaskWithPlants, TaskAdapter.TaskViewHolder> {

    private OnItemMenuClickListener listener;

    public interface OnItemMenuClickListener {
        void onEditClick(int taskId);
        void onDeleteClick(TaskWithPlants taskWithPlants);
        void onCompleteClick(TaskWithPlants taskWithPlants);
    }

    public void setOnItemMenuClickListener(OnItemMenuClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TaskWithPlants> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TaskWithPlants>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaskWithPlants oldItem, @NonNull TaskWithPlants newItem) {
                    // So sánh ID để xác định cùng một item
                    return oldItem.task.getTaskId() == newItem.task.getTaskId();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull TaskWithPlants oldItem, @NonNull TaskWithPlants newItem) {

                    Task oldTask = oldItem.task;
                    Task newTask = newItem.task;

                    // Compare notify time truncated to minutes
                    LocalDateTime oldTime = truncateToMinute(oldTask.getNotifyTime());
                    LocalDateTime newTime = truncateToMinute(newTask.getNotifyTime());

                    // Quick checks for basic fields shown in UI
                    boolean basicSame = Objects.equals(oldTime, newTime)
                            && Objects.equals(oldTask.getName(), newTask.getName())
                            && oldTask.getStatus() == newTask.getStatus()
                            && oldTask.getType() == newTask.getType()
                            && oldTask.getFrequency() == newTask.getFrequency()
                            && Objects.equals(oldTask.getFrequencyUnit(), newTask.getFrequencyUnit());

                    Log.d("DIFF", "old: " + oldItem.task.getNotifyTime());
                    Log.d("DIFF", "new: " + newItem.task.getNotifyTime());
                    if (!basicSame) return false;

                    // Compare plant id lists in a stable, API-safe way
                    List<Integer> oldPlantIds = new ArrayList<>();
                    if (oldItem.plants != null) {
                        for (Plant p : oldItem.plants) {
                            // adjust method name if your Plant uses another getter e.g. getId()
                            oldPlantIds.add(p.getPlantId());
                        }
                    }

                    List<Integer> newPlantIds = new ArrayList<>();
                    if (newItem.plants != null) {
                        for (Plant p : newItem.plants) {
                            newPlantIds.add(p.getPlantId());
                        }
                    }

                    Collections.sort(oldPlantIds);
                    Collections.sort(newPlantIds);

                    return Objects.equals(oldPlantIds, newPlantIds);
                }
            };

    private static LocalDateTime truncateToMinute(LocalDateTime time) {
        return time == null ? null :
                time.withSecond(0).withNano(0);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding =
                ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding, listener);
    }

    // Xóa onBindViewHolder với payload để đơn giản hóa
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding, OnItemMenuClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            // Sự kiện click menu
            binding.taskMenu.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    TaskWithPlants taskWithPlants = ((TaskAdapter) getBindingAdapter()).getItem(position);

                    MenuUtils.showCustomPopupMenu(v, R.menu.edit_delete_menu, item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_edit) {
                            listener.onEditClick(taskWithPlants.task.getTaskId());
                            return true;
                        } else if (itemId == R.id.menu_delete) {
                            listener.onDeleteClick(taskWithPlants);
                            return true;
                        }
                        return false;
                    });
                }
            });

            // Nút hoàn thành
            binding.completeButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    TaskWithPlants taskWithPlants = ((TaskAdapter) getBindingAdapter()).getItem(position);
                    listener.onCompleteClick(taskWithPlants);
                }
            });
        }

        public void bind(TaskWithPlants taskWithPlants) {
            binding.setTask(taskWithPlants.task);

            if (taskWithPlants.plants != null && !taskWithPlants.plants.isEmpty()) {
                String plantNames = taskWithPlants.plants.stream()
                        .map(Plant::getName)
                        .collect(Collectors.joining(", "));
                binding.plantName.setText("Cho: " + plantNames);
            } else {
                binding.plantName.setText("Cho: Không có cây nào");
            }

            binding.executePendingBindings();
        }
    }
}
