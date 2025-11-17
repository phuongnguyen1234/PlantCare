package com.example.plantcare.ui.journal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.databinding.ItemJournalCardBinding;

import java.util.Objects;

public class JournalAdapter extends ListAdapter<JournalWithImages, JournalAdapter.JournalViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(JournalWithImages journal);
        void onMenuClick(View view, JournalWithImages journal);
    }

    public JournalAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<JournalWithImages> DIFF_CALLBACK = new DiffUtil.ItemCallback<JournalWithImages>() {
        @Override
        public boolean areItemsTheSame(@NonNull JournalWithImages oldItem, @NonNull JournalWithImages newItem) {
            return oldItem.journal.getPlantId() == newItem.journal.getPlantId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull JournalWithImages oldItem, @NonNull JournalWithImages newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalCardBinding binding = ItemJournalCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalWithImages currentJournal = getItem(position);
        holder.bind(currentJournal, listener);
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        private final ItemJournalCardBinding binding;

        public JournalViewHolder(ItemJournalCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JournalWithImages journalWithImages, OnItemClickListener listener) {
            binding.setJournal(journalWithImages);
            binding.setListener(listener);
            binding.executePendingBindings();
        }
    }
}
