package com.founderlink.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableFeignClients
@SpringBootApplication
public class StartupServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartupServiceApplication.class, args);
	}

}
