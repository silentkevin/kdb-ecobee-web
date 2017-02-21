package com.sksi.ecobee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/hi")
public class MyController {
    public MyController() {
        String a = "";
    }

    @RequestMapping(method = RequestMethod.GET)
//    @PreAuthorize("permitAll()")
    public String get() {
        return "shit";
    }
}
