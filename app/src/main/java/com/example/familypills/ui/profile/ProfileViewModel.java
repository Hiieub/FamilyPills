package com.example.familypills.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>("Hi");
    private final MutableLiveData<String> userEmail = new MutableLiveData<>("hi.nguyen@example.com");

    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getUserEmail() { return userEmail; }
}
