package com.example.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DemoApplication {

	private final Environment environment;

	public DemoApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void printAppLink(ApplicationReadyEvent event) {
		WebServerApplicationContext context = (WebServerApplicationContext) event.getApplicationContext();
		int port = context.getWebServer().getPort();
		String contextPath = environment.getProperty("server.servlet.context-path", "");
		System.out.println();
		System.out.println("Eligibuddy is running at:");
		System.out.println("http://localhost:" + port + contextPath + "/");
		System.out.println();
	}

}
