package com.hlidskialf.spellcast.server.question;

/**
 * Created by wiggins on 1/20/15.
 */
public class Question {
    protected String identifier;
    protected String answer;

    public Question() { }

    public Question(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean hasAnswer() {
        return (this.answer != null && this.answer.length() > 0);
    }
}
