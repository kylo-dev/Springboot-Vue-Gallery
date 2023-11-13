package com.gallery.backend.dto;

import lombok.Data;

@Data
public class OrderDto {

    private String name;
    private String address;
    private String payment;
    private String cardNumber;
    private String items;
}
