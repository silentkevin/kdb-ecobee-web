package com.sksi.ecobee.controller

import com.sksi.ecobee.data.EcobeeUser
import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository
import com.sksi.ecobee.manager.EcobeeAuthManager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
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

    @Value('${com.sksi.ecobee.devUserName:#{null}}')
    String devUserName

    @RequestMapping(method = RequestMethod.GET)
    UserModel get() {
        User user = getCurrentUser()
        return getUserModel(user)
    }

    @RequestMapping(path = "/authorize", method = RequestMethod.POST)
    UserModel authorize() {
        User user = getCurrentUser()
        ecobeeAuthManager.getAccessToken(user)
        return getUserModel(user)
    }

    protected User getCurrentUser() {
        String userName = null
        SecurityContext context = SecurityContextHolder.getContext()
        Authentication authentication = context.getAuthentication()

        if (authentication.getAuthorities().find({ GrantedAuthority ga -> ga.getAuthority() == "ROLE_ANONYMOUS" })) {
            if (devUserName != null) {
                userName = devUserName
            }
        } else {
            userName = authentication.getPrincipal().toString()
        }
        User user = userRepository.findByName(userName)
        if (user == null) {
            throw new ResourceNotFoundException("user not found")
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
