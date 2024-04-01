package com.traveller.traintraverse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.traveller.models.Traveler;
import com.traveller.models.TravellerRegRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    AppCompatEditText nicET, fullNameET, emailET, phoneET, passwordET, confirmPasswordET;
    Button registerBtn;
    private String NIC, fullName, email, phone, password, confirmPassword;
    private ArrayList<String> fieldNames = new ArrayList<>(6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nicET = findViewById(R.id.nicET);
        fullNameET = findViewById(R.id.fullNameET);
        emailET = findViewById(R.id.emailET);
        phoneET = findViewById(R.id.phoneET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        registerBtn = findViewById(R.id.registerBtn);

        addFieldsToArrayList();

        registerBtn.setOnClickListener(v -> travelerRegister());
    }

    private void addFieldsToArrayList() {
        fieldNames.add(getString(R.string.nic_field_hint));
        fieldNames.add(getString(R.string.name_field_hint));
        fieldNames.add(getString(R.string.email_field_hint));
        fieldNames.add(getString(R.string.phone_number_field_hint));
        fieldNames.add(getString(R.string.password_field_hint));
        fieldNames.add(getString(R.string.confirm_password_field_hint));
    }

    private void travelerRegister() {
        if (!validateFields()) return;
        // API Integration
        TravellerRegRequest.TravellerRegRequestBuilder regRequestBuilder = TravellerRegRequest.builder();

        regRequestBuilder.nic(NIC);
        regRequestBuilder.firstName(fullName);
        regRequestBuilder.lastName(fullName);
        regRequestBuilder.phoneNumber(phone);
        regRequestBuilder.username(email);
        regRequestBuilder.email(email);
        regRequestBuilder.password(password);

        Call<Traveler> call = RetrofitClient.getInstance().getMyApi().register(regRequestBuilder.build());

        call.enqueue(new Callback<Traveler>() {
            @Override
            public void onResponse(Call<Traveler> call, Response<Traveler> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.registration_success_message), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.registration_fail_message), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Traveler> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.registration_fail_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeFields() {
        NIC = getFieldValue(nicET);
        fullName = getFieldValue(fullNameET);
        email = getFieldValue(emailET);
        phone = getFieldValue(phoneET);
        password = getFieldValue(passwordET);
        confirmPassword = getFieldValue(confirmPasswordET);
    }

    private boolean validateFields() {
        initializeFields();

        if (hasEmptyField(nicET, fieldNames.get(0))) return false;

        if (!getFieldValue(nicET).matches("^(?:19|20)?\\d{2}[0-9]{10}|[0-9]{9}[xXvV]$")) {
            nicET.setError(getString(R.string.invalid_nic_error_text));
            nicET.requestFocus();
            return false;
        }
        if (hasEmptyField(fullNameET, fieldNames.get(1)) || hasEmptyField(emailET, fieldNames.get(2)))
            return false;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError(getString(R.string.invalid_email_error_text));
            emailET.requestFocus();
            return false;
        }
        if (hasEmptyField(phoneET, fieldNames.get(3)) || hasEmptyField(passwordET, fieldNames.get(4)))
            return false;
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            passwordET.setError(getString(R.string.invalid_password_error_text));
            passwordET.requestFocus();
            return false;
        }
        if (!confirmPassword.equals(getFieldValue(passwordET))) {
            confirmPasswordET.setError(getString(R.string.passwords_mismatch_error_text));
            confirmPasswordET.requestFocus();
            return false;
        }

        return true;
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

}