package com.competitive.games.game.math;

import com.competitive.games.game.math.model.GameMessage;
import com.competitive.games.game.math.model.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MathGameController {

    @Autowired
    private MathGameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/math/join")
    public void joinQueue(@Payload GameMessage message) {
        String username = message.getSender();
        GameSession session = gameService.joinQueue(username);

        if (session != null) {
            // Match found! Notify both players privately
            GameMessage matchFoundMsg = new GameMessage(GameMessage.MessageType.MATCH_FOUND, "Opponent found");
            matchFoundMsg.setPayload(session);

            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer1().getUsername(), matchFoundMsg);
            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer2().getUsername(), matchFoundMsg);
        } else {
            // Still waiting
            GameMessage waitMsg = new GameMessage(GameMessage.MessageType.JOIN_QUEUE, "Waiting for opponent...");
            messagingTemplate.convertAndSend("/topic/game/" + username, waitMsg);
        }
    }

    @MessageMapping("/math/answer")
    public void submitAnswer(@Payload GameMessage message) {
        String username = message.getSender();
        int answer;
        try {
            answer = Integer.parseInt(message.getPayload().toString());
        } catch (NumberFormatException e) {
            return;
        }

        GameSession session = gameService.submitAnswer(username, answer);

        if (session != null) {
            GameMessage updateMsg;
            if (session.getStatus() == GameSession.GameStatus.FINISHED) {
                updateMsg = new GameMessage(GameMessage.MessageType.GAME_OVER,
                        "Game Over! Winner: " + session.getWinner());
            } else {
                updateMsg = new GameMessage(GameMessage.MessageType.GAME_UPDATE, "Score Update");
            }
            updateMsg.setPayload(session);

            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer1().getUsername(), updateMsg);
            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer2().getUsername(), updateMsg);
        }
    }

    @MessageMapping("/math/buzz")
    public void buzz(@Payload GameMessage message) {
        String username = message.getSender();
        GameSession session = gameService.buzz(username);

        if (session != null) {
            GameMessage buzzMsg = new GameMessage(GameMessage.MessageType.BUZZED, username + " buzzed!");
            buzzMsg.setPayload(session);

            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer1().getUsername(), buzzMsg);
            messagingTemplate.convertAndSend("/topic/game/" + session.getPlayer2().getUsername(), buzzMsg);
        }
    }
}
