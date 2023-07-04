package com.jokes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class JokesApplication {

	public static void main(String[] args) {
		var springApplication = new SpringApplication(JokesApplication.class);

		// Added because I had some issue with injection of value from properties file
		// I had no time to solve it
		var properties = new Properties();
		properties.put("random.jokes.uri.base", "https://official-joke-api.appspot.com");
		properties.put("random.jokes.uri.random", "random_joke");
		properties.put("random.jokes.uri.ten", "/ten");
		properties.put("jokes.default.count", 5);

		springApplication.setDefaultProperties(properties);
		springApplication.run(args);
	}

}
