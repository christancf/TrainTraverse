package com.traveller.traintraverse;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.traveller.TravellerSession;
import com.traveller.traintraverse.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TravellerSession.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        setItemSelectListener();
    }

    private void setItemSelectListener() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (R.id.homeItem == item.getItemId()) {
                replaceFragment(new HomeFragment());
            } else if (R.id.reservationsItem == item.getItemId()) {
                replaceFragment(new ReservationHistoryFragment());
            } else {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerFL, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}