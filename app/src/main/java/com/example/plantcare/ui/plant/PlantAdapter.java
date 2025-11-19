package com.example.plantcare.ui.plant;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.databinding.ItemPlantBinding;
import com.example.plantcare.ui.listeners.OnItemMenuClickListener;
import com.example.plantcare.utils.MenuUtils;

import java.util.Objects;

public class PlantAdapter extends ListAdapter<Plant, PlantAdapter.ViewHolder> {

    private final OnItemMenuClickListener<Plant> menuClickListener;

    public PlantAdapter(OnItemMenuClickListener<Plant> menuClickListener) {
        super(DIFF_CALLBACK);
        this.menuClickListener = menuClickListener;
    }

    private static final DiffUtil.ItemCallback<Plant> DIFF_CALLBACK = new DiffUtil.ItemCallback<Plant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            return oldItem.getPlantId() == newItem.getPlantId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Plant oldItem, @NonNull Plant newItem) {
            // So sánh cả tên và URL ảnh để phát hiện thay đổi
            return oldItem.getName().equals(newItem.getName()) &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPlantBinding binding = ItemPlantBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plant plant = getItem(position);
        holder.bind(plant);

        holder.binding.ibMore.setOnClickListener(v -> {
            PopupMenu.OnMenuItemClickListener listener = item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_edit) {
                    if (menuClickListener != null) {
                        menuClickListener.onEditClicked(plant);
                    }
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    if (menuClickListener != null) {
                        menuClickListener.onDeleteClicked(plant);
                    }
                    return true;
                }
                return false;
            };

            MenuUtils.showCustomPopupMenu(v, R.menu.edit_delete_menu, listener);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlantBinding binding;

        ViewHolder(ItemPlantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Plant plant) {
            binding.setPlant(plant);
            binding.executePendingBindings();
        }
    }
}
