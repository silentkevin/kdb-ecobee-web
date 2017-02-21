package com.sksi.ecobee.config

import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@Configuration
@CompileStatic
@Slf4j
class BootstrapConfig {
    @Autowired UserRepository userRepository

    @PostConstruct
    void init() {
        User user = new User(id: UUID.randomUUID().toString(), name: "kevin")
        userRepository.save(user)
    }
}
