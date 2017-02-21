package com.sksi.ecobee.controller

import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import org.joda.time.DateTime

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.lang.reflect.Array

@RestController
@RequestMapping(path = "/hi")
@CompileStatic
@Slf4j
class MyController {
    @Autowired UserRepository userRepository

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    Map get() {
        String msg = "hi shithead ${DateTime.now()}"
        log.info("called /hi msg={}", msg)

        List<User> users = new ArrayList<>(userRepository.collect({ it }))

        Map ret = [:]
        ret.msg = msg
        ret.users = users
        return ret
    }
}
