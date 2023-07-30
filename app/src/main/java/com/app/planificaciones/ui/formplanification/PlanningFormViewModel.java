package com.app.planificaciones.ui.formplanification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlanningFormViewModel extends ViewModel {

    private final MutableLiveData<String> txtTitle;
    private final MutableLiveData<String> txtDescription;
    private final MutableLiveData<String> txtFileName;


    public PlanningFormViewModel() {
        txtTitle = new MutableLiveData<>();
        txtDescription = new MutableLiveData<>();
        txtFileName = new MutableLiveData<>();


    }

    public MutableLiveData<String> getTxtTitle() {
        return txtTitle;
    }

    public MutableLiveData<String> getTxtDescription() {
        return txtDescription;
    }

    public MutableLiveData<String> getTxtFileName() {
        return txtFileName;
    }
}