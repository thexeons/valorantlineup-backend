package org.gtf.valorantlineup.config;


import org.gtf.valorantlineup.exception.ForbiddenHandler;
import org.gtf.valorantlineup.security.implementation.UserDetailsServiceImpl;
import org.gtf.valorantlineup.security.jwt.AuthEntryPointJwt;
import org.gtf.valorantlineup.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//Enables spring to detect this class and apply it to the global web security
@EnableWebSecurity
//Enables AOP security on methods to use @PreAuthorize and @PostAuthorize on controllers
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	private ForbiddenHandler forbiddenHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	//Define password encoder, otherwise plaintext password will be used.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//
		http.headers().xssProtection();
		http.cors().and().csrf().disable() //disable CSRF because JWT token in invulnerable
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).accessDeniedHandler(forbiddenHandler).and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //JWT is stateless thus can't maintain user session
			.authorizeRequests().antMatchers("/api/auth/**").permitAll()
			.antMatchers(HttpMethod.GET,
					"/swagger-ui/**",
					"/api/**",
					"/v3/api-docs/**",           // swagger
					"/webjars/**",            // swagger-ui webjars
					"/swagger-resources/**",  // swagger-ui resources
					"/configuration/**",      // swagger configuration
					"/swagger-ui.html"
			).permitAll()
			.anyRequest().authenticated();
		//security filter, load JWT before
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		//		Another method of registering Authentication Entry Point by using arrow function
//		http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//			response.setHeader("WWW-Authenticate", "Bearer"); // we can point explicitly to register/login/refresh URL
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//		});
	}
}