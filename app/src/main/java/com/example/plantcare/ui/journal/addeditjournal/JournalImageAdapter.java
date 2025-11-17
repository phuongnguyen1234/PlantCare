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

import java.util.ArrayList;
import java.util.List;

public class JournalImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_IMAGE = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int MAX_IMAGES = 6;

    private final List<String> imageUris = new ArrayList<>();
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onAddImageClick();
        void onDeleteImageClick(int position);
    }

    public JournalImageAdapter(OnImageClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // If we are at the last position and have less than MAX_IMAGES, it's an "Add" button
        if (position == imageUris.size() && imageUris.size() < MAX_IMAGES) {
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
            String uri = imageUris.get(position);
            Glide.with(imageHolder.imageView.getContext())
                    .load(Uri.parse(uri))
                    .into(imageHolder.imageView);

            imageHolder.deleteButton.setOnClickListener(v -> listener.onDeleteImageClick(position));
        } else {
            // AddViewHolder
            holder.itemView.setOnClickListener(v -> listener.onAddImageClick());
        }
    }

    @Override
    public int getItemCount() {
        // If we have less than MAX_IMAGES, we have one extra item for the "Add" button
        if (imageUris.size() < MAX_IMAGES) {
            return imageUris.size() + 1;
        }
        return imageUris.size();
    }

    public void submitList(List<String> uris) {
        imageUris.clear();
        imageUris.addAll(uris);
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
