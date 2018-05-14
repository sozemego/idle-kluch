package com.soze.idlekluch.security;

import com.soze.idlekluch.user.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.ServletRequestUtils;

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
    if(header == null) {
      header = getQueryParamToken(req);
    }

    if(header != null) {
      final String[] tokens = header.split(" ");
      if(tokens.length == 2 && "null".equalsIgnoreCase(tokens[1])) {
        chain.doFilter(req, res);
        return;
      }
    }

    if (header == null || !header.startsWith(AUTHENTICATION_SCHEME)) {
      chain.doFilter(req, res);
      return;
    }

    final UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(final String header) {
    final String token = header.substring(AUTHENTICATION_SCHEME.length()).trim();

    if(!authService.validateToken(token)) {
      return null;
    }

    final String username = authService.getUsernameClaim(token);
    return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
  }

  private String getQueryParamToken(final HttpServletRequest request) {
    final String token = ServletRequestUtils.getStringParameter(request, "token", null);
    if(token == null) {
      return null;
    }

    return "Bearer " + token;
  }

}
