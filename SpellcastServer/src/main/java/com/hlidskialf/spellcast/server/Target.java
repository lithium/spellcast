package com.hlidskialf.spellcast.server;

/**
 * Created by wiggins on 1/19/15.
 */
public abstract class Target {
    protected int hitpoints;
    protected int maxHitpoints;

    public Target() {}
    public Target(int maxHitpoints) {
        this.maxHitpoints = maxHitpoints;
        this.hitpoints = maxHitpoints;
    }

    public int getMaxHitpoints() {
        return maxHitpoints;
    }

    public void setMaxHitpoints(int maxHitpoints) {
        this.maxHitpoints = maxHitpoints;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void takeDamage(int damage) {
        this.hitpoints -= damage;
    }

    public void healDamage(int damage) {
        this.hitpoints += damage;
    }
    public void heal() {
        this.hitpoints = this.maxHitpoints;
    }

    public boolean isDead() {
        return this.hitpoints < 1;
    }
}
