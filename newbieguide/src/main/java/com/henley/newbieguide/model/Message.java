package com.henley.newbieguide.model;

/**
 * @author Henley
 * @date 2017/9/28 14:48
 */
public class Message {

    public String message;
    public int textSize = -1;

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, int textSize) {
        this.message = message;
        this.textSize = textSize;
    }

}
