package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime

import java.text.SimpleDateFormat

class EventModel {
    String type
    String name
    Boolean running

    Integer coolHoldTemp
    Integer heatHoldTemp

    @JsonProperty("startDate")
    String startDateDatePart
    @JsonProperty("startTime")
    String startDateTimePart

    @JsonProperty("endDate")
    String endDateDatePart
    @JsonProperty("endTime")
    String endDateTimePart

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateTime getStartDate() {
        return new DateTime(sdf.parse(startDateDatePart + " " + startDateTimePart))
    }
    DateTime getEndDate() {
        return new DateTime(sdf.parse(endDateDatePart + " " + endDateTimePart))
    }
}
