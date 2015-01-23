package com.hlidskialf.spellcast.server.test.helpers;

import com.hlidskialf.spellcast.server.SpellcastClient;
import org.junit.Before;
import org.junit.ComparisonFailure;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by wiggins on 1/22/15.
 */
public class SpellcastTest {
    protected TestSpellcastServer server;
    protected TestSpellcastChannel firstChannel;
    protected TestSpellcastChannel secondChannel;
    protected SpellcastClient first;
    protected SpellcastClient second;

    protected ArrayList<TestSpellcastChannel> channels;

    @Before
    public void initialize() {
        server = new TestSpellcastServer("TestServer", "v0.0");
        firstChannel = new TestSpellcastChannel();
        secondChannel = new TestSpellcastChannel();

        first = server.addChannel(firstChannel);
        assertEquals("222 SPELLCAST TestServer v0.0", firstChannel.nextMessage());

        second = server.addChannel(secondChannel);
        assertEquals("222 SPELLCAST TestServer v0.0", secondChannel.nextMessage());

        channels = new ArrayList<TestSpellcastChannel>();
        channels.add(firstChannel);
        channels.add(secondChannel);
    }


    protected void authenticate() {
        server.processChannelMessage(firstChannel, "NAME first");
        assertEquals("200 Hello first", firstChannel.nextMessage());

        server.processChannelMessage(secondChannel, "NAME second");
        assertEquals("200 Hello second", secondChannel.nextMessage());
    }
    protected void authenticateAndStart() {
        authenticate();
        sendFirst("READY");
        sendSecond("READY");
    }

    protected void sendFirst(String msg) {
        server.processChannelMessage(firstChannel, msg);
    }
    protected void sendSecond(String msg) {
        server.processChannelMessage(secondChannel, msg);
    }

    public void assertBroadcasted(String expected) {
        for (TestSpellcastChannel c : channels) {
            assertChannelContains(expected, c);
        }
    }
    public void assertBroadcastedStartingWith(String expected) {
        for (TestSpellcastChannel c : channels) {
            assertContainsStartingWith(expected, c);
        }
    }
    public void assertNotBroadcastedStartingWith(String expected) {
        for (TestSpellcastChannel c : channels) {
            assertDoesNotContainStartingWith(expected, c);
        }
    }

    protected String getRoundId() {
        return server.getCurrentMatchId()+"."+server.getCurrentRoundNumber();
    }


    protected void sendGestures(String firstLeft, String firstRight, String secondLeft, String secondRight) {
        int i;
        int l = firstLeft.length();
        for (i=0; i < l; i++) {
            sendFirst("GESTURE "+firstLeft.charAt(i)+" "+firstRight.charAt(i));
            sendSecond("GESTURE " + secondLeft.charAt(i) + " " + secondRight.charAt(i));
        }
    }



    public static void assertTookNoDamage(SpellcastClient client) {
        assertTookDamage(client, 0);
    }
    public static void assertTookDamage(SpellcastClient client, int howMuch) {
        assertEquals(client.getMaxHitpoints()-howMuch, client.getHitpoints());
    }

    public static void assertStartsWith(String expected, String actual) {
        if (!actual.startsWith(expected)) {
            throw new ComparisonFailure("does not start with", (String)expected, (String)actual);
        }
    }
    public static void assertChannelContains(String expected, TestSpellcastChannel channel) {
        for (String msg : channel.messages) {
            if (expected.equals(msg)) {
                return;
            }
        }
        throw new ComparisonFailure("channel did not contain", expected, channel.messages.toString());
    }
    public static void assertContainsStartingWith(String expected, TestSpellcastChannel channel) {
        for (String msg : channel.messages) {
            if (msg.startsWith(expected)) {
                return;
            }
        }
        throw new ComparisonFailure("channel did not contain", expected, channel.messages.toString());
    }
    public static void assertDoesNotContainStartingWith(String expected, TestSpellcastChannel channel) {
        for (String msg : channel.messages) {
            if (msg.startsWith(expected)) {
                throw new ComparisonFailure("channel contained", expected, channel.messages.toString());
            }
        }

    }




}
