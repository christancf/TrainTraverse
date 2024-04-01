package com.traveller.traintraverse;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.traveller.TravellerSession;
import com.traveller.components.HistoryAdapter;
import com.traveller.components.HistoryHeaderItem;
import com.traveller.components.HistoryItem;
import com.traveller.components.HistoryListItem;
import com.traveller.models.Booking;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservationHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationHistoryFragment extends Fragment {
    LinearLayout noReservation;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HistoryAdapter historyAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReservationHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationHistoryFragment newInstance(String param1, String param2) {
        ReservationHistoryFragment fragment = new ReservationHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View infView = inflater.inflate(R.layout.fragment_reservation_history, container, false);
        Context context = infView.getContext();

        noReservation = infView.findViewById(R.id.no_reservations);

        String uid = TravellerSession.getInstance().getTraveler().nic;
        RecyclerView recyclerView = infView.findViewById(R.id.historyContainer);
        historyAdapter = new HistoryAdapter(new ArrayList<>());

        recyclerView.setAdapter(historyAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        try {
            refreshRequest(context, uid);
        }catch (Exception e){
            Log.e("TAG", "onCreateView: ", e);
        }


        Button reservationBtn = infView.findViewById(R.id.make_reservation);
        reservationBtn.setOnClickListener(l -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerFL, new HomeFragment());
            transaction.commit();
        });
        return  infView;
    }
    public void refreshRequest(Context context, String uid) {
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
                Toast.makeText(context, "An error has occured", Toast.LENGTH_LONG).show();
                Log.e("rererer", "onFailure: " + t.toString());
            }
        });
    }
    public void refreshHistory(List<Booking> bookings) {
        if(bookings.size() == 0) {
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

    @Override
    public void onResume() {
        super.onResume();
        try {
            refreshRequest(getActivity(), TravellerSession.getInstance().getTraveler().nic);
        }catch (Exception e){
            Log.e("", "onResume: ", e);
        }
    }
}