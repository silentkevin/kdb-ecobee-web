package com.sksi.ecobee.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class EcobeeUser {
    private String id;
    private User user;
    private String pinCode;
    private String ecobeeCode;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpirationDate;

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

    @OneToOne
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getPinCode() {
        return pinCode;
    }
    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getEcobeeCode() {
        return ecobeeCode;
    }
    public void setEcobeeCode(String ecobeeCode) {
        this.ecobeeCode = ecobeeCode;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getAccessTokenExpirationDate() {
        return accessTokenExpirationDate;
    }
    public void setAccessTokenExpirationDate(Date accessTokenExpirationDate) {
        this.accessTokenExpirationDate = accessTokenExpirationDate;
    }
}
