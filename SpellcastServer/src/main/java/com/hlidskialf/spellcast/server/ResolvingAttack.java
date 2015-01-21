package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/20/15.
 */
public class ResolvingAttack {


    private Target attacker;
    private Target target;
    private int damage;

    public ResolvingAttack(Target attacker, Target target, int damage) {
        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
    }


    public boolean resolveAttack(SpellcastMatchState matchState) {
        if (target.hasEffect(ShieldEffect.Name)) {
            return false;
        }
        target.takeDamage(damage);
        return true;
    }

    public String get352() {
        StringBuilder sb = new StringBuilder("352 ");
        sb.append(attacker.getNickname());
        sb.append(" ATTACKS ");
        sb.append(target.getNickname());
        return sb.toString();
    }
    public String get353() {
        StringBuilder sb = new StringBuilder("353 ");
        sb.append(target.getNickname());
        sb.append(" BLOCKS ");
        sb.append(attacker.getNickname());
        sb.append(" :the attack is blocked by a shield");
        return sb.toString();
    }

    /*
     * Getters/Setters
     */

    public Target getAttacker() {
        return attacker;
    }

    public void setAttacker(Target attacker) {
        this.attacker = attacker;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
