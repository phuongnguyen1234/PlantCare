package com.example.plantcare.ui.journal.addeditjournal;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.databinding.FragmentAddEditJournalBinding;
import com.example.plantcare.ui.main.BaseFragment;

import java.util.List;

public class AddEditJournalFragment extends BaseFragment<FragmentAddEditJournalBinding> implements JournalImageAdapter.OnImageClickListener {

    private AddEditJournalViewModel viewModel;
    private JournalImageAdapter imageAdapter;

    private int plantId;
    private String plantName;
    private int journalId = -1;
    private Journal currentJournal; // To hold the journal being edited

    private final ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(6), uris -> {
                if (uris != null && !uris.isEmpty()) {
                    for (Uri uri : uris) {
                        if (viewModel.getImageUrls().getValue() == null || viewModel.getImageUrls().getValue().size() < 6) {
                            viewModel.addImageUri(uri.toString());
                        } else {
                            Toast.makeText(getContext(), "Bạn chỉ có thể thêm tối đa 6 ảnh", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getInt("plantId");
            plantName = getArguments().getString("plantName");
            if (getArguments().containsKey("journalId")) {
                journalId = getArguments().getInt("journalId");
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_edit_journal;
    }

    @Override
    protected String getToolbarTitle() {
        return journalId == -1 ? "Thêm nhật ký" : "Sửa nhật ký";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AddEditJournalViewModel.class);

        setupRecyclerView();

        binding.setPlantName(plantName);
        binding.setIsEditMode(journalId != -1);

        observeViewModel();

        if (journalId != -1) {
            // Edit mode: Observe the journal with its images
            viewModel.getJournalWithImages(journalId).observe(getViewLifecycleOwner(), journalWithImages -> {
                if (journalWithImages != null) {
                    currentJournal = journalWithImages.journal;
                    binding.etContent.setText(currentJournal.getContent());
                    viewModel.updateDateText(currentJournal.getDateCreated());
                    viewModel.setImageUrls(journalWithImages.images);
                }
            });
        } else {
            // Add mode: Just prepare a new date
            viewModel.prepareNewJournalDate();
        }

        binding.btnSave.setOnClickListener(v -> saveJournal());
    }

    private void setupRecyclerView() {
        imageAdapter = new JournalImageAdapter(this);
        binding.rvImages.setAdapter(imageAdapter);
        binding.rvImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void observeViewModel() {
        viewModel.getDateText().observe(getViewLifecycleOwner(), date -> binding.tvDate.setText(date));

        viewModel.getImageUrls().observe(getViewLifecycleOwner(), uris -> {
            if (uris != null) {
                imageAdapter.submitList(uris);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.onToastMessageShown();
            }
        });

        viewModel.navigateBack.observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                getParentFragmentManager().popBackStack();
                viewModel.onNavigatedBack();
            }
        });
    }

    private void saveJournal() {
        String content = binding.etContent.getText().toString();
        List<String> imageUrls = viewModel.getImageUrls().getValue();

        if (imageUrls == null || imageUrls.isEmpty()) {
            Toast.makeText(getContext(), "Phải có ít nhất 1 ảnh trong nhật ký.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (journalId == -1) {
            // Add new journal
            viewModel.saveJournal(plantId, plantName, content, imageUrls);
        } else {
            // Update existing journal
            if (currentJournal != null) {
                viewModel.updateJournal(currentJournal, content, imageUrls);
            }
        }
    }

    @Override
    public void onAddImageClick() {
        imagePickerLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void onDeleteImageClick(int position) {
        viewModel.removeImageUri(position);
    }
}
