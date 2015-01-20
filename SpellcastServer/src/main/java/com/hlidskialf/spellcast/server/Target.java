package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.effect.SpellEffect;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wiggins on 1/19/15.
 */
public abstract class Target {
    protected String nickname;
    protected String visibleName;
    protected int hitpoints;
    protected int maxHitpoints;
    protected ArrayList<SpellEffect> effects;

    public Target() {
        effects = new ArrayList<SpellEffect>();
    }
    public Target(String nickname, int maxHitpoints) {
        this();
        this.nickname = nickname;
        this.maxHitpoints = maxHitpoints;
        this.hitpoints = maxHitpoints;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }
    public String getVisibleName() {
        return visibleName == null ? nickname : visibleName;
    }
    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
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
        this.hitpoints = Math.max(hitpoints-damage, 0);
    }
    public void healDamage(int damage) {
        this.hitpoints = Math.min(hitpoints + damage, maxHitpoints);
    }
    public void heal() {
        this.hitpoints = this.maxHitpoints;
    }
    public boolean isDead() {
        return this.hitpoints < 1;
    }

    public boolean hasEffect(String effectName) {
        return getEffect(effectName) != null;
    }
    public SpellEffect getEffect(String effectName) {
        for (SpellEffect se : effects) {
            if (se.getName().equals(effectName)) {
                return se;
            }
        }
        return null;
    }
    public boolean addEffect(SpellEffect effect) {
        if (!hasEffect(effect.getName())) {
            effects.add(effect);
            return true;
        }
        return false;
    }
    public ArrayList<String> expireEffects(String matchId, int roundNumber) {
        ArrayList<String> expired = new ArrayList<String>();

        Iterator<SpellEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            SpellEffect se = iterator.next();
            if (!matchId.equals(se.getMatchId()) || roundNumber >= se.getRoundCast()+se.getDuration()) {
                expired.add(se.expire());
                iterator.remove();
            }
        }
        return expired;
    }

}
