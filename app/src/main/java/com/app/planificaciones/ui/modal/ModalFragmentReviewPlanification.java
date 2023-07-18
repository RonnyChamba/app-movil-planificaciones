package com.app.planificaciones.ui.modal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.app.planificaciones.R;

import java.util.Objects;

public class ModalFragmentReviewPlanification extends DialogFragment {


    public ModalFragmentReviewPlanification() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Obtener instancia de la action bar
        ActionBar actionBar = ((AppCompatActivity) requireActivity())
                .getSupportActionBar();

        if (actionBar != null) {
            // Habilitar el Up Button
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Cambiar icono del Up Button
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_close);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_default, container, false);
        //iniciarHora(view);// Setear hora inicial
        //iniciarFecha(view);// Setear fecha inicial
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // procesarDescartar()
                break;
//            case R.id.btnSigIn:
//                // procesarGuardar()
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_default, null);

        // Establecer las dimensiones del diálogo modal
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        builder.setView(view)
                .setTitle("Título de la pantalla modal")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Acciones a realizar al hacer clic en el botón "Aceptar"
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Acciones a realizar al hacer clic en el botón "Cancelar"
                });

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_default, container, false);

        // Establecer las dimensiones del diálogo modal
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        return view;
    }*/

}
