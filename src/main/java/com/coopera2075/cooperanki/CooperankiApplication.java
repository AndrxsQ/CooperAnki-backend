package com.coopera2075.cooperanki;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.coopera2075")
@EntityScan(basePackages = "com.coopera2075")
@EnableJpaRepositories(basePackages = "com.coopera2075")
@EnableScheduling
public class CooperankiApplication {

	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(CooperankiApplication.class, args);
		System.out.println("CooperAnki backend is succesfully running on port 8080");
	}

}
