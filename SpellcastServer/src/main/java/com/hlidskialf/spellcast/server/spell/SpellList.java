package com.hlidskialf.spellcast.server.spell;

import com.hlidskialf.spellcast.server.Element;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellList {

    /*
     * spells with no targets:
     *  - dispel magic
     *  - fire storm
     *  - ice storm
     *
     * spells with specialized targets:
     *  - charm person
     *  - charm monster
     *  - raise dead
     */

    // protection spells
    public static final Spell Shield = new ShieldSpell("Shield", "P");
    public static final Spell RemoveEnchantment = new RemoveEnchantmentSpell("Remove Enchantment", "PDWP");
    public static final Spell MagicMirror = new MagicMirrorSpell("Magic Mirror", "cw");
    public static final Spell Counterspell = new CounterspellSpell("Counterspell", "WPP");
    public static final Spell Counterspell2 = new CounterspellSpell("Counterspell", "WWS");
    public static final Spell DispelMagic = new DispelMagicSpell("Dispel Magic", "CDPW");
    public static final Spell RaiseDead = new Spell("Raise Dead", "DWWFWC", Spell.SpellType.Protection);
    public static final Spell CureLightWounds = new CureWoundsSpell("Cure Light Wounds", "DFW", 1);
    public static final Spell CureHeavyWounds = new CureWoundsSpell("Cure Heavy Wounds", "DFPW", 2);

    // summon spells
    public static final Spell Summon1 = new SummonMonsterSpell("Summon Goblin",   "SFW", 1);
    public static final Spell Summon2 = new SummonMonsterSpell("Summon Ogre",    "PSFW", 2);
    public static final Spell Summon3 = new SummonMonsterSpell("Summon Troll",  "FPSFW", 3);
    public static final Spell Summon4 = new SummonMonsterSpell("Summon Giant", "WFPSFW", 4);
    public static final Spell SummonFireElemental = new SummonElementalSpell("Summon Fire Elemental", "cSWWS", Element.fire);
    public static final Spell SummonIceElemental = new SummonElementalSpell("Summon Ice Elemental", "cSWWS", Element.ice);

    // damage spells
    public static final Spell Missile = new MissileSpell("Missile", "SD");
    public static final Spell FingerOfDeath = new FingerOfDeathSpell("Finger of Death", "PWPFSSSD");
    public static final Spell LightningBolt = new LightningBoltSpell("Lightning Bolt", "DFFDD");
    public static final Spell LightningBolt2 = new LightningBoltSpell("Lightning Bolt", "WDDc");
    public static final Spell CauseLightWounds = new CauseWoundsSpell("Cause Light Wounds", "WFP", 2);
    public static final Spell CauseHeavyWounds = new CauseWoundsSpell("Cause Heavy Wounds", "WPFD", 3);
    public static final Spell Fireball = new FireballSpell("Fireball", "FSSDD");
    public static final Spell FireStorm = new ElementStormSpell("Fire Storm", "SWWc", Element.fire);
    public static final Spell IceStorm = new ElementStormSpell("Ice Storm", "WSSc", Element.ice);

    // enchantments
    // control enchantments (cancel each other)
    public static final Spell Amnesia = new AmnesiaSpell("Amnesia", "DPP");
    public static final Spell Confusion = new ConfusionSpell("Confusion", "DSF");
    public static final Spell CharmPerson = new CharmPersonSpell("Charm Person", "PSDF");
    public static final Spell CharmMonster = new CharmMonsterSpell("Charm Monster", "PSDD");
    public static final Spell Paralysis = new ParalysisSpell("Paralysis", "FFF");
    public static final Spell Fear = new FearSpell("Fear", "SWD");
    // regular enchantments
    public static final Spell Antispell = new AntispellSpell("Antispell", "SPF");
    public static final Spell ProtectionFromEvil = new ProtectionFromEvilSpell("Protection From Evil", "WWP");
    public static final Spell ResistHeat = new ResistElementSpell("Resist Heat", "WWFP", Element.fire);
    public static final Spell ResistCold = new ResistElementSpell("Resist Cold", "SSFP", Element.ice);
    public static final Spell Disease = new DiseaseSpell("Disease", "DSFFFc");
    public static final Spell Poison = new PoisonSpell("Poison", "DWWFWD");
    public static final Spell Blindness = new BlindnessSpell("Blindness", "DWFFd");
    public static final Spell Invisibility = new InvisibilitySpell("Invisibility", "PPws");
    public static final Spell Haste = new HasteSpell("Haste", "PWPWWc");
    public static final Spell TimeStop = new TimeStopSpell("Time Stop", "SPPC");
    public static final Spell DelayedEffect = new Spell("Delayed Effect", "DWSSSP", Spell.SpellType.Enchantment);
    public static final Spell Permanency = new Spell("Permanancy", "SPFPSDW", Spell.SpellType.Enchantment);

    // non spells
    public static final Spell Surrender = new Spell("Surrender", "p", Spell.SpellType.None);
    public static final Spell Stab = new Spell("Stab", "K", Spell.SpellType.PhysicalAttack);


    public static final Spell[] AllSpells = {
            Stab,
            Surrender,
            Permanency,
            DelayedEffect,
            TimeStop,
            Haste,
            Invisibility,
            Blindness,
            Poison,
            Disease,
            ResistCold,
            ResistHeat,
            ProtectionFromEvil,
            Antispell,
            Fear,
            Paralysis,
            CharmMonster,
            CharmPerson,
            Confusion,
            Amnesia,
            IceStorm,
            FireStorm,
            Fireball,
            CauseHeavyWounds,
            CauseLightWounds,
            LightningBolt,
            LightningBolt2,
            FingerOfDeath,
            Missile,
            SummonFireElemental,
            SummonIceElemental,
            Summon4,
            Summon3,
            Summon2,
            Summon1,
            CureHeavyWounds,
            CureLightWounds,
            RaiseDead,
            DispelMagic,
            Counterspell,
            Counterspell2,
            MagicMirror,
            RemoveEnchantment,
            Shield
    };

    public static Spell lookupSpellBySlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            return null;
        }
        for (Spell s : SpellList.AllSpells) {
            if (s.getSlug().equals(slug)) {
                return s;
            }
        }
        return null;
    }
}
