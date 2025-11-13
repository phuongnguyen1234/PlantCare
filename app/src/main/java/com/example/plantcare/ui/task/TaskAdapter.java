package com.example.plantcare.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.databinding.ItemTaskBinding;
import com.example.plantcare.utils.MenuUtils;

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
                    return oldItem.task.getTaskId() == newItem.task.getTaskId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaskWithPlants oldItem, @NonNull TaskWithPlants newItem) {
                    return oldItem.task.getName().equals(newItem.task.getName())
                            && oldItem.task.getStatus().equals(newItem.task.getStatus())
                            && oldItem.task.getType().equals(newItem.task.getType())
                            && oldItem.plants.size() == newItem.plants.size();
                }
            };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskWithPlants currentTaskWithPlants = getItem(position);
        holder.bind(currentTaskWithPlants);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskBinding binding;

        public TaskViewHolder(ItemTaskBinding binding, OnItemMenuClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            // Sự kiện click menu
            binding.taskMenu.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    TaskAdapter adapter = (TaskAdapter) getBindingAdapter();
                    TaskWithPlants taskWithPlants = adapter.getItem(position);

                    MenuUtils.showCustomPopupMenu(v, R.menu.task_item_menu, item -> {
                        int itemId = item.getItemId();
                        if (listener == null) return false;

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
                    TaskAdapter adapter = (TaskAdapter) getBindingAdapter();
                    TaskWithPlants taskWithPlants = adapter.getItem(position);
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
