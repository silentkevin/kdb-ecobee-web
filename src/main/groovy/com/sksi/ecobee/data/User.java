package com.sksi.ecobee.data;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kdb_user") // 'user' is a reserved word in postgresql
@Access(AccessType.PROPERTY)
public class User {
    private String id;
    private String name;
    private String displayName;

    @Id
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

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
