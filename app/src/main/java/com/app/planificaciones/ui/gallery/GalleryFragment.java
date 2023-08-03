package com.app.planificaciones.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterCourse;
import com.app.planificaciones.adapters.AdapterTeacher;
import com.app.planificaciones.databinding.FragmentGalleryBinding;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.Teacher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private GalleryViewModel galleryViewModel;

    private RecyclerView recyclerView;

    public static final String COLLECTION_NAME = "teachers";

    private FirebaseFirestore db = null;

    private AdapterTeacher adapterCourse;

    private List<Teacher> teachers = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recycleView;

        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);

        loadTeachers();

        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void loadTeachers() {


        db.collection(COLLECTION_NAME).get().addOnSuccessListener(task -> {


            for (QueryDocumentSnapshot document : task) {
                // Obtener los datos de cada documento y agregarlos a la lista
                Teacher teacher = document.toObject(Teacher.class);
                teacher.setUid(document.getId());
                teachers.add(teacher);
            }
            showDataCourses();
        });
    }

    private void showDataCourses() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCourse = new AdapterTeacher(getContext(), teachers);
        recyclerView.setAdapter(adapterCourse);
        adapterCourse.setOnClickListener(view -> {

            // obtene el objeto sobre el cual se hizo click
            Teacher course = teachers.get(recyclerView.getChildAdapterPosition(view));

            // Crear un Bundle para pasar el curso como argumento
            Bundle bundle = new Bundle();

            // Pasar el objeto como argumento
            bundle.putSerializable("course", course);

            // Navegar al nuevo fragmento cuando se selecciona un curso, pasando el bundle como argumento
            //navController.navigate(R.id.nav_planning, bundle);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Metodo que se ejecuta cuando se crea el menu de opciones para volver a cargar
     * el menu y hacerlo dinamico
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();

        getActivity().getMenuInflater().inflate(R.menu.planifi, menu);

        MenuItem galleryItem = menu.findItem(R.id.action_new_planning);
        galleryItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

}