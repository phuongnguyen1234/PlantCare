package com.example.plantcare.ui.journal.journaldetail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.databinding.FragmentJournalDetailBinding;
import com.example.plantcare.ui.dialog.ConfirmDialog;
import com.example.plantcare.ui.journal.JournalViewModel;
import com.example.plantcare.ui.journal.addeditjournal.AddEditJournalFragment;
import com.example.plantcare.ui.listeners.OnItemMenuClickListener;
import com.example.plantcare.ui.main.BaseFragment;

public class JournalDetailFragment extends BaseFragment<FragmentJournalDetailBinding> implements OnItemMenuClickListener<JournalWithImages> {

    private JournalViewModel viewModel;
    private int plantId;
    private String plantName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getInt("plantId", -1);
            plantName = getArguments().getString("plantName", "Không xác định");
        } else {
            plantName = "Không xác định";
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_journal_detail;
    }

    @Override
    protected String getToolbarTitle() {
        return "Nhật ký của " + plantName;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        JournalDetailAdapter adapter = new JournalDetailAdapter(this);
        binding.recyclerViewDetail.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewDetail.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(JournalViewModel.class);

        viewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
            if (plant != null) {
                binding.fabLayout.fab.setVisibility(View.VISIBLE);
                binding.fabLayout.fab.setOnClickListener(v -> openAddEditFragment(-1));
            } else {
                binding.fabLayout.fab.setVisibility(View.GONE);
            }
        });

        viewModel.getJournalsWithImagesByPlantId(plantId).observe(getViewLifecycleOwner(), journals -> {
            if (journals == null || journals.isEmpty()) {
                binding.recyclerViewDetail.setVisibility(View.GONE);
            } else {
                binding.recyclerViewDetail.setVisibility(View.VISIBLE);
                adapter.submitList(journals);
            }
        });
    }

    private void openAddEditFragment(int journalId) {
        Bundle bundle = new Bundle();
        bundle.putInt("plantId", plantId);
        bundle.putString("plantName", plantName);
        if (journalId != -1) {
            bundle.putInt("journalId", journalId);
        }

        AddEditJournalFragment fragment = new AddEditJournalFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditClicked(JournalWithImages item) {
        openAddEditFragment(item.journal.getJournalId());
    }

    @Override
    public void onDeleteClicked(JournalWithImages item) {
        new ConfirmDialog.Builder()
                .setTitle("Xóa nhật ký")
                .setMessage("Bạn có chắc chắn muốn xóa mục nhật ký này?")
                .setPositiveButton("Xóa", () -> viewModel.deleteJournal(item.journal))
                .setNegativeButton("Hủy", null)
                .show(getParentFragmentManager(), "ConfirmDeleteJournalDialog");
    }
}
