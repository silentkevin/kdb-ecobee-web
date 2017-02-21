package com.sksi.ecobee;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import groovy.transform.TypeChecked;
import groovy.transform.TypeCheckingMode;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitAllExpression = {"/hi"};
        http
                .authorizeRequests()
                .antMatchers(permitAllExpression)
                .permitAll()
            .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }
}
