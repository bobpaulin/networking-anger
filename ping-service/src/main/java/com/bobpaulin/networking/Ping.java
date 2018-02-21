package com.bobpaulin.networking;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class Ping {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new Ping100TCPRouteBuilder());
		context.addRoutes(new Ping100UDPRouteBuilder());
		context.addRoutes(new PingJsonHttpRouteBuilder());
		context.addRoutes(new PingTcpProtobufRouteBuilder());
		context.addRoutes(new PingTcpSerializationRouteBuilder());
		context.addRoutes(new PingUdpProtobufRouteBuilder());
		context.addRoutes(new PingJsonTcpRouteBuilder());
		context.start();
		
		System.in.read();

	}

}
