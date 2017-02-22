package com.sksi.ecobee.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EcobeeUserRepository extends JpaRepository<EcobeeUser, String> {
}
