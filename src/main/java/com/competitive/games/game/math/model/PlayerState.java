package com.competitive.games.game.math.model;

public class PlayerState {
    private String username;
    private int score;

    public PlayerState(String username) {
        this.username = username;
        this.score = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int points) {
        this.score += points;
    }
}
