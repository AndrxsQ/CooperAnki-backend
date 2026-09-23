package com.coopera2075.cooperanki;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CooperankiApplication {

	public static void main(String[] args) {
		org.springframework.boot.SpringApplication.run(CooperankiApplication.class, args);
		System.out.println("CooperAnki backend is succesfully running on port 8080");
	}

}
