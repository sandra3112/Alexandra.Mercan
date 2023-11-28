package com.ecommerce;

//Import clase din Spring Boot framework
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

//Adnotare prin care se mentioneaza ca aceasta clasa este o aplicatie Spring Boot
@SpringBootApplication

//Cautare entitati Java Persistence APIin package-ul mentionat
@EntityScan(basePackages = "com.ecommerce.model")
public class EcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
