package com.example.plantcare.ui.main;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.data.repository.HistoryRepository;
import com.example.plantcare.data.repository.JournalRepository;
import com.example.plantcare.data.repository.PlantRepository;
import com.example.plantcare.databinding.ActivityMainBinding;
import com.example.plantcare.ui.history.HistoryFragment;
import com.example.plantcare.ui.journal.JournalFragment;
import com.example.plantcare.ui.plant.PlantFragment;
import com.example.plantcare.ui.stat.StatFragment;
import com.example.plantcare.ui.task.TaskFragment;
import com.google.android.material.appbar.AppBarLayout;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements ToolbarAndNavControl {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // --- Thiết lập phiên bản ứng dụng ---
        setAppVersion();

        // --- Thiết lập DrawerLayout và NavigationDrawer ---
        drawerLayout = binding.drawerLayout;
        setSupportActionBar(binding.toolbar);

        // --- Xử lý nút Back mới (OnBackPressedDispatcher) ---
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                onBackPressedCallback.setEnabled(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                onBackPressedCallback.setEnabled(false);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);

        binding.btnBurger.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        // === XỬ LÝ INSETS ===
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams appBarParams = (ViewGroup.MarginLayoutParams) binding.appbar.getLayoutParams();
            appBarParams.topMargin = insets.top;
            binding.appbar.setLayoutParams(appBarParams);
            binding.fragmentContainer.setPadding(
                    binding.fragmentContainer.getPaddingLeft(),
                    binding.fragmentContainer.getPaddingTop(),
                    binding.fragmentContainer.getPaddingRight(),
                    binding.bottomNavigation.getHeight()
            );
            return windowInsets;
        });

        // === BOTTOM NAVIGATION ===
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_stat) {
                selectedFragment = new StatFragment();
            } else if (itemId == R.id.nav_plant) {
                selectedFragment = new PlantFragment();
            } else if (itemId == R.id.nav_task) {
                selectedFragment = new TaskFragment();
            } else if (itemId == R.id.nav_journal) {
                selectedFragment = new JournalFragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment, false);
            }
            return true;
        });

        binding.btnHistory.setOnClickListener(v -> loadFragment(new HistoryFragment(), true));

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_stat);
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.fragmentContainer.getLayoutParams();
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());

        // DEBUG: Insert records into all tables to test database creation
        insertDebugData();
    }

    private void insertDebugData() {
        // Insert a sample Plant
        PlantRepository plantRepository = new PlantRepository();
        Plant debugPlant = new Plant();
        debugPlant.setName("Debug Plant");
        debugPlant.setWaterFrequency(1);
        debugPlant.setWaterUnit(FrequencyUnit.HOUR);
        plantRepository.insert(debugPlant);
        Log.d("DEBUG_DB", "Inserted debug plant");

        // Insert a sample Journal (assuming plantId=1 for the plant above)
        JournalRepository journalRepository = new JournalRepository();
        Journal debugJournal = new Journal();
        debugJournal.setPlantId(1);
        debugJournal.setPlantName("Debug Plant");
        debugJournal.setContent("This is a debug journal entry.");
        debugJournal.setDateCreated(LocalDateTime.now());
        journalRepository.insert(debugJournal);
        Log.d("DEBUG_DB", "Inserted debug journal");

        // Insert a sample History
        HistoryRepository historyRepository = new HistoryRepository();
        History debugHistory = new History();
        debugHistory.setTaskName("Debug Task: Water Plant");
        debugHistory.setTaskType(TaskType.WATER);
        debugHistory.setStatus(Status.COMPLETED);
        debugHistory.setContent("Debug history log for watering.");
        debugHistory.setDateCompleted(LocalDateTime.now());
        historyRepository.insert(debugHistory);
        Log.d("DEBUG_DB", "Inserted debug history");
    }

    private void setAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "Version " + pInfo.versionName;
            binding.appVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity", "Failed to get package info", e);
            binding.appVersion.setText(""); // Ẩn đi nếu có lỗi
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void showToolbarAndNav(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        binding.appbar.setVisibility(visibility);
        binding.bottomNavigation.setVisibility(visibility);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.fragmentContainer.getLayoutParams();
        if (show) {
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        } else {
            params.setBehavior(null);
        }
        binding.fragmentContainer.setLayoutParams(params);
    }
}
