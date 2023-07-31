package com.app.planificaciones;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.planificaciones.databinding.ActivityHomeBinding;
import com.app.planificaciones.databinding.ActivityMainBinding;
import com.app.planificaciones.models.AuthCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Struct;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSigIn;
    private EditText txtEmail;
    private EditText txtPassword;

    private FirebaseAuth mAuth;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflayar el layout xml
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // obtener el root del layout xml
        setContentView(binding.getRoot());

        //setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findWidgets();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Usuario logeado", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Usuario No logeado", Toast.LENGTH_SHORT).show();
    }

    private void findWidgets() {

        btnSigIn = binding.btnSigIn;
        txtEmail = binding.txtEmail;
        txtPassword = binding.txtPassword;
    }

    private void setListener() {
        btnSigIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int idEvent = v.getId();
        if (idEvent == btnSigIn.getId()) {
            authUser();
        }
    }

    private void authUser() {

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isBlank()) {
            txtEmail.setError("El email es requerido");
            return;
        }
        if (password.isBlank()) {
            txtPassword.setError("La contraseña es requerido");
            return;
        }

        signIn(new AuthCredential(email, password));
    }

    private void signIn(AuthCredential authCredential) {

        mAuth.signInWithEmailAndPassword(authCredential.getEmail(), authCredential.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignIn", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        final String displayName = user == null ? "" : user.getDisplayName();

                        //Toast.makeText(getBaseContext(), "Bienvenido " + displayName, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignIn", "signInWithEmail:failure", task.getException());

                        Toast.makeText(getBaseContext(), "Error, email o contraseña incorrecta",
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }
}