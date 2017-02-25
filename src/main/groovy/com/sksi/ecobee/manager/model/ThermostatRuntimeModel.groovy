package com.sksi.ecobee.manager.model

class ThermostatRuntimeModel {
    Boolean connected
    Integer actualTemperature
    Integer actualHumidity
    Integer desiredHeat
    Integer desiredCool
    String desiredFanMode
    List<Integer> desiredHeatRange
    List<Integer> desiredCoolRange
}
