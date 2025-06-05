package com.example.realestate.ui.user.contactus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactUsViewModel extends ViewModel {

    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> locationUrl;

    public ContactUsViewModel() {
        phoneNumber = new MutableLiveData<>();
        email = new MutableLiveData<>();
        locationUrl = new MutableLiveData<>();

        // Set default contact information
        phoneNumber.setValue("+970599000000");
        email.setValue("RealEstateHub@agency.com");
        locationUrl.setValue("geo:31.5017,34.4668?q=Real+Estate+Agency");
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getLocationUrl() {
        return locationUrl;
    }
}
