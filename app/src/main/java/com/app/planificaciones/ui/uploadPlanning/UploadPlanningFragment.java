package com.app.planificaciones.ui.uploadPlanning;

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

import com.app.planificaciones.R;
import com.app.planificaciones.databinding.FragmentUploadPlaniBinding;
import com.app.planificaciones.models.Planification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class UploadPlanningFragment extends Fragment implements View.OnClickListener {

    private FragmentUploadPlaniBinding binding;

    private FirebaseFirestore db = null;

    private NavController navController;

    public static final String COLLECTION_NAME = "details_planification";

    private TextView txtDescription;

    private TextView txtFileName;
    private TextView txtTitle;
    private Button btnSave;
    private Button btnFile;

    private UploadPlanningViewModel planningViewModel;

    private Uri fileUri;

    private Planification planificationCurrent;

    private FirebaseUser user;

    private static final int REQUEST_CODE_FILE_PICKER = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        planningViewModel =
                new ViewModelProvider(this).get(UploadPlanningViewModel.class);

        binding = FragmentUploadPlaniBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        user = FirebaseAuth.getInstance().getCurrentUser();

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

        txtTitle = binding.textTitle;
        txtDescription = binding.textDetails;
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
            actionBar.setTitle("Subir Planificación");
        }

        if (arguments != null && arguments.containsKey("planification")) {
            planificationCurrent = (Planification) arguments.getSerializable("planification");
            //Toast.makeText(getContext(), planificationCurrent.getUid() + "", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), planificationCurrent.getDetailsPlanification().size() + "", Toast.LENGTH_SHORT).show();
        }

    }

    private void setValueScreen() {

        planningViewModel.getTxtTitle().postValue(planificationCurrent.getTitle());
        planningViewModel.getTxtDescription().postValue(planificationCurrent.getDetails());

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

    private void uploadPlanification(Uri fileRef) {

        // verificar si la actual planificacion tiene  ya almenos subido un details_planification

        // Indica que la planificacion no tiene ningun details_planification, por lo tanto  es
        // el primer details_planification que se sube para esta planificacion
        if (planificationCurrent.getDetailsPlanification().size() == 0) {


            Map<String, Object> data = new HashMap<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

            Calendar calendar = Calendar.getInstance();

            String dateUpload = dateFormat.format(calendar.getTime());
            Long dateCreatedMiles = calendar.getTimeInMillis();

            data.put("dateCreated", dateUpload);
            data.put("observation", "");
            data.put("status", false);
            data.put("planification", planificationCurrent.getUid());

            Map<String, Object> dataTeacher = new HashMap<>();

            dataTeacher.put("email", user.getEmail());
            dataTeacher.put("uid", user.getUid());
            dataTeacher.put("fullName", user.getDisplayName());

            data.put("teacher", dataTeacher);

            List<Map<String, Object>> listItems = new ArrayList<>();

            listItems.add(Map.of(
                    "dateCreated", dateCreatedMiles,
                    "dateUpload", dateUpload,
                    "name", getFileName(fileUri),
                    "observation", "",
                    "status", false,
                    "type", getFileType(fileUri),
                    "url", fileRef.toString()
            ));

            data.put("items", listItems);

            Map<String, Object> resources = new HashMap<>();

            resources.put("name", getFileName(fileUri));
            resources.put("type", getFileType(fileUri));
            resources.put("url", fileRef.toString());

            data.put("resource", resources);

            db.collection(COLLECTION_NAME)
                    .add(data)
                    .addOnSuccessListener(documentReference -> {


                        // actualiza la tabla planificación  el campo details_planificación
                        List<Map<String, Object>> details = new ArrayList<>();
                        details.add(Map.of(
                                "details_uid", documentReference.getId(),
                                "teacher_uid", user.getUid(),
                                "status", false
                        ));

                        Toast.makeText(getContext(), "Planificación subida", Toast.LENGTH_SHORT).show();

                        db.collection("planifications")
                                .document(planificationCurrent.getUid())
                                .update("details_planification", details)
                                .addOnSuccessListener(aVoid -> {
                                    //Toast.makeText(this.getContext(), "Planificación actualizada", Toast.LENGTH_SHORT).show();

                                    createReporteLaterPlanification(dateUpload, dateCreatedMiles);

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al actualizar la planificación", Toast.LENGTH_SHORT).show();
                                });

                        //  regrese a la pantalla anterior
                        navController.popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al subir la planificación", Toast.LENGTH_SHORT).show();
                    });

        } else uploadPlanificationWhenAlready(fileRef);


    }

    private void createReporteLaterPlanification(String dateUpload, Long dateCreatedMiles) {

        // item del campo details_planificación de un documento en reporte
        Map<String, Object> itemDetailsPlanification = new HashMap<>();
        itemDetailsPlanification.put("countUpload", "1");
        itemDetailsPlanification.put("dateCreated", dateUpload);
        itemDetailsPlanification.put("dateCreatedTime", dateCreatedMiles);
        itemDetailsPlanification.put("fullName", user.getDisplayName());
        itemDetailsPlanification.put("status", false);
        itemDetailsPlanification.put("uid_teacher", user.getUid());

        // consultar un documento en reportes donde el campo uidPlanification sea igual al uid de la planificación actual
        // y actualizar el campo details_planification

        db.collection("reportes")
                .whereEqualTo("uidPlanification", planificationCurrent.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {

                        //Toast.makeText(getContext(), "Reporte encontrado", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getContext(), queryDocumentSnapshots.getDocuments().get(0).getId(), Toast.LENGTH_SHORT).show();

                        // Obtener el campo details_planification del documento encontrado

                        QuerySnapshot querySnapshot = queryDocumentSnapshots;

                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        Log.i("TAG ReporteDetails", "documents: " + documents.size());


                        if (documents.size() > 0) {

                            DocumentSnapshot document = documents.get(0);

                            Log.i("TAG ReporteDetails", "document ide: " + document.getId());
                            List<Map<String, Object>> detailsPlanification = (List<Map<String, Object>>) document.get("details_planification");

                            if (detailsPlanification == null || detailsPlanification.size() == 0) {
                                detailsPlanification = new ArrayList<>();
                                Log.d("TAG ReporteDetails", "detailsPlanification es null");
                            }

                            detailsPlanification.add(itemDetailsPlanification);

                            updateItemDetailReport(detailsPlanification, document);

                            Log.d("TAG ReporteDetails", "detailsPlanification fin: ");

                        }


                    } else {
                        Toast.makeText(getContext(), "Reporte no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al buscar el reporte", Toast.LENGTH_SHORT).show();
                });

    }


    private void updateItemDetailReport(List<Map<String, Object>> detailsPlanification, DocumentSnapshot documentSnapshot) {

        db.collection("reportes")
                .document(documentSnapshot.getId())
                .update("details_planification", detailsPlanification)
                .addOnSuccessListener(aVoid1 -> {
                    Log.d("TAG ReporteDetails", "detailsPlanification actualizado");
                    //Toast.makeText(getContext(), "Reporte actualizado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al actualizar el reporte", Toast.LENGTH_SHORT).show();
                });

    }


    /**
     * Método para subir la planificaciona  pero cuando la planificación ya tiene una a mas planificaciones subidas
     */
    private void uploadPlanificationWhenAlready(Uri fileRef) {


        // Obtener el campo details_planification de la planificación actual

        List<Map<String, Object>> detailsPlanification = planificationCurrent.getDetailsPlanification();

        // verificar si el usuario actual ya subió la planificación

        boolean teacherAlreadyUpload = detailsPlanification
                .stream()
                .anyMatch(item -> {
                    Object uidTeacher = item.get("teacher_uid");
                    if (uidTeacher != null) {
                        return uidTeacher.equals(user.getUid());
                    }
                    return false;
                });


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();

        String dateUpload = dateFormat.format(calendar.getTime());
        Long dateCreatedMiles = calendar.getTimeInMillis();


        // si el usuario actual ya subió una  planificación, ya tiene un item en el campo details_planification de la planificación actual
        // es decir, ya tien  un documento en la coleccion detalles_planification
        if (teacherAlreadyUpload) {


            // obtener el item del campo details_planification de la planificación actual donde el campo teacher_uid sea igual al uid del usuario actual
            Map<String, Object> itemDetailsPlanification = detailsPlanification
                    .stream()
                    .filter(item -> {
                        Object uidTeacher = item.get("teacher_uid");
                        if (uidTeacher != null) {
                            return uidTeacher.equals(user.getUid());
                        }
                        return false;
                    })
                    .collect(Collectors.toList())
                    .get(0);

            // obtener el campo uid del item encontrado
            String uidDetailsPlanification = (String) itemDetailsPlanification.get("details_uid");

            // crear un nuevo item para el campo items del documento en detalles_planification
            Map<String, Object> newItem = Map.of(
                    "dateCreated", dateCreatedMiles,
                    "dateUpload", dateUpload,
                    "name", getFileName(fileUri),
                    "observation", "",
                    "status", false,
                    "type", getFileType(fileUri),
                    "url", fileRef.toString()
            );

            // consultar el documento en detalles_planification donde el campo uid sea igual al uid del item encontrado
            db.collection("details_planification")
                    .document(uidDetailsPlanification)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            // obtener el campo items del documento encontrado

                            List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");

                            if (items == null || items.size() == 0) {
                                Log.d("TAG", "items es null");
                                items = new ArrayList<>();
                            }

                            items.add(newItem);

                            // actualizar el campo items del documento encontrado
                            db.collection("details_planification")
                                    .document(uidDetailsPlanification)
                                    .update("items", items)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.i("TAG", "items actualizado");
                                        Toast.makeText(getContext(), "Planificación actualizada", Toast.LENGTH_SHORT).show();

                                        // Obtener el documento de repoporte donde el campo uidPlanification sea igual al uid de la planificación actual
                                        db.collection("reportes")
                                                .whereEqualTo("uidPlanification", planificationCurrent.getUid())
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {

                                                            // Obtener el campo details_planification del documento encontrado

                                                            QuerySnapshot querySnapshot = queryDocumentSnapshots;

                                                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                                            DocumentSnapshot document = documents.get(0);

                                                            List<Map<String, Object>> detailsPlanificationRe = (List<Map<String, Object>>) document.get("details_planification");


                                                            if (detailsPlanificationRe != null && detailsPlanificationRe.size() > 0) {

                                                                // buscar el item donde el campo teacher_uid sea igual al uid del usuario actual

                                                                Map<String, Object> itemDetailsPlanificationRe = detailsPlanificationRe
                                                                        .stream()
                                                                        .filter(item -> {
                                                                            Object uidTeacher = item.get("uid_teacher");
                                                                            if (uidTeacher != null) {
                                                                                return uidTeacher.equals(user.getUid());
                                                                            }
                                                                            return false;
                                                                        })
                                                                        .collect(Collectors.toList())
                                                                        .get(0);

                                                                Log.i("TAG ReporteDetails", "itemDetailsPlanificationRe: " + itemDetailsPlanificationRe);


                                                                Long countUpload = itemDetailsPlanificationRe.get("countUpload") == null ? 0 :
                                                                        Long.parseLong(itemDetailsPlanificationRe.get("countUpload").toString());

                                                                Log.i("TAG ReporteDetails", "countUpload: " + countUpload);

                                                                // actualizar el campo countUpload del item encontrado
                                                                itemDetailsPlanificationRe.put("countUpload", countUpload + 1);

                                                                // actualizar el campo details_planification del documento encontrado
                                                                db.collection("reportes")
                                                                        .document(document.getId())
                                                                        .update("details_planification", detailsPlanificationRe)
                                                                        .addOnSuccessListener(aVoid1 -> {
                                                                            Log.d("TAG ReporteDetails", "detailsPlanification actualizado item reportes");
                                                                            //Toast.makeText(getContext(), "Reporte actualizado", Toast.LENGTH_SHORT).show();

                                                                            // ir a la pantalla de reportes
                                                                            navController.popBackStack();
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Toast.makeText(getContext(), "Error al actualizar el reporte", Toast.LENGTH_SHORT).show();
                                                                        });


                                                            } else {
                                                                Log.d("TAG ReporteDetails", "detailsPlanification es null");
                                                            }

                                                            Log.i("TAG ReporteDetails", "documents: " + documents.size());
                                                        }
                                                );

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error al actualizar la planificación", Toast.LENGTH_SHORT).show();
                                    });


                        } else {
                            Toast.makeText(getContext(), "Documento no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al buscar el documento", Toast.LENGTH_SHORT).show();
                    });


        } else teacherNoSubidoPlanification(fileRef);


    }

    private void teacherNoSubidoPlanification(Uri fileRef) {


        Map<String, Object> data = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();

        String dateUpload = dateFormat.format(calendar.getTime());
        Long dateCreatedMiles = calendar.getTimeInMillis();

        data.put("dateCreated", dateUpload);
        data.put("observation", "");
        data.put("status", false);
        data.put("planification", planificationCurrent.getUid());

        Map<String, Object> dataTeacher = new HashMap<>();

        dataTeacher.put("email", user.getEmail());
        dataTeacher.put("uid", user.getUid());
        dataTeacher.put("fullName", user.getDisplayName());

        data.put("teacher", dataTeacher);

        List<Map<String, Object>> listItems = new ArrayList<>();

        listItems.add(Map.of(
                "dateCreated", dateCreatedMiles,
                "dateUpload", dateUpload,
                "name", getFileName(fileUri),
                "observation", "",
                "status", false,
                "type", getFileType(fileUri),
                "url", fileRef.toString()
        ));

        data.put("items", listItems);

        Map<String, Object> resources = new HashMap<>();

        resources.put("name", getFileName(fileUri));
        resources.put("type", getFileType(fileUri));
        resources.put("url", fileRef.toString());

        data.put("resource", resources);

        db.collection(COLLECTION_NAME)
                .add(data)
                .addOnSuccessListener(documentReference -> {

                    // ahora agregar un nuevo item en la planificación actual

                    // actualiza la tabla planificación  el campo details_planificación
                    Map<String, Object> details = Map.of(
                            "details_uid", documentReference.getId(),
                            "teacher_uid", user.getUid(),
                            "status", false
                    );

                    // // buscar la planificacion actual

                    db.collection("planifications")
                            .document(planificationCurrent.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {


                                if (documentSnapshot.exists()) {

                                    // Obtener e documento


                                    List<Map<String, Object>> listDetailsPlanification = (List<Map<String, Object>>) documentSnapshot.get("details_planification");


                                    if (listDetailsPlanification == null) {
                                        listDetailsPlanification = new ArrayList<>();
                                    }

                                    listDetailsPlanification.add(details);


                                    // actulizar el campo details_planification del documento(planification) encontrado

                                    db.collection("planifications")
                                            .document(planificationCurrent.getUid())
                                            .update("details_planification", listDetailsPlanification)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d("TAG ReporteDetails", "detailsPlanification actualizado item planifications");
                                                //Toast.makeText(getContext(), "Reporte actualizado", Toast.LENGTH_SHORT).show();

                                                // ir a la pantalla de reportes
                                                navController.popBackStack();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al actualizar la planificación", Toast.LENGTH_SHORT).show();
                                            });
                                }

                                Log.i("TAG ReporteDetails", "planificationCurrent: " + planificationCurrent);

                                // crear un item en la tabla reportes con el uid de la planificación actual
                                createReporteLaterPlanification(dateUpload, dateCreatedMiles);

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar la planificación", Toast.LENGTH_SHORT).show();
                            });


                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al subir el archivo", Toast.LENGTH_SHORT).show();
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


//        uploadPlanification(null);

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
                            uploadPlanification(uri);
                        });


                    })
                    .addOnFailureListener(e -> {
                        // Ocurrió un error en la subida del archivo
                        Toast.makeText(getContext(), "Error al subir el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else
            Toast.makeText(getContext(), "Seleccione un archivo a subir", Toast.LENGTH_SHORT).show();


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