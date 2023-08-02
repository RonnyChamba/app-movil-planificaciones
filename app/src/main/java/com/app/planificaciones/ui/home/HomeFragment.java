package com.app.planificaciones.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.app.planificaciones.models.Periodo;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private AdapterCourse adapterCourse;
    private RecyclerView recyclerView;

    private NavController navController;
    private List<Course> courses = new ArrayList<>();

    private Spinner spCourses;

    public static final String COLLECTION_NAME = "periodos";

    private FirebaseFirestore db = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recycleView;

        db = FirebaseFirestore.getInstance();


        setBindingWidgets();
        loadPeriodos();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void setBindingWidgets() {
        spCourses = binding.spCourses;
    }

    private void loadPeriodos() {

        List<Periodo> listPeriodos = new ArrayList<>();
        db.collection(COLLECTION_NAME).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Obtener los datos de cada documento y agregarlos a la lista

                Periodo periodo = document.toObject(Periodo.class);
                periodo.setUid(document.getId());
                listPeriodos.add(periodo);
            }

            ArrayAdapter<Periodo> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_dropdown_item_1line, listPeriodos);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourses.setAdapter(adapter);

            spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Periodo periodo = (Periodo) parent.getItemAtPosition(position);

                    ConstantApp.periodo = periodo;

                    Log.i("PERIODO SELECCIONADO", periodo.getUid() + "");
                    courses.clear();
                    loadCourses(periodo);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Log.i("SIZE PERIODOS EN SERVICIO", listPeriodos.size() + "");
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Obtener la referencia al controlador de navegacion
        navController = Navigation.findNavController(view);
    }

    private void loadCourses(Periodo periodo) {

        //Query query = collectionRef.whereEqualTo("periodo", periodoFiltrado);
        // find all course by current period
        Log.i("PERIODO SELECCIONADO course", periodo.getUid() + "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("courses");

        String periodoFiltrado = periodo.getUid(); // Valor del periodo que deseas filtrar

        Query query = collectionRef.whereEqualTo("periodo", periodoFiltrado);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                        /*// Aquí puedes acceder a los documentos filtrados
                        // DocumentSnapshot documentSnapshot contiene los datos de cada documento
                        // Accede a los campos del documento utilizando documentSnapshot.get("nombre_campo")
                        // Por ejemplo:
                        String nombre = documentSnapshot.getString("name");
                        String periodoCurrent = documentSnapshot.getString("periodo");
                        String parallel = documentSnapshot.getString("parallel");

                        if (documentSnapshot.contains("tutor")) {

                            Map<String, Object> teacherMap = (Map<String, Object>) documentSnapshot.getData().get("tutor");

                            Teacher teacher = null;

                            if (teacherMap != null) {
                                Log.i("tutor", "SI EXISTE");
                                Log.i("tutor fullName: ", Objects.requireNonNull(teacherMap.get("fullName")).toString());
                                Log.i("tutor uid: ", Objects.requireNonNull(teacherMap.get("uid")).toString());
                                teacher = new Teacher();
                                teacher.setDisplayName(Objects.requireNonNull(teacherMap.get("fullName")).toString());
                                teacher.setUid(Objects.requireNonNull(teacherMap.get("uid")).toString());
                            } else Log.i("tutor", "NO EXISTE TEACHER");

                        }
                        Log.i("NOMBRE CURSO", nombre + " " + periodoCurrent + " " + parallel);*/

                        Course course = documentSnapshot.toObject(Course.class);
                        if (course != null) {
                            course.setUid(documentSnapshot.getId());
                            Log.i("COURSE", course.getName());


                            // verificar los cursos que tiene el docente asignado
                            if (!ConstantApp.isAdmin) {

                                List<String> coursesStudent = ConstantApp.teacher.getCourses();

                                if (coursesStudent != null) {

                                    boolean contains = coursesStudent
                                            .stream()
                                            .anyMatch(course.getUid()::equals);

                                    if (contains) courses.add(course);
                                    else Log.i("COURSE", "NO CONTIENE EL CURSO");
                                }
                            } else courses.add(course);


                        } else Log.i("COURSE", "ES NULL");
                    }

                    showDataCourses();
                    Log.i("SIZE CURSOS", courses.size() + "");

                } else
                    Toast.makeText(getContext(), "No hay cursos para el periodo seleccionado", Toast.LENGTH_SHORT).show();
            } else {
                // Maneja el error
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Firestore", "Error al obtener documentos filtrados: " + exception.getMessage());

                    Toast.makeText(getContext(), "Error al cargar los cursos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDataCourses() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCourse = new AdapterCourse(getContext(), courses);
        recyclerView.setAdapter(adapterCourse);
        adapterCourse.setOnClickListener(view -> {

            // obtene el objeto sobre el cual se hizo click
            Course course = courses.get(recyclerView.getChildAdapterPosition(view));

            // Crear un Bundle para pasar el curso como argumento
            Bundle bundle = new Bundle();

            // Pasar el objeto como argumento
            bundle.putSerializable("course", course);

            // Navegar al nuevo fragmento cuando se selecciona un curso, pasando el bundle como argumento
            navController.navigate(R.id.nav_planning, bundle);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}