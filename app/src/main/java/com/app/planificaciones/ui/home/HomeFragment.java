package com.app.planificaciones.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterCourse;
import com.app.planificaciones.databinding.FragmentHomeBinding;
import com.app.planificaciones.models.Course;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private AdapterCourse adapterCourse;
    private RecyclerView recyclerView;


    private NavController navController;
    private List<Course> courses = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recycleView;


        loadList();
        showData();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Obtener la referencia al controlador de navegacion
        navController = Navigation.findNavController(view);
    }

    private void loadList() {

        for (int i = 0; i < 10; i++) {

            courses.add(new Course("Cuarto " + i, "A " + i, "ALGUNA DESCRIPCION"));
        }

    }

    private void showData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCourse = new AdapterCourse(getContext(), courses);
        recyclerView.setAdapter(adapterCourse);

        // setOnClickListener method
        adapterCourse.setOnClickListener(view -> {

            // obtene el objeto sobre el cual se hizo click
            String title = courses.get(recyclerView.getChildAdapterPosition(view)).getName();
//            Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();

            // Navegar al nuevo fragmento
            navController.navigate(R.id.nav_planning);

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}