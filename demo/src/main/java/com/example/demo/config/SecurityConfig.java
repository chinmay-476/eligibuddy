package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, but you can enable it later
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/test-auth", "/debug-users", "/users-info").permitAll()
				.requestMatchers("/contact").authenticated()
				.requestMatchers("/profile", "/profile/**").authenticated()
				.requestMatchers("/view-contacts", "/view-users", "/manage-scholarships").hasRole("ADMIN")
				.requestMatchers("/api/scholarships/**", "/api/schemes/**", "/api/exams/**", "/api/jobs/**", "/api/gemini/**").permitAll() // Allow public access to all APIs
				.anyRequest().authenticated() // Changed from permitAll() to authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login?error=true")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/")
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll()
			)
			.exceptionHandling(exceptions -> exceptions
				.accessDeniedPage("/login?error=access_denied")
			);

		return http.build();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder);
	}
} 

