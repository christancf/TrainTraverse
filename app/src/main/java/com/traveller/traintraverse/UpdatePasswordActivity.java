package com.traveller.traintraverse;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;
import com.traveller.models.ChangePasswordRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordActivity extends AppCompatActivity {

    ImageButton backBtn;
    AppCompatEditText currentPasswordET, newPasswordET, confirmNewPasswordET;
    MaterialButton updatePasswordBtn;
    private ArrayList<String> fieldNames = new ArrayList<>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        backBtn = findViewById(R.id.backBtn);
        currentPasswordET = findViewById(R.id.currentPasswordET);
        newPasswordET = findViewById(R.id.newPasswordET);
        confirmNewPasswordET = findViewById(R.id.confirmNewPasswordET);
        updatePasswordBtn = findViewById(R.id.updatePasswordBtn);

        backBtn.setOnClickListener(v -> onBackPressed());
        updatePasswordBtn.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        try {
            if (!validateFields()) return;
            changePasswordRequest();
            //API Integration
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.password_update_fail_message), Toast.LENGTH_SHORT).show();
        }
    }

    private void addEditableFieldsToArrayList() {
        fieldNames.add(getString(R.string.current_password_hint));
        fieldNames.add(getString(R.string.new_password_hint));
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

    private void clearValidationErrors() {
        currentPasswordET.setError(null);
        newPasswordET.setError(null);
        confirmNewPasswordET.setError(null);
    }

    private boolean validateFields() {
        addEditableFieldsToArrayList();
        clearValidationErrors();
        String currentPassword = "12345";
        if (hasEmptyField(currentPasswordET, fieldNames.get(0))) return false;

        if (hasEmptyField(newPasswordET, fieldNames.get(1))) return false;
        if (getFieldValue(newPasswordET).equals(currentPassword)) {
            newPasswordET.setError(getString(R.string.same_password_error_text));
            newPasswordET.requestFocus();
            return false;
        }
        if (!getFieldValue(newPasswordET).matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            newPasswordET.setError(getString(R.string.invalid_password_error_text));
            newPasswordET.requestFocus();
            return false;
        }
        if (!getFieldValue(confirmNewPasswordET).equals(getFieldValue(newPasswordET))) {
            confirmNewPasswordET.setError(getString(R.string.passwords_mismatch_error_text));
            confirmNewPasswordET.requestFocus();
            return false;
        }
        return true;
    }

    public void changePasswordRequest() {
        try {
            ChangePasswordRequest.ChangePasswordRequestBuilder builder = ChangePasswordRequest.builder();
            builder.oldPassword(getFieldValue(currentPasswordET));
            builder.newPassword(getFieldValue(newPasswordET));

            Call<Void> call = RetrofitClient.getInstance().getMyApi().changePassword(builder.build());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    try {
                        if(response.isSuccessful()){
                            Toast.makeText(getApplicationContext(), getString(R.string.password_update_success_message), Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Log.e("TAG", "changePasswordRequest: " + response.toString());
                            Toast.makeText(getApplicationContext(), getString(R.string.password_update_fail_message), Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("TAG", "changePasswordRequest: ", e);
                        Toast.makeText(getApplicationContext(), getString(R.string.password_update_fail_message), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("TAG", "changePasswordRequest: ", t);
                    Toast.makeText(getApplicationContext(), getString(R.string.password_update_fail_message), Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.e("TAG", "changePasswordRequest: ", e);
            Toast.makeText(getApplicationContext(), getString(R.string.password_update_fail_message), Toast.LENGTH_SHORT).show();
        }
    }
}