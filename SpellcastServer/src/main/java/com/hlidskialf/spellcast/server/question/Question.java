package com.hlidskialf.spellcast.server.question;

import java.util.ArrayList;

/**
 * Created by wiggins on 1/20/15.
 */
public class Question {
    protected String identifier;
    protected String answer;
    protected ArrayList<String> options;

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

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }
}
