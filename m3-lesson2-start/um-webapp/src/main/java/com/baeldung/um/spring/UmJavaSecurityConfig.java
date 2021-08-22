package com.baeldung.um.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@ComponentScan("com.baeldung.um.security")
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class UmJavaSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    //

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
            .anyRequest()
            //.antMatchers("/api/**"). //with a matcher to all /api endpoints
            //.regexMatchers("^/login.*")
            //permitAll()
            .authenticated().and()
            //anonymous()
            //denyAll()
            //.fullyAuthenticated().and() //enforce authentication on the resource
            //.hasAnyAuthority("ROLE_SOMETHING", "ROLE_2").and()//many roles
            //.hasAuthority("ROLE_1") //
            //.hasIpAddress("127.0.0.1") //only by address
            //.acces("hasRole(ROLE_SOMETHING)") via security expression, it is more flexible
            .httpBasic().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .csrf()
            .disable();
        // @formatter:on
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
