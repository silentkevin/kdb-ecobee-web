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
import java.util.Date;

@Entity
@Table
public class Thermostat {
    private String id;
    private String name;
    private String ecobeeId;
    private BigDecimal currentTemperature;
    private BigDecimal currentHumidity;
    private Integer desiredTemperature;
    private String hvacMode;
    private String holdMode;  // one of "Schedule", "2H", "4H", "8H", "NT", "Hold"
    private Date holdUntil;
    private String holdAction;

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

    public Integer getDesiredTemperature() {
        return desiredTemperature;
    }
    public void setDesiredTemperature(Integer desiredTemperature) {
        this.desiredTemperature = desiredTemperature;
    }

    public String getEcobeeId() {
        return ecobeeId;
    }
    public void setEcobeeId(String ecobeeId) {
        this.ecobeeId = ecobeeId;
    }

    public String getHvacMode() {
        return hvacMode;
    }
    public void setHvacMode(String hvacMode) {
        this.hvacMode = hvacMode;
    }

    public String getHoldMode() {
        return holdMode;
    }
    public void setHoldMode(String holdMode) {
        this.holdMode = holdMode;
    }

    public Date getHoldUntil() {
        return holdUntil;
    }
    public void setHoldUntil(Date holdUntil) {
        this.holdUntil = holdUntil;
    }

    public String getHoldAction() {
        return holdAction;
    }
    public void setHoldAction(String holdAction) {
        this.holdAction = holdAction;
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
