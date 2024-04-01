package com.traveller.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class Train {
    public String id;
    public String name;
    public String description;
    public String departureStation;
    public String arrivalStation;
    public Date departureTime;
    public Date arrivalTime;
    public Boolean disabled;
    public Map<String,Shedule> shedules;
}
