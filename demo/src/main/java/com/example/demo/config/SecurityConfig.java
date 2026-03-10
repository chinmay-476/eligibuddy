package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authenticationProvider(authenticationProvider())
			.csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, but you can enable it later
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/data/**", "/test-auth", "/debug-users", "/users-info").permitAll()
				.requestMatchers("/contact").authenticated()
				.requestMatchers("/profile", "/profile/**").authenticated()
				.requestMatchers("/view-contacts", "/view-users", "/manage-scholarships", "/manage-schemes", "/manage-exams", "/manage-jobs").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/api/scholarships/admin/**", "/api/schemes/admin/**", "/api/exams/admin/**", "/api/jobs/admin/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/api/scholarships/**", "/api/schemes/**", "/api/exams/**", "/api/jobs/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/scholarships/**", "/api/schemes/**", "/api/exams/**", "/api/jobs/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/scholarships/**", "/api/schemes/**", "/api/exams/**", "/api/jobs/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/scholarships/**", "/api/schemes/**", "/api/exams/**", "/api/jobs/**").hasRole("ADMIN")
				.requestMatchers("/api/chat/**").authenticated()
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
} 

