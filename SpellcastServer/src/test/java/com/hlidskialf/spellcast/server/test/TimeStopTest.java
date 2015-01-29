package com.hlidskialf.spellcast.server.test;

import com.hlidskialf.spellcast.server.spell.TimeStopSpell;
import com.hlidskialf.spellcast.server.test.helpers.SpellcastTest;
import org.junit.Test;

/**
 * Created by wiggins on 1/27/15.
 */
public class TimeStopTest extends SpellcastTest {

    @Test
    public void shouldTimeStopMonster() {
        authenticateAndStart();

        //second: summon goblin
        //first: time stop goblin
        sendGestures("SP", "__", "SF", "__");
        sendFirst("ANSWER left first");
        sendGestures("P", "_", "W", "_");
        sendFirst("ANSWER left first");

        sendSecond("ANSWER left first");
        sendSecond("ANSWER left$summongoblin.target first");
        String mob = second.getMonsters().iterator().next().getNickname();

        sendGestures("c","c","_","_");
        sendFirst("ANSWER both " + mob);
        sendSecond("ANSWER "+mob+" first");

//        assertBroadcasted("351 first CASTS "+ TimeStopSpell.Slug+" AT "+mob+" WITH both");



    }
}
