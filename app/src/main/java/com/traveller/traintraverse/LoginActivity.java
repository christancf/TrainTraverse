package com.traveller.traintraverse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.traveller.TravellerSession;
import com.traveller.models.AuthRequest;
import com.traveller.models.AuthResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    AppCompatEditText username, password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.usernameET);
        password = findViewById(R.id.passwordET);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerTV);

        register.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        login.setOnClickListener(v -> travelerLogin());
    }

    private void travelerLogin() {

        String username = Objects.requireNonNull(this.username.getText()).toString().trim();
        String password = Objects.requireNonNull(this.password.getText()).toString();

        if (username.isEmpty()) {
            this.username.setError(getString(R.string.required_email_error_text));
            this.username.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            this.username.setError(getString(R.string.invalid_email_error_text));
            this.username.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            this.password.setError(getString(R.string.required_password_error_text));
            this.password.requestFocus();
            return;
        }
        Call<AuthResponse> call = RetrofitClient.getInstance().getMyApi().login(new AuthRequest(username,password, ""));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                try {
                    Log.d("TAG", "onResponse: " + response.toString());
                    AuthResponse res = response.body();
                    /**
                     * Reference - https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial
                     */
                    SharedPreferences preferences = getApplication().getSharedPreferences("Preferences", 0);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("token", res.accessToken);
                    editor.putString("currUserName", res.user.username);

                    editor.commit();

                    TravellerSession session = TravellerSession.getInstance();
                    session.setTraveler(res.user);
                    session.setToken(res.accessToken);

                    if(response.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.logging_in_fail_message), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){
                    Log.e("TAG", "onResponse: ", ex);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("auth", "onFailure: " + t.toString());
                Toast.makeText(getApplicationContext(), getString(R.string.logging_in_fail_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
}