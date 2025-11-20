package com.example.plantcare.ui.journal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.ui.dialog.FullScreenImageDialogFragment;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class JournalImageAdapter extends RecyclerView.Adapter<JournalImageAdapter.ImageViewHolder> {

    private List<String> imageUris = Collections.emptyList();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = imageUris.get(position);
        Glide.with(holder.itemView.getContext())
                .load(new File(imagePath))
                .centerCrop()
                .into(holder.imageView);

        // Set click listener directly on the image view
        holder.imageView.setOnClickListener(v -> {
            if (holder.itemView.getContext() instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) holder.itemView.getContext();
                FragmentManager fm = activity.getSupportFragmentManager();
                FullScreenImageDialogFragment.newInstance(imagePath).show(fm, "FullScreenImageDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void submitList(List<String> newImageUris) {
        this.imageUris = newImageUris == null ? Collections.emptyList() : newImageUris;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Correctly find the ImageView by its ID from item_journal_image.xml
            imageView = itemView.findViewById(R.id.ivJournalImage);

            itemView.setOnClickListener(v -> imageView.performClick());
        }
    }
}
