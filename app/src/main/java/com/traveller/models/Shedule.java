package com.traveller.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Shedule {
    public String id;
    public Date departureDate;
    public int availableSeats;
    public int totalSeats;
}
