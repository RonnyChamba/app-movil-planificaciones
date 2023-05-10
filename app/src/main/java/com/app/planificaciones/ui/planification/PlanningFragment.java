package com.app.planificaciones.ui.planification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.planificaciones.databinding.FragmentGalleryBinding;
import com.app.planificaciones.databinding.FragmentPlanningBinding;
import com.app.planificaciones.ui.gallery.GalleryViewModel;

public class PlanningFragment extends Fragment {

    private FragmentPlanningBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanningViewModel planningViewModel =
                new ViewModelProvider(this).get(PlanningViewModel.class);

        binding = FragmentPlanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPlanning;
        planningViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}