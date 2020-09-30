package com.example.cameldemo.mappers;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.ReportingPolicy.IGNORE;

import java.util.Arrays;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.cameldemo.customer.CustomerRequest;
import com.example.cameldemo.models.CustomerPaymentRequest;



@Mapper(implementationName = ICustomerMapper.CUSTOMER_MAPPER, unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_DEFAULT,componentModel = ICustomerMapper.SPRING, imports = {Arrays.class} )
public interface ICustomerMapper {
	String SPRING = "SPRING";
	String CUSTOMER_MAPPER = "CustomerMapper";
	
	@Mapping(target="customerName", source= "customerName")
	@Mapping(target="customerId", source= "customerId")
	@Mapping(target="customerAddress.country", source= "country")
	@Mapping(target="customerAddress.state", source= "state")
	@Mapping(target="customerAddress.city", source= "city")
	@Mapping(target="customerAddress.pincode", source= "pincode")
	CustomerPaymentRequest map(final  CustomerRequest customerRequest);
}

