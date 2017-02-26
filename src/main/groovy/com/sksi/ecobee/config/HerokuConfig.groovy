package com.sksi.ecobee.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@Configuration
@Profile("heroku")
@CompileStatic
@Slf4j
class HerokuConfig {
    @PostConstruct
    void init() {
        log.info("HEROKU IS ACTIVE")
    }

    @Scheduled(initialDelay = 30_000L, fixedRate = 30_000L)
    void doSomethingScheduled() {
        log.info("**************** SCHEDULED ****************")
    }
}
