package com.example.plantcare.ui.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.databinding.ItemHistoryBinding;


import java.util.List;


public class HistoryViewModelApdapter extends RecyclerView.Adapter<HistoryViewModelApdapter.HistoryViewHolder>{
   List<History> historyList;

    public HistoryViewModelApdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding itemHistoryBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.item_history,parent,false);
        // inflate inflate viewgroup type
        return new HistoryViewHolder(itemHistoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.itemHistoryBinding.setHistory(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding itemHistoryBinding;
        public HistoryViewHolder(@NonNull ItemHistoryBinding binding) {
            super(binding.getRoot());
            itemHistoryBinding = binding;
        }
    }
}
