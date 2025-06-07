package com.example.realestate.ui.admin.add_admins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realestate.ui.registration.RegistrationFragment;

/**
 * Legacy wrapper for AddNewAdminFragment that uses the new RegistrationFragment
 * This maintains backward compatibility while using the new unified registration system
 */
public class AddNewAdminFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Create the new RegistrationFragment configured for admin registration
        RegistrationFragment registrationFragment = RegistrationFragment.newInstance(
                true, // isAdmin = true
                "Add New Admin", // title
                "Add Admin" // buttonText
        );

        // Replace this fragment with the registration fragment
        getParentFragmentManager()
                .beginTransaction()
                .replace(getId(), registrationFragment)
                .commit();

        // Return a placeholder view that will be immediately replaced
        return new View(requireContext());
    }
}