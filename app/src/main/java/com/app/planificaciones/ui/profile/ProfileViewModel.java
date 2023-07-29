package com.app.planificaciones.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> title;

    private final MutableLiveData<String> cedula;

    private final MutableLiveData<String> nombres;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> apellidos;
    private final MutableLiveData<String> telefono;
    private final MutableLiveData<String> titulos;


    public ProfileViewModel() {
        title = new MutableLiveData<>();
        cedula = new MutableLiveData<>();
        nombres = new MutableLiveData<>();
        email = new MutableLiveData<>();
        apellidos = new MutableLiveData<>();
        telefono = new MutableLiveData<>();
        titulos = new MutableLiveData<>();

        title.setValue("Mis Datos Personales");
    }


    public MutableLiveData<String> getTitle() {
        return title;
    }

    public MutableLiveData<String> getCedula() {
        return cedula;
    }

    public MutableLiveData<String> getNombres() {
        return nombres;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getApellidos() {
        return apellidos;
    }

    public MutableLiveData<String> getTelefono() {
        return telefono;
    }

    public MutableLiveData<String> getTitulos() {
        return titulos;
    }

}