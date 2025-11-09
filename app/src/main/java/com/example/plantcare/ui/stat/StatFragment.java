package com.example.plantcare.ui.stat;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantcare.R;
import com.example.plantcare.databinding.FragmentStatBinding;
import com.example.plantcare.ui.main.ToolbarAndNavControl;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatFragment extends Fragment {

    private StatViewModel mViewModel;
    private FragmentStatBinding binding;

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StatViewModel.class);
        binding.setViewModel(mViewModel);

        mViewModel.getDailyCompletedTaskCounts().observe(getViewLifecycleOwner(), dailyTaskCounts -> {
            if (dailyTaskCounts != null && !dailyTaskCounts.isEmpty()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM");

                for (int i = 0; i < dailyTaskCounts.size(); i++) {
                    entries.add(new BarEntry(i, dailyTaskCounts.get(i).count));
                    LocalDate date = LocalDate.parse(dailyTaskCounts.get(i).date, dbFormatter);
                    labels.add(date.format(displayFormatter));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Công việc hoàn thành");
                dataSet.setColor(Color.parseColor("#4CAF50"));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(10f);

                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.5f);

                // Disable zoom
                binding.barChart.setTouchEnabled(false);
                binding.barChart.setDragEnabled(false);
                binding.barChart.setScaleEnabled(false);
                binding.barChart.setPinchZoom(false);

                binding.barChart.setData(barData);
                binding.barChart.getDescription().setEnabled(false);
                binding.barChart.getLegend().setEnabled(false);
                binding.barChart.setExtraBottomOffset(40f); 

                XAxis xAxis = binding.barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setLabelCount(labels.size()); 
                xAxis.setLabelRotationAngle(-45);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setDrawGridLines(false); // Disable X-axis grid lines

                YAxis leftAxis = binding.barChart.getAxisLeft();
                leftAxis.setGranularity(1f);
                leftAxis.setAxisMinimum(0f);
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setDrawGridLines(false); // Disable Y-axis grid lines


                binding.barChart.getAxisRight().setEnabled(false);
                binding.barChart.setFitBars(true); 
                binding.barChart.animateY(1000);
                binding.barChart.invalidate();
            } else {
                binding.barChart.clear();
                binding.barChart.invalidate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    public StatFragment() {
    }
}
