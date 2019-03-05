package com.uniovi;

import com.uniovi.services.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final RolesService rolesService;

	@Autowired
	public WebSecurityConfig(UserDetailsService userDetailsService, RolesService rolesService) {
		this.userDetailsService = userDetailsService;
		this.rolesService = rolesService;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
				.disable()
				.authorizeRequests()
				.antMatchers("/css/**", "/img/**", "/script/**", "/", "/signup", "/login/**")
				.permitAll()
				.antMatchers("/admin/**")
				.hasAnyAuthority(rolesService.getRoles()[1])
				.antMatchers("/item/**")
				.hasAnyAuthority(rolesService.getRoles()[0])
				.antMatchers("/chat/**")
				.hasAnyAuthority(rolesService.getRoles()[0])
				.anyRequest()
				.authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.defaultSuccessUrl("/loginSuccess")
				.and()
				.logout()
				.permitAll();
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public SpringSecurityDialect securityDialect() {
		return new SpringSecurityDialect();
	}

}