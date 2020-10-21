package com.example.cameldemo.routes;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultExchange;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.cameldemo.customer.CustomerRequest;
import com.example.cameldemo.customer.CustomerResponse;
import com.example.cameldemo.mappers.CustomerMapper;
import com.example.cameldemo.mappers.CustomerResponseMapper;
import com.example.cameldemo.models.CustomerPaymentRequest;
import reactor.core.publisher.Flux;

@Component
class Test {

	@Autowired
	public CamelReactiveStreamsService rsCamel;

	Subscriber<String> elements = rsCamel.streamSubscriber("elements", String.class);

//	Flowable.interval(1, TimeUnit.SECONDS)
//    .map(i -> "Item " + i)
//    .subscribe(elements);
}

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

	@Autowired
	private CamelReactiveStreamsService camel;
	
	@Autowired
	private CamelContext camelContext;	

	@PostConstruct
	public void setup() {
		restConfiguration().component(componentName).bindingMode(RestBindingMode.off)
		.dataFormatProperty("prettyPrint", "true").host(host).port(port);
		// Rest endpoint to retrieve all orders: http://localhost:8080/camel/orders
		camel.process("rest:get:customer", exchange -> Flux.from(exchange).map(ex -> {
			System.out.println("hello");
			return ex.getIn().getBody(String.class);
		})
//		.flatMap(ex -> camel.to("direct:hello", new DefaultExchange(camelContext)))
		.subscribe());
	}

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

//		from("reactive-streams:elements").process(exchange -> {
//
//			System.out.println(exchange.getIn().getBody(String.class));
//		});

		rest(customerInputPath + "hai").post().consumes(consumes).produces(produces).route().convertBodyTo(String.class)
				.to("validator:" + customerRequestValidation).process(exchange -> {
					String customerRequestXML = exchange.getIn().getBody(String.class);
					System.out.println(customerRequestXML);
					CustomerRequest customerRequest = customerRouteHelper.unmarshalCustomerRequest(customerRequestXML);
					CustomerPaymentRequest customerPaymentRequest = customerMapper.map(customerRequest);
					exchange.getIn().setBody(customerPaymentRequest, CustomerPaymentRequest.class);
				}).marshal().json(JsonLibrary.Jackson).process(exchange -> {
					System.out.println(exchange.getIn().getBody(String.class));
				}).multicast().parallelProcessing()
				.to("direct:callRestEndpoint1", "direct:callRestEndpoint2", "db:url");

		from("direct:callRestEndpoint1").to("http://localhost:3000/customer?bridgeEndpoint=true").unmarshal()
				.json(JsonLibrary.Jackson, CustomerPaymentRequest.class).process(exchange -> {
					CustomerPaymentRequest customerPaymentRequest = exchange.getIn()
							.getBody(CustomerPaymentRequest.class);
					CustomerResponse customerResponse = customerResponseMapper.map(customerPaymentRequest);
					exchange.getIn().setBody(customerResponse, CustomerResponse.class);
				}).marshal().jacksonxml(CustomerResponse.class).to("validator:" + customerResponseValidation).end();

		from("direct:callRestEndpoint2").to("http://localhost:3001/customer?bridgeEndpoint=true").unmarshal()
				.json(JsonLibrary.Jackson, CustomerPaymentRequest.class).process(exchange -> {
					CustomerPaymentRequest customerPaymentRequest = exchange.getIn()
							.getBody(CustomerPaymentRequest.class);
					CustomerResponse customerResponse = customerResponseMapper.map(customerPaymentRequest);
					exchange.getIn().setBody(customerResponse, CustomerResponse.class);
				}).marshal().jacksonxml(CustomerResponse.class).to("validator:" + customerResponseValidation).end();
	}
}
