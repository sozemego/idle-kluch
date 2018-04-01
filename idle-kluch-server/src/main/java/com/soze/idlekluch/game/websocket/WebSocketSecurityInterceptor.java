package com.soze.idlekluch.game.websocket;

import com.soze.idlekluch.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class WebSocketSecurityInterceptor extends ChannelInterceptorAdapter {

  private static final String TOKEN = "token";

  private final AuthService authService;

  @Autowired
  public WebSocketSecurityInterceptor(final AuthService authService) {
    this.authService = Objects.requireNonNull(authService);
  }

  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if(StompCommand.CONNECT == accessor.getCommand()) {
      final String token = accessor.getFirstNativeHeader(TOKEN);

      if(token == null) {
        return message;
      }

      final Optional<UsernamePasswordAuthenticationToken> usernamePasswordAuthenticationToken = getAuthentication(token);
      if(usernamePasswordAuthenticationToken.isPresent()) {
        accessor.setUser(usernamePasswordAuthenticationToken.get());
      }

    }

    return message;
  }

  private Optional<UsernamePasswordAuthenticationToken> getAuthentication(final String token) {
    if(!authService.validateToken(token)) {
      return Optional.empty();
    }

    String username = authService.getUsernameClaim(token);
    return Optional.of(
      new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>())
    );
  }

}
