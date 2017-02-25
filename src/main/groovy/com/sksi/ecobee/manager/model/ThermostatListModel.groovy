package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty

class ThermostatListModel {
    PageModel page
    @JsonProperty("thermostatList")
    List<ThermostatModel> thermostats
    StatusModel status
}
