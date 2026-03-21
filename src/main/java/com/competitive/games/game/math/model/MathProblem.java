package com.competitive.games.game.math.model;

import java.util.List;

public class MathProblem {
    private String question;
    private int answer;
    private List<Integer> options;

    public MathProblem(String question, int answer, List<Integer> options) {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    public List<Integer> getOptions() {
        return options;
    }

    public String getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }
}
