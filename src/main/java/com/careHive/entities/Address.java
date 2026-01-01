package com.careHive.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String houseNo;
    private String area;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
