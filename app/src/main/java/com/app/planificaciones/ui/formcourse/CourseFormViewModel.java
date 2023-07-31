package com.app.planificaciones.ui.formcourse;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CourseFormViewModel extends ViewModel {

    private final MutableLiveData<String> txtTitle;
    private final MutableLiveData<String> txtName;
    private final MutableLiveData<String> txtParalelo;


    public CourseFormViewModel() {
        txtTitle = new MutableLiveData<>();
        txtName = new MutableLiveData<>();
        txtParalelo = new MutableLiveData<>();

    }

    public MutableLiveData<String> getTxtTitle() {
        return txtTitle;
    }

    public MutableLiveData<String> getTxtName() {
        return txtName;
    }

    public MutableLiveData<String> getTxtParalelo() {
        return txtParalelo;
    }
}