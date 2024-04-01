package com.traveller.traintraverse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.traveller.models.DeactivateRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeactivateAccountActivity extends AppCompatActivity {

    AppCompatEditText passwordET;
    MaterialButton deactivateBtn;
    ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate_account);

        passwordET = findViewById(R.id.confirmationPasswordET);
        deactivateBtn = findViewById(R.id.deactivateAccountBtn);
        backBtn = findViewById(R.id.backBtn);

        deactivateBtn.setOnClickListener(v -> deactivateAccount());
        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void deactivateAccount() {
        try {
            String password = passwordET.getText().toString();
            if (password.isEmpty()) {
                passwordET.setError(getString(R.string.required_password_error_text));
                passwordET.requestFocus();
                return;
            }

            //API Integration
            DeactivateRequest request = DeactivateRequest.builder()
                    .password(password)
                    .build();
            Call<Void> call = RetrofitClient.getInstance().getMyApi().deactivate(request);
            Context context = getApplicationContext();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(context, getString(R.string.deactivate_success_message), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, LoginActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(context, "Invalid Password!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, getString(R.string.deactivate_fail_message), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("TAG", "deactivateAccount: e", e);
            Toast.makeText(this, getString(R.string.deactivate_fail_message), Toast.LENGTH_SHORT).show();
        }
    }
}