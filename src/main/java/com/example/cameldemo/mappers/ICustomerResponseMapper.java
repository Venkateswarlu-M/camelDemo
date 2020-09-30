package com.example.cameldemo.mappers;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.ReportingPolicy.IGNORE;

import java.util.Arrays;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.cameldemo.customer.CustomerResponse;
import com.example.cameldemo.models.CustomerPaymentRequest;



@Mapper(implementationName = ICustomerResponseMapper.CUSTOMER_RESPONSE_MAPPER, unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_DEFAULT,componentModel = ICustomerResponseMapper.SPRING, imports = {Arrays.class} )
public interface ICustomerResponseMapper {
	String SPRING = "SPRING";
	String CUSTOMER_RESPONSE_MAPPER = "CustomerResponseMapper";
	
	@Mapping(target="customerName", source= "customerName")
	@Mapping(target="customerId", source= "customerId")
	@Mapping(target="address.country", source= "customerAddress.country")
	@Mapping(target="address.state", source= "customerAddress.state")
	@Mapping(target="address.city", source= "customerAddress.city")
	@Mapping(target="address.pincode", source= "customerAddress.pincode")
	@Mapping(target="isCustomerValid", constant="True")
	CustomerResponse map(final CustomerPaymentRequest customerRequest);
}

