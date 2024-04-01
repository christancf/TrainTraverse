package com.traveller.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateUserRequest {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
}
