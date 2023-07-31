package com.app.planificaciones.ui.formplanification;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.adapters.AdapterPlanning;
import com.app.planificaciones.databinding.FragmentFormPlaniBinding;
import com.app.planificaciones.databinding.FragmentPlanningBinding;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.Planification;
import com.app.planificaciones.models.PlanificationDTO;
import com.app.planificaciones.models.Trimestre;
import com.app.planificaciones.util.ConstantApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlanningFormFragment extends Fragment implements View.OnClickListener {

    private FragmentFormPlaniBinding binding;

    private FirebaseFirestore db = null;

    private NavController navController;

    public static final String COLLECTION_NAME = "planifications";

    private Course courseCurrent;

    private Trimestre trimestreCurrent;

    private EditText txtTitle;
    private EditText txtDescription;

    private TextView txtFileName;
    private Button btnSave;
    private Button btnFile;

    private PlanningFormViewModel planningViewModel;

    private Uri fileUri;

    private static final int REQUEST_CODE_FILE_PICKER = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        planningViewModel =
                new ViewModelProvider(this).get(PlanningFormViewModel.class);

        binding = FragmentFormPlaniBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();
        initBinding();
        setTitleFragment();
        setValueScreen();

        planningViewModel.getTxtTitle().observe(getViewLifecycleOwner(), txtTitle::setText);
        planningViewModel.getTxtDescription().observe(getViewLifecycleOwner(), txtDescription::setText);
        planningViewModel.getTxtFileName().observe(getViewLifecycleOwner(), txtFileName::setText);
        return root;
    }

    private void initBinding() {
        txtTitle = binding.txtTitle;
        txtDescription = binding.txtDetails;
        txtFileName = binding.txtFileName;
        btnSave = binding.btnSave;
        btnFile = binding.btnFile;

        btnSave.setOnClickListener(this);
        btnFile.setOnClickListener(this);
    }

    private void setTitleFragment() {

        // Get the Bundle of arguments
        Bundle arguments = getArguments();

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Nueva Planificación");
        }

        if (arguments != null && arguments.containsKey("course")) {
            courseCurrent = (Course) arguments.getSerializable("course");
        }

        if (arguments != null && arguments.containsKey("trimestre")) {
            trimestreCurrent = (Trimestre) arguments.getSerializable("trimestre");
        }

    }

    private void setValueScreen() {

        binding.textCourse.setText(courseCurrent.getName() + " " + courseCurrent.getParallel());
        binding.textTrimestre.setText(trimestreCurrent.getTitle());

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
            uploadFile();
        }

        if (v.getId() == btnFile.getId()) {
            openFilePicker();
        }
    }

    private void savePlanification(Uri fileRef) {

        String title = txtTitle.getText().toString();
        String description = txtDescription.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "El título es requerido", Toast.LENGTH_SHORT).show();
            return;
        }

        PlanificationDTO planification = new PlanificationDTO();
        planification.setTitle(title);
        planification.setDetails(description);
        planification.setDeleted(false);
        planification.setStatus(false);
        planification.setWeek(trimestreCurrent.getUid());

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(calendar.getTime());
        Long timestamp = calendar.getTimeInMillis();
        planification.setDateCreated(date);
        planification.setTimestamp(timestamp);

        // verificar si se subio un archivo, asignar los valores
        if (fileRef != null && fileUri != null) {
            Map<String, Object> resources = new HashMap<>();
            resources.put("url", fileRef.toString());

            String name = getFileName(fileUri);

            String extension = getFileType(fileUri);
            resources.put("name", name);
            resources.put("type", extension);
            planification.getResources().add(resources);
        }

        db.collection(COLLECTION_NAME)
                .add(planification)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Planificación guardada", Toast.LENGTH_SHORT).show();


                    // Crear un documento en la colección  reportes por cada planificación creada
                    Map<String, Object> dataReport = new HashMap<>();

                    dataReport.put("uidPeriodo", ConstantApp.periodo.getUid());
                    dataReport.put("descriptionPerido", ConstantApp.periodo.getTitle());
                    dataReport.put("uidCurso", courseCurrent.getUid());
                    dataReport.put("descriptionCurso", courseCurrent.getName() + " " + courseCurrent.getParallel());
                    dataReport.put("uidPlanification", documentReference.getId());
                    dataReport.put("descriptionPlanification", planification.getDetails());
                    dataReport.put("titlePlanification", planification.getTitle());
                    dataReport.put("uidTrimestre", trimestreCurrent.getUid());
                    dataReport.put("descriptionTrimestre", trimestreCurrent.getTitle());
                    dataReport.put("statusDeleted", false);
                    dataReport.put("dateCreated", planification.getDateCreated());
                    dataReport.put("dateCreatedTimestamp", planification.getTimestamp());
                    dataReport.put("details_planification", new ArrayList<>());

                    db.collection("reportes")
                            .add(dataReport)
                            .addOnSuccessListener(documentReference1 -> {
                                Log.i("TAG Reporte", "DocumentSnapshot added with ID: " + documentReference1.getId());
                                Toast.makeText(getContext(), "Reporte guardado", Toast.LENGTH_SHORT).show();
                                //  regrese a la pantalla anterior
                                navController.popBackStack();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al guardar el reporte", Toast.LENGTH_SHORT).show();
                            });


                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar la planificación", Toast.LENGTH_SHORT).show();
                });
    }


    // Método para abrir el selector de archivos
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Para seleccionar cualquier tipo de archivo
        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getContext(), "onActivityResult", Toast.LENGTH_SHORT).show();

        Log.i("TAG", "onActivityResult code: " + requestCode + " " + resultCode);

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                //Uri fileUri = data.getData();

                // guardar el archivo seleccionado en una variable global
                fileUri = data.getData();

                // Obtener el nombre del archivo
                String fileName = getFileName(fileUri);


                planningViewModel.getTxtFileName().postValue(fileName);

                // Obtener el tipo (extensión) del archivo
                String fileType = getFileType(fileUri);

                Log.i("TAG", "onActivityResult file: " + fileName + " " + fileType);
//
//                // Aquí ya tienes el nombre del archivo y el tipo, ahora puedes proceder a subirlo a Firebase Storage
                //   uploadFile(fileUri, fileName, fileType);
            }
        }
    }

    private void uploadFile() {

        // Verificar si se seleccionó un archivo, si no se seleccionó, se guarda la planificación sin archivo
        if (fileUri != null) {

            // Obtener una referencia al almacenamiento de Firebase
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            String fileName = getFileName(fileUri);

            // Crear una referencia para el archivo en Firebase Storage
            StorageReference fileRef = storageRef.child("myfiles/" + fileName);

            // Subir el archivo a Firebase Storage
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Subida exitosa del archivo, procedemos a guardar la planificación

                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.i("TAG", "onActivityResult file: " + uri.toString());

                            // guardar planificacion, le pasamos la url del archivo subido
                            savePlanification(uri);
                        });


                    })
                    .addOnFailureListener(e -> {
                        // Ocurrió un error en la subida del archivo
                        Toast.makeText(getContext(), "Error al subir el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else savePlanification(null);


    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private String getFileType(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void guardarReferenciaEnFirestore(StorageReference fileRef) {
        // Obtener una referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear un objeto que contenga la referencia del archivo
        Map<String, Object> archivoData = new HashMap<>();
        archivoData.put("url", fileRef.toString());

        // Guardar el objeto en Firestore
        db.collection("archivos")
                .add(archivoData)
                .addOnSuccessListener(documentReference -> {
                    // El objeto se guardó exitosamente en Firestore
                    // Puedes realizar alguna acción adicional aquí si lo deseas
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al guardar el objeto en Firestore
                    Toast.makeText(getContext(), "Error al guardar referencia en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        galleryItem.setVisible(false);

        super.onPrepareOptionsMenu(menu);
    }

}