package com.app.planificaciones.ui.reviewplanification;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterPlanning;
import com.app.planificaciones.adapters.AdapterReviewDetailPlanning;
import com.app.planificaciones.databinding.FragmentReviewPlanificationBinding;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.DetailsPlanification;
import com.app.planificaciones.models.Planification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewPlanification extends Fragment {

    private FragmentReviewPlanificationBinding binding;

    private NavController navController;

    private FirebaseFirestore db = null;

    public static final String COLLECTION_NAME = "details_planification";

    private AdapterReviewDetailPlanning adapterPlanning;

    private RecyclerView recyclerView;


    private List<DetailsPlanification> detailsPlanifications = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReviewPlanificationViewModel planningViewModel =
                new ViewModelProvider(this).get(ReviewPlanificationViewModel.class);

        binding = FragmentReviewPlanificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        setBindingWidgets();
        db = FirebaseFirestore.getInstance();
        setTitleFragment();

        // final TextView textView = binding.textPlanning;
        //planningViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Obtener la referencia al controlador de navegacion
        navController = Navigation.findNavController(view);
    }

    private void setTitleFragment() {

        // Get the Bundle of arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("planification")) {

            Planification planificationCurrent = (Planification) arguments.getSerializable("planification");
            if (planificationCurrent != null) {

                // set title for the action bar dynamically
                ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(String.format("%s", planificationCurrent.getTitle()));

                    detailsPlanifications.clear();

                    loadDetailsPlanificationByCurrentPlanification(planificationCurrent);

                }
            } else Toast.makeText(getContext(), "No hay anda", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBindingWidgets() {
//        spCourses = binding.spTrimestre;
        recyclerView = binding.recycleReViewPlanification;
    }

    private void loadDetailsPlanificationByCurrentPlanification(Planification planification) {

        db.collection(COLLECTION_NAME).whereEqualTo("planification", planification.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                        DetailsPlanification planning = documentSnapshot.toObject(DetailsPlanification.class);
                        if (planning != null) {

                            Log.i("Detail Teacher name", planning.getTeacher() == null ? "ES NULL" : planning.getTeacher().toString());

                            // Toast.makeText(getContext(), "timestamp: " + planning.getTimestampDate() + planifications.size(), Toast.LENGTH_SHORT).show();
                            planning.setUid(documentSnapshot.getId());
                            Log.i("Planning", planning.getUid());

                            // set the planification object
                            planning.setPlanificationObject(planification);

                            detailsPlanifications.add(planning);

                        } else Log.i("Details Planificacion item", "ES NULL");
                    }

                    populatePlaning();

                    Toast.makeText(getContext(), " Details Planificacion size: " + detailsPlanifications.size(), Toast.LENGTH_SHORT).show();
                    //Log.i("Size planning", planifications.size() + "");

                } else
                    Toast.makeText(getContext(), "No hay planificaciones para la semana seleccionado", Toast.LENGTH_SHORT).show();
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
        adapterPlanning = new AdapterReviewDetailPlanning(getContext(), detailsPlanifications);
        recyclerView.setAdapter(adapterPlanning);

        // click en el item
        adapterPlanning.setOnClickListener(view -> {

            // obtene el objeto sobre el cual se hizo click
            DetailsPlanification detailsPlanification = detailsPlanifications.get(recyclerView.getChildAdapterPosition(view));

            // Crear un Bundle para pasar el curso como argumento
            Bundle bundle = new Bundle();

            // Pasar el objeto como argumento
            bundle.putSerializable("detailPlanification", detailsPlanification);

            // Navegar al nuevo fragmento cuando se selecciona un curso, pasando el bundle como argumento
            navController.navigate(R.id.nav_review_detail_planning, bundle);

            //Toast.makeText(getContext(), "Click en el item", Toast.LENGTH_SHORT).show();

        });


//        // click en el boton del item
//        adapterPlanning.setOnButtonClickListener(planification -> {
//
//            // Aquí se ejecutará el evento cuando se presione el botón en el RecyclerView
//            // Puedes acceder a los datos del objeto "planification" y realizar la acción deseada
//
//
//            // Crear un Bundle para pasar el curso como argumento
//            Bundle bundle = new Bundle();
//
//            // Pasar el objeto como argumento
//            bundle.putSerializable("planification", planification);
//
//
////            navController.navigate(R.id.nav_review_planning, bundle);
//
//            Toast.makeText(getContext(), "Click en el boton del item", Toast.LENGTH_SHORT).show();
//
//
//        });


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