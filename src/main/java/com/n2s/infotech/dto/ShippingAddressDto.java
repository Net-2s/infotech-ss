package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDto {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String phone;
}
