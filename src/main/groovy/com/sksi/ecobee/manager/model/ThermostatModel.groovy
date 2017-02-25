package com.sksi.ecobee.manager.model

class ThermostatModel {
    String identifier
    String name
    String modelNumber
    String brand
    Boolean isRegistered

    ThermostatSettingsModel settings

    ThermostatRuntimeModel runtime

    List<EventModel> events

    ProgramModel program
}
