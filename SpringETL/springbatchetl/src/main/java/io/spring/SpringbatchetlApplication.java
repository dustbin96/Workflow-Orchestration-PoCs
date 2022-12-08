package io.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"io.spring.*"})
@EntityScan(basePackages = {"io.spring.*"})
public class SpringbatchetlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbatchetlApplication.class, args);
	}

}
