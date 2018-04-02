package com.soze.idlekluch;

import com.soze.idlekluch.game.websocket.GameSocketSessionInterceptor;
import com.soze.idlekluch.game.websocket.WebSocketSecurityInterceptor;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Autowired
  private WebSocketSecurityInterceptor webSocketSecurityInterceptor;

  @Autowired
  private GameSocketSessionInterceptor gameSocketSessionInterceptor;

  @Override
  protected void customizeClientInboundChannel(final ChannelRegistration registration) {
    registration.interceptors(
      webSocketSecurityInterceptor,
      gameSocketSessionInterceptor
    );
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
