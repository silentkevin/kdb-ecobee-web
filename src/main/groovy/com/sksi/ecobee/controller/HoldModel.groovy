package com.sksi.ecobee.controller

import org.springframework.hateoas.ResourceSupport

class HoldModel extends ResourceSupport {
    String thermostatName
    Integer desiredTemperature
    String holdMode
}
