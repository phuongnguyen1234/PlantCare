package com.example.plantcare.ui.journal.journaldetail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantcare.R;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.databinding.ItemJournalDetailBinding;
import com.example.plantcare.ui.journal.addeditjournal.JournalImageAdapter;
import com.example.plantcare.ui.listeners.OnItemMenuClickListener;
import com.example.plantcare.utils.MenuUtils;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

public class JournalDetailAdapter extends ListAdapter<JournalWithImages, JournalDetailAdapter.DetailViewHolder> {

    private final OnItemMenuClickListener<JournalWithImages> menuClickListener;

    public JournalDetailAdapter(OnItemMenuClickListener<JournalWithImages> menuClickListener) {
        super(DIFF_CALLBACK);
        this.menuClickListener = menuClickListener;
    }

    private static final DiffUtil.ItemCallback<JournalWithImages> DIFF_CALLBACK = new DiffUtil.ItemCallback<JournalWithImages>() {
        @Override
        public boolean areItemsTheSame(@NonNull JournalWithImages oldItem, @NonNull JournalWithImages newItem) {
            return oldItem.journal.getJournalId() == newItem.journal.getJournalId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull JournalWithImages oldItem, @NonNull JournalWithImages newItem) {
            return Objects.equals(oldItem.journal, newItem.journal) &&
                    Objects.equals(oldItem.images, newItem.images);
        }
    };

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
        JournalWithImages currentJournal = getItem(position);
        holder.bind(currentJournal, menuClickListener);
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {

        private final ItemJournalDetailBinding binding;

        DetailViewHolder(@NonNull ItemJournalDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(JournalWithImages journalWithImages, OnItemMenuClickListener<JournalWithImages> listener) {
            binding.tvDate.setText(journalWithImages.journal.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            binding.tvContent.setText(journalWithImages.journal.getContent());

            if (journalWithImages.images != null && !journalWithImages.images.isEmpty()) {
                binding.recyclerViewImages.setVisibility(RecyclerView.VISIBLE);
                int imageCount = journalWithImages.images.size();
                RecyclerView.LayoutManager layoutManager = (imageCount == 1) ?
                        new GridLayoutManager(itemView.getContext(), 1) :
                        new GridLayoutManager(itemView.getContext(), 2);

                JournalImageAdapter imageAdapter = new JournalImageAdapter(); // Use view-only constructor
                imageAdapter.submitList(journalWithImages.images.stream()
                        .map(image -> image.getImageUrl())
                        .collect(Collectors.toList()));

                binding.recyclerViewImages.setLayoutManager(layoutManager);
                binding.recyclerViewImages.setAdapter(imageAdapter);
            } else {
                binding.recyclerViewImages.setVisibility(RecyclerView.GONE);
            }

            binding.ibMore.setOnClickListener(v -> {
                PopupMenu.OnMenuItemClickListener menuListener = item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit) {
                        if (listener != null) {
                            listener.onEditClicked(journalWithImages);
                        }
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        if (listener != null) {
                            listener.onDeleteClicked(journalWithImages);
                        }
                        return true;
                    }
                    return false;
                };
                MenuUtils.showCustomPopupMenu(v, R.menu.edit_delete_menu, menuListener);
            });
        }
    }
}
