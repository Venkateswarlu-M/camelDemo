package com.example.cameldemo.models;

import lombok.Data;

@Data
public class CustomerRequest {
	private String customerName;
	private String customerId;
	private CustomerAddress customerAddress;
}
