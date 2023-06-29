package com.app.planificaciones.services;

import android.util.Log;

import com.app.planificaciones.models.Periodo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ServicePeriodo {

    public static final String COLLECTION_NAME = "periodos";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionRef = db.collection(COLLECTION_NAME);

    public List<Periodo> findAllPeriodo() {

        List<Periodo> dataList = new ArrayList<>();

        collectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Obtener los datos de cada documento y agregarlos a la lista

                Periodo periodo = document.toObject(Periodo.class);
                periodo.setUid(document.getId());
                dataList.add(periodo);
            }

            Log.i("SIZE PERIODOS EN SERVICIO", dataList.size() + "");
        });


        Log.i("findAllPeridoso FINAL ", dataList.toString());
        return dataList;
    }


}
