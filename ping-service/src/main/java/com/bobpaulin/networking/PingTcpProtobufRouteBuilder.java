package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.protobuf.AddressBookProtos.AddressBook;
import org.apache.camel.component.protobuf.AddressBookProtos.Person;
import org.apache.camel.impl.DefaultCamelContext;

public class PingTcpProtobufRouteBuilder extends RouteBuilder {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new PingTcpProtobufRouteBuilder());
		context.start();
		
		Thread.currentThread().sleep(20000);

	}
	
	@Override
	public void configure() throws Exception {		
		
		from("timer:protoBufTimer?fixedRate=true&period=10s")
			.process(new Processor() {
				
				public void process(Exchange exchange) throws Exception {
					Person person = Person.newBuilder().setId(1).setName("Bob").build();
					exchange.getIn().setBody(person);
				}
				
			})
			.marshal().protobuf()
            .to("netty4:tcp://localhost:8585")
            .unmarshal().protobuf(AddressBook.getDefaultInstance())
            .to("log:com.bobpaulin.networking.Ping?showAll=true");
		
	}

}
