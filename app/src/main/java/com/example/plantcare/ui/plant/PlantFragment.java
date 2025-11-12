package com.example.plantcare.ui.plant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.databinding.FragmentPlantBinding;
import com.example.plantcare.ui.listeners.OnItemMenuClickListener;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.example.plantcare.ui.plant.addeditplant.AddEditPlantFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PlantFragment extends Fragment implements OnItemMenuClickListener<Plant> {

    private PlantViewModel mViewModel;
    private FragmentPlantBinding binding;
    private PlantAdapter plantAdapter;

    public static PlantFragment newInstance() {
        return new PlantFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plant, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel
        mViewModel = new ViewModelProvider(this).get(PlantViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup RecyclerView and Adapter
        plantAdapter = new PlantAdapter(this); // Pass 'this' as the listener
        binding.rvPlants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPlants.setAdapter(plantAdapter);

        // Observe LiveData
        mViewModel.getAllPlants().observe(getViewLifecycleOwner(), plants -> {
            plantAdapter.submitList(plants);
        });

        // Setup FAB
        binding.fabLayout.fab.setVisibility(View.VISIBLE);
        binding.fabLayout.fab.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddEditPlantFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onEditClicked(Plant plant) {
        Bundle bundle = new Bundle();
        bundle.putInt("plantId", plant.getPlantId());

        AddEditPlantFragment addEditPlantFragment = new AddEditPlantFragment();
        addEditPlantFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addEditPlantFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteClicked(Plant plant) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa cây")
                .setMessage("Bạn có chắc chắn muốn xóa cây '" + plant.getName() + "' không? Mọi dữ liệu liên quan cũng sẽ bị xóa.")
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xóa", (dialog, which) -> {
                    mViewModel.delete(plant);
                    Toast.makeText(getContext(), "Đã xóa cây: " + plant.getName(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
