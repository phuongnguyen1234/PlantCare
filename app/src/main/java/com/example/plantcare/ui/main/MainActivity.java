package com.example.plantcare.ui.main;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.plantcare.R;
import com.example.plantcare.databinding.ActivityMainBinding;
import com.example.plantcare.ui.history.HistoryFragment;
import com.example.plantcare.ui.journal.JournalFragment;
import com.example.plantcare.ui.plant.PlantFragment;
import com.example.plantcare.ui.stat.StatFragment;
import com.example.plantcare.ui.task.TaskFragment;
import com.google.android.material.appbar.AppBarLayout;

public class MainActivity extends AppCompatActivity implements ToolbarAndNavControl {
    // 2. Khai báo biến binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 3. "Thổi phồng" layout và lấy root view thông qua binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // === PHIÊN BẢN CUỐI CÙNG, ĐƠN GIẢN VÀ CHÍNH XÁC ===
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // 1. Chỉ áp dụng margin TOP cho AppBarLayout
            ViewGroup.MarginLayoutParams appBarParams = (ViewGroup.MarginLayoutParams) binding.appbar.getLayoutParams();
            appBarParams.topMargin = insets.top;
            binding.appbar.setLayoutParams(appBarParams);

            // 2. KHÔNG CẦN làm gì với BottomNavigationView ở đây cả.
            //    layout_gravity="bottom" đã tự động xử lý inset cho nó.

            // 3. Áp dụng padding BOTTOM cho Fragment Container để nó không bị BottomNav che.
            //    Chúng ta không dùng insets.bottom trực tiếp nữa, mà dùng chiều cao của chính BottomNav.
            binding.fragmentContainer.setPadding(
                    binding.fragmentContainer.getPaddingLeft(),
                    binding.fragmentContainer.getPaddingTop(),
                    binding.fragmentContainer.getPaddingRight(),
                    binding.bottomNavigation.getHeight() // Lấy chiều cao thực tế của BottomNav
            );


            // 4. Không tiêu thụ insets
            return windowInsets;
        });

        // 5. Thiết lập listener cho BottomNavigationView (sử dụng binding)
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Cập nhật menu theo yêu cầu của bạn
            if (itemId == R.id.nav_stat) { // Thay nav_home bằng nav_stat
                selectedFragment = new StatFragment();
            } else if (itemId == R.id.nav_plant) {
                selectedFragment = new PlantFragment();
            } else if (itemId == R.id.nav_task) {
                selectedFragment = new TaskFragment();
            } else if (itemId == R.id.nav_journal) {
                selectedFragment = new JournalFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, false); // Không cần đưa vào backstack
            }
            return true;
        });

        // 6. Thiết lập listener cho nút Lịch sử (sử dụng binding)
        binding.btnHistory.setOnClickListener(v -> { // Truy cập qua ID của include layout
            loadFragment(new HistoryFragment(), true); // Cần đưa vào backstack
        });

        // 7. Load Fragment mặc định là StatFragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_stat); // Đặt Stat làm mục mặc định
        }

        // THÊM ĐOẠN NÀY VÀO
        // Đặt behavior mặc định khi Activity mới khởi tạo
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.fragmentContainer.getLayoutParams();
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
    }

    /**
     * Phương thức chung để load và thay thế các Fragment
     * @param fragment Fragment cần hiển thị
     * @param addToBackStack true nếu muốn người dùng có thể nhấn "Back" để quay lại.
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Sử dụng binding để lấy container
        transaction.replace(binding.fragmentContainer.getId(), fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void showToolbarAndNav(boolean show) {
        // Lấy trạng thái hiển thị mong muốn (VISIBLE hoặc GONE)
        int visibility = show ? View.VISIBLE : View.GONE;

        // Dùng binding để điều khiển AppBarLayout và BottomNavigationView
        binding.appbar.setVisibility(visibility);
        binding.bottomNavigation.setVisibility(visibility);

        // === PHẦN SỬA LỖI QUAN TRỌNG NHẤT ===
        // Lấy LayoutParams của FrameLayout (fragment_container)
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.fragmentContainer.getLayoutParams();

        if (show) {
            // NẾU HIỂN THỊ: Gắn lại behavior để các fragment chính cuộn đúng
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        } else {
            // NẾU ẨN: Gỡ bỏ behavior để HistoryFragment chiếm toàn bộ màn hình
            params.setBehavior(null);
        }
        // Áp dụng lại LayoutParams đã thay đổi
        binding.fragmentContainer.setLayoutParams(params);
    }
}