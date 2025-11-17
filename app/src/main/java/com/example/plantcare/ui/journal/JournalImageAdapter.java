package com.example.plantcare.ui.journal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.databinding.ItemJournalImageBinding;

import java.util.List;

public class JournalImageAdapter extends RecyclerView.Adapter<JournalImageAdapter.ImageViewHolder> {

    private final List<JournalImage> imageList;

    public JournalImageAdapter(List<JournalImage> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalImageBinding binding = ItemJournalImageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        JournalImage image = imageList.get(position);
        Glide.with(holder.binding.ivJournalImage.getContext())
                .load(image.getImageUrl())
                .placeholder(R.drawable.default_plant)
                .centerCrop()
                .into(holder.binding.ivJournalImage);
    }

    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ItemJournalImageBinding binding;
        ImageViewHolder(@NonNull ItemJournalImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
