package com.bobpaulin.networking.ssl;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;

public class Main {

	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();

		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				
				
				KeyStoreParameters ksp = new KeyStoreParameters();
				ksp.setResource("/home/bpaulin/git/networking-anger/ssl-demo/src/main/resources/localhost-keystore.jks");
				ksp.setPassword("devnexus");
				 
				KeyManagersParameters kmp = new KeyManagersParameters();
				kmp.setKeyStore(ksp);
				kmp.setKeyPassword("devnexus");
				 
				SSLContextParameters scp = new SSLContextParameters();
				scp.setKeyManagers(kmp);
				scp.setSecureSocketProtocol("TLSv1.2");
				 
				HttpComponent httpComponent = getContext().getComponent("https4", HttpComponent.class);
				httpComponent.setSslContextParameters(scp);

				restConfiguration().component("netty4-http").port(9191).bindingMode(RestBindingMode.off);
				
				
				rest("/secure").get().to("direct:secure");
				
				from("direct:secure")
				.setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setBody(simple("null"))
                .to("https4://localhost:8443?bridgeEndpoint=true&throwExceptionOnFailure=false");
				
			}
		});
		context.start();
		
	}
}
