package com.example.plantcare.ui.journal;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.databinding.FragmentJournalBinding;
import com.example.plantcare.ui.dialog.ConfirmDialog;
import com.example.plantcare.ui.journal.addeditjournal.AddEditJournalFragment;
import com.example.plantcare.ui.journal.journaldetail.JournalDetailFragment;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.List;

public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;
    private JournalViewModel viewModel;
    private JournalAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new JournalAdapter(new JournalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JournalWithImages journal) {
                if (journal.images != null && !journal.images.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("plantId", journal.journal.getPlantId());
                    bundle.putString("plantName", journal.journal.getPlantName());

                    JournalDetailFragment detailFragment = new JournalDetailFragment();
                    detailFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    openAddEditFragment(journal.journal.getPlantId(), journal.journal.getPlantName());
                }
            }

            @Override
            public void onMenuClick(View v, JournalWithImages journal) {
                showPopupMenu(v, journal);
            }
        });


        binding.recyclerViewJournal.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewJournal.setAdapter(adapter);

        viewModel.getLatestJournalForEachPlant().observe(getViewLifecycleOwner(), journals -> {
            adapter.submitList(journals);
        });

        binding.fabLayout.fab.setVisibility(View.VISIBLE);
        binding.fabLayout.fab.setOnClickListener(v -> showPlantSelectionDialog());
    }

    private void showPopupMenu(View view, JournalWithImages journal) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.journal_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete_all) {
                new ConfirmDialog.Builder()
                        .setTitle("Xóa tất cả nhật ký")
                        .setMessage("Bạn có chắc chắn muốn xóa tất cả nhật ký cho cây \"" + journal.journal.getPlantName() + "\"?")
                        .setPositiveButton("Xóa", () -> viewModel.deleteAllJournalsForPlant(journal.journal.getPlantId()))
                        .setNegativeButton("Hủy", null)
                        .show(getParentFragmentManager(), "ConfirmDeleteAllJournalsDialog");
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void showPlantSelectionDialog() {
        viewModel.getAllPlants().observe(getViewLifecycleOwner(), plants -> {
            if (isAdded()) {
                if (plants != null && !plants.isEmpty()) {
                    // Create custom adapter
                    PlantSelectionAdapter adapter = new PlantSelectionAdapter(requireContext(), plants);

                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Chọn cây để thêm nhật ký")
                            .setAdapter(adapter, (dialog, which) -> {
                                Plant selectedPlant = plants.get(which);
                                openAddEditFragment(selectedPlant.getPlantId(), selectedPlant.getName());
                            })
                            .create().show();
                } else {
                    Toast.makeText(requireContext(), "Cần có ít nhất 1 cây để thêm nhật ký", Toast.LENGTH_SHORT).show();
                }
                // We observe only once to avoid multiple dialogs
                viewModel.getAllPlants().removeObservers(getViewLifecycleOwner());
            }
        });
    }

    private void openAddEditFragment(int plantId, String plantName) {
        Bundle bundle = new Bundle();
        if (plantId != -1) {
            bundle.putInt("plantId", plantId);
        }
        if (plantName != null) {
            bundle.putString("plantName", plantName);
        }

        AddEditJournalFragment addFragment = new AddEditJournalFragment();
        addFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    private static class PlantSelectionAdapter extends ArrayAdapter<Plant> {

        public PlantSelectionAdapter(@NonNull Context context, @NonNull List<Plant> plants) {
            super(context, 0, plants);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_plant_selection_item, parent, false);
            }

            ImageView plantImage = convertView.findViewById(R.id.plant_image);
            TextView plantName = convertView.findViewById(R.id.plant_name);

            Plant plant = getItem(position);

            if (plant != null) {
                plantName.setText(plant.getName());

                if (!TextUtils.isEmpty(plant.getImageUrl())) {
                    Glide.with(getContext())
                            .load(new File(plant.getImageUrl()))
                            .placeholder(R.drawable.plant_64)
                            .error(R.drawable.plant_64)
                            .into(plantImage);
                } else {
                    Glide.with(getContext()).load(R.drawable.plant_64).into(plantImage);
                }
            }
            return convertView;
        }
    }
}
