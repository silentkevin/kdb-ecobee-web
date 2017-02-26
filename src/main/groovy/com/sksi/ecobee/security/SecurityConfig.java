package com.sksi.ecobee.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig {//extends WebSecurityConfigurerAdapter {
//    @Autowired private UserDetailsService userDetailsService;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // @formatter:off
//        http.antMatcher("/**").authorizeRequests()
//            .antMatchers("/", "/login**", "/webjars/**").permitAll().anyRequest().authenticated()
//            .and().logout().logoutSuccessUrl("/").permitAll()
//            .and().csrf().disable();
//        // @formatter:on
//    }

//    @Autowired private OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        String a = "";
////            // @formatter:off
////            http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**").permitAll().anyRequest()
////                .authenticated().and().exceptionHandling()
////                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and().logout()
////                .logoutSuccessUrl("/").permitAll().and().csrf()
////                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
////                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
////            // @formatter:on
//    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
//    }
}
