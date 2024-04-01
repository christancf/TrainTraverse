package com.traveller;

import com.traveller.models.Booking;
import com.traveller.models.Traveler;

public class TravellerSession {
    Traveler traveler;
    String accessToken;
    public static TravellerSession s;
    String getCurrentToken() {
        return this.accessToken;
    }

    public static TravellerSession getInstance() {
        synchronized (TravellerSession.class) {
            if(s == null) {
                s = new TravellerSession();
            }
            return s;
        }
    }

    public static void clear() {
        s = null;
    }

    public void setToken(String accessToken){
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
    }
    public Traveler getTraveler() {
        return this.traveler;
    }

    public Boolean isLoggedIn() {
        return traveler != null && accessToken != null;
    }
}
