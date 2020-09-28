package com.example.cameldemo.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.cameldemo.customer.CustomerRequest;
import com.example.cameldemo.mappers.CustomerMapper;
import com.example.cameldemo.models.CustomerPaymentRequest;

@Component
public class CustomerRoute extends RouteBuilder {

	@Autowired
	CustomerMapper customerMapper;

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

	@Value("${customer-inventory-service.validations.customer-input-validation}")
	private String customerInputValidation;

	@Override
	public void configure() throws Exception {
		restConfiguration().component(componentName).bindingMode(RestBindingMode.off)
				.dataFormatProperty("prettyPrint", "true").host(host).port(port);
		rest(customerInputPath).post().consumes(consumes).produces(produces).route().convertBodyTo(String.class)
				.to("validator:" + customerInputValidation).process(exchange -> {
					String customerRequestXML = exchange.getIn().getBody(String.class);
					CustomerRequest customerRequest = customerRouteHelper.unmarshalCustomerRequest(customerRequestXML);
					System.out.println(customerRequest);
					CustomerPaymentRequest customerPaymentRequest = customerMapper.map(customerRequest);
					System.out.println(customerPaymentRequest);
					exchange.getIn().setBody(customerPaymentRequest, CustomerPaymentRequest.class);
				}).marshal().json(JsonLibrary.Jackson).process(exchange -> {
					System.out.println(exchange.getIn().getBody(String.class));
				}).to("http://localhost:3000/customer?bridgeEndpoint=true").process(exchange -> {
					System.out.println();
					exchange.getIn().setBody(exchange.getIn().getBody(String.class), String.class);

				}).end();

	}
}
