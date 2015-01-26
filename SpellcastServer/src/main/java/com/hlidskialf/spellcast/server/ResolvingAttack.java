package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.effect.ShieldEffect;

/**
 * Created by wiggins on 1/20/15.
 */
public class ResolvingAttack {


    private String monsterRef;
    private Target attacker;
    private Target target;
    private int damage;
    private SpellcastClient caster;

    public ResolvingAttack(Target target, int damage) {
        this.target = target;
        this.damage = damage;
    }
    public ResolvingAttack(Target attacker, Target target, int damage) {
        this(target,damage);
        this.attacker = attacker;
    }
    public ResolvingAttack(SpellcastClient caster, String monsterRef, Target target, int damage) {
        this(target, damage);
        this.caster = caster;
        this.monsterRef = monsterRef;
    }

    private void resolveMonsterRef() {
        if (attacker == null && monsterRef != null) {
            for (Monster m : caster.getMonsters()) {
                if (m.getRef().equals(monsterRef)) {
                    attacker = m;
                    damage = m.getDamage();
                    return;
                }
            }

        }
    }

    public boolean resolveAttack(SpellcastMatchState matchState) {
        resolveMonsterRef();

        if (target.hasEffect(ShieldEffect.Name)) {
            return false;
        }
        target.takeDamage(damage);
        return true;
    }

    public String get352() {
        return get352(attacker, target);
    }
    public String get353(String message) {
        return get353(attacker, target, message);
    }

    public static String get352(Target attacker, Target target) {
        StringBuilder sb = new StringBuilder("352 ");
        sb.append(attacker.getNickname());
        sb.append(" ATTACKS ");
        sb.append(target.getNickname());
        return sb.toString();
    }
    public static String get353(Target attacker, Target target, String message) {
        StringBuilder sb = new StringBuilder("353 ");
        sb.append(target.getNickname());
        sb.append(" BLOCKS ");
        sb.append(attacker.getNickname());
        if (message != null) {
            sb.append(" :");
            sb.append(message);
        }
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

    public String getMonsterRef() {
        return monsterRef;
    }

    public void setMonsterRef(String monsterRef) {
        this.monsterRef = monsterRef;
    }
}
