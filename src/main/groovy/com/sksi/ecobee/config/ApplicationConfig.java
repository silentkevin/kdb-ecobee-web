package com.sksi.ecobee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.sksi.ecobee.data")
@EntityScan(basePackages = "com.sksi.ecobee.data")
public class ApplicationConfig {
    @Autowired private ObjectMapper objectMapper;

    @PostConstruct
    void afterPropertiesSet() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
