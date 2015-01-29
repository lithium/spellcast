package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.DelayedEffect;

/**
 * Created by wiggins on 1/28/15.
 *
 * Waving Hands Rules-
 *
 * This spell only works if cast upon a wizard.
 * The next spell he completes, provided it is on this turn or one of the next 3 is "banked" until needed,
 * i.e. it fails to work until its caster desires.  This next spell which is to be banked does not include the actual
 * spell doing the banking. The spell must be written down to be used by its caster at the same time that he writes his
 * gestures.
 * Note that spells banked are those cast by the subject not those cast at him.
 * If he casts more than one spell at the same time he chooses which is to be banked.
 * Remember that P is a shield spell, and surrender is not a spell.
 * A wizard may only have one spell banked at any one time.

 */
public class DelayedEffectSpell extends Spell {
    private static final String Slug = "delayedeffect";

    public DelayedEffectSpell(String name, String gestures) {
        super(name, Slug, gestures, SpellType.Enchantment);
    }

    @Override
    public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
        if (target instanceof SpellcastClient) {
            target.addEffect(new DelayedEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target));
        }

    }
}
