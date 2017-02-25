package com.sksi.ecobee.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table
public class Thermostat {
    private String id;
    private String name;
    private BigDecimal currentTemperature;
    private BigDecimal currentHumidity;
    private String hvacMode;

    private Long version;

    private EcobeeUser ecobeeUser;

    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "string-uuid")
    @GenericGenerator(name = "string-uuid", strategy = "com.sksi.ecobee.data.StringUUIDGenerator")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentTemperature() {
        return currentTemperature;
    }
    public void setCurrentTemperature(BigDecimal currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public BigDecimal getCurrentHumidity() {
        return currentHumidity;
    }
    public void setCurrentHumidity(BigDecimal currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public String getHvacMode() {
        return hvacMode;
    }
    public void setHvacMode(String hvacMode) {
        this.hvacMode = hvacMode;
    }

    @ManyToOne(optional = false)
    public EcobeeUser getEcobeeUser() {
        return ecobeeUser;
    }
    public void setEcobeeUser(EcobeeUser ecobeeUser) {
        this.ecobeeUser = ecobeeUser;
    }

    @Version
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
}
