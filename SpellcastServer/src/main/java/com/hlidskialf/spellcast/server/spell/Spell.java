package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;

/**
 * Created by wiggins on 1/11/15.
 */
public class Spell {
    private final String name;
    private final String gestures;
    private final String reverse;
    private final String slug;
    private final SpellType type;

    public enum SpellType {
        None,
        Protection,
        Summon,
        Damage,
        Enchantment,
        PhysicalAttack
    }

	public Spell(String name, String gestures, SpellType type) {
		this(name, name.replaceAll(" ", "").toLowerCase(), gestures, type);
	}
    public Spell(Spell copy) {
        this(copy.name, copy.slug, copy.gestures, copy.type);
    }
    public Spell(String name, String slug, String gestures, SpellType type) {
        this.name = name;
	    this.slug = slug;
        this.gestures = gestures;
        this.type = type;
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
