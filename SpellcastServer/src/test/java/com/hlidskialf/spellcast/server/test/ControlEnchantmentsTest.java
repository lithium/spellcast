package com.hlidskialf.spellcast.server.test;

import com.hlidskialf.spellcast.server.Monster;
import com.hlidskialf.spellcast.server.test.helpers.SpellcastTest;
import org.junit.Test;

/**
 * Created by wiggins on 1/24/15.
 */
public class ControlEnchantmentsTest extends SpellcastTest {

    @Test
    public void shouldCastAmnesia() {
        authenticateAndStart();

        //first: amnesia
        sendGestures("DP","__","__","__");

        sendFirst("ANSWER left first");  //first: shield on self

        sendGestures("P","_","S","W");  //first: amnesia on second
        sendFirst("ANSWER left amnesia");
        sendFirst("ANSWER left second");

        assertBroadcasted("351 first CASTS amnesia AT second WITH left");

        sendGestures("_","_","F","S");

        assertNotBroadcastedStartingWith("331 " + server.getCurrentMatchId() + ".4 second F S");
        assertBroadcasted("331 "+server.getCurrentMatchId()+".4 second S W");
    }

    @Test
    public void shouldCastConfusion() {
        authenticateAndStart();

        //first: confusion at second
        sendGestures("DSF","___",  "___","___");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS confusion AT second WITH left");

        //next round of gestures
        sendGestures("_","_","_","_");

        //second did something random, but not nothing
        assertNotBroadcastedStartingWith("331 " + server.getCurrentMatchId() + ".4 second _ _");
        assertBroadcastedStartingWith("331 " + server.getCurrentMatchId() + ".4 second ");
    }

    @Test
    public void shouldCastConfusionOnMonster() {
        authenticateAndStart();

        //second: summon goblin
        sendGestures("_DS","___", "SFW","___");
        sendSecond("ANSWER left second");
        sendSecond("ANSWER left$summongoblin.target first");
        assertBroadcasted("351 second CASTS summongoblin AT second WITH left");
        final Monster mob = second.getMonsters().iterator().next();

        //first: confusion on monster
        sendGestures("F", "_", "_", "_");
        sendFirst("ANSWER left " + mob.getNickname());
        sendSecond("ANSWER "+mob.getNickname()+" first");
        assertBroadcasted("351 first CASTS confusion AT " + mob.getNickname() + " WITH left");

        //next round
        sendGestures("_", "_", "_", "_");

        //there should be no questions asked on this round and monster attacks someone randomly
        assertNotBroadcastedStartingWith("340 " + server.getCurrentMatchId() + ".5 :Questions");
        assertBroadcastedStartingWith("352 "+mob.getNickname()+" ATTACKS ");
    }

    @Test
    public void shouldCastFearOnWizard() {
        authenticateAndStart();

        //first: fear on second
        sendGestures("SWD","___", "___","___");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS fear AT second WITH left");

        sendGestures("__", "__", "CD", "FS");

        assertNotBroadcastedStartingWith("331 "+server.getCurrentMatchId()+".4 second C F");
        assertBroadcastedStartingWith("331 " + server.getCurrentMatchId() + ".4 second _ _");
        assertBroadcastedStartingWith("331 " + server.getCurrentMatchId() + ".5 second D S");
    }


    @Test
    public void shouldCastCharmMonster() {
        authenticateAndStart();

        sendGestures("S", "_", "P", "_");
        sendSecond("ANSWER left second");

        sendGestures("FW","___", "SD","_P"); // first: summon goblin
        sendFirst("ANSWER left first");
        sendFirst("ANSWER left$summongoblin.target second");  //goblin: attack second
        sendSecond("ANSWER left first"); //second: missile at first
        sendSecond("ANSWER right second"); // second: shield at self

        Monster mob = first.getMonsters().iterator().next();

        sendGestures("_","_","D","_");  // second: charm monster
        sendFirst("ANSWER " + mob.getNickname() + " second"); //first: have goblin attack second

        sendSecond("ANSWER left " + mob.getNickname());               //second: charm monster and have it attack first
        sendSecond("ANSWER left$charmmonster.target first");

        assertBroadcasted("351 second CASTS charmmonster AT "+mob.getNickname()+" WITH left");
        assertBroadcasted("352 "+mob.getNickname()+" ATTACKS first");
        assertNotBroadcastedStartingWith("352 "+mob.getNickname()+" ATTACKS second");

        assertTookDamage(first, 2);  // 1 from missile, 1 from goblin
        assertTookNoDamage(second);


    }

    @Test
    public void shouldCastCharmPerson() {
        authenticateAndStart();

        sendGestures("P","_","_","_");
        sendFirst("ANSWER left first");  // first: shield on self

        sendGestures("SD","__","__","__");
        sendFirst("ANSWER left second");  // first: missile on second

        sendGestures("F","_","_","_");
        sendFirst("ANSWER left second");  // first: charm person on second

        assertBroadcasted("351 first CASTS charmperson AT second WITH left");
    }

    @Test
    public void shouldCastParalysis() {
        authenticateAndStart();

        sendGestures("FFF","___", "SSS","___");

        sendFirst("ANSWER left second");                //first: paralysis on second
        sendFirst("ANSWER left$paralysis.hand left");   //          on second's left hand

        assertBroadcasted("351 first CASTS paralysis AT second WITH left");

    }

}
