package com.app.planificaciones.ui.planification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialogKt;
import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterCourse;
import com.app.planificaciones.adapters.AdapterPlanning;
import com.app.planificaciones.databinding.FragmentPlanningBinding;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.Planification;
import com.app.planificaciones.models.Trimestre;
import com.app.planificaciones.ui.modal.ModalFragmentReviewPlanification;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PlanningFragment extends Fragment implements View.OnClickListener {

    private FragmentPlanningBinding binding;

    private FirebaseFirestore db = null;

    private Spinner spCourses;

    private AdapterPlanning adapterPlanning;

    private RecyclerView recyclerView;

    private NavController navController;

    public static final String COLLECTION_NAME = "weeks";

    private List<Planification> planifications = new ArrayList<>();


    private Trimestre trimestreCurrent;

    private Course courseCurrent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlanningViewModel planningViewModel =
                new ViewModelProvider(this).get(PlanningViewModel.class);

        binding = FragmentPlanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        setBindingWidgets();

        db = FirebaseFirestore.getInstance();
        setTitleFragment();

        return root;
    }

    private void setTitleFragment() {

        // Get the Bundle of arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("course")) {

            courseCurrent = (Course) arguments.getSerializable("course");
            if (courseCurrent != null) {

                // set title for the action bar dynamically
                ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(String.format("%s %s", courseCurrent.getName(), courseCurrent.getParallel()));
                    loadTrimestreToCurrentCourse();

                }
            }
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Obtener la referencia al controlador de navegacion
        navController = Navigation.findNavController(view);
    }

    private void setBindingWidgets() {
        spCourses = binding.spTrimestre;
        recyclerView = binding.recycleViewPlanification;
    }

    private void loadTrimestreToCurrentCourse() {

        List<Trimestre> listTrimestres = new ArrayList<>();

        db.collection(COLLECTION_NAME).whereEqualTo("course", courseCurrent.getUid()).orderBy("timestamp", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                // Get the data of each document and add it to the list
                Trimestre selectedTrimestre = document.toObject(Trimestre.class);
                selectedTrimestre.setUid(document.getId());
                listTrimestres.add(selectedTrimestre);
            }

            ArrayAdapter<Trimestre> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_dropdown_item_1line, listTrimestres);
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourses.setAdapter(adapter);

            spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    trimestreCurrent = (Trimestre) parent.getItemAtPosition(position);
                    // Toast.makeText(getContext(), itemTrimestre.getTitle(), Toast.LENGTH_SHORT).show();
                    planifications.clear();
                    loadPlaningByTrimestreSelected();


                    Log.i("Trimestre SELECCIONADO", trimestreCurrent.getUid() + "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            Log.i("SIZE Trimestres", listTrimestres.size() + "");
        });
    }


    private void loadPlaningByTrimestreSelected() {

        final String COLLECTION_NAME_PLANI = "planifications";

        db.collection(COLLECTION_NAME_PLANI).whereEqualTo("week", trimestreCurrent.getUid()).orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                        Planification planning = documentSnapshot.toObject(Planification.class);

                        if (planning != null) {

                            Object timestamp = documentSnapshot.get("timestamp");
                            if (timestamp != null) {
                                planning.setTimestampDate(timestamp.toString());
                            }

                            // recuperar los detalles de cada planificacion
                            List<Map<String, Object>> list = (List<Map<String, Object>>) documentSnapshot.get("details_planification");
                            if (list != null) {
                                planning.setDetailsPlanification(list);
                            }// si es details_planification es null, la lista ya esta inicializada como vacia

                            // Toast.makeText(getContext(), "timestamp: " + planning.getTimestampDate() + planifications.size(), Toast.LENGTH_SHORT).show();
                            planning.setUid(documentSnapshot.getId());
                            // Log.i("Planning", planning.getTitle());
                            planifications.add(planning);

                        } else Log.i("Planificacion item", "ES NULL");
                    }

                    populatePlaning();

//                    Toast.makeText(getContext(), "Planificacion size: " + planifications.size(), Toast.LENGTH_SHORT).show();
                    Log.i("Size planning", planifications.size() + "");

                } else {

                    // Toast.makeText(getContext(), "No hay planificaciones para la semana seleccionado", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Maneja el error
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Firestore", "Error al obtener documentos planificaciones: " + exception.getMessage());

                    Toast.makeText(getContext(), "Error al cargar las planificaciones", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populatePlaning() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterPlanning = new AdapterPlanning(getContext(), planifications);
        recyclerView.setAdapter(adapterPlanning);

        // evento click en el item
        adapterPlanning.setOnClickListener(view -> {

            // verificar si el usuario no es a dmin

            if (!ConstantApp.isAdmin) {

                //Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();

                // obtene el objeto sobre el cual se hizo click
                Planification course = planifications.get(recyclerView.getChildAdapterPosition(view));

                // Crear un Bundle para pasar el curso como argumento
                Bundle bundle = new Bundle();

                // Pasar el objeto como argumento
                bundle.putSerializable("planification", course);

                // Navegar al nuevo fragmento cuando se selecciona un curso, pasando el bundle como argumento
                navController.navigate(R.id.nav_review_detail_planning, bundle);
            }

        });

        // click en el boton subir del item
        adapterPlanning.setOnButtonClickListener(planification -> {

            // Aquí se ejecutará el evento cuando se presione el botón en el RecyclerView
            // Puedes acceder a los datos del objeto "planification" y realizar la acción deseada


            // Crear un Bundle para pasar el curso como argumento
            Bundle bundle = new Bundle();

            // Pasar el objeto como argumento
            bundle.putSerializable("planification", planification);

            int idNav = ConstantApp.isAdmin ? R.id.nav_review_planning : R.id.nav_upload_planning;
            navController.navigate(idNav, bundle);


        });
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


        if (ConstantApp.isAdmin) {
            galleryItem.setVisible(true);
        } else {
            galleryItem.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.action_new_planning) {

            clickNewPlani();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {


    }

    private void clickNewPlani() {

        //Crear un Bundle para pasar el curso y el trimestre como argumento
        Bundle bundle = new Bundle();

        // Pasar el objeto como argumento
        bundle.putSerializable("course", courseCurrent);
        bundle.putSerializable("trimestre", trimestreCurrent);
        navController.navigate(R.id.nav_form_planning, bundle);
    }
}