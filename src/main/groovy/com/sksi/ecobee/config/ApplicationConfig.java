package com.sksi.ecobee.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.sksi.ecobee.data")
@EntityScan(basePackages = "com.sksi.ecobee.data")
public class ApplicationConfig {
}
