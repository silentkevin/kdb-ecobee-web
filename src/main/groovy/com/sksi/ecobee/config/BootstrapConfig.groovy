package com.sksi.ecobee.config

import com.sksi.ecobee.data.EcobeeUserRepository
import com.sksi.ecobee.data.Role
import com.sksi.ecobee.data.RoleRepository
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.EcobeeAuthManager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Configuration
@CompileStatic
@Slf4j
class BootstrapConfig implements CommandLineRunner {
    @Autowired UserRepository userRepository
    @Autowired RoleRepository roleRepository
    @Autowired EcobeeUserRepository ecobeeUserRepository
    @Autowired EcobeeAuthManager ecobeeAuthManager

    @Override
    void run(String... strings) throws Exception {
        if (roleRepository.count() > 0) {
            return
        }

        Role userRole = new Role(
            name: "user"
        )
        roleRepository.save(userRole)

        Role adminRole = new Role(
            name: "admin"
        )
        roleRepository.save(adminRole)
    }
}
