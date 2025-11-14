package com.example.plantcare.ui.journal.detailjournal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.databinding.ItemJournalDetailBinding;
import com.example.plantcare.ui.journal.JournalImageAdapter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class JournalDetailAdapter extends RecyclerView.Adapter<JournalDetailAdapter.DetailViewHolder> {

    private final List<Journal> journalList;
    private final Map<Integer, List<JournalImage>> journalImagesMap;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onJournalClick(Journal journal);
    }

    public JournalDetailAdapter(List<Journal> journalList, Map<Integer, List<JournalImage>> journalImagesMap) {
        this.journalList = journalList;
        this.journalImagesMap = journalImagesMap;
        this.listener = null;
    }

    public JournalDetailAdapter(List<Journal> journalList, Map<Integer, List<JournalImage>> journalImagesMap,
                                OnItemClickListener listener) {
        this.journalList = journalList;
        this.journalImagesMap = journalImagesMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalDetailBinding binding = ItemJournalDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new DetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        List<JournalImage> images = journalImagesMap.get(journal.getJournalId());
        holder.bind(journal, images);

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onJournalClick(journal));
        }
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {

        private final ItemJournalDetailBinding binding;

        DetailViewHolder(@NonNull ItemJournalDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Journal journal, List<JournalImage> images) {
            // Hiển thị ngày và nội dung
            binding.tvDate.setText(journal.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            binding.tvContent.setText(journal.getContent());

            if (images != null && !images.isEmpty()) {
                int imageCount = images.size();
                RecyclerView.LayoutManager layoutManager;

                if (imageCount == 1) {
                    // Chỉ 1 ảnh -> LinearLayout
                    layoutManager = new LinearLayoutManager(itemView.getContext(),
                            LinearLayoutManager.VERTICAL, false);
                } else {
                    // Nhiều ảnh -> Grid 2 cột
                    layoutManager = new GridLayoutManager(itemView.getContext(), 2);
                }

                binding.recyclerViewImages.setLayoutManager(layoutManager);
                binding.recyclerViewImages.setAdapter(new JournalImageAdapter(images));
                binding.recyclerViewImages.setVisibility(RecyclerView.VISIBLE);
            } else {
                binding.recyclerViewImages.setVisibility(RecyclerView.GONE);
            }
        }
    }
}
