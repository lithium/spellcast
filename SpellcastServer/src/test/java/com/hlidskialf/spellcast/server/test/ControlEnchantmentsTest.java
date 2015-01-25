package com.hlidskialf.spellcast.server.test;

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
        assertBroadcastedStartingWith("331 "+server.getCurrentMatchId()+".4 second ");
    }
}
