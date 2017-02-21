package com.sksi.ecobee.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@Configuration
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Profile("heroku")
@CompileStatic
@Slf4j
class HerokuConfig {
    @PostConstruct
    void init() {
        log.info("HEROKU IS ACTIVE")
    }

//    @Bean
//    public BasicDataSource dataSource() throws URISyntaxException {
//        URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];
//        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
//
//        BasicDataSource basicDataSource = new BasicDataSource();
//        basicDataSource.setUrl(dbUrl);
//        basicDataSource.setUsername(username);
//        basicDataSource.setPassword(password);
//
//        return basicDataSource;
//    }
}
