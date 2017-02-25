package com.sksi.ecobee.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThermostatRepository extends JpaRepository<Thermostat, String> {
}
