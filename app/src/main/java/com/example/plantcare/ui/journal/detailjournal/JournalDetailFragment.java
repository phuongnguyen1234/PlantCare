package com.example.plantcare.ui.journal.detailjournal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.repository.JournalImageRepository;
import com.example.plantcare.databinding.FragmentJournalDetailBinding;

import com.example.plantcare.ui.journal.JournalViewModel;
import com.example.plantcare.ui.journal.addeditjournal.AddEditJournalFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class JournalDetailFragment extends Fragment {

    private FragmentJournalDetailBinding binding;
    private JournalViewModel viewModel;
    private JournalImageRepository imageRepo;
    private int plantId;
    private String plantName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            plantId = getArguments().getInt("plantId", -1);
            plantName = getArguments().getString("plantName", "Không xác định");
        }

        binding.toolbarTitle.setText("Nhật ký của " + plantName);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.recyclerViewDetail.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(JournalViewModel.class);
        imageRepo = new JournalImageRepository(requireActivity().getApplication());

        viewModel.getJournalsByPlantId(plantId).observe(getViewLifecycleOwner(), journals -> {
            if (journals == null || journals.isEmpty()) {
                binding.recyclerViewDetail.setVisibility(View.GONE);
                binding.emptyLayout.setVisibility(View.VISIBLE);

                binding.tvEmptyPlantName.setText("Cây " + plantName + " chưa có nhật ký nào.");
                binding.btnAddJournal.setOnClickListener(v -> openAddEditFragment(-1));
                return;
            }

            binding.emptyLayout.setVisibility(View.GONE);
            binding.recyclerViewDetail.setVisibility(View.VISIBLE);

            Executors.newSingleThreadExecutor().execute(() -> {
                Map<Integer, List<JournalImage>> journalImagesMap = new HashMap<>();
                for (Journal j : journals) {
                    List<JournalImage> imgs = imageRepo.getImagesByJournalSync(j.getJournalId());
                    journalImagesMap.put(j.getJournalId(), imgs);
                }

                requireActivity().runOnUiThread(() -> {
                    JournalDetailAdapter adapter = new JournalDetailAdapter(journals, journalImagesMap, journal -> {
                        openAddEditFragment(journal.getJournalId());
                    });
                    binding.recyclerViewDetail.setAdapter(adapter);
                });
            });
        });

        binding.fab.setOnClickListener(v -> openAddEditFragment(-1));
    }

    private void openAddEditFragment(int journalId) {
        Bundle bundle = new Bundle();
        bundle.putInt("plantId", plantId);
        bundle.putString("plantName", plantName);
        if (journalId != -1) bundle.putInt("journalId", journalId);

        AddEditJournalFragment fragment = new AddEditJournalFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
