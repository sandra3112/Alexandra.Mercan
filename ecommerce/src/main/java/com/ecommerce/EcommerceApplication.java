package com.ecommerce;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication 
@EntityScan(basePackages = "com.ecommerce.model")
@ComponentScan("com.ecommerce")
public class EcommerceApplication {

    public static void main(String[] args) {
	SpringApplication.run(EcommerceApplication.class, args);
    }

}
