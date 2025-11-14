package com.example.plantcare.ui.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.databinding.ItemHistoryBinding;

public class HistoryAdapter extends ListAdapter<History, HistoryAdapter.HistoryViewHolder> {

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<History> DIFF_CALLBACK = new DiffUtil.ItemCallback<History>() {
        @Override
        public boolean areItemsTheSame(@NonNull History oldItem, @NonNull History newItem) {
            return oldItem.getHistoryId() == newItem.getHistoryId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull History oldItem, @NonNull History newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding itemHistoryBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_history, parent, false);
        return new HistoryViewHolder(itemHistoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = getItem(position);
        holder.itemHistoryBinding.setHistory(history);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding itemHistoryBinding;

        public HistoryViewHolder(@NonNull ItemHistoryBinding binding) {
            super(binding.getRoot());
            itemHistoryBinding = binding;
        }
    }
}
