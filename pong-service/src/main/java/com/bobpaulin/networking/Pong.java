package com.bobpaulin.networking;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.protobuf.AddressBookProtos.AddressBook;
import org.apache.camel.component.protobuf.AddressBookProtos.AddressBook.Builder;
import org.apache.camel.component.protobuf.AddressBookProtos.Person;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

import static io.github.benas.randombeans.api.EnhancedRandom.*;

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
				
				JettyHttpComponent jettyComponent = getContext().getComponent("jetty", JettyHttpComponent.class);
				jettyComponent.setMaxThreads(40);
				jettyComponent.setMinThreads(1);
				
				from("netty4:tcp://localhost:8585")
					.to("direct:pongPersonProtoBuf");
				
				from("netty4:udp://localhost:8686")
					.to("direct:pongPersonProtoBuf");
				
				from("netty4:tcp://localhost:8787")
				.to("direct:pongPersonSerialization");
				
				from("jetty:http://localhost:8888/threads/test")
				.to("direct:webPerfTest");
				
				from("netty4:udp://localhost:8989")
					.to("direct:countMessages");
				
				from("netty4:tcp://localhost:9090")
					.to("direct:countMessages");
				
				from("netty4:tcp://localhost:7979")
					.to("direct:pongPersonJson");
				
				from("direct:pongPersonProtoBuf")
					.unmarshal().protobuf(Person.getDefaultInstance())
					.process((exchange) -> {
							Person person = exchange.getIn().getBody(Person.class);
							System.out.println("ID: " + person.getId());
							
							Builder addressBookBuilder = AddressBook.newBuilder();
							for(int i = 0; i < 100; i++)
							{
								addressBookBuilder.addPerson(Person.newBuilder().setId(1).setName("John").build());
							}
							
							
							
							exchange.getIn().setBody(addressBookBuilder.build());
						})
					.marshal().protobuf();
				
				from("direct:pongPersonJson")
				//.unmarshal().json(JsonLibrary.Jackson, com.bobpaulin.networking.models.json.Person.class)
				.process((exchange) -> {
						//com.bobpaulin.networking.models.json.Person person = exchange.getIn().getBody(com.bobpaulin.networking.models.json.Person.class);
					//	System.out.println("ID: " + person.getId());
						com.bobpaulin.networking.models.json.AddressBook addressBook = new com.bobpaulin.networking.models.json.AddressBook();
						for(int i = 0; i < 100; i++)
						{
							com.bobpaulin.networking.models.json.Person person = new com.bobpaulin.networking.models.json.Person();
							person.setId(1);
							person.setName("John");
							addressBook.getAddressList().add(person);
						}
						
						exchange.getIn().setBody(addressBook);
					})
				.marshal().json(JsonLibrary.Jackson);
				
				from("direct:pongPersonSerialization")
				.unmarshal().serialization()
				.process((exchange) -> {
						com.bobpaulin.networking.models.json.AddressBook addressBook = new com.bobpaulin.networking.models.json.AddressBook();
						for(int i = 0; i < 100; i++)
						{
							com.bobpaulin.networking.models.json.Person person = new com.bobpaulin.networking.models.json.Person();
							person.setId(1);
							person.setName("John");
							addressBook.getAddressList().add(person);
						}
						exchange.getIn().setBody(addressBook);
					})
				.marshal().serialization();
				
				from("direct:webPerfTest")
				.process(new Processor() {
					
					Random random = new Random();
					
					public void process(Exchange exchange) throws Exception {
						try {
							Thread.sleep(this.random.nextInt(2000));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						exchange.getIn().setHeader("Content-Type", constant("text/plain"));
						exchange.getIn().setBody("Test");
						
					}
				});
				
				from("direct:countMessages")
					.process(new Processor() {
						private AtomicInteger messageCount = new AtomicInteger(0);
						public void process(Exchange exchange) throws Exception {
							System.out.println("Message " + messageCount.getAndIncrement() + " recieved.");
							
						}
					});
				
			}
		});
		context.start();
	}
}
