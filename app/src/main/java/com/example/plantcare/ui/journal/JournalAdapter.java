package com.example.plantcare.ui.journal;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.JournalImageRepository;
import com.example.plantcare.databinding.ItemJournalCardBinding;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<PlantJournals> plantList = new ArrayList<>();
    private final OnItemClickListener listener;
    private final JournalImageRepository imageRepository;

    public interface OnItemClickListener {
        void onItemClick(Journal journal);
        void onAddJournalClick(int plantId, String plantName);
    }

    public static class PlantJournals {
        public final int plantId;
        public final String plantName;
        public final List<Journal> journals;

        public PlantJournals(int plantId, String plantName, List<Journal> journals) {
            this.plantId = plantId;
            this.plantName = plantName;
            this.journals = journals;
        }

        public boolean hasJournal() {
            return journals != null && !journals.isEmpty();
        }

        public Journal getLatestJournal() {
            return hasJournal() ? journals.get(0) : null; // đã sắp xếp DESC theo dateCreated
        }
    }

    public JournalAdapter(Application application, OnItemClickListener listener) {
        this.listener = listener;
        this.imageRepository = new JournalImageRepository(application);
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalCardBinding binding = ItemJournalCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new JournalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        PlantJournals plant = plantList.get(position);
        holder.bind(plant, listener, imageRepository);
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public void setPlants(List<PlantJournals> plants) {
        this.plantList = plants;
        notifyDataSetChanged();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        private final ItemJournalCardBinding binding;

        JournalViewHolder(@NonNull ItemJournalCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PlantJournals plant, OnItemClickListener listener, JournalImageRepository imageRepo) {
            binding.tvPlantName.setText(plant.plantName);

            if (plant.hasJournal()) {
                Journal journal = plant.getLatestJournal();
                binding.cardJournal.setVisibility(View.VISIBLE);
                binding.tvAction.setText("Xem thêm");

                binding.tvDate.setText(journal.getDateCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                binding.tvContent.setText(journal.getContent());

                // Load ảnh mới nhất của nhật ký
                imageRepo.getLatestImagePath(journal.getJournalId(), imagePath -> {
                    binding.imgPlant.post(() -> {
                        if (imagePath != null && !imagePath.isEmpty()) {
                            Glide.with(binding.imgPlant.getContext())
                                    .load(imagePath)
                                    .placeholder(R.drawable.default_plant)
                                    .into(binding.imgPlant);
                        } else {
                            Glide.with(binding.imgPlant.getContext())
                                    .load(R.drawable.default_plant)
                                    .into(binding.imgPlant);
                        }
                    });
                });

                // Click trên nút
                binding.tvAction.setOnClickListener(v -> listener.onItemClick(journal));

            } else {
                binding.cardJournal.setVisibility(View.GONE);
                binding.tvAction.setText("Thêm nhật ký");

                // Click trên nút
                binding.tvAction.setOnClickListener(v -> listener.onAddJournalClick(plant.plantId, plant.plantName));
            }

            // Optional: nếu muốn click toàn bộ item cũng giống click nút
            binding.getRoot().setOnClickListener(v -> {
                if (plant.hasJournal()) {
                    listener.onItemClick(plant.getLatestJournal());
                } else {
                    listener.onAddJournalClick(plant.plantId, plant.plantName);
                }
            });
        }
    }

}
