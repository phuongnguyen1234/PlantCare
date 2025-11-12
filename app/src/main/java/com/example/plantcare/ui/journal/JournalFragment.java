package com.example.plantcare.ui.journal;

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
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.databinding.FragmentJournalBinding;
import com.example.plantcare.ui.journal.addeditjournal.AddEditJournalFragment;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.example.plantcare.ui.journal.detailjournal.JournalDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;
    private JournalViewModel viewModel;
    private JournalAdapter adapter;

    private final List<JournalAdapter.PlantJournals> plantWithJournalsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);

        // Adapter
        adapter = new JournalAdapter(requireActivity().getApplication(), new JournalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Journal journal) {
                // Mở màn hình xem chi tiết nhật ký
                Bundle bundle = new Bundle();
                bundle.putInt("plantId", journal.getPlantId());
                bundle.putString("plantName", journal.getPlantName());
                bundle.putInt("journalId", journal.getJournalId());

                JournalDetailFragment detailFragment = new JournalDetailFragment();
                detailFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onAddJournalClick(int plantId, String plantName) {
                // Mở màn hình thêm nhật ký
                Bundle bundle = new Bundle();
                bundle.putInt("plantId", plantId);
                bundle.putString("plantName", plantName);

                AddEditJournalFragment addFragment = new AddEditJournalFragment();
                addFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, addFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // RecyclerView
        binding.recyclerViewJournal.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewJournal.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(JournalViewModel.class);

        // Load danh sách cây
        viewModel.getAllPlants().observe(getViewLifecycleOwner(), plants -> {
            if (plants != null) {
                plantWithJournalsList.clear();
                for (Plant plant : plants) {
                    final int plantId = plant.getPlantId();

                    // Load danh sách nhật ký của cây
                    viewModel.getJournalsByPlantId(plantId).observe(getViewLifecycleOwner(), journals -> {
                        JournalAdapter.PlantJournals plantJournals = new JournalAdapter.PlantJournals(
                                plantId,
                                plant.getName(),
                                journals
                        );

                        // Xóa nếu đã có entry cũ
                        plantWithJournalsList.removeIf(p -> p.plantId == plantId);
                        plantWithJournalsList.add(plantJournals);

                        // Cập nhật adapter
                        adapter.setPlants(new ArrayList<>(plantWithJournalsList));
                    });
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }
}
