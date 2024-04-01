package com.traveller.traintraverse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TestNav extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_nav);

        Button mainActivity = findViewById(R.id.mainActivityNavbtn);
        Button loginActivity = findViewById(R.id.loginActivitybtn);
        mainActivity.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        });
        loginActivity.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        Button reserveBtn = findViewById(R.id.reservationActivity);
        reserveBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReservationActivity.class);
            startActivity(intent);
        });

        Button reservationHistoryBtn = findViewById(R.id.reservationHistoryBtn);
        reservationHistoryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReservationHistoryActivity.class);
            startActivity(intent);
        });

        Button viewReservation = findViewById(R.id.viewReservation);
        viewReservation.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewReservationActivity.class);
            startActivity(intent);
        });
    }
}