package com.example.cameldemo.routes;



import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.example.cameldemo.customer.CustomerRequest;

import com.fasterxml.jackson.dataformat.xml.*;

@Component
public class InsertProcessor implements Processor {
	
	 XmlMapper xmlMapper = new XmlMapper();
	
  
	@Override
	public void process(Exchange exchange) throws Exception {

		String input = (String) exchange.getIn().getBody();
		
		CustomerRequest pojo= xmlMapper.readValue(input,CustomerRequest.class);
	    
        System.out.println("Input to be persisted : " + input);
     
      String insertQuery ="Insert into history (message,customer_id,name,country,state,city,pincode,timestmp) values ('" + input + "','"
    		  + pojo.getCustomerName() +"','" + pojo.getCustomerId() +"','" + pojo.getCountry() +
    		  "','" + pojo.getState() + "','"+ pojo.getCity() +"'," + pojo.getPincode()+
    		  "," + "GETDATE());" ;
        System.out.println("Insert Query is : " + insertQuery);
        exchange.getIn().setBody(insertQuery);
	}

}
