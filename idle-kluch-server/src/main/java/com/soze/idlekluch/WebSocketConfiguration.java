package com.soze.idlekluch;

import com.soze.idlekluch.game.websocket.GameSocketLoggerInterceptor;
import com.soze.idlekluch.game.websocket.GameSocketOutgoingLoggerInterceptor;
import com.soze.idlekluch.game.websocket.GameSocketSessionInterceptor;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@Component
public class WebSocketConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  private final GameSocketSessionInterceptor gameSocketSessionInterceptor;
  private final GameSocketLoggerInterceptor gameSocketLoggerInterceptor;
  private final GameSocketOutgoingLoggerInterceptor gameSocketOutgoingLoggerInterceptor;

  @Autowired
  public WebSocketConfiguration(final GameSocketSessionInterceptor gameSocketSessionInterceptor, final GameSocketLoggerInterceptor gameSocketLoggerInterceptor, final GameSocketOutgoingLoggerInterceptor gameSocketOutgoingLoggerInterceptor) {
    this.gameSocketSessionInterceptor = Objects.requireNonNull(gameSocketSessionInterceptor);
    this.gameSocketLoggerInterceptor = Objects.requireNonNull(gameSocketLoggerInterceptor);
    this.gameSocketOutgoingLoggerInterceptor = Objects.requireNonNull(gameSocketOutgoingLoggerInterceptor);
  }

  @Override
  protected void customizeClientInboundChannel(final ChannelRegistration registration) {
    registration.interceptors(
      gameSocketLoggerInterceptor,
      gameSocketSessionInterceptor
    );
  }

  @Override
  public void configureClientOutboundChannel(final ChannelRegistration registration) {
    registration.interceptors(gameSocketOutgoingLoggerInterceptor);
  }

  @Override
  protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
    messages.simpDestMatchers(Routes.GAME_SOCKET + "/**").authenticated();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(Routes.GAME);
    registry.setApplicationDestinationPrefixes(Routes.GAME);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(Routes.GAME_SOCKET).setAllowedOrigins("*").withSockJS();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }

}
