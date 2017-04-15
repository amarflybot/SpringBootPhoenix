package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class SpringBootPhoenixApplication extends SpringBootServletInitializer {

	private static Logger logger = LoggerFactory.getLogger(SpringBootPhoenixApplication.class);

	public static void main(String[] args) {
		new SpringBootPhoenixApplication()
				.configure(new SpringApplicationBuilder(SpringBootPhoenixApplication.class))
				.run(args);
	}
}
