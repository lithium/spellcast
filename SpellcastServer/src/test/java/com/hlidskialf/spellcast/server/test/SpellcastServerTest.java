package com.hlidskialf.spellcast.server.test;


import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.test.helpers.SpellcastTest;
import com.hlidskialf.spellcast.server.test.helpers.TestSpellcastChannel;
import com.hlidskialf.spellcast.server.test.helpers.TestSpellcastServer;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by wiggins on 1/22/15.
 */
public class SpellcastServerTest extends SpellcastTest {


    @Test
    public void shouldInitializeTwoClients() {
        assertEquals(server.getAllClients().size(), 2);
    }


    @Test
    public void shouldIdentifyClient() {

        server.processChannelMessage(firstChannel, "NAME first");

        SpellcastClient client = server.getClientByNickname("first");
        assertNotNull(client);
        assertEquals(client.getNickname(), "first");
        assertEquals(client.getState(), SpellcastClient.ClientState.Watching);

    }

    @Test
    public void shouldSendQuitMessage() {
        authenticate();
        server.closeClient(first);

        assertStartsWith("303 first ", secondChannel.lastMessage());
    }

    @Test
    public void shouldStartMatch() {
        authenticate();

        firstChannel.setIndex();
        sendFirst("READY");
        assertStartsWith("301 first +", firstChannel.nextMessage());

        secondChannel.setIndex();
        sendSecond("READY");
        assertStartsWith("301 second +", secondChannel.nextMessage());

        for (TestSpellcastChannel channel : channels) {
            //match started
            assertContainsStartingWith("250 " + server.getCurrentMatchId() + " ", channel);
            //round started
            assertContainsStartingWith("251 " + server.getCurrentMatchId() + ".1 ", channel);

            //saw wizard status
            assertChannelContains("301 first 15/15 none :first", channel);
            assertChannelContains("301 second 15/15 none :second", channel);

            //saw who was asked for gestures
            assertContainsStartingWith("320 " + server.getCurrentMatchId() + ".1 first", channel);
            assertContainsStartingWith("320 " + server.getCurrentMatchId() + ".1 second", channel);
        }
    }

    @Test
    public void shouldTakeGesturesFromAllPlayers() {
        authenticateAndStart();

        String roundId = getRoundId();
        sendFirst("GESTURE s W");

        assertStartsWith("321 " + getRoundId() + " first ", firstChannel.lastMessage());

        sendSecond("GESTURE C C");

        assertBroadcasted("331 "+roundId+ " first S W");
        assertBroadcasted("331 "+roundId+ " second c c");
        assertBroadcastedStartingWith("332 ");  //saw end of gestures


    }

    private String getRoundId() {
        return server.getCurrentMatchId()+"."+server.getCurrentRoundNumber();
    }

    @Test
    public void shouldTakeDamageFromStab() {
        authenticateAndStart();

        sendFirst("GESTURE K _");
        sendSecond("GESTURE _ _");
        sendFirst("ANSWER left second");    //first stabs second

        //first got asked which tartget to stab
        assertContainsStartingWith("345 left stab ", firstChannel);

        //notified of stabs
        assertBroadcasted("352 first ATTACKS second");

        //second took 1 damage
        assertEquals(second.getMaxHitpoints() - 1, second.getHitpoints());
        assertBroadcastedStartingWith("301 second 14/15 ");
    }

    @Test
    public void shouldCastShield() {
        authenticateAndStart();

        sendFirst("GESTURE P _");
        sendSecond("GESTURE K _");

        sendFirst("ANSWER left first");     //first casts shield on self
        sendSecond("ANSWER left first");    //second stabs first

        assertBroadcasted("351 first CASTS shield AT first WITH left");
        assertBroadcastedStartingWith("353 first BLOCKS second ");

        assertEquals(first.getHitpoints(), first.getMaxHitpoints());

    }

}
