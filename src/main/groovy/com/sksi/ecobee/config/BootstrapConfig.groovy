package com.sksi.ecobee.config

import com.sksi.ecobee.data.EcobeeUser
import com.sksi.ecobee.data.EcobeeUserRepository
import com.sksi.ecobee.data.Role
import com.sksi.ecobee.data.RoleRepository
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.EcobeeAuthManager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@Configuration
@CompileStatic
@Slf4j
class BootstrapConfig implements CommandLineRunner {
    @Autowired UserRepository userRepository
    @Autowired RoleRepository roleRepository
    @Autowired EcobeeUserRepository ecobeeUserRepository
    @Autowired BCryptPasswordEncoder bCryptPasswordEncoder
    @Autowired EcobeeAuthManager ecobeeAuthManager

    @Override
    void run(String... strings) throws Exception {
        if (userRepository.count() > 0) {
            return
        }

        Role userRole = new Role(
            id: "27bf569f-9515-4a29-b33b-f54f74b95cc5",
            name: "user"
        )
        roleRepository.save(userRole)

        Role adminRole = new Role(
            id: "8d11800a-a663-44e4-baa2-18fd2a675305",
            name: "admin"
        )
        roleRepository.save(adminRole)

        User user = new User(
            id: "3802802c-5cf8-40a4-96c6-095ccf653d06",
            name: "kevin",
            displayName: "Kevin",
            email: "myEmail@email.com",
            password: bCryptPasswordEncoder.encode("password"),
            enabled: true,
            roles: [userRole, adminRole].toSet()
        )
        userRepository.save(user)

        ecobeeAuthManager.initUser(user)

//        user = new User(
//            id: UUID.randomUUID().toString(),
//            name: "other",
//            displayName: "Other Person",
//            email: "other@email.com",
//            password: bCryptPasswordEncoder.encode("password"),
//            enabled: true,
//            roles: [userRole].toSet()
//        )
//        userRepository.save(user)
//
//        user = new User(
//            id: UUID.randomUUID().toString(),
//            name: "other2",
//            displayName: "Other Person 2",
//            email: "other2@email.com",
//            password: bCryptPasswordEncoder.encode("password"),
//            enabled: false,
//            roles: [userRole].toSet()
//        )
//        userRepository.save(user)

//        ecobeeAuthManager.initUser(user)
    }
}
