package com.example.plantcare.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.example.plantcare.R;

public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment {

    protected View rootView;
    protected B binding;

    private ImageButton backButton;
    private TextView titleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_secondary_page_layout, container, false);

        backButton = rootView.findViewById(R.id.btn_back);
        titleTextView = rootView.findViewById(R.id.toolbar_title);
        FrameLayout contentContainer = rootView.findViewById(R.id.secondary_content_container);

        // Inflate the content layout and get the binding object
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), contentContainer, true);

        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(false);
        }

        if (getActivity() instanceof DrawerLocker) {
            ((DrawerLocker) getActivity()).setDrawerLocked(true);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView.setText(getToolbarTitle());

        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Important to avoid memory leaks
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }

        if (getActivity() instanceof DrawerLocker) {
            ((DrawerLocker) getActivity()).setDrawerLocked(false);
        }
    }

    protected abstract int getLayoutResourceId();

    protected abstract String getToolbarTitle();
}
