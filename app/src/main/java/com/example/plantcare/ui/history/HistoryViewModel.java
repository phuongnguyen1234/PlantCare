package com.example.plantcare.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.repository.HistoryRepository;

import java.util.List;
import java.util.Objects;

public class HistoryViewModel extends AndroidViewModel {
    private final HistoryRepository historyRepository;
    private final MutableLiveData<FilterParams> filterLiveData = new MutableLiveData<>();
    private final LiveData<List<History>> histories;
    public final LiveData<Boolean> isFilterActive;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository(application);

        histories = Transformations.switchMap(filterLiveData, filter -> {
            if (filter == null || filter.isClear()) {
                return historyRepository.getAllHistory();
            }
            return historyRepository.getFilteredHistories(filter.taskType, filter.statuses, filter.date, filter.searchQuery);
        });

        isFilterActive = Transformations.map(filterLiveData, filter -> filter != null && !filter.isClear());

        // Load all histories initially
        setFilter(null, null, null, null);
    }

    public LiveData<List<History>> getHistories() {
        return histories;
    }

    public LiveData<FilterParams> getFilterParams() {
        return filterLiveData;
    }

    public void setFilter(String taskType, List<String> statuses, String date) {
        FilterParams currentFilter = filterLiveData.getValue();
        String searchQuery = (currentFilter != null) ? currentFilter.searchQuery : null;
        FilterParams newFilter = new FilterParams(taskType, statuses, date, searchQuery);
        filterLiveData.setValue(newFilter);
    }

    public void setSearchQuery(String query) {
        FilterParams currentFilter = filterLiveData.getValue();
        if (currentFilter == null) {
            currentFilter = new FilterParams(null, null, null, null);
        }
        FilterParams newFilter = new FilterParams(currentFilter.taskType, currentFilter.statuses, currentFilter.date, query);
        filterLiveData.setValue(newFilter);
    }

    // Overload for initial setting
    private void setFilter(String taskType, List<String> statuses, String date, String searchQuery) {
        FilterParams newFilter = new FilterParams(taskType, statuses, date, searchQuery);
        filterLiveData.setValue(newFilter);
    }

    static class FilterParams {
        final String taskType;
        final List<String> statuses;
        final String date;
        final String searchQuery;

        FilterParams(String taskType, List<String> statuses, String date, String searchQuery) {
            this.taskType = taskType;
            this.statuses = statuses;
            this.date = date;
            this.searchQuery = searchQuery;
        }

        boolean isClear() {
            return (taskType == null || taskType.isEmpty()) &&
                    (statuses == null || statuses.isEmpty()) &&
                    (date == null || date.isEmpty()) &&
                    (searchQuery == null || searchQuery.isEmpty());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FilterParams that = (FilterParams) o;
            return Objects.equals(taskType, that.taskType) &&
                    Objects.equals(statuses, that.statuses) &&
                    Objects.equals(date, that.date) &&
                    Objects.equals(searchQuery, that.searchQuery);
        }

        @Override
        public int hashCode() {
            return Objects.hash(taskType, statuses, date, searchQuery);
        }
    }
}
