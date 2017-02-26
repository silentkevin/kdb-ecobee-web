package com.sksi.ecobee.security

import com.sksi.ecobee.data.User
import com.sksi.ecobee.data.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Service
@CompileStatic
@Slf4j
class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
        log.debug("finding user username={}", username)
        if (user == null) {
            throw new UsernameNotFoundException("User $username not found")
        }

        Set<GrantedAuthority> grantedAuthorities = user.getRoles().collect({ it.asGrantedAuthority() }).toSet()

        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), grantedAuthorities);
    }
}
