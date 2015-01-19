package com.hlidskialf.spellcast.server;

import com.hlidskialf.spellcast.server.spell.ShieldSpell;

/**
 * Created by wiggins on 1/11/15.
 */
public class SpellList {

    // protection spells
    public static final Spell Shield = new ShieldSpell("Shield", "P");
    public static final Spell RemoveEnchantment = new Spell("Remove Enchantment", "PDWP");
    public static final Spell MagicMirror = new Spell("Magic Mirror", "cw");
    public static final Spell Counterspell = new Spell("Counterspell", "WPP");
    public static final Spell Counterspell2 = new Spell("Counterspell", "WWS");
    public static final Spell DispelMagic = new Spell("Dispel Magic", "CDPW");
    public static final Spell RaiseDead = new Spell("Raise Dead", "DWWFWC");
    public static final Spell CureLightWounds = new Spell("Cure Light Wounds", "DFW");
    public static final Spell CureHeavyWounds = new Spell("Cure Heavy Wounds", "DFPW");

    // summon spells
    public static final Spell Summon1 = new Spell("Summon Goblin",   "SFW");
    public static final Spell Summon2 = new Spell("Summon Ogre",    "PSFW");
    public static final Spell Summon3 = new Spell("Summon Troll",  "FPSFW");
    public static final Spell Summon4 = new Spell("Summon Giant", "WFPSFW");
    public static final Spell SummonFireElemental = new Spell("Summon Fire Elemental", "cSWWS");
    public static final Spell SummonIceElemental = new Spell("Summon Ice Elemental", "cSWWS");

    // damage spells
    public static final Spell Missile = new Spell("Missile", "SD");
    public static final Spell FingerOfDeath = new Spell("Finger of Death", "PWPFSSSD");
    public static final Spell LightningBolt = new Spell("Lightning Bolt", "DFFDD");
    public static final Spell LightningBolt2 = new Spell("Lightning Bolt", "WDDc");
    public static final Spell CauseLightWounds = new Spell("Cause Light Wounds", "WFP");
    public static final Spell CauseHeavyWounds = new Spell("Cause Heavy Wounds", "WPFD");
    public static final Spell Fireball = new Spell("Fireball", "FSSDD");
    public static final Spell FireStorm = new Spell("Fire Storm", "SWWc");
    public static final Spell IceStorm = new Spell("Ice Storm", "WSSc");

    // enchantments
    public static final Spell Amnesia = new Spell("Amnesia", "DPP");
    public static final Spell Confusion = new Spell("Confusion", "DSF");
    public static final Spell CharmPerson = new Spell("Charm Person", "PSDF");
    public static final Spell CharmMonster = new Spell("Charm Monster", "PSDD");
    public static final Spell Paralysis = new Spell("Paralysis", "FFF");
    public static final Spell Fear = new Spell("Fear", "SWD");
    public static final Spell AntiSpell = new Spell("Anti-spell", "SPF");
    public static final Spell ProtectionFromEvil = new Spell("Protection From Evil", "WWP");
    public static final Spell ResistHeat = new Spell("Resist Heat", "WWFP");
    public static final Spell ResistCold = new Spell("Resist Cold", "SSFP");
    public static final Spell Disease = new Spell("Disease", "DSFFFc");
    public static final Spell Poison = new Spell("Poison", "DWWFWD");
    public static final Spell Blindness = new Spell("Blindness", "DWFFd");
    public static final Spell Invisibility = new Spell("Invisibility", "PPws");
    public static final Spell Haste = new Spell("Haste", "PWPWWc");
    public static final Spell TimeStop = new Spell("Time Stop", "SPPC");
    public static final Spell DelayedEffect = new Spell("Delayed Effect", "DWSSSP");
    public static final Spell Permanency = new Spell("Permanancy", "SPFPSDW");

    // non spells
    public static final Spell Surrender = new Spell("Surrender", "p");
    public static final Spell Stab = new Spell("Stab", "K");


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
            AntiSpell,
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
