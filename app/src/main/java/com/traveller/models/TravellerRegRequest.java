package com.traveller.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TravellerRegRequest {
    public String nic;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public boolean disabled;
    public String phoneNumber;
    public String password;
}
