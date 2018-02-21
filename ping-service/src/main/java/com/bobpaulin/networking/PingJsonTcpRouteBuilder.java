package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.bobpaulin.networking.models.json.AddressBook;
import com.bobpaulin.networking.models.json.Person;

import static io.github.benas.randombeans.api.EnhancedRandom.*;

public class PingJsonTcpRouteBuilder extends RouteBuilder {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new PingJsonTcpRouteBuilder());
		context.start();
		
		Thread.currentThread().sleep(20000);
	}
	
	@Override
	public void configure() throws Exception {		
		
		from("timer:protoBufTimer?fixedRate=true&period=10s")
			.process(new Processor() {
				
				public void process(Exchange exchange) throws Exception {
					Person person = random(Person.class);
					exchange.getIn().setBody(person);
				}
				
			})
			.marshal().json(JsonLibrary.Jackson)
            .to("netty4:tcp://localhost:7979")
            .unmarshal().json(JsonLibrary.Jackson, AddressBook.class)
            .to("log:com.bobpaulin.networking.Ping?showAll=true");
		
	}
}
