package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty

class ClimateModel {
    String name
    @JsonProperty("isOccupied")
    Boolean occupied
    String type
    Integer coolTemp
    Integer heatTemp
}
