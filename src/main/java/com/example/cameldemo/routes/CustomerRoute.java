package com.example.cameldemo.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CustomerRoute extends RouteBuilder {

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
				.to("validator:"+customerInputValidation)
				.process(exchange -> {
					System.out.println(exchange.getIn().getBody());
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
				})
				.to("http://localhost:3000/customer?bridgeEndpoint=true")
				.process(exchange -> {
					System.out.println(exchange.getIn().getBody(String.class));
				})
				.end();
		

	}
}
