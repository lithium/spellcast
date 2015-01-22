package com.hlidskialf.spellcast.server.test;


import com.hlidskialf.spellcast.server.SpellcastClient;
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
public class SpellcastServerTest {

    private TestSpellcastServer server;
    private TestSpellcastChannel firstChannel;
    private TestSpellcastChannel secondChannel;
    private SpellcastClient first;
    private SpellcastClient second;

    private ArrayList<TestSpellcastChannel> channels;

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


    private void authenticate() {
        server.processChannelMessage(firstChannel, "NAME first");
        assertEquals("200 Hello first", firstChannel.nextMessage());

        server.processChannelMessage(secondChannel, "NAME second");
        assertEquals("200 Hello second", secondChannel.nextMessage());
    }

    private void sendFirst(String msg) {
        server.processChannelMessage(firstChannel, msg);
    }
    private void sendSecond(String msg) {
        server.processChannelMessage(secondChannel, msg);
    }

    private static void assertStartsWith(String expected, String actual) {
        if (!actual.startsWith(expected)) {
            throw new ComparisonFailure("", (String)expected, (String)actual);
        }
    }
    private static void assertChannelContains(String expected, TestSpellcastChannel channel) {
        for (String msg : channel.messages) {
            if (expected.equals(msg)) {
                return;
            }
        }
        throw new ComparisonFailure("channel did not contain", expected, channel.messages.toString());
    }
    private static void assertContainsStartingWith(String expected, TestSpellcastChannel channel) {
        for (String msg : channel.messages) {
            if (msg.startsWith(expected)) {
                return;
            }
        }
        throw new ComparisonFailure("channel did not contain", expected, channel.messages.toString());
    }


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
}
