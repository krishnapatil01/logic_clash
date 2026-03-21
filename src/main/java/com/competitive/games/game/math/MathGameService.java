package com.competitive.games.game.math;

import com.competitive.games.game.math.model.GameSession;
import com.competitive.games.game.math.model.MathProblem;
import com.competitive.games.game.math.model.PlayerState;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MathGameService {

    private final Queue<String> waitingPlayers = new ConcurrentLinkedQueue<>();
    private final Map<String, GameSession> playerSessions = new ConcurrentHashMap<>();
    private final Map<String, GameSession> activeSessions = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public GameSession joinQueue(String username) {
        if (playerSessions.containsKey(username)) {
            return playerSessions.get(username);
        }

        waitingPlayers.remove(username); // prevent duplicate

        if (!waitingPlayers.isEmpty()) {
            String opponent = waitingPlayers.poll();
            if (opponent != null && !opponent.equals(username)) {
                String sessionId = UUID.randomUUID().toString();
                GameSession session = new GameSession(sessionId, opponent, username);
                session.setCurrentProblem(generateProblem(1)); // start with level 1

                activeSessions.put(sessionId, session);
                playerSessions.put(opponent, session);
                playerSessions.put(username, session);

                return session;
            }
        }

        waitingPlayers.add(username);
        return null; // Still waiting
    }

    public GameSession buzz(String username) {
        GameSession session = playerSessions.get(username);
        if (session == null || session.getStatus() != GameSession.GameStatus.PLAYING) {
            return null;
        }

        if (session.getBuzzedBy() == null) {
            session.setBuzzedBy(username);
            return session;
        }
        return null;
    }

    public GameSession submitAnswer(String username, int answer) {
        GameSession session = playerSessions.get(username);
        if (session == null || session.getStatus() != GameSession.GameStatus.PLAYING) return null;

        if (session.getBuzzedBy() == null || !session.getBuzzedBy().equals(username)) return null;

        PlayerState currentPlayer = session.getPlayer1().getUsername().equals(username) ? session.getPlayer1() : session.getPlayer2();
        
        if (session.getCurrentProblem().getAnswer() == answer) {
            currentPlayer.incrementScore(10);
        } else {
            currentPlayer.incrementScore(-5);
        }

        if (session.getCurrentRound() >= GameSession.getMaxRounds()) {
            session.setStatus(GameSession.GameStatus.FINISHED);
            if (session.getPlayer1().getScore() > session.getPlayer2().getScore()) {
                session.setWinner(session.getPlayer1().getUsername());
            } else if (session.getPlayer2().getScore() > session.getPlayer1().getScore()) {
                session.setWinner(session.getPlayer2().getUsername());
            } else {
                session.setWinner("Tie");
            }
            playerSessions.remove(session.getPlayer1().getUsername());
            playerSessions.remove(session.getPlayer2().getUsername());
        } else {
            session.incrementRound();
            session.setCurrentProblem(generateProblem(session.getCurrentRound()));
            session.resetBuzzer();
        }
        return session;
    }

    private MathProblem generateProblem(int level) {
        int a = random.nextInt(20 * level) + 5;
        int b = random.nextInt(20 * level) + 5;
        int type = random.nextInt(6);
        String question;
        int res;
        switch (type) {
            case 0: question = a + " + " + b; res = a + b; break;
            case 1: question = a + " - " + b; res = a - b; break;
            case 2: question = (a % 10 + 2) + " * " + (b % 10 + 2); res = (a % 10 + 2) * (b % 10 + 2); break;
            case 3: 
                int product = (a % 10 + 1) * (b % 10 + 1);
                question = product + " / " + (a % 10 + 1); 
                res = b % 10 + 1; 
                break;
            case 4: 
                int base = random.nextInt(15) + 2;
                question = base + "²";
                res = base * base;
                break;
            default:
                int base3 = random.nextInt(10) + 2;
                question = base3 + "³";
                res = base3 * base3 * base3;
                break;
        }
        
        List<Integer> options = new ArrayList<>();
        options.add(res);
        while (options.size() < 4) {
            int decoy = res + random.nextInt(21) - 10;
            if (!options.contains(decoy)) options.add(decoy);
        }
        Collections.shuffle(options);
        return new MathProblem(question, res, options);
    }

    public GameSession getSessionForUser(String username) {
        return playerSessions.get(username);
    }

    public void leaveGame(String username) {
        waitingPlayers.remove(username);
        GameSession session = playerSessions.remove(username);
        if (session != null) {
            String opponent = session.getPlayer1().getUsername().equals(username) ? session.getPlayer2().getUsername()
                    : session.getPlayer1().getUsername();
            playerSessions.remove(opponent);
            session.setStatus(GameSession.GameStatus.FINISHED);
            session.setWinner(opponent); // default win for opponent if one leaves
        }
    }
}
