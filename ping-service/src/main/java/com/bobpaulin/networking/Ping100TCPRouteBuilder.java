package com.bobpaulin.networking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.bobpaulin.networking.models.json.Person;

public class Ping100TCPRouteBuilder extends RouteBuilder {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new Ping100TCPRouteBuilder());
		context.start();

		Thread.currentThread().sleep(20000);
	}

	@Override
	public void configure() throws Exception {

		from("timer:serializationTimer?fixedRate=true&period=10s").setHeader("counter", constant(0))
				.process(new Processor() {

					public void process(Exchange exchange) throws Exception {
						List<Integer> messageList = new ArrayList<Integer>();
						for (int i = 0; i < 100; i++) {
							messageList.add(i);
						}
						exchange.getIn().setBody(messageList);

					}
				}).split(body()).to("netty4:tcp://localhost:9090").end();
	}

}
