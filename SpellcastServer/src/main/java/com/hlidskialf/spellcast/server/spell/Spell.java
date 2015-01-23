package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/11/15.
 */
public class Spell {
    protected final String name;
    protected final String gestures;
    protected final String reverse;
    protected final String slug;
    protected final SpellType type;
    protected final boolean hasTarget;

    public enum SpellType {
        None,
        Protection,
        Summon,
        Damage,
        Enchantment,
        PhysicalAttack
    }

    public Spell(String name, String gestures, SpellType type) {
        this(name, name.replaceAll(" ", "").toLowerCase(), gestures, type, true);
    }
	public Spell(String name, String gestures, SpellType type, boolean hasTarget) {
		this(name, name.replaceAll(" ", "").toLowerCase(), gestures, type, hasTarget);
	}
    public Spell(String name, String slug, String gestures, SpellType type) {
        this(name, slug, gestures, type, true);
    }
    public Spell(Spell copy) {
        this(copy.name, copy.slug, copy.gestures, copy.type, copy.hasTarget);
    }
    public Spell(String name, String slug, String gestures, SpellType type, boolean hasTarget) {
        this.name = name;
	    this.slug = slug;
        this.gestures = gestures;
        this.type = type;
        this.hasTarget = hasTarget;
        this.reverse = new StringBuilder(gestures).reverse().toString();
    }

    public String getName() {
        return name;
    }

    public String getGestures() {
        return gestures;
    }

    public String getReverseGestures() {
        return reverse;
    }

    public String getSlug() { return slug; }

    public SpellType getType() {
        return type;
    }

    public boolean hasTarget() {
        return this.hasTarget;
    }

    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) { }

    protected void broadcastFizzle(SpellcastMatchState matchState, Target caster, String slug, Target target, String message) {
        broadcast("356",matchState,caster,slug,target,message);
    }
    protected void broadcastFizzle(SpellcastMatchState matchState, Target caster, Target target, String message) {
        broadcastFizzle(matchState,caster,slug,target,message);
    }
    protected void broadcastSuccess(SpellcastMatchState matchState, Target caster, Target target, String message) {
        broadcastSuccess(matchState, caster,slug,target,message);
    }
    protected void broadcastSuccess(SpellcastMatchState matchState, Target caster, String slug, Target target, String message) {
        broadcast("357", matchState, caster,slug,target,message);
    }
    protected void broadcast(String cmd, SpellcastMatchState matchState, Target caster, Target target, String message) {
        broadcast(cmd, matchState, caster, this.slug, target, message);
    }
    protected void broadcast(String cmd, SpellcastMatchState matchState, Target caster, String slug, Target target, String message) {
        StringBuilder sb = new StringBuilder(cmd);
        sb.append(" ");
        sb.append(caster.getNickname());
        sb.append(" ");
        sb.append(slug);
        sb.append(" ");
        sb.append(target.getNickname());
        sb.append(" :");
        sb.append(message);
        matchState.broadcast(sb.toString());
    }

    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

    }
}
