package com.sksi.ecobee

import org.joda.time.DateTime

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
    @RequestMapping(method = RequestMethod.GET)
    String get() {
        String msg = "hi shithead ${DateTime.now()}"
        log.info("called /hi msg={}", msg)
        return msg
    }
}
