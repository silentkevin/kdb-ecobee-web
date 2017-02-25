package com.sksi.ecobee.manager.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
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

    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime getStartDate(DateTimeZone dateTimeZone) {
        String d = "${startDateDatePart} ${startDateTimePart}"
        log.debug("about to parse d={},zone={}", d, dateTimeZone)
        DateTime ret = dateTimeFormatter.withZone(dateTimeZone).parseDateTime(d)
        log.debug("parsed ret={},d={},zone={}", ret, d, dateTimeZone)
        return ret
    }

    DateTime getEndDate(DateTimeZone dateTimeZone) {
        String d = "${endDateDatePart} ${endDateTimePart}"
        log.debug("about to parse d={},zone={}", d, dateTimeZone)
        DateTime ret = dateTimeFormatter.withZone(dateTimeZone).parseDateTime(d)
        log.debug("parsed ret={},d={},zone={}", ret, d, dateTimeZone)
        return ret
    }
}
