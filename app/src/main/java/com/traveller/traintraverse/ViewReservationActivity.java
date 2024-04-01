package com.traveller.traintraverse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveller.TravellerSession;
import com.traveller.Utils;
import com.traveller.models.Booking;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewReservationActivity extends AppCompatActivity {
    String eBid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservation);

        String userId = TravellerSession.getInstance().getTraveler().nic;
        String bookingId = "6522c003875dd7f9a9ee4af8";
        eBid = getIntent().getExtras().getString("BOOKING_ID");

        if(eBid != null){
            bookingId = eBid;
        }

        TextView departure = findViewById(R.id.reserved_departure);
        TextView arrival = findViewById(R.id.reserved_train_arrival);
        TextView tName = findViewById(R.id.reserved_train_name);
        TextView departureDate = findViewById(R.id.reserved_train_date);
        Button cancelBtn = findViewById(R.id.cancel_reservation_btn);
        TextView cancelText = findViewById(R.id.cancel_early_warning_text);
        ImageView image = findViewById(R.id.edit_date);

        cancelBtn.setOnClickListener(l -> {
            cancelReservationConfirmation();
        });

        Call<Map<String,Booking>> call = RetrofitClient.getInstance().getMyApi().getBookingById(bookingId, true);
        call.enqueue(new Callback<Map<String, Booking>>() {
            @Override
            public void onResponse(Call<Map<String, Booking>> call, Response<Map<String, Booking>> response) {
                try {
                    Map<String, Booking> res = response.body();

                    Booking booking = res.get("result");

                    tName.setText(booking.train.name);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
                    departureDate.setText(sdf.format(booking.reservationDate));

                    departure.setText("Departure station : " + booking.train.departureStation);
                    arrival.setText("Arrival station : " + booking.train.arrivalStation);
                    Log.d("TAG", "onResponse: " + Utils.daysDifference(booking.reservationDate));
                    if(Utils.daysDifference(booking.reservationDate) < 5){
                        cancelBtn.setEnabled(false);
                        cancelText.setVisibility(View.INVISIBLE);
                    }

                    Log.d("TAG", "onResponse: " + booking.status);
                    if(booking.status.compareTo("processing") != 0) {
                        cancelBtn.setVisibility(View.INVISIBLE);
                        cancelText.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){
                    Log.e("TAG", "onResponse: ", e);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Booking>> call, Throwable t) {

            }
        });
    }

    /**
     * Cancel reservation function that shows a dialog to get the confirmation of user
     */
    public void cancelReservationConfirmation() {
        /**
         * Reference - https://developer.android.com/develop/ui/views/components/dialogs
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Log.d("TAG", "onClick: Proceed");
                    dialog.dismiss();
                    cancelReservation();
                }catch (Exception e){
                    Log.e("TAG", "onClick: ", e);
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TAG", "onCancel: ");
            }
        });

        builder.setTitle("Are you sure you want to cancel this reservation?");

        Dialog confirmDelete = builder.create();
        confirmDelete.show();
    }

    /**
     * Call the reservation cancel API
     */
    public void cancelReservation() {
        ProgressDialog dialog = ProgressDialog.show(ViewReservationActivity.this, "",
                "Please wait while we complete your request...", true);
        Call call = RetrofitClient.getInstance().getMyApi().cancelBooking(TravellerSession.getInstance().getTraveler().nic, eBid);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(ViewReservationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
//        SystemClock.sleep(1000);
//        dialog.hide();
    }
}