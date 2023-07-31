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
import com.app.planificaciones.adapters.AdapterReviewDetailPlanning;
import com.app.planificaciones.databinding.FragmentReviewDetailPlanificationBinding;
import com.app.planificaciones.models.DetailsPlanification;
import com.app.planificaciones.models.ModelItemDetail;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReviewDetailPlanification extends Fragment {

    private FragmentReviewDetailPlanificationBinding binding;

    private NavController navController;

    private FirebaseFirestore db = null;

    public static final String COLLECTION_NAME = "details_planification";

    private AdapterDetailRevieTeacherPlanning adapterPlanning;

    private RecyclerView recyclerView;

    private TextView txtTeacher;

    private TextView txtStatusDetailPlanification;

    private Button btnApproveDetailPlanification;

//    private List<DetailsPlanification> detailsPlanifications = new ArrayList<>();

    private List<ModelItemDetail> itemDetails = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReviewDetailPlanificationViewModel planningViewModel =
                new ViewModelProvider(this).get(ReviewDetailPlanificationViewModel.class);

        binding = FragmentReviewDetailPlanificationBinding.inflate(inflater, container, false);
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
        if (arguments != null && arguments.containsKey("detailPlanification")) {

            DetailsPlanification planificationCurrent = (DetailsPlanification) arguments.getSerializable("detailPlanification");
            if (planificationCurrent != null) {

                // set title for the action bar dynamically
                ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Detalle Planificación");

                    loadDetailsPlanificationByCurrentPlanification(planificationCurrent);
                }
                setValueDefault(planificationCurrent);

            } else Toast.makeText(getContext(), "No hay anda", Toast.LENGTH_SHORT).show();
        }
    }

    private void setValueDefault(DetailsPlanification detailsPlanification) {


        Map<String, Object> teacher = detailsPlanification.getTeacher();
        if (teacher != null) {

            String fullName = teacher.get("fullName") == null ? "Docente: NA" : "Docente: " + teacher.get("fullName");
            txtTeacher.setText(fullName);

            txtStatusDetailPlanification.setText("Estado: " + (detailsPlanification.isStatus() ? "Aprobado" : "Pendiente"));
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

            // los demas datos no los necesito

            modelItemDetail.setDateUpload(dateUpload);
            modelItemDetail.setUrl(url);
            modelItemDetail.setStatus(status);
            modelItemDetail.setName(name);
            modelItemDetail.setType(extension);
            itemDetails.add(modelItemDetail);
        }
        populatePlaning();

        Toast.makeText(getContext(), "size items: " + itemDetails.size(), Toast.LENGTH_SHORT).show();


    }

    private void populatePlaning() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterPlanning = new AdapterDetailRevieTeacherPlanning(getContext(), itemDetails);
        recyclerView.setAdapter(adapterPlanning);


        // click en el boton del item de cada fila
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

            downloadFile2(requireContext(), itemDetail.getName(), itemDetail.getType(), Environment.DIRECTORY_DOCUMENTS, uri.toString());
            Toast.makeText(getContext(), "Documento descargado", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(getContext(), "Error al descargar: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadFile2(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

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