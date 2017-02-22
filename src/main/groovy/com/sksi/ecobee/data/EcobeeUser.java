package com.sksi.ecobee.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @OneToOne(fetch = FetchType.EAGER, optional = false)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EcobeeUser that = (EcobeeUser) o;

        return new EqualsBuilder()
            .append(getId(), that.getId())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(getId())
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("user", user)
            .append("pinCode", pinCode)
            .append("ecobeeCode", ecobeeCode)
            .append("accessToken", accessToken)
            .append("refreshToken", refreshToken)
            .append("accessTokenExpirationDate", accessTokenExpirationDate)
            .toString();
    }
}
