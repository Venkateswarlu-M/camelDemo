package com.example.cameldemo.routes;

import org.springframework.stereotype.Component;

import com.example.cameldemo.customer.CustomerRequest;
import com.example.cameldemo.util.JaxbTransformer;

@Component
public class CustomerRouterHelper{
	public static final String CUSTOMER_REQUEST_CONTEXT = "com.example.cameldemo.customer";
	public static final String CUSTOMER_PAYMENT_REQUEST_NAME = "CustomerPaymentRequest";
	
	public CustomerRequest unmarshalCustomerRequest(String incomingXML) {
		return JaxbTransformer.fromXml(incomingXML, CUSTOMER_REQUEST_CONTEXT, CustomerRequest.class);
	}
	
	
}