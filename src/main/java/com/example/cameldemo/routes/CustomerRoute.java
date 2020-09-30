package com.example.cameldemo.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.cameldemo.customer.CustomerRequest;
import com.example.cameldemo.customer.CustomerResponse;
import com.example.cameldemo.mappers.CustomerMapper;
import com.example.cameldemo.mappers.CustomerResponseMapper;
import com.example.cameldemo.models.CustomerPaymentRequest;

@Component
public class CustomerRoute extends RouteBuilder {

	@Autowired
	CustomerMapper customerMapper;

	@Autowired
	CustomerResponseMapper customerResponseMapper;

	@Autowired
	CustomerRouterHelper customerRouteHelper;

	@Value("${customer-inventory-service.rest-config.component-name}")
	private String componentName;

	@Value("${customer-inventory-service.rest-config.host}")
	private String host;

	@Value("${customer-inventory-service.rest-config.port}")
	private String port;

	@Value("${customer-inventory-service.input-path}")
	private String customerInputPath;

	@Value("${customer-inventory-service.url}")
	private String url;

	@Value("${customer-inventory-service.consumes}")
	private String consumes;

	@Value("${customer-inventory-service.produces}")
	private String produces;

	@Value("${customer-inventory-service.validations.customer-request-validation}")
	private String customerRequestValidation;

	@Value("${customer-inventory-service.validations.customer-response-validation}")
	private String customerResponseValidation;

	@Override
	public void configure() throws Exception {
		checkException();
		
		restRoute();
	}
	
	private void checkException() {
		onException(Exception.class).handled(true).useOriginalMessage().process(exchange -> {
			Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
			System.out.println(exception);
			System.out.println("Error");
			System.out.println(exchange.getIn().getBody(String.class));
		}).end();
		
	}

	public void restRoute() {
		restConfiguration().component(componentName).bindingMode(RestBindingMode.off)
				.dataFormatProperty("prettyPrint", "true").host(host).port(port);
		rest(customerInputPath).post().consumes(consumes).produces(produces).route()
		.convertBodyTo(String.class)
				.to("validator:" + customerRequestValidation).process(exchange -> {
					String customerRequestXML = exchange.getIn().getBody(String.class);
					System.out.println(customerRequestXML);
					CustomerRequest customerRequest = customerRouteHelper.unmarshalCustomerRequest(customerRequestXML);
					CustomerPaymentRequest customerPaymentRequest = customerMapper.map(customerRequest);
					exchange.getIn().setBody(customerPaymentRequest, CustomerPaymentRequest.class);
				}).marshal().json(JsonLibrary.Jackson).process(exchange -> {
					System.out.println(exchange.getIn().getBody(String.class));
				}).to("http://localhost:3000/customer?bridgeEndpoint=true").unmarshal()
				.json(JsonLibrary.Jackson, CustomerPaymentRequest.class).process(exchange -> {
					CustomerPaymentRequest customerPaymentRequest = exchange.getIn()
							.getBody(CustomerPaymentRequest.class);
					CustomerResponse customerResponse = customerResponseMapper.map(customerPaymentRequest);
					exchange.getIn().setBody(customerResponse, CustomerResponse.class);
				}).marshal().jacksonxml(CustomerResponse.class).to("validator:" + customerResponseValidation).end();
	}
}
