package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/20/15.
 */
public class Question {
    private String target;

    public Question() {
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean hasTarget() {
        return (this.target != null && this.target.length() > 0);
    }
}
