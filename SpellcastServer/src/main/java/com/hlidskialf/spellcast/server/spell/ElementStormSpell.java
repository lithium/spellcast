package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.*;
import com.hlidskialf.spellcast.server.effect.DeathEffect;
import com.hlidskialf.spellcast.server.effect.ResistElementEffect;

/**
 * Created by wiggins on 1/20/15.
 *
 * Waving Hands Rules-
 *
 * Fire Storm-
 * Everything not resistant to heat sustains 5 points of damage that turn.
 * The spell cancels wholly, causing no damage, with either an 'ice storm' or an ice elemental.
 * It will destroy but not be destroyed by a fire elemental.
 * Two 'fire storms' act as one.
 *
 * Ice Storm-
 * Everything not resistant to cold sustains 5 points of damage that turn.
 * The spell cancels wholly, causing no damage, with either a 'fire storm' or a fire elemental,
 * and will cancel locally with a 'fireball'.
 * It will destroy but not be destroyed by an ice elemental.
 * Two 'ice storms' act as one.
 *
 */
public class ElementStormSpell extends Spell {

    public static final String IceStormSlug = "icestorm";
    public static final String FireStormSlug = "firestorm";

    private Element element;

    public ElementStormSpell(String name, String gestures, Element element) {
        super(name, element == Element.ice ? IceStormSlug : FireStormSlug, gestures, SpellType.Damage, false);
        this.element = element;
    }

    private String opposingStorm() {
        switch (element) {
            case fire: return IceStormSlug;
            case ice: return FireStormSlug;
        }
        return null;
    }

    @Override
    public void fireSpell(SpellcastMatchState matchState, SpellcastClient caster, Target target) {

        Elemental elemental = matchState.getElemental();
        if (elemental != null) {
            // cancel spell if there is an elemental of opposing element
            if (elemental.getElement().equals(Element.opposing(element))) {
                broadcastFizzle(matchState, caster, target, getName()+" is cancelled by the ice elemental");
                return;
            }

            if (elemental.getElement().equals(element)) {
                // if there is a elemental of same element destroy it
                elemental.addEffect(new DeathEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), elemental));
            }
        }

        //cancelled by storm of opposing element
        if (ResolvingSpell.isSpellResolving(matchState.getResolvingSpells(), opposingStorm())) {
            broadcastFizzle(matchState, caster, target, getName()+" is cancelled by the "+opposingStorm());
            return;
        }

        String resistName = "resist"+element;

        for (Target t : matchState.getAllTargets()) {
            if (!t.hasEffect(resistName) && !(t instanceof Elemental) && !targetGetsFireballProtection(matchState, t)) {
                t.takeDamage(5);
            }
        }

    }

    private boolean targetGetsFireballProtection(SpellcastMatchState matchState, Target t) {
        if (element != Element.ice) {
            return false;
        }
        ResolvingSpell rs = ResolvingSpell.resolvingSpell(matchState.getResolvingSpells(), FireballSpell.Slug);
        if (rs != null && rs.getTarget().getNickname().equals(t.getNickname())) {
            return true;
        }
        return false;
    }
}
