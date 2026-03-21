package com.competitive.games.game.math.model;

public class GameMessage {
    private MessageType type;
    private String content;
    private String sender;
    private Object payload;

    public enum MessageType {
        JOIN_QUEUE,
        MATCH_FOUND,
        SUBMIT_ANSWER,
        GAME_UPDATE,
        GAME_OVER,
        BUZZED,
        ERROR
    }

    public GameMessage() {
    }

    public GameMessage(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
