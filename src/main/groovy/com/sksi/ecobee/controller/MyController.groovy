package com.sksi.ecobee.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import org.joda.time.DateTime

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@RestController
@RequestMapping(path = "/hi")
@CompileStatic
@Slf4j
class MyController {
    @Autowired UserRepository userRepository
    @Autowired ObjectMapper objectMapper

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    String get() {
        String msg = "hi shithead ${DateTime.now()}"
        log.info("called /hi msg={}", msg)

        List<User> users = new ArrayList<>(userRepository.findAll().collect({ it }) as List<User>)

        Map ret = [:]
        ret.msg = msg
        ret.users = users
        return objectMapper.writeValueAsString(ret)
    }
}
