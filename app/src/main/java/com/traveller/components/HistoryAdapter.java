package com.traveller.components;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.traveller.traintraverse.R;
import com.traveller.traintraverse.ViewReservationActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    public List<HistoryListItem> historyListItems = Collections.emptyList();
    private Context context;
    public HistoryAdapter(List<HistoryListItem> items) {
        historyListItems = items;
    }

    public void setData(List<HistoryListItem> items) {
        historyListItems = items;
    }
    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType == HistoryListItem.TYPE_ITEM) {
            View rowView = inflater.inflate(R.layout.history_row, parent, false);
            HistoryAdapter.ViewHolder rv = new RowViewHolder(rowView);
            return rv;
        } else if (viewType == HistoryListItem.TYPE_HEADER) {
            View hView = inflater.inflate(R.layout.history_list_title, parent, false);
            HistoryAdapter.ViewHolder hv = new HeaderViewHolder(hView);
            return hv;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if(type == HistoryListItem.TYPE_ITEM) {
            HistoryItem historyListItem = (HistoryItem) historyListItems.get(position);
            RowViewHolder h = (RowViewHolder) holder;
            h.journey.setText(String.format("%s -> %s", historyListItem.booking.train.departureStation, historyListItem.booking.train.arrivalStation));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(historyListItem.booking.bookingDate);

            h.reservationDate.setText(formattedDate);
            h.trainTitle.setText(historyListItem.booking.train.name);
            h.layout.setOnClickListener(l -> {
                Intent intent = new Intent(context, ViewReservationActivity.class);
                intent.putExtra("BOOKING_ID", historyListItem.booking.id);
                context.startActivity(intent);
            });

        } else if (type == HistoryListItem.TYPE_HEADER) {
            HistoryHeaderItem historyItem = (HistoryHeaderItem) historyListItems.get(position);
            HeaderViewHolder hv = (HeaderViewHolder) holder;
            hv.titleView.setText(historyItem.header);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("TAG", "getItemCount: " + historyListItems.size());
        return historyListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class RowViewHolder extends HistoryAdapter.ViewHolder {
        TextView trainTitle;
        TextView journey;
        TextView reservationDate;
        LinearLayout layout;
        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            trainTitle = itemView.findViewById(R.id.train_title);
            journey = itemView.findViewById(R.id.journey);
            reservationDate = itemView.findViewById(R.id.reservation_date);
            layout = itemView.findViewById(R.id.history_row);
        }
    }

    public class HeaderViewHolder extends HistoryAdapter.ViewHolder {
        TextView titleView;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.historyListTitleView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return historyListItems.get(position).getType();
    }
}
