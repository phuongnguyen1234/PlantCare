package com.example.plantcare.ui.journal.addeditjournal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.plantcare.databinding.FragmentAddEditJournalBinding;
import com.example.plantcare.R;
import java.util.ArrayList;
import java.util.List;

public class AddEditJournalFragment extends Fragment {

    private FragmentAddEditJournalBinding binding;
    private AddEditJournalViewModel viewModel;

    private int plantId;
    private String plantName;
    private int journalId = -1;

    private final List<ImageView> imageViews = new ArrayList<>();

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count && i < 6; i++) {
                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                            viewModel.addImageUri(uri.toString());
                        }
                    } else if (result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        viewModel.addImageUri(uri.toString());
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAddEditJournalBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AddEditJournalViewModel.class);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        imageViews.add(binding.img1);
        imageViews.add(binding.img2);
        imageViews.add(binding.img3);
        imageViews.add(binding.img4);
        imageViews.add(binding.img5);
        imageViews.add(binding.img6);

        if (getArguments() != null) {
            plantId = getArguments().getInt("plantId");
            plantName = getArguments().getString("plantName");
            binding.tvPlantName.setText(plantName);

            if (getArguments().containsKey("journalId")) {
                journalId = getArguments().getInt("journalId");
                viewModel.loadJournal(journalId);
            } else {
                viewModel.prepareNewJournalDate();
            }
        }

        // Quan sát dữ liệu
        viewModel.getContent().observe(getViewLifecycleOwner(), s -> binding.etContent.setText(s));
        viewModel.getDateText().observe(getViewLifecycleOwner(), date -> binding.tvDate.setText(date));

        viewModel.getImageUrls().observe(getViewLifecycleOwner(), uris -> {
            for (int i = 0; i < imageViews.size(); i++) {
                ImageView img = imageViews.get(i);
                if (i < uris.size()) {
                    Glide.with(img.getContext())
                            .load(Uri.parse(uris.get(i)))
                            .placeholder(R.drawable.ic_add_photo)
                            .into(img);
                } else {
                    img.setImageResource(R.drawable.ic_add_photo);
                }
            }
        });

        for (ImageView img : imageViews) {
            img.setOnClickListener(v -> openGallery());
        }

        binding.btnSave.setOnClickListener(v -> {
            String content = binding.etContent.getText().toString();
            viewModel.setContent(content);
            viewModel.saveJournal(plantId, plantName);

            Toast.makeText(requireContext(),
                    journalId == -1 ? "Đã thêm nhật ký" : "Đã cập nhật nhật ký",
                    Toast.LENGTH_SHORT).show();

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return binding.getRoot();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
    }
}
