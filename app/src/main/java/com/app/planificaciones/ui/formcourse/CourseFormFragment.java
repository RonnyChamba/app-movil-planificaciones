package com.app.planificaciones.ui.formcourse;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.app.planificaciones.R;
import com.app.planificaciones.databinding.FragmentFormCourseBinding;

import com.app.planificaciones.models.Teacher;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseFormFragment extends Fragment implements View.OnClickListener {

    private FragmentFormCourseBinding binding;

    private FirebaseFirestore db = null;

    private NavController navController;

    private List<Teacher> courses = new ArrayList<>();

    private Spinner spTeachers;

    public static final String COLLECTION_NAME = "courses";

    private EditText txtName;

    private TextView txtParalelo;
    private Button btnSave;

    private Teacher teacherSelected;

    private CourseFormViewModel planningViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        planningViewModel =
                new ViewModelProvider(this).get(CourseFormViewModel.class);
        binding = FragmentFormCourseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();
        initBinding();
        setTitleFragment();
        setValueScreen();
        loadTeachers();

        planningViewModel.getTxtName().observe(getViewLifecycleOwner(), txtName::setText);
        planningViewModel.getTxtParalelo().observe(getViewLifecycleOwner(), txtParalelo::setText);
        return root;
    }

    private void initBinding() {
        txtName = binding.txtName;
        txtParalelo = binding.txtParalelo;
        btnSave = binding.btnSave;
        spTeachers = binding.spCourses;

        btnSave.setOnClickListener(this);
    }

    private void setTitleFragment() {

        // Get the Bundle of arguments
        Bundle arguments = getArguments();

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Nuevo Curso");
        }
    }

    private void loadTeachers() {

        List<Teacher> listTeachers = new ArrayList<>();

        listTeachers.add(new Teacher());
        db.collection("teachers")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        // Obtener los datos de cada documento y agregarlos a la lista
                        Teacher teacher = document.toObject(Teacher.class);
                        teacher.setUid(document.getId());
                        listTeachers.add(teacher);
                    }

                    ArrayAdapter<Teacher> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_dropdown_item_1line, listTeachers);
                    // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTeachers.setAdapter(adapter);

                    spTeachers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            teacherSelected = (Teacher) parent.getItemAtPosition(position);

                            Log.i("Teacher SELECCIONADO", teacherSelected.getUid() + "");
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    Log.i("SIZE PERIODOS EN SERVICIO", listTeachers.size() + "");
                });
    }

    private void setValueScreen() {

        if (ConstantApp.periodo != null) {

            binding.textCourse.setText(ConstantApp.periodo.getTitle() + " - " + ConstantApp.periodo.getDateEnd() + " - " + ConstantApp.periodo.getDateEnd());
        } else {
            binding.textCourse.setText("No hay periodo seleccionado");
        }
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Obtener la referencia al controlador de navegacion
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == btnSave.getId()) {
            saveCourse();
        }

    }

    private void saveCourse() {

        if (txtName.getText().toString().isEmpty()) {
            txtName.setError("El nombre es requerido");
            return;
        }

        if (txtParalelo.getText().toString().isEmpty()) {
            txtParalelo.setError("El paralelo es requerido");
            return;
        }

        HashMap<String, Object> data = new HashMap<>();

        data.put("name", txtName.getText().toString());
        data.put("parallel", txtParalelo.getText().toString());
        data.put("periodo", ConstantApp.periodo.getUid());

        HashMap<String, Object> dataTeacher = new HashMap<>();

        if (teacherSelected != null) {

            if (teacherSelected.getDisplayName() != null) {
                dataTeacher.put("fullName", teacherSelected.getDisplayName() + " " + teacherSelected.getLastName());
                dataTeacher.put("uid", teacherSelected.getUid());
            } else {
                dataTeacher.put("fullName", "");
                dataTeacher.put("uid", "");
            }


        } else {
            dataTeacher.put("fullName", "");
            dataTeacher.put("uid", "");
        }

        data.put("tutor", dataTeacher);


        db.collection(COLLECTION_NAME)
                .add(data)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(getContext(), "Curso creado con exito", Toast.LENGTH_SHORT).show();
                    navController.navigateUp();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(getContext(), "Error al crear el curso", Toast.LENGTH_SHORT).show();
                });
    }


}