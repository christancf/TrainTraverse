package com.traveller.traintraverse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.traveller.components.Record;
import com.traveller.models.Booking;
import com.traveller.models.BookingRequest;
import com.traveller.models.GetAllTrainsResponse;
import com.traveller.models.Shedule;
import com.traveller.models.Train;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity {
    String departureSelection = "";
    String arrivalSelection = "";
    String selectedTrainId = "";
    String travellerNic = "";

    String userId = "001";
    List<Record> dateRecords = new ArrayList<>();
    Record selectedRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Spinner sItems = (Spinner) findViewById(R.id.departure_selection);
        Spinner aItems = (Spinner) findViewById(R.id.arivalSelection);
        Spinner dateTimes = (Spinner) findViewById(R.id.departure_date_time);

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String departureStation = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + departureStation, Toast.LENGTH_LONG).show();

                if(departureStation == "Choose one")
                    return;

                departureSelection = departureStation;

                Call<GetAllTrainsResponse> call = RetrofitClient.getInstance().getMyApi().getTrains(departureStation, null);

                call.enqueue(new Callback<GetAllTrainsResponse>() {
                    @Override
                    public void onResponse(Call<GetAllTrainsResponse> call, Response<GetAllTrainsResponse> response) {
                        GetAllTrainsResponse res = response.body();
                        populateArrival(res.trains);
                    }

                    @Override
                    public void onFailure(Call<GetAllTrainsResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                        Log.e("rererer", "onFailure: " + t.toString());
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        aItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String arivalStation = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + arivalStation, Toast.LENGTH_LONG).show();

                if(arivalStation == "Choose one")
                    return;

                arivalStation = arivalStation;

                Call<GetAllTrainsResponse> call = RetrofitClient.getInstance().getMyApi().getTrains(departureSelection, arivalStation);

                call.enqueue(new Callback<GetAllTrainsResponse>() {
                    @Override
                    public void onResponse(Call<GetAllTrainsResponse> call, Response<GetAllTrainsResponse> response) {
                        GetAllTrainsResponse res = response.body();
                        populateDates(res.trains);
                    }

                    @Override
                    public void onFailure(Call<GetAllTrainsResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dateTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Record record = dateRecords.get(i);
                    selectedRec = record;
                    Toast.makeText(getApplicationContext(), "Selected " + record.value, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.d("TAG", "onItemSelected: ", e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Call<GetAllTrainsResponse> call = RetrofitClient.getInstance().getMyApi().getTrains(null, null);

        call.enqueue(new Callback<GetAllTrainsResponse>() {
            @Override
            public void onResponse(Call<GetAllTrainsResponse> call, Response<GetAllTrainsResponse> response) {
                GetAllTrainsResponse res = response.body();
                populateDeparture(res.trains);
            }

            @Override
            public void onFailure(Call<GetAllTrainsResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                Log.e("rererer", "onFailure: " + t.toString());
            }
        });

        Button reserveBtn = findViewById(R.id.reserve_now);
        reserveBtn.setOnClickListener(l -> {
            try {
                bookTrain(this.selectedRec);
            }catch (Exception e) {
                Log.e("TAG", "onCreate: ", e);
            }
        });
    }

    public void populateArrival(List<Train> trains) {
        Log.d("dwdawdawdwa", String.valueOf(trains.size()));

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Choose one");
        for (Train t: trains) {
            spinnerArray.add(t.arrivalStation);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        Spinner sItems = (Spinner) findViewById(R.id.arivalSelection);
        // sItems.setEnabled(false);
        sItems.setAdapter(adapter);
    }

    public void populateDates(List<Train> trains) {
        Log.d("dwdawdawdwa", String.valueOf(trains.size()));

        List<String> spinnerArray =  new ArrayList<String>();
        dateRecords = new ArrayList<>();

        for (Train t: trains) {
            for(Shedule s: t.shedules.values()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(s.departureDate);

                SimpleDateFormat asdf = new SimpleDateFormat("HH:MM");
                String aformattedDate = asdf.format(t.arrivalTime);

                SimpleDateFormat dsdf = new SimpleDateFormat("HH:MM");
                String dformattedDate = dsdf.format(t.departureTime);

                StringBuilder builder = new StringBuilder();
                builder.append(t.name);
                builder.append(" on ");
                builder.append(formattedDate);
                builder.append(" ");
                builder.append(aformattedDate);
                builder.append(" to ");
                builder.append(dformattedDate);

                spinnerArray.add(builder.toString());
                dateRecords.add(new Record(t.id, builder.toString(), s.departureDate));
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        Spinner sItems = (Spinner) findViewById(R.id.departure_date_time);
        sItems.setAdapter(adapter);
    }

    public void populateDeparture(List<Train> trains) {
        Spinner sItems = (Spinner) findViewById(R.id.departure_selection);
        Log.d("dwdawdawdwa", String.valueOf(trains.size()));


        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Choose one");
        for (Train t: trains) {
            spinnerArray.add(t.departureStation);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        // sItems.setEnabled(false);
        sItems.setAdapter(adapter);
    }
    public void bookTrain(Record rec) {
        Log.d("TAG", "bookTrain: ");
        BookingRequest.BookingRequestBuilder br = BookingRequest.builder();
        br.trainId(rec.key);
        br.travellerNic(userId);
        br.reservationDate(rec.value2);
        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Reserving a seat for you...", true);

        //br.setTrainId();
        Call<Booking> call = RetrofitClient.getInstance().getMyApi().bookTrain(br.build());

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if(response.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                dialog.hide();

                SystemClock.sleep(1000);

                Intent intent = new Intent(getApplicationContext(), ReservationHistoryActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                Log.e("rererer", "onFailure: " + t.toString());
                dialog.hide();
            }
        });
    }
}