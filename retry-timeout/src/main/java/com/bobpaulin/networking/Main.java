package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;

public class Main {

	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {

				restConfiguration().component("netty4-http").port(8282).bindingMode(RestBindingMode.json);
				
				
				rest("/timeout").get().to("direct:timeout");
				rest("/retry").get().to("direct:retry");
				rest("/notimeout").get().to("direct:notimeout");
				
				from("direct:notimeout")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple("null"))
                .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false");
				
				from("direct:timeout")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple("null"))
                .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClient.socketTimeout=600");
				
				from("direct:retry")
					.onException(Exception.class)
		                .maximumRedeliveries(4)
		                .backOffMultiplier(2)
		                .redeliveryDelay(100)
		                .maximumRedeliveryDelay(5000)
		                .useExponentialBackOff()
		            .end()
					.setHeader(Exchange.HTTP_METHOD, constant("GET"))
	                .setBody(simple("null"))
	                .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClient.socketTimeout=1500");
				
			}
		});
		context.start();
		
	}
}
