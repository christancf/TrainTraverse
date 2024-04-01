package com.traveller.models;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingRequest {
    public String travellerNic;
    public Date reservationDate;
    public String trainId;
}
