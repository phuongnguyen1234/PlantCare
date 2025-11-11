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
import com.example.plantcare.ui.plant.addplant.AddPlantFragment;

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
                    .replace(R.id.fragment_container, new AddPlantFragment())
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
        Toast.makeText(getContext(), "Edit plant: " + plant.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Edit Plant screen
    }

    @Override
    public void onDeleteClicked(Plant plant) {
        Toast.makeText(getContext(), "Delete plant: " + plant.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Show a confirmation dialog and call ViewModel to delete
    }
}
