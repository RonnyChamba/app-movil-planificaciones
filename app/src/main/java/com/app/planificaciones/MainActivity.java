package com.app.planificaciones;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.planificaciones.models.AuthCredential;

import java.sql.Struct;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnSigIn;
    private EditText txtEmail;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findWidgets();
        setListener();
    }

    private void findWidgets() {
        btnSigIn = findViewById(R.id.btnSigIn);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
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

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);


        /*String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential auth = new AuthCredential(email, password);
        Toast.makeText(this, auth.toString(), Toast.LENGTH_SHORT).show();
        */

    }
}