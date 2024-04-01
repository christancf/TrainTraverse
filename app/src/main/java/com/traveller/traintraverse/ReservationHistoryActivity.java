package com.traveller.traintraverse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.traveller.components.HistoryAdapter;
import com.traveller.components.HistoryHeaderItem;
import com.traveller.components.HistoryItem;
import com.traveller.components.HistoryListItem;
import com.traveller.models.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationHistoryActivity extends AppCompatActivity {
    HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        String uid = "001";
        RecyclerView recyclerView = findViewById(R.id.historyContainer);
        historyAdapter = new HistoryAdapter(new ArrayList<>());

        recyclerView.setAdapter(historyAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        Call<List<Booking>> call = RetrofitClient.getInstance().getMyApi().getBookings(uid, true);

        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                List<Booking> bookings = response.body();
                Log.d("dfsaf", "onResponse: " + bookings.size());

                refreshHistory(bookings);
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                Log.e("rererer", "onFailure: " + t.toString());
            }
        });

    }

    public void refreshHistory(List<Booking> bookings) {
        if(bookings.size() == 0) {
            LinearLayout noReservation = (LinearLayout) findViewById(R.id.no_reservations);
            noReservation.setVisibility(View.VISIBLE);
            historyAdapter.setData(new ArrayList<>());
            historyAdapter.notifyDataSetChanged();
            return;
        }

        List<HistoryListItem> upcommingItems = new ArrayList<>();
        List<HistoryListItem> pastItems = new ArrayList<>();
        List<HistoryListItem> canceledItems = new ArrayList<>();
        List<HistoryListItem> items = new ArrayList<>();

        for(Booking b : bookings) {
            if(b.status.compareTo("processing") == 0) {
                upcommingItems.add(new HistoryItem(b));
            } else if (b.status.compareTo("cancelled") == 0) {
                canceledItems.add(new HistoryItem(b));
            } else {
                pastItems.add(new HistoryItem(b));
            }
        }

        if(upcommingItems.size() > 0) {
            items.add(new HistoryHeaderItem("Upcomming"));
            upcommingItems.stream().forEach(items::add);
        }

        if(pastItems.size() > 0) {
            items.add(new HistoryHeaderItem("Past"));
            pastItems.stream().forEach(items::add);
        }

        if(canceledItems.size() > 0) {
            items.add(new HistoryHeaderItem("Cancelled"));
            canceledItems.stream().forEach(items::add);
        }

        Log.d("TAG", "refreshHistoryppp: " + items.size());

        historyAdapter.setData(items);
        historyAdapter.notifyDataSetChanged();
    }
}