package com.traveller.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DeactivateRequest {
    public String password;
}
