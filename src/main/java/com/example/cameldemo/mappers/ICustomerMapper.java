package com.example.cameldemo.mappers;

import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;
import static org.mapstruct.ReportingPolicy.IGNORE;

import java.util.Arrays;

import org.mapstruct.Mapper;
@Mapper(implementationName = ICustomerMapper.CUSTOMER_MAPPER, unmappedTargetPolicy = IGNORE, nullValueMappingStrategy = RETURN_DEFAULT,componentModel = ICustomerMapper.SPRING, imports = {Arrays.class} )
public interface ICustomerMapper {
	String SPRING = "SPRING";
	String CUSTOMER_MAPPER = "CustomerMapper";
	
}
