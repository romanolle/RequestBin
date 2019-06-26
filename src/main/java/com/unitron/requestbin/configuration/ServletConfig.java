package com.unitron.requestbin.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

@Controller
/**
 * Configure AJP protocol for Tomcat to could communicate with Apache.
 * @author roman.olle
 *
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ServletConfig {
	

    @Autowired
    private Environment env;


    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> server = new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
			
			@Override
			public void customize(TomcatServletWebServerFactory factory) {
				factory.addAdditionalTomcatConnectors(redirectConnector());
			}
		};
        return server;
    }
    
    
    
	private Connector redirectConnector() {
		Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setPort(new Integer(env.getRequiredProperty("ajp.port")));
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        return ajpConnector;
	}
}
