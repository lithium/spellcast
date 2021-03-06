package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.effect.SpellEffect;
import com.hlidskialf.spellcast.server.spell.Spell;

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
    protected boolean dead;

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
        return dead || this.hitpoints < 1;
    }
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean hasEffect(String effectName) {
        return effectName != null && getEffect(effectName) != null;
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
    public void removeEffect(String effectName) {
        for (SpellEffect se : effects) {
            if (se.getName().equals(effectName)) {
                effects.remove(se);
                return;
            }
        }
    }
    public ArrayList<String> expireEffects(String matchId, int roundNumber) {
        ArrayList<String> expired = new ArrayList<String>();
        ArrayList<SpellEffect> expiring = new ArrayList<SpellEffect>();

        Iterator<SpellEffect> iterator = effects.iterator();
        while (iterator.hasNext()) {
            SpellEffect se = iterator.next();
            if (se.getDuration() > 0 && roundNumber >= se.getRoundCast()+se.getDuration()) {
                iterator.remove();
                expiring.add(se);
            }
        }
        for (SpellEffect se : expiring) {
            expired.add(se.expire());
        }
        return expired;
    }

	public void removeEnchantments() {
		Iterator<SpellEffect> it = effects.iterator();
		while (it.hasNext()) {
			SpellEffect effect = it.next();
			if (Spell.SpellType.Enchantment.equals(effect.getSpellType())) {
				it.remove();
			}
		}
	}
}
