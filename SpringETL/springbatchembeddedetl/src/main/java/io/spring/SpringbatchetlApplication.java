package io.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import io.micrometer.core.annotation.Timed;

@SpringBootApplication
public class SpringbatchetlApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbatchetlApplication.class, args);
	}

}

