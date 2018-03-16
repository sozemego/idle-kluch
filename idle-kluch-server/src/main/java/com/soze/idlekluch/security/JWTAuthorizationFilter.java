package com.soze.idlekluch.security;

import com.soze.idlekluch.user.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String AUTHORIZATION = "Authorization";
  private static final String AUTHENTICATION_SCHEME = "Bearer";

  private final AuthService authService;

  public JWTAuthorizationFilter(final AuthenticationManager authenticationManager, final AuthService authService) {
    super(authenticationManager);
    this.authService = Objects.requireNonNull(authService);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(AUTHORIZATION);
    System.out.println("AUTHORIZING HEADER " + header);

    if(header != null) {
      final String[] tokens = header.split(" ");
      if(tokens.length == 2 && "null".equalsIgnoreCase(tokens[1])) {
        System.out.println("Bearer is null");
        chain.doFilter(req, res);
        return;
      }
    }

    if (header == null || !header.startsWith(AUTHENTICATION_SCHEME)) {
      System.out.println("SKIPPING AUTH HEADER");
      chain.doFilter(req, res);
      return;
    }

    System.out.println("NOT SKIPPING AUTH HEADER");

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(AUTHORIZATION).substring(AUTHENTICATION_SCHEME.length()).trim();

    if(!authService.validateToken(token)) {
      return null;
    }

    String username = authService.getUsernameClaim(token);
    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
  }

}
