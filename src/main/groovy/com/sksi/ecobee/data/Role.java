package com.sksi.ecobee.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table
@Access(AccessType.PROPERTY)
public class Role {
    private String id;
    private String name;
    private Long version;

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

    @Column(unique = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public GrantedAuthority asGrantedAuthority() {
        return new SimpleGrantedAuthority(this.getName().toUpperCase());
    }

    @Version
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return new EqualsBuilder()
            .append(getId(), role.getId())
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
            .append("name", name)
            .toString();
    }
}
