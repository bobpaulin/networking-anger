package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.netty4.http.NettyHttpComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;

public class RetryMain {

	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {

				restConfiguration().component("netty4-http").port(8282).bindingMode(RestBindingMode.json);
				
				NettyHttpComponent netty4Component = getContext().getComponent("netty4-http", NettyHttpComponent.class);
				netty4Component.setMaximumPoolSize(1000);
				
				HttpComponent httpComponent = getContext().getComponent("http4", HttpComponent.class);
				httpComponent.setConnectionsPerRoute(1000);
				httpComponent.setMaxTotalConnections(1000);
				
				rest("/timeout").get().to("direct:timeout");
				rest("/retry").get().to("direct:retry");
				rest("/notimeout").get().to("direct:notimeout");
				rest("/hystrix").get().to("direct:hystrix");
				
				from("direct:notimeout")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple("null"))
                .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false");
				
				from("direct:timeout")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple("null"))
                .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClient.socketTimeout=1500");
				
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
				
				from("direct:hystrix")
					.hystrix()
				        .hystrixConfiguration()
				             .executionTimeoutInMilliseconds(2000)
				             .queueSizeRejectionThreshold(100)
				             .corePoolSize(200)
				        .end()
				        .to("http4://localhost:8888/threads/test?bridgeEndpoint=true&throwExceptionOnFailure=false")
					    .onFallback()
					        .transform().constant("Fallback message")
					    .end();
				
			}
		});
		context.start();
		
	}
}
