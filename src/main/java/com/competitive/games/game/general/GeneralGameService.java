package com.competitive.games.game.general;

import com.competitive.games.game.math.model.GameSession;
import com.competitive.games.game.math.model.MathProblem;
import com.competitive.games.game.math.model.PlayerState;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GeneralGameService {

    private final Map<String, Queue<String>> gameQueues = new ConcurrentHashMap<>();
    private final Map<String, GameSession> playerSessions = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public GameSession joinQueue(String username, String gameType) {
        if (playerSessions.containsKey(username)) {
            return playerSessions.get(username);
        }

        gameQueues.putIfAbsent(gameType, new ConcurrentLinkedQueue<>());
        Queue<String> queue = gameQueues.get(gameType);
        
        queue.remove(username);

        if (!queue.isEmpty()) {
            String opponent = queue.poll();
            if (opponent != null && !opponent.equals(username)) {
                String sessionId = UUID.randomUUID().toString();
                GameSession session = new GameSession(sessionId, opponent, username);
                session.setCurrentProblem(generateProblem(gameType, 1));

                playerSessions.put(opponent, session);
                playerSessions.put(username, session);

                return session;
            }
        }

        queue.add(username);
        return null;
    }

    public GameSession buzz(String username) {
        GameSession session = playerSessions.get(username);
        if (session != null && session.getBuzzedBy() == null) {
            session.setBuzzedBy(username);
            return session;
        }
        return null;
    }

    public GameSession submitAnswer(String username, String gameType, int answer) {
        GameSession session = playerSessions.get(username);
        if (session == null || !username.equals(session.getBuzzedBy())) return null;

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
            session.setCurrentProblem(generateProblem(gameType, session.getCurrentRound()));
            session.resetBuzzer();
        }
        return session;
    }

    private MathProblem generateProblem(String gameType, int level) {
        switch (gameType) {
            case "math":
                return generateMath(level);
            case "code":
                return generateCode(level);
            case "react":
                return generateReact(level);
            case "memory":
                return generateMemory(level);
            case "equation":
                return generateEquation(level);
            case "bluff":
                return generateBluff(level);
            case "graph":
                return generateGraph(level);
            case "ai":
                return generateAI(level);
            default:
                return generateMath(level);
        }
    }

    private MathProblem generateMath(int level) {
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
        return new MathProblem(question, res, generateOptions(res));
    }

    private MathProblem generateCode(int level) {
        int code = 1000 + random.nextInt(9000);
        int sumDigits = 0;
        int temp = code;
        while (temp > 0) {
            sumDigits += temp % 10;
            temp /= 10;
        }
        return new MathProblem("CODE " + code + ": Sum of digits?", sumDigits, generateOptions(sumDigits));
    }

    private MathProblem generateReact(int level) {
        int target = random.nextInt(100) + 1;
        int shift = random.nextInt(10) - 5;
        int nextState = target + shift;
        return new MathProblem("STIMULUS: " + target + " (IF SHIFT=" + shift + ", NEXT STATE?)", nextState, generateOptions(nextState));
    }

    private MathProblem generateMemory(int level) {
        int sequence = 10000 + random.nextInt(90000);
        int digitIndex = random.nextInt(5) + 1;
        return new MathProblem("MEMORIZE: " + sequence + " (WHAT WAS DIGIT #" + digitIndex + "?)", (String.valueOf(sequence).charAt(digitIndex-1) - '0'), generateOptions(sequence % 10));
    }

    private MathProblem generateEquation(int level) {
        int target = 20 + random.nextInt(80);
        int start = target - random.nextInt(20);
        int diff = target - start;
        return new MathProblem("TARGET: " + target + " (BUILD: " + start + " + ?)", diff, generateOptions(diff));
    }

    private MathProblem generateBluff(int level) {
        String[] questions = {
            "Is 0.1 + 0.2 === 0.3? (1:No, 0:Yes)",
            "Does (x > y) imply (y < x)? (1:Yes, 0:No)",
            "Is Math.sqrt(-1) rational? (1:No, 0:Yes)",
            "Does 10 >> 1 == 5? (1:Yes, 0:No)",
            "Is null == undefined? (1:Yes, 0:No)"
        };
        int[] answers = {1, 1, 1, 1, 1}; // Using 1 for True/Yes in these contexts for simplicity, adjust if needed
        int idx = random.nextInt(questions.length);
        return new MathProblem(questions[idx], answers[idx], Arrays.asList(0, 1, 2, 3));
    }

    private MathProblem generateGraph(int level) {
        int nodes = random.nextInt(10) + level + 5;
        int type = random.nextInt(3);
        String q;
        int a;
        if (type == 0) {
            q = "Complete Graph K-" + nodes + ": Total Edges?";
            a = nodes * (nodes - 1) / 2;
        } else if (type == 1) {
            q = "Star Graph S-" + nodes + ": Max Degree?";
            a = nodes - 1;
        } else {
            q = "Cycle Graph C-" + nodes + ": Total Edges?";
            a = nodes;
        }
        return new MathProblem(q, a, generateOptions(a));
    }

    private MathProblem generateAI(int level) {
        int depth = 2 + random.nextInt(3);
        int branching = 2 + random.nextInt(2);
        int type = random.nextInt(2);
        String q;
        int a;
        if (type == 0) {
            a = (int) ((Math.pow(branching, depth + 1) - 1) / (branching - 1));
            q = "Tree D=" + depth + ", B=" + branching + ": Total Nodes?";
        } else {
            a = (int) Math.pow(branching, depth);
            q = "Tree D=" + depth + ", B=" + branching + ": Leaf Nodes?";
        }
        return new MathProblem("AI SEARCH: " + q, a, generateOptions(a));
    }

    private List<Integer> generateOptions(int correct) {
        List<Integer> options = new ArrayList<>();
        options.add(correct);
        while (options.size() < 4) {
            int decoy = correct + random.nextInt(21) - 10;
            if (!options.contains(decoy)) options.add(decoy);
        }
        Collections.shuffle(options);
        return options;
    }
}
