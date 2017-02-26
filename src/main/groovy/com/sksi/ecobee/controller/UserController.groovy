package com.sksi.ecobee.controller

import com.sksi.ecobee.data.EcobeeUser
import com.sksi.ecobee.data.Role
import com.sksi.ecobee.data.RoleRepository
import com.sksi.ecobee.data.Thermostat
import com.sksi.ecobee.data.ThermostatRepository
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.EcobeeAuthManager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@RestController
@RequestMapping("/user")
@CompileStatic
@Slf4j
class UserController {
    @Autowired UserRepository userRepository
    @Autowired RepositoryEntityLinks repositoryEntityLinks
    @Autowired EcobeeAuthManager ecobeeAuthManager
    @Autowired ThermostatRepository thermostatRepository
    @Autowired RoleRepository roleRepository

    @Value('${com.sksi.ecobee.devUserName:#{null}}')
    String devUserName

    @RequestMapping(method = RequestMethod.GET)
    UserModel get() {
        User user = getCurrentUser()
        if (user?.ecobeeUser?.accessToken) {
            ecobeeAuthManager.updateThermostats(user.ecobeeUser)
        }
        return getUserModel(user)
    }

    @RequestMapping(path = "/regenerate", method = RequestMethod.POST)
    UserModel regenerate() {
        User user = getCurrentUser()
        user.ecobeeUser.pinCode = null
        user.ecobeeUser.ecobeeCode = null
        user.ecobeeUser.accessToken = null
        user.ecobeeUser.refreshToken = null
        user.ecobeeUser.accessToken = null
        ecobeeAuthManager.initUser(user)
        return getUserModel(user)
    }

    @RequestMapping(path = "/authorize", method = RequestMethod.POST)
    UserModel authorize() {
        User user = getCurrentUser()
        ecobeeAuthManager.getAccessToken(user)
        return getUserModel(user)
    }

    @RequestMapping(path = "/hold", method = RequestMethod.POST)
    HoldModel hold(@RequestBody HoldModel holdModel) {
        User user = getCurrentUser()
        Thermostat thermostat = user.ecobeeUser.thermostats.find { holdModel.thermostatName == it.name }
        String holdType = holdModel.holdMode
        Integer hours = null
        if (holdType == "Resume Schedule") {
            holdType = null
        } else {
            if (holdType == "2 Hours" || holdType == "useEndTime2Hour") {
                holdType = "holdHours"
                hours = 2
            } else if (holdType == "4 Hours" || holdType == "useEndTime4hour") {
                holdType = "holdHours"
                hours = 4
            } else if (holdType == "8 Hours" || holdType == "useEndTime8Hour") {
                holdType = "holdHours"
                hours = 8
            } else if (holdType == "Next Transition" || holdType == "nextPeriod" || holdType == "nextTransition") {
                holdType = "nextTransition"
                hours = null
            } else if (holdType == "Hold Forever" || holdType == "indefinite") {
                holdType = "indefinite"
                hours = null
            }
        }
        ecobeeAuthManager.setHold(thermostat, holdModel.desiredTemperature, holdType, hours)
        return holdModel
    }

    protected User getCurrentUser() {
        Boolean allowUserCreation = false
        String userName = null
        SecurityContext context = SecurityContextHolder.getContext()
        log.debug("context={}", context)
        Authentication authentication = context.getAuthentication()
        log.debug("devUserName={},context={},authentication={}", devUserName, context, authentication)

        if (devUserName && authentication.getAuthorities().find({ it.getAuthority() == "ROLE_ANONYMOUS" })) {
            userName = devUserName
        } else if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication
            userName = oAuth2Authentication.getPrincipal()
            if (userName && authentication.getAuthorities().find({ it.getAuthority() == "ROLE_USER" })) {
                allowUserCreation = true
            }
        } else {
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal()
            userName = springUser.getUsername()
        }
        log.debug("userName={}", userName)
        User user = userRepository.findByName(userName)
        if (user == null) {
            if (allowUserCreation) {
                Role userRole = roleRepository.findByName("user")
                user = new User(
                    name: userName,
                    displayName: userName,
                    email: "${userName}@github.com".toString(),
                    enabled: true,
                    roles: [userRole].toSet()
                )
                userRepository.save(user)

                ecobeeAuthManager.initUser(user)
                log.info("created userName={},user={}", userName, user)
            } else {
                throw new ResourceNotFoundException("user not found")
            }
        }
        return user
    }

    protected UserModel getUserModel(User user) {
        UserModel ret = new UserModel()
        ret.add(repositoryEntityLinks.linkToSingleResource(User.class, user.getId()))
        ret.add(repositoryEntityLinks.linkToSingleResource(EcobeeUser.class, user.getEcobeeUser().getId()))
        return ret
    }
}
