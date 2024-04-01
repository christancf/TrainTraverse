package com.traveller.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Booking {
    public String id;
    public String travellerNic;
    public Date reservationDate;
    public String referenceId;
    public String status;
    public String trainId;
    public Date bookingDate ;
    public Train train;
}
