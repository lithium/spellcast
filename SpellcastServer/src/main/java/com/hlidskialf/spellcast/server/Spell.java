package com.hlidskialf.spellcast.server;

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
        this.name = name;
        this.gestures = gestures;
        this.type = type;
        this.reverse = new StringBuilder(gestures).reverse().toString();
        this.slug = this.name.replaceAll(" ", "").toLowerCase();
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

    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, SpellcastClient target) {
    }
}
