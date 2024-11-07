package com.example.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disabling CSRF for simplicity (not recommended for production)
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()  // Secure all /api endpoints
                .and()
                .httpBasic();  // Enable Basic Authentication
    }
}
