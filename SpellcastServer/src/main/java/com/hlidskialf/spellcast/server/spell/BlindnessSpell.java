package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Monster;
import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.SpellcastMatchState;
import com.hlidskialf.spellcast.server.Target;
import com.hlidskialf.spellcast.server.effect.BlindnessEffect;
import com.hlidskialf.spellcast.server.effect.DeathEffect;

/**
 * Created by wiggins on 1/21/15.
 *
 * Waving Hands Rules-
 * For the next 3 turns not including the one in which the spell was cast, the subject is unable to see.
 * If he is a wizard, he cannot tell what his opponent's gestures are, although he must be informed of any which
 * affect him (e.g. summons spells, 'missile' etc cast at him) but not 'counter- spells' to his own attacks.
 * Indeed he will not know if his own spells work unless they also affect him (e.g. a 'fire storm' when he isn't
 * resistant to fire.) He can control his monsters (e.g. "Attack whatever it was that just attacked me"). Blinded
 * monsters are instantly destroyed and cannot attack in that turn.

 */
public class BlindnessSpell extends Spell {

   private static final String Slug = "blindness";

   public BlindnessSpell(String name, String gestures) {
      super(name, Slug, gestures, SpellType.Enchantment);
   }

   @Override
   public void effects(SpellcastMatchState matchState, SpellcastClient caster, Target target) {
      if (target instanceof Monster) {
         target.addEffect(new DeathEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target));
      } else {
         target.addEffect(new BlindnessEffect(matchState.getCurrentMatchId(), matchState.getCurrentRoundNumber(), target, 4));
      }
   }
}
