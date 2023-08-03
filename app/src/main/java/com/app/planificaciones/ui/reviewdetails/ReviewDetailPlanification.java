package com.app.planificaciones.ui.reviewdetails;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterDetailRevieTeacherPlanning;
import com.app.planificaciones.databinding.FragmentReviewDetailPlanificationBinding;
import com.app.planificaciones.models.DetailsPlanification;
import com.app.planificaciones.models.ModelItemDetail;
import com.app.planificaciones.models.Planification;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewDetailPlanification extends Fragment {

    private FragmentReviewDetailPlanificationBinding binding;

    private NavController navController;

    private FirebaseFirestore db = null;

    public static final String COLLECTION_NAME = "details_planification";

    private AdapterDetailRevieTeacherPlanning adapterPlanning;

    private RecyclerView recyclerView;

    private TextView txtTeacher;

    private TextView txtStatusDetailPlanification;

    private FirebaseUser user;

    private Button btnApproveDetailPlanification;

//    private List<DetailsPlanification> detailsPlanifications = new ArrayList<>();

    private List<ModelItemDetail> itemDetails = new ArrayList<>();

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReviewDetailPlanificationViewModel planningViewModel =
                new ViewModelProvider(this).get(ReviewDetailPlanificationViewModel.class);

        binding = FragmentReviewDetailPlanificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        context = getContext();
        user = FirebaseAuth.getInstance().getCurrentUser();

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

        // set title for the action bar dynamically
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Detalle Planificación");
        }

        if (ConstantApp.isAdmin) initWhenIsAdminUser();
        else initWhenIsNormalUser();

        // verificar si es usuario o admin
        // si es admin: llega ya la lista de detalles de planificaciones a mostrar
        // si es usuario: llega solo llega la planificacion, toca consultar los detalles de planificacion


    }

    private void initWhenIsNormalUser() {

        // Get the Bundle of arguments
        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("planification")) {

            List<DetailsPlanification> detailsPlanifications = new ArrayList<>();

            Planification planificationCurrent = (Planification) arguments.getSerializable("planification");

            if (planificationCurrent != null) {


                db.collection(COLLECTION_NAME)
                        .whereEqualTo("planification", planificationCurrent.getUid())
                        .whereEqualTo("teacher.uid", user.getUid())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null) {
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                                        DetailsPlanification detailsPlanningItem = documentSnapshot.toObject(DetailsPlanification.class);
                                        if (detailsPlanningItem != null) {

                                            Log.i("Detail Teacher name", detailsPlanningItem.getTeacher() == null ? "ES NULL" : detailsPlanningItem.getTeacher().toString());

                                            // Toast.makeText(getContext(), "timestamp: " + planning.getTimestampDate() + planifications.size(), Toast.LENGTH_SHORT).show();
                                            detailsPlanningItem.setUid(documentSnapshot.getId());
                                            Log.i("Planning", detailsPlanningItem.getUid());

                                            // set the planification object
                                            detailsPlanningItem.setPlanificationObject(planificationCurrent);

                                            detailsPlanifications.add(detailsPlanningItem);

                                        } else Log.i("Details Planificacion item", "ES NULL");
                                    }

                                    if (detailsPlanifications.size() > 0) {

                                        loadDetailsPlanificationByCurrentPlanification(detailsPlanifications.get(0));
                                        setValueDefault(detailsPlanifications.get(0));
                                    } else
                                        Toast.makeText(context, "No hay planificaciones subida aún", Toast.LENGTH_SHORT).show();

                                    //populatePlaning();

//                                    Toast.makeText(getContext(), " Details Planificacion size: " + detailsPlanifications.size(), Toast.LENGTH_SHORT).show();
                                    //Log.i("Size planning", planifications.size() + "");

                                } else
                                    Toast.makeText(getContext(), "No se encontro detalles", Toast.LENGTH_SHORT).show();
                            } else {
                                // Maneja el error
                                Exception exception = task.getException();
                                if (exception != null) {
                                    Log.e("Firestore", "Error al obtener documentos planificaciones: " + exception.getMessage());

                                    Toast.makeText(getContext(), "Error al cargar las planificaciones", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            } else Toast.makeText(getContext(), "No hay anda", Toast.LENGTH_SHORT).show();
        }

    }


    private void initWhenIsAdminUser() {


        // Get the Bundle of arguments
        Bundle arguments = getArguments();

        if (arguments != null && arguments.containsKey("detailPlanification")) {

            DetailsPlanification planificationCurrent = (DetailsPlanification) arguments.getSerializable("detailPlanification");
            if (planificationCurrent != null) {

                loadDetailsPlanificationByCurrentPlanification(planificationCurrent);

                setValueDefault(planificationCurrent);

            } else Toast.makeText(getContext(), "No hay anda", Toast.LENGTH_SHORT).show();
        }

    }

    private void setValueDefault(DetailsPlanification detailsPlanification) {


        Map<String, Object> teacher = detailsPlanification.getTeacher();
        if (teacher != null) {

            String fullName = teacher.get("fullName") == null ? "No asignado" : "" + teacher.get("fullName");
            txtTeacher.setText(fullName);

            txtStatusDetailPlanification.setText((detailsPlanification.isStatus() ? "Aprobado" : "Pendiente"));
//            btnApproveDetailPlanification.setText(
//                    detailsPlanification.isStatus() ? "Rechazar" : "Aprobar");

        } else Log.i("Detail Teacher name", "ES NULL");

    }

    private void setBindingWidgets() {
        recyclerView = binding.recycleReViewDetailPlanification;
        txtTeacher = binding.txtNameTeacherDetail;
        txtStatusDetailPlanification = binding.txtStatusCurrentDetail;
//        btnApproveDetailPlanification = binding.btnChangeStatus;
    }

    private void loadDetailsPlanificationByCurrentPlanification(DetailsPlanification planification) {


        for (Map<String, Object> item : planification.getItems()) {

            ModelItemDetail modelItemDetail = new ModelItemDetail();

            String dateUpload = item.get("dateUpload") == null ? "NA" : "" + item.get("dateUpload");
            String url = item.get("url") == null ? "NA" : "" + item.get("url");
            boolean status = item.get("status") != null && (boolean) item.get("status");

            String name = item.get("name") == null ? "NA" : "" + item.get("name");
            String extension = item.get("type") == null ? "NA" : "" + item.get("type");


            String observation = item.get("observation") == null ? ""
                    : "" + item.get("observation");


            // los demas datos no los necesito

            modelItemDetail.setDateUpload(dateUpload);
            modelItemDetail.setUrl(url);
            modelItemDetail.setStatus(status);
            modelItemDetail.setName(name);
            modelItemDetail.setType(extension);
            modelItemDetail.setObservation(observation.equals("") ? "Sin Observación" : observation);

            itemDetails.add(modelItemDetail);
        }
        populatePlaning();

        // Toast.makeText(getContext(), "size items: " + itemDetails.size(), Toast.LENGTH_SHORT).show();


    }

    private void populatePlaning() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterPlanning = new AdapterDetailRevieTeacherPlanning(getContext(), itemDetails);
        recyclerView.setAdapter(adapterPlanning);


        // click en el boton descagar del item de cada fila
        adapterPlanning.setOnButtonClickListener(this::downloadFile);
    }

    private void downloadFile(ModelItemDetail itemDetail) {


        // Crea una instancia de FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Obtén una referencia al archivo en Firebase Storage
        StorageReference storageRef = storage.getReference();

        //Toast.makeText(requireContext(), itemDetail.getName() + itemDetail.getType(), Toast.LENGTH_SHORT).show();

        // El nombre del archivo en este caso ya trae su extension
        // especificar la ruta de la carpeta donde se guardara el archivo, si se guardara en la raiz no es necesario
        StorageReference islandRef = storageRef.child("myfiles/" + itemDetail.getName());

        islandRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL for 'users/me/profile.png'

            executeDownloadFile(requireContext(), itemDetail.getName(), itemDetail.getType(), Environment.DIRECTORY_DOCUMENTS, uri.toString());
            Toast.makeText(getContext(), "Documento descargado", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(getContext(), "Error al descargar: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void executeDownloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

//        Toast.makeText(getContext(), fileName, Toast.LENGTH_SHORT).show();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
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