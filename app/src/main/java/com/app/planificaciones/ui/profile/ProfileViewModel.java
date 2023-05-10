package com.app.planificaciones.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is prfile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}