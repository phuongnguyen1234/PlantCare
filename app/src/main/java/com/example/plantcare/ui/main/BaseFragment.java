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
import androidx.fragment.app.Fragment;

import com.example.plantcare.R;

public abstract class BaseFragment extends Fragment {

    protected View rootView;
    private ImageButton backButton;
    private TextView titleTextView;
    private FrameLayout contentContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_secondary_page_layout, container, false);

        backButton = rootView.findViewById(R.id.btn_back);
        titleTextView = rootView.findViewById(R.id.toolbar_title);
        contentContainer = rootView.findViewById(R.id.secondary_content_container);

        View contentView = inflater.inflate(getLayoutResourceId(), contentContainer, false);
        contentContainer.addView(contentView);

        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(false);
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
        if (getActivity() instanceof ToolbarAndNavControl) {
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    protected abstract int getLayoutResourceId();

    protected abstract String getToolbarTitle();
}