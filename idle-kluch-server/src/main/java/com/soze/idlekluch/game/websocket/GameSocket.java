package com.soze.idlekluch.game.websocket;

import com.soze.idlekluch.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

@Service
public class GameSocket extends TextWebSocketHandler {

  private final GameService gameService;

  @Autowired
  public GameSocket(final GameService gameService) {
    this.gameService = Objects.requireNonNull(gameService);
  }

  @Override
  protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {
    super.handleBinaryMessage(session, message);
  }

  @Override
  public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
    gameService.onConnect(session);
  }

  @Override
  protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
  }

  @Override
  protected void handlePongMessage(final WebSocketSession session, final PongMessage message) throws Exception {
    super.handlePongMessage(session, message);
  }

  @Override
  public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
    super.handleTransportError(session, exception);
  }

  @Override
  public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
    gameService.onDisconnect(session);
  }
}
