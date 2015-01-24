package com.hlidskialf.spellcast.server.test;

import com.hlidskialf.spellcast.server.test.helpers.SpellcastTest;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

/**
 * Created by wiggins on 1/23/15.
 */
public class ElementalsTest extends SpellcastTest {


    @Test
    public void shouldSummonElemental() {
        authenticateAndStart();
        sendGestures("cSWWS","c____",  "_cSWW","_c___");


        //asked which elemental
        assertContainsStartingWith("342 left summonfireelemental ", firstChannel);
        assertContainsStartingWith("342 left summoniceelemental ", firstChannel);

        //summon a fire elemental
        sendFirst("ANSWER left summonfireelemental");
        sendFirst("ANSWER left first");

        //all players should have been attacked
        assertTookDamage(first, 3);
        assertTookDamage(second, 3);

        assertNotNull(first.getElemental());
        String nick = first.getElemental().getNickname();

        //summon an ice elemental
        sendGestures("_","_",  "S","_");
        sendSecond("ANSWER left summoniceelemental");
        sendSecond("ANSWER left second");

        //ice elemental should have cancelled fire and itself
        assertBroadcastedStartingWith("356 second summoniceelemental "+nick+" :Elementals of opposing types destroy each other");

        //should have taken no more damage
        assertTookDamage(first, 3);
        assertTookDamage(second, 3);
    }


    @Test
    public void shouldCancelFireElementals() {
        cancelElementals("WWFP", "resistheat", "summonfireelemental");
    }

    @Test
    public void shouldCancelIceElementals() {
        cancelElementals("SSFP","resistcold","summoniceelemental");
    }
    private void cancelElementals(String resistSpell, String resist, String summon) {
        authenticateAndStart();
        sendGestures(resistSpell, "____",    //first: resist heat
            "cSWW","c___");         //second: summon fire elemental...

        sendFirst("ANSWER left "+resist);
        sendFirst("ANSWER left first");

        sendGestures("c","c", "S","P");

        sendSecond("ANSWER left "+summon);
        sendSecond("ANSWER left second");
        sendSecond("ANSWER right second"); //second: shield on self

        assertBroadcasted("351 first CASTS "+resist+" AT first WITH left");
        assertBroadcasted("351 second CASTS "+summon+" AT second WITH left");
        assertBroadcasted("351 second CASTS shield AT second WITH right");

        assertNotNull(second.getElemental());
        String nick = second.getElemental().getNickname();

        assertBroadcastedStartingWith("353 second BLOCKS "+nick+" ");
        assertBroadcastedStartingWith("353 first BLOCKS "+nick+" ");

        sendGestures("SWWS", "____", "____", "____"); // first: summon fire elemental

        sendFirst("ANSWER left "+summon);
        sendFirst("ANSWER left first");

        assertBroadcasted("351 first CASTS "+summon+" AT first WITH left");
        assertBroadcasted("356 first "+summon+" second :Elementals of the same type fuse");

        //new elemental is created
        assertNull(second.getElemental());
        assertNotNull(first.getElemental());
        String nextNick = first.getElemental().getNickname();
        assertFalse(nick.equals(nextNick));
        assertBroadcasted("352 "+nextNick+" ATTACKS second");

        assertTookNoDamage(first);
        assertEquals(3, second.getHitpoints());
    }

    @Test
    public void shouldStillTakeDamageWhenKillingFireElementalWithFireball() {
        authenticateAndStart();
        sendGestures("cSWWS","c____",    //first: summon fire elemental
            "_FSSD","_____");

        sendFirst("ANSWER left summonfireelemental");
        sendFirst("ANSWER left first");
        sendSecond("ANSWER left first"); //second: missile

        assertNotNull(first.getElemental());


        sendGestures("_","_", "D","_");  //second: fireball
        sendSecond("ANSWER left "+first.getElemental().getNickname());

        assertTookDamage(second, 6);
        assertTookDamage(first, 7);
    }

    @Test
    public void shouldKillIceElementalWithFireballBeforeAttacking() {
        authenticateAndStart();
        sendGestures("cSWWS","c____",    //first: summon ice elemental
            "_FSSD","_____");

        sendFirst("ANSWER left summoniceelemental");
        sendFirst("ANSWER left first");
        sendSecond("ANSWER left first"); //second: missile

        assertNotNull(first.getElemental());

        sendGestures("_","_", "D","_");  //second: fireball
        sendSecond("ANSWER left "+first.getElemental().getNickname());

        assertTookDamage(second, 3);
        assertTookDamage(first, 4);
    }

    @Test
    public void shouldDestoryElementalWithSameStormBeforeAttacking() {
        authenticateAndStart();

        sendGestures("cSWWS","c____",    //first: summon ice elemental
            "_WSSc","____c");   //second: ice storm

        sendFirst("ANSWER left summoniceelemental");
        sendFirst("ANSWER left first");


        //everyone should have taken 5 damage from ice storm -- none from elemental
        assertTookDamage(first, 5);
        assertTookDamage(second, 5);
    }

    @Test
    public void shouldDestoryElementalWithOpposingStormAfterAttacking() {
        authenticateAndStart();

        sendGestures("cSWWS","c____",    //first: summon fire elemental
            "_WSSc","____c");   //second: ice storm

        sendFirst("ANSWER left summonfireelemental");
        sendFirst("ANSWER left first");

        //everyone should have taken 3 from elemental none from ice storm
        assertTookDamage(first, 3);
        assertTookDamage(second, 3);
    }


    @Test
    public void shouldDestroyFireElementalWithResistHeat() {
        destroyElementalWithResist("summonfireelemental","resistheat","__WWF");
        assertTookDamage(first, 3);
        assertTookDamage(second, 3);
    }
    @Test
    public void shouldDestroyIceElementalWithResistCold() {
        destroyElementalWithResist("summoniceelemental","resistcold","__SSF");
        assertTookDamage(first, 3);
        assertTookDamage(second, 3);
    }
    @Test
    public void shouldNotDestroyFireElementtalWithResistCold() {
        destroyElementalWithResist("summonfireelemental", "resistcold", "__SSF");
        assertTookDamage(first, 6);
        assertTookDamage(second, 6);
    }
    private void destroyElementalWithResist(String summon, String resist, String resistSpell)  {
        authenticateAndStart();
        sendGestures("cSWWS","c____",       //first: summon fire elemental
            resistSpell,"_____");

        sendFirst("ANSWER left "+summon);
        sendFirst("ANSWER left first");

        assertNotNull(first.getElemental());

        //second: resistheat on elemental
        sendGestures("_", "_", "P", "_");
        sendSecond("ANSWER left "+resist);
        sendSecond("ANSWER left "+first.getElemental().getNickname());

    }
}
