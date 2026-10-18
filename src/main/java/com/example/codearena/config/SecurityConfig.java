
package com.example.codearena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http) throws Exception {

		http
				.cors(cors -> {})
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((request, response, ex) ->
								response.sendError(
										HttpServletResponse.SC_UNAUTHORIZED
								)
						)
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								HttpMethod.POST,
								"/api/auth/login",
								"/api/auth/signup",
								"/api/auth/sendotp",
								"/api/auth/changepassword"
						).permitAll()
						.requestMatchers(
								"/",
								"/favicon.ico",
								"/error"
						).permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(
						jwtAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class
				);

		return http.build();
	}

		@Bean
		public FilterRegistrationBean<JwtAuthenticationFilter>
		jwtFilterServletRegistration(
				JwtAuthenticationFilter filter) {

			FilterRegistrationBean<JwtAuthenticationFilter> registration =
					new FilterRegistrationBean<>(filter);

			registration.setEnabled(false);
			return registration;
		}

}
