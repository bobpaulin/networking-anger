package com.bobpaulin.networking;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Ping100UDPRouteBuilder extends RouteBuilder{

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new Ping100UDPRouteBuilder());
		context.start();
		
		Thread.currentThread().sleep(20000);
	}

	@Override
	public void configure() throws Exception {		
		
		from("timer:serializationTimer?fixedRate=true&period=10s")
		.setHeader("counter", constant(0))
		.process(new Processor() {
			
			public void process(Exchange exchange) throws Exception {
				List<Integer> messageList = new ArrayList<Integer>();
				for(int i = 0; i < 100; i++)
				{
					messageList.add(i);
				}
				exchange.getIn().setBody(messageList);
				
			}
		})
		.split(body())
            .to("netty4:udp://localhost:8989?sync=false")
            .process(new Processor() {
				
				public void process(Exchange exchange) throws Exception {
					System.out.println(Integer.toString(exchange.getIn().getBody(Integer.class)));
					
				}
			})
        .end();
		
	}

}
