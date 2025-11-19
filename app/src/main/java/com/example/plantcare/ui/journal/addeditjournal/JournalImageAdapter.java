package com.example.plantcare.ui.journal.addeditjournal;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JournalImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_IMAGE = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int MAX_IMAGES = 6;

    private final List<String> imageUris = new ArrayList<>();
    private final OnImageClickListener listener;
    private final boolean isEditMode;

    public interface OnImageClickListener {
        void onAddImageClick();
        void onDeleteImageClick(int position);
    }

    // Constructor for view-only mode
    public JournalImageAdapter() {
        this.listener = null;
        this.isEditMode = false;
    }

    // Constructor for edit mode
    public JournalImageAdapter(OnImageClickListener listener) {
        this.listener = listener;
        this.isEditMode = true;
    }

    @Override
    public int getItemViewType(int position) {
        // If in edit mode, show the "Add" button if there's space
        if (isEditMode && position == imageUris.size() && imageUris.size() < MAX_IMAGES) {
            return VIEW_TYPE_ADD;
        }
        return VIEW_TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ADD) {
            View view = inflater.inflate(R.layout.item_add_image, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_journal_image, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_IMAGE) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            String uriString = imageUris.get(position);

            // Glide can handle file paths, content URIs, and URLs directly from a string.
            Glide.with(imageHolder.imageView.getContext())
                    .load(uriString)
                    .into(imageHolder.imageView);

            // Show delete button only in edit mode
            if (isEditMode) {
                imageHolder.deleteButton.setVisibility(View.VISIBLE);
                imageHolder.deleteButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteImageClick(holder.getAdapterPosition());
                    }
                });
            } else {
                imageHolder.deleteButton.setVisibility(View.GONE);
            }
        } else {
            // AddViewHolder - set click listener only in edit mode
            if (isEditMode && listener != null) {
                holder.itemView.setOnClickListener(v -> listener.onAddImageClick());
            }
        }
    }

    @Override
    public int getItemCount() {
        // If in edit mode and there's space, add one for the "Add" button
        if (isEditMode && imageUris.size() < MAX_IMAGES) {
            return imageUris.size() + 1;
        }
        return imageUris.size();
    }

    public void submitList(List<String> uris) {
        imageUris.clear();
        if (uris != null) {
            imageUris.addAll(uris);
        }
        notifyDataSetChanged();
    }

    // --- ViewHolders ---

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivJournalImage);
            deleteButton = itemView.findViewById(R.id.ivDeleteImage);
        }
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
