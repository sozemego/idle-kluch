package com.soze.idlekluch;

import com.google.common.collect.ImmutableList;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.security.JWTAuthenticationFilter;
import com.soze.idlekluch.security.JWTAuthorizationFilter;
import com.soze.idlekluch.user.service.AuthService;
import com.soze.idlekluch.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Objects;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final UserService userService;
  private final AuthService authService;

  @Autowired
  public SecurityConfiguration(final UserService userService, final AuthService authService) {
    this.userService = Objects.requireNonNull(userService);
    this.authService = Objects.requireNonNull(authService);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
      .cors()
      .configurationSource(corsConfigurationSource())
      .and()
        .csrf().disable()
      .authorizeRequests()
        .antMatchers(
          Routes.USER_BASE + Routes.USER_DELETE_SINGLE,
          Routes.AUTH_BASE + Routes.AUTH_PASSWORD_CHANGE,
          Routes.USER_BASE + Routes.USER_DELETE_SINGLE,
          Routes.AUTH_BASE + Routes.AUTH_PASSWORD_CHANGE,
          Routes.AUTH_BASE + Routes.AUTH_LOGOUT,
          Routes.KINGDOM_BASE + Routes.KINGDOM_CREATE,
          Routes.KINGDOM_BASE + Routes.KINGDOM_DELETE
        )
        .authenticated()
        .anyRequest().permitAll()
      .and()
        .addFilter(new JWTAuthenticationFilter(authenticationManager(), authService))
        .addFilter(new JWTAuthorizationFilter(authenticationManager(), authService))
        // this disables session creation on Spring Security
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(ImmutableList.of("*"));
    corsConfiguration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

}
