package com.traveller.traintraverse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.traveller.TravellerSession;
import com.traveller.models.Traveler;
import com.traveller.models.UpdateUserRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    final static String TAG = "ProfileFragment";
    AppCompatEditText nicET, fullNameET, emailET, phoneET;
    MaterialButton updateProfileBtn, cancelBtn, updatePasswordBtn, logOutBtn, deactivateAccountBtn;
    View view;

    private ArrayList<String> fieldNames = new ArrayList<>(3);

    ArrayList<String> profileDetails = new ArrayList<>(5);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeProfileFragmentElements();
        return view;
    }

    private void addEditableFieldsToArrayList() {
        fieldNames.add(getString(R.string.name_field_hint));
        fieldNames.add(getString(R.string.email_field_hint));
        fieldNames.add(getString(R.string.phone_number_field_hint));
    }

    private void initializeProfileFragmentElements() {
        nicET = view.findViewById(R.id.nicET);
        fullNameET = view.findViewById(R.id.fullNameET);
        emailET = view.findViewById(R.id.emailET);
        phoneET = view.findViewById(R.id.phoneET);

        updateProfileBtn = view.findViewById(R.id.updateProfileBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        deactivateAccountBtn = view.findViewById(R.id.deactivateAccountBtn);

        updateProfileBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        updatePasswordBtn.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
        deactivateAccountBtn.setOnClickListener(this);
        retrieveProfileDetails();
        setInitialFieldValues();
    }

    private void retrieveProfileDetails() {
        TravellerSession session = TravellerSession.getInstance();
        Traveler t = session.getTraveler();
        profileDetails.add(t.nic);
        profileDetails.add(t.firstName);
        profileDetails.add(t.lastName);
        profileDetails.add(t.email);
        profileDetails.add(t.phoneNumber);
    }

    private void setInitialFieldValues() {
        nicET.setText(profileDetails.get(0)); //nic
        fullNameET.setText(profileDetails.get(1) + " " + profileDetails.get(2)); //first name + last name
        emailET.setText(profileDetails.get(3)); //email
        phoneET.setText(profileDetails.get(4)); //phone
    }


    private void toggleEditAccessToFields(Boolean isEditable) {
        fullNameET.setEnabled(isEditable);
        emailET.setEnabled(isEditable);
        phoneET.setEnabled(isEditable);
        updateProfileBtn.setText(isEditable ? getString(R.string.save_button) : getString(R.string.edit_profile_button));
        cancelBtn.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    private String getFieldValue(AppCompatEditText field) {
        return field.getText().toString().trim();
    }

    private boolean hasEmptyField(AppCompatEditText field, String fieldName) {
        if (getFieldValue(field).isEmpty()) {
            field.setError(fieldName + " " + getString(R.string.cannot_be_empty_text));
            field.requestFocus();
            return true;
        }
        return false;
    }

    private boolean validateFields() {
        addEditableFieldsToArrayList();
        removeValidationErrors();
        if(hasEmptyField(fullNameET, fieldNames.get(0)) || hasEmptyField(emailET, fieldNames.get(1))) return false;
        if(!getFieldValue(fullNameET).matches("^[a-zA-Z\\u00C0-\\u00ff']+([ a-zA-Z\\u00C0-\\u00ff']+)*$")){
            fullNameET.setError(getString(R.string.invalid_name_error_text));
            fullNameET.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(getFieldValue(emailET)).matches()){
            emailET.setError(getString(R.string.invalid_email_error_text));
            emailET.requestFocus();
            return false;
        }
        if(hasEmptyField(phoneET, fieldNames.get(2))) return false;
        if(!getFieldValue(phoneET).matches("^(\\+94)?0?[0-9]{9}$")){
            phoneET.setError(getString(R.string.invalid_phone_number_error_text));
            phoneET.requestFocus();
            return false;
        }
        return true;
    }

    private void removeValidationErrors() {
        fullNameET.setError(null);
        emailET.setError(null);
        phoneET.setError(null);
    }

    private void updateProfile() {
        Boolean isEditable = updateProfileBtn.getText().toString().equals("Edit Profile");
        if (!isEditable) {
            if(!validateFields()) return;
            String firstName = getFieldValue(fullNameET);
            String lastName = getFieldValue(fullNameET);
            String email = getFieldValue(emailET);
            String phoneNumber = getFieldValue(phoneET);

            UpdateUserRequest.UpdateUserRequestBuilder builder = UpdateUserRequest.builder();

            builder.username(firstName);
            builder.firstName(firstName);
            builder.email(email);
            builder.lastName(lastName);
            builder.phoneNumber(phoneNumber);

            try {
                Traveler traveler = TravellerSession.getInstance().getTraveler();

                Call<Void> call = RetrofitClient.getInstance().getMyApi().updateUser(traveler.nic, builder.build());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        try {
                            if(response.isSuccessful()) {
                                profileDetails.set(1, getFieldValue(fullNameET).split(" ")[0]);
                                profileDetails.set(2, getFieldValue(fullNameET).split(" ")[1]);
                                profileDetails.set(3, getFieldValue(emailET));
                                profileDetails.set(4, getFieldValue(phoneET));
                                Toast.makeText(getActivity(), getString(R.string.profile_update_success_message), Toast.LENGTH_SHORT).show();

                                Traveler t = TravellerSession.getInstance().getTraveler();
                                t.email = email;
                                t.firstName = firstName;
                                t.lastName = lastName;
                                t.phoneNumber = phoneNumber;
                                t.username = firstName;

                                TravellerSession.getInstance().setTraveler(t);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.profile_update_fail_message), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.e(TAG, "onResponse: ", e);
                            Toast.makeText(getActivity(), getString(R.string.profile_update_fail_message), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Toast.makeText(getActivity(), getString(R.string.profile_update_fail_message), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "updateProfile: ", e);
                Toast.makeText(getActivity(), getString(R.string.profile_update_fail_message), Toast.LENGTH_SHORT).show();
            }
        }
        toggleEditAccessToFields(isEditable);
    }

    private void cancelUpdate() {
        toggleEditAccessToFields(false);
        setInitialFieldValues();
        removeValidationErrors();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.updateProfileBtn) {
            updateProfile();
            return;
        }
        if (v.getId() == R.id.cancelBtn) {
            cancelUpdate();
            return;
        }
        if (v.getId() == R.id.updatePasswordBtn) {
            startActivity(new Intent(getActivity(), UpdatePasswordActivity.class));
            return;
        }
        if (v.getId() == R.id.logOutBtn) {
            Toast.makeText(getActivity(), getString(R.string.logging_out_message), Toast.LENGTH_SHORT).show();
            TravellerSession.clear();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }
        if (v.getId() == R.id.deactivateAccountBtn) {
            startActivity(new Intent(getActivity(), DeactivateAccountActivity.class));
        }
    }
}