package com.sksi.ecobee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sksi.ecobee"})
public class KdbEcobeeWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(KdbEcobeeWebApplication.class, args);
	}
}
