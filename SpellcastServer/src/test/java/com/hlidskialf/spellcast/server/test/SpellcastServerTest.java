package com.hlidskialf.spellcast.server.test;


import com.hlidskialf.spellcast.server.SpellcastClient;
import com.hlidskialf.spellcast.server.test.helpers.SpellcastTest;
import com.hlidskialf.spellcast.server.test.helpers.TestSpellcastChannel;
import org.junit.Test;

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
    public void shouldNotTakeDamageIfShield() {
        authenticateAndStart();

        sendFirst("GESTURE P _");
        sendSecond("GESTURE K _");

        sendFirst("ANSWER left first");     //first casts shield on self
        sendSecond("ANSWER left first");    //second stabs first

        assertBroadcasted("351 first CASTS shield AT first WITH left");
        assertBroadcastedStartingWith("353 first BLOCKS second ");

        assertEquals(first.getHitpoints(), first.getMaxHitpoints());

    }

    @Test
    public void shouldSummonMonster() {
        authenticateAndStart();
        sendGestures("SFW", "___",
                     "___", "___");

        //asked which target for summongoblin
        assertContainsStartingWith("345 left summongoblin ", firstChannel);

        //asked which target we wanted newly summoned goblin to attack
        assertContainsStartingWith("335 left$summongoblin ", firstChannel);

        //cast it on first and have it attack second
        sendFirst("ANSWER left first");
        sendFirst("ANSWER left$summongoblin second");

        // no answer errors
        assertNotBroadcastedStartingWith("403 ");

        assertBroadcastedStartingWith("351 first CASTS summongoblin AT first WITH left");
        assertBroadcasted("352 mon1000 ATTACKS second");

        assertEquals(second.getMaxHitpoints()-1, second.getHitpoints());

    }

    @Test
    public void shouldTakeDamageFromMissile() {
        authenticateAndStart();
        sendGestures("SD","__",
                     "__","__");

        //first: cast missile at second
        sendFirst("ANSWER LEFT second");

        assertBroadcasted("351 first CASTS missile AT second WITH left");
        assertEquals(second.getMaxHitpoints() - 1, second.getHitpoints());

    }

    @Test
    public void shouldBlockMissileWithShield() {
        authenticateAndStart();
        sendGestures("SD","__",
                     "_P","__");

        //second: cast shield on self
        sendSecond("ANSWER LEFT second");

        //first: cast missile at second
        sendFirst("ANSWER LEFT second");


        assertBroadcasted("351 first CASTS missile AT second WITH left");
        assertBroadcasted("351 second CASTS shield AT second WITH left");

        assertEquals(second.getMaxHitpoints(), second.getHitpoints());

    }

    @Test
    public void shouldReflectMissileWithMagicMirror() {
        authenticateAndStart();
        sendGestures("SD","__",  //first: missile
                     "cw","cw"); //second: magic mirror

        sendFirst("ANSWER LEFT second");

        sendSecond("ANSWER both second");

        //spells fired correctly
        assertBroadcasted("351 second CASTS magicmirror AT second WITH both");
        assertBroadcasted("351 first CASTS missile AT first WITH left");

        //got mirror reflect message
        assertBroadcastedStartingWith("356 second magicmirror first ");

        //first took damage, not second
        assertEquals(first.getMaxHitpoints()-1, first.getHitpoints());

    }

    @Test
    public void shouldCounterspellShield() {
        authenticateAndStart();
        sendGestures("WWS","__K",
                     "__P","___");

        sendFirst("ANSWER left second"); //first: counterspell on second with left
        sendFirst("ANSWER right second"); //first: stab second with right

        sendSecond("ANSWER left second"); //second: shield on second



        assertBroadcasted("351 first CASTS counterspell AT second WITH left");

        //should not counter itself
        assertNotBroadcastedStartingWith("356 second counterspell second ");

        //notified of counter
        assertBroadcastedStartingWith("356 first shield second ");

        //second still doesnt take damage because counterspell provides shield
        assertBroadcastedStartingWith("353 second BLOCKS first ");
        assertEquals(second.getMaxHitpoints(), second.getHitpoints());
    }

    @Test
    public void shouldFingerOfDeath() {
        authenticateAndStart();

        //PWPFSSSD

        sendGestures("P","_","_","_");
        sendFirst("ANSWER left first");  //first: shield on self
        sendGestures("WP","__","__","__");
        sendFirst("ANSWER left first");  //first: shield on self

        sendGestures("FSSSD","_____",
                     "__WWS","_____");

        sendSecond("ANSWER left second"); //second: counterspell on self

        assertContainsStartingWith("341 left ", firstChannel);  //ask for disambiguation between finger of death and missile

        sendFirst("ANSWER left fingerofdeath");  // first: finger of death on second
        sendFirst("ANSWER left second");

        assertBroadcasted("351 first CASTS fingerofdeath AT second WITH left");

        // counterspell has no effect on finger of death
        assertBroadcasted("351 second CASTS counterspell AT second WITH left");

        //second dies
        assertBroadcastedStartingWith("380 second ");

        //first wins
        assertBroadcastedStartingWith("390 " + server.getCurrentMatchId() + " first ");
    }

    @Test
    public void shouldInterruptWithAntispell() {
        authenticateAndStart();

        // first: antispell on second
        // second: try to finish interrupted missile
        sendGestures("SP","__",   "__","__");

        sendFirst("ANSWER left first"); // shield on self

        sendGestures("F", "_", "S", "_");

        sendFirst("ANSWER left second"); // antispell on second

        sendGestures("_","_",  "D","_");


        //second did not complete missile
        assertDoesNotContainStartingWith("345 left missile ", secondChannel);
        assertNotBroadcastedStartingWith("351 second CASTS missile");

        //next round started with no missile
        assertBroadcastedStartingWith("320 m1001.5 ");

    }


    @Test
    public void shouldProtectionFromEvil() {
        authenticateAndStart();

        // first: protectionfromevil on self
        sendGestures("WWP","___",  "__K","___");
        sendFirst("ANSWER left protectionfromevil");
        sendFirst("ANSWER left first");
        sendSecond("ANSWER left first"); //stab first shield

        assertBroadcasted("351 first CASTS protectionfromevil AT first WITH left");

        sendGestures("____","____","___K","____");
        sendSecond("ANSWER left first"); //stab first after shield is over

        // should expire beginning of round 7
        assertBroadcastedStartingWith("355 m1001.7 first :The shield dissipates");
        assertBroadcasted("352 second ATTACKS first");

        assertEquals(first.getMaxHitpoints()-1, first.getHitpoints());
    }

    @Test
    public void shouldTakeDamageFromFireball() {
        authenticateAndStart();

        //first- fireball at second
        sendGestures("FSSD","____", "____","____");
        sendFirst("ANSWER left first"); //first: fire missile at self

        sendGestures("D","_","_","_");
        sendFirst("ANSWER left second"); //first: fireball at second

        assertBroadcasted("351 first CASTS fireball AT second WITH left");

        assertEquals(second.getMaxHitpoints()-5, second.getHitpoints());
    }

    @Test
    public void souldResistHeatFromFireball() {
        authenticateAndStart();

        //first- fireball at second
        sendGestures("FSSD","____",
                     "WWFP","____");
        sendFirst("ANSWER left first"); //first: fire missile at self
        sendSecond("ANSWER left resistheat"); //second: resist heat on self
        sendSecond("ANSWER left second");

        sendGestures("D","_","_","_");
        sendFirst("ANSWER left second"); //first: fireball at second

        assertBroadcasted("351 first CASTS fireball AT second WITH left");

        assertEquals(second.getMaxHitpoints(), second.getHitpoints());
    }

    @Test
    public void shouldDamageEveryoneWithFirestorm() {
        authenticateAndStart();
        sendGestures("SWWc","___c", "____","____");

        assertBroadcasted("351 first CASTS firestorm AT everyone WITH both");

        assertTookDamage(first, 5);
        assertTookDamage(second, 5);

    }

    @Test
    public void shouldDamageAllExceptResistedWithStorm() {
        authenticateAndStart();
        sendGestures("SWWc","___c", "WWFP","____"); // first: firestorm on all

        sendSecond("ANSWER left resistheat"); // second: resist heat on self
        sendSecond("ANSWER left second");

        assertBroadcasted("351 second CASTS resistheat AT second WITH left");
        assertBroadcasted("351 first CASTS firestorm AT everyone WITH both");
        assertTookDamage(first, 5);
        assertTookNoDamage(second);
    }

}
