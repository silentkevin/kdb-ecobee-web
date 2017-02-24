package com.sksi.ecobee.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired private UserDetailsService userDetailsService;

    @Value("${com.sksi.ecobee.extraPermitAllExpressions:''}")
    String extraPermitAllExpressions;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] splits = extraPermitAllExpressions.split(",");
        List<String> asList = new ArrayList<>(Arrays.asList("/logout", "/login"));
        if (splits.length > 1) {
            List<String> s = Arrays.asList(splits);
            asList.addAll(s);
        }
        String[] permitAllExpression = asList.toArray(new String[asList.size()]);
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
//        auth
//            .inMemoryAuthentication()
//               .withUser("user").password("password").roles("USER");
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
