package com.traveller.components;

import com.traveller.models.Booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class HistoryItem extends HistoryListItem {
    public Booking booking;

    public HistoryItem(Booking b) {
        booking = b;
    }
    @Override
    public int getType() {
        return TYPE_ITEM;
    }
}
