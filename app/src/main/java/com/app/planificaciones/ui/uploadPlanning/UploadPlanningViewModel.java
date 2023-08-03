package com.app.planificaciones.ui.uploadPlanning;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UploadPlanningViewModel extends ViewModel {

    private final MutableLiveData<String> txtTitle;
    private final MutableLiveData<String> txtDescription;
    private final MutableLiveData<String> txtFileName;


    public UploadPlanningViewModel() {
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