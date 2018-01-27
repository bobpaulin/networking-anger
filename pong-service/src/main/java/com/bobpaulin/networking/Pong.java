package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.protobuf.AddressBookProtos.AddressBook;
import org.apache.camel.component.protobuf.AddressBookProtos.Person;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

public class Pong {

	public static void main(String[] args) throws Exception {
CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {

				restConfiguration().component("netty4-http").port(8484).bindingMode(RestBindingMode.off);
				
				
				rest("/pong")
					.put("json/person")
						.to("direct:pongPersonJson");
				
				from("netty4:tcp://localhost:8585")
					.to("direct:pongPersonProtoBuf");
				
				from("netty4:udp://localhost:8686")
					.to("direct:pongPersonProtoBuf");
				
				from("direct:pongPersonProtoBuf")
					.unmarshal().protobuf(Person.getDefaultInstance())
					.process(new Processor() {
						
						public void process(Exchange exchange) throws Exception {
							Person person = exchange.getIn().getBody(Person.class);
							System.out.println("ID: " + person.getId());
							AddressBook addressBook = AddressBook.newBuilder().addPerson(Person.newBuilder().setId(2).setName("John").build()).build();
							exchange.getIn().setBody(addressBook);
						}
					})
					.marshal().protobuf();
				
				from("direct:pongPersonJson")
				.unmarshal().json(JsonLibrary.Jackson, com.bobpaulin.networking.models.json.Person.class)
				.process(new Processor() {
					
					public void process(Exchange exchange) throws Exception {
						com.bobpaulin.networking.models.json.Person person = exchange.getIn().getBody(com.bobpaulin.networking.models.json.Person.class);
						System.out.println("ID: " + person.getId());
						com.bobpaulin.networking.models.json.AddressBook addressBook = new com.bobpaulin.networking.models.json.AddressBook();
						com.bobpaulin.networking.models.json.Person johnPerson = new  com.bobpaulin.networking.models.json.Person();
						johnPerson.setId(2);
						johnPerson.setName("John");
						addressBook.getAddressList().add(johnPerson);
						exchange.getIn().setBody(addressBook);
					}
				})
				.marshal().json(JsonLibrary.Jackson);
				
			}
		});
		context.start();
	}
}
