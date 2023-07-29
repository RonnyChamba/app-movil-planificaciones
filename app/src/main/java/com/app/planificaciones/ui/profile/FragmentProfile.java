package com.app.planificaciones.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.planificaciones.MainActivity;
import com.app.planificaciones.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentProfile extends Fragment implements View.OnClickListener {

    private FragmentProfileBinding binding;

    private TextView textView;
    private EditText txtCedula;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtLastName;
    private EditText txtPhone;

    private EditText txtTitles;

    private Button btnSave;

    private ProfileViewModel profileViewModel;

    private FirebaseFirestore db = null;

    public static final String COLLECTION_NAME = "teachers";

    private FirebaseUser user;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        initBindings();
        setListeners();
        setObservers();

        selectDataProfile();

        return root;
    }

    private void initBindings() {

        textView = binding.textProfile;
        txtCedula = binding.txtCedula;
        txtName = binding.txtName;
        txtEmail = binding.txtEmail;
        txtLastName = binding.txtLastName;
        txtPhone = binding.txtPhone;

        btnSave = binding.btnTest;

        db = FirebaseFirestore.getInstance();

        getCurrentUser();
    }

    private void getCurrentUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        // verificar si el usuario esta logueado
        if (user == null) {
            // si no esta logueado, redirigir al login
//            Intent intent = new Intent(, MainActivity.class);

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            onDestroyView();
        }
    }


    private void setListeners() {
        btnSave.setOnClickListener(this);
    }

    private void setObservers() {
        profileViewModel.getTitle().observe(getViewLifecycleOwner(), textView::setText);
        profileViewModel.getCedula().observe(getViewLifecycleOwner(), txtCedula::setText);
        profileViewModel.getNombres().observe(getViewLifecycleOwner(), txtName::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), txtEmail::setText);
        profileViewModel.getApellidos().observe(getViewLifecycleOwner(), txtLastName::setText);
        profileViewModel.getTelefono().observe(getViewLifecycleOwner(), txtPhone::setText);

    }


    private void selectDataProfile() {


        final String uidUser = user.getUid();

        db.collection(COLLECTION_NAME).document(uidUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    String cedula = task.getResult().getString("dni");
                    String nombres = task.getResult().getString("displayName");
                    String apellidos = task.getResult().getString("lastName");
                    String email = task.getResult().getString("email");
                    String telefono = task.getResult().getString("phoneNumber");


                    profileViewModel.getNombres().postValue(nombres);
                    profileViewModel.getApellidos().postValue(apellidos);
                    profileViewModel.getCedula().postValue(cedula);
                    profileViewModel.getEmail().postValue(email);
                    profileViewModel.getTelefono().postValue(telefono);

                } else {
                    Toast.makeText(getActivity(), "No existe el docente", Toast.LENGTH_SHORT).show();
                }
            } else {
                String error = task.getException().getMessage();
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == btnSave.getId()) {
            updateData();
        }
    }

    private void updateData() {


        final String uidUser = user.getUid();

        String cedula = txtCedula.getText().toString();
        String nombres = txtName.getText().toString();
        String apellidos = txtLastName.getText().toString();
        String email = txtEmail.getText().toString();
        String telefono = txtPhone.getText().toString();

        db.collection(COLLECTION_NAME).document(uidUser).update("dni", cedula,
                "displayName", nombres,
                "lastName", apellidos,
                "phoneNumber", telefono).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // update displayName in firebase auth
                user.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(nombres + " " + apellidos)
                        .build());

                profileViewModel.getNombres().postValue(nombres);
                profileViewModel.getApellidos().postValue(apellidos);
                profileViewModel.getCedula().postValue(cedula);
                profileViewModel.getEmail().postValue(email);
                profileViewModel.getTelefono().postValue(telefono);

                Toast.makeText(getActivity(), "Datos actualizados", Toast.LENGTH_SHORT).show();
            } else {
                String error = task.getException().getMessage();
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}