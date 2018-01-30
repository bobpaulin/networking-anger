package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.bobpaulin.networking.models.json.AddressBook;
import com.bobpaulin.networking.models.json.Person;

public class PingTcpSerialization {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {		
				
				from("timer:serializationTimer?fixedRate=true&period=10s")
				.process(new Processor() {
					
					public void process(Exchange exchange) throws Exception {
						Person person = new Person();
						person.setId(1);
						person.setName("Bob");
						exchange.getIn().setBody(person);
					}
					
					})
					.setHeader(Exchange.HTTP_METHOD, constant("PUT"))
					.marshal().serialization()
	                .to("netty4:tcp://localhost:8787")
	                .unmarshal().serialization()
	                .to("log:com.bobpaulin.networking.Ping?showAll=true");
				
			}
		});
		context.start();
		
		Thread.currentThread().sleep(20000);

	}

}
