package com.sksi.ecobee.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitAllExpression = {"/", "/home", "/hi"};
//        http
//                .authorizeRequests()
//                .antMatchers(permitAllExpression)
//                .permitAll()
//            .and()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated();
//        http.authorizeRequests().antMatchers(permitAllExpression).permitAll();
        http
                .authorizeRequests()
                .antMatchers(permitAllExpression).permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
               .withUser("user").password("password").roles("USER");
    }
}
