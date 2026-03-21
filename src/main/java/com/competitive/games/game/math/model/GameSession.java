package com.competitive.games.game.math.model;

public class GameSession {
    private String id;
    private PlayerState player1;
    private PlayerState player2;
    private MathProblem currentProblem;
    private GameStatus status;
    private String winner;
    private String buzzedBy;
    private int currentRound = 1;
    private static final int MAX_ROUNDS = 10;

    public enum GameStatus {
        WAITING, PLAYING, FINISHED
    }

    public GameSession(String id, String player1Username, String player2Username) {
        this.id = id;
        this.player1 = new PlayerState(player1Username);
        this.player2 = new PlayerState(player2Username);
        this.status = GameStatus.PLAYING;
        this.buzzedBy = null;
    }

    public String getBuzzedBy() {
        return buzzedBy;
    }

    public void setBuzzedBy(String buzzedBy) {
        this.buzzedBy = buzzedBy;
    }

    public void resetBuzzer() {
        this.buzzedBy = null;
    }

    public String getId() {
        return id;
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public MathProblem getCurrentProblem() {
        return currentProblem;
    }

    public void setCurrentProblem(MathProblem currentProblem) {
        this.currentProblem = currentProblem;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void incrementRound() {
        this.currentRound++;
    }

    public static int getMaxRounds() {
        return MAX_ROUNDS;
    }
}
