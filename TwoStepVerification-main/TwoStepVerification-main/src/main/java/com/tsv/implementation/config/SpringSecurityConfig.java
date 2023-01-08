package com.tsv.implementation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tsv.implementation.service.DefaultUserService;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private  DefaultUserService userDetailsService;     //Initialising User Service
	
	@Autowired
	AuthenticationSuccessHandler successHandler;    //after success of login

	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }   //Password Encryption
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {         //Provides Authentication object
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
	
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
	
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    
        http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/registration/**").permitAll()        //Permission Granting
        .and()
        .formLogin(             //Flow of the program
                form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")  
                        .permitAll().successHandler(successHandler)
        ).logout(
                logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()


        );
        return http.build();
    }
}