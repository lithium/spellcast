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
        assertContainsStartingWith("XX1 left$summongoblin.target ", firstChannel);

        //cast it on first and have it attack second
        sendFirst("ANSWER left first");
        sendFirst("ANSWER left$summongoblin.target second");

        assertTrue(first.getMonsters().iterator().hasNext());
        String monsterNick = first.getMonsters().iterator().next().getNickname();

        // no answer errors
        assertNotBroadcastedStartingWith("403 ");

        assertBroadcastedStartingWith("351 first CASTS summongoblin AT first WITH left");
        assertBroadcasted("352 "+monsterNick+" ATTACKS second");

        assertEquals(second.getMaxHitpoints() - 1, second.getHitpoints());

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


    @Test
    public void shouldCastCureWounds() {
        authenticateAndStart();
        sendGestures("K","S","_","D");
        sendFirst("ANSWER left second");

        sendGestures("K","D","_","F");
        sendFirst("ANSWER left second");
        sendFirst("ANSWER right second");

        assertTookDamage(second, 3);

        sendGestures("_", "D", "_", "W");

        //second: cure light wounds on self
        sendSecond("ANSWER right second");
        assertBroadcasted("351 second CASTS curelightwounds AT second WITH right");

        sendGestures("__","FP","__","__");
        sendFirst("ANSWER right first"); // first: shield

        //first: cure heavy wounds on second
        sendGestures("_","W","_","_");
        sendFirst("ANSWER right second");
        assertBroadcasted("351 first CASTS cureheavywounds AT second WITH right");

        assertTookNoDamage(second);
    }

    @Test
    public void shouldCastCauseWounds() {
        authenticateAndStart();
        sendGestures("WFP","___","___","___");
        sendFirst("ANSWER left causelightwounds");
        sendFirst("ANSWER left second");

        assertTookDamage(second, 2);

        sendGestures("WP","__","__","__");
        sendFirst("ANSWER left first"); // shield

        sendGestures("FD","__","__","__");
        sendFirst("ANSWER left second");

        assertTookDamage(second,5);

    }

    @Test
    public void shouldCastLightningBolt() {
        authenticateAndStart();
        sendGestures("WDDc","___c","DFFD","____");

        assertTrue(firstChannel.matchingMessage("345 both lightningbolt"));
        firstChannel.setIndex();

        //first: short lightning bolt on second
        sendFirst("ANSWER both second");
        assertBroadcasted("351 first CASTS lightningbolt AT second WITH both");
        assertTookDamage(second, 5);

        //second: long lightning bolt at first
        sendGestures("W","_","D","_");
        sendSecond("ANSWER left first");
        assertBroadcasted("351 second CASTS lightningbolt AT first WITH left");
        assertTookDamage(first, 5);

        //first: try short lightning bolt on second
        sendGestures("DDc", "__c", "FFD", "___");
        assertFalse(firstChannel.matchingMessage("345 both lightningbolt"));

        sendGestures("_", "_", "D", "_");
        sendSecond("ANSWER left first");
        assertTookDamage(first, 10);
    }


    private void castDiseaseOnSecond() {

        //confusion
        sendGestures("DSF","___","___","___");
        sendFirst("ANSWER left second");

        //confusion may have made second do P so send answers just in case
        sendGestures("F","_","_","_");
        sendSecond("ANSWER left second");
        sendSecond("ANSWER right second");

        //paralysis
        sendGestures("F","_","_","_");
        sendFirst("ANSWER left second");
        sendFirst("ANSWER left$paralysis.hand left");

        //disease
        sendGestures("c","c","_","_");
        sendFirst("ANSWER both second");

        assertBroadcasted("351 first CASTS disease AT second WITH both");
    }

    @Test
    public void shouldDieFromDisease() {
        authenticateAndStart();
        castDiseaseOnSecond();

        //wait 6 turns
        sendGestures("_____","_____","_____","_____");

        assertBroadcasted("355 m1001.12 second :second keels over and dies from a disease");
        assertBroadcastedStartingWith("380 second "); // second dies
        assertBroadcastedStartingWith("390 "+server.getCurrentMatchId()+" first "); // first wins
    }

    @Test
    public void shouldCureDiseaseWithCureWounds() {
        authenticateAndStart();
        castDiseaseOnSecond();

        cureHeavyWoundsOnSecond();
        //wait 6 turns
        sendGestures("_____","_____","_____","_____");
        assertNotBroadcastedStartingWith("380 second "); // second shouldn't die from disease
    }

    private void cureHeavyWoundsOnSecond() {
        sendGestures("___","___","DFP","___");
        sendSecond("ANSWER left second");
        sendGestures("_","_","W","_");
        sendSecond("ANSWER left second");
        assertBroadcasted("351 second CASTS cureheavywounds AT second WITH left");
    }

    @Test
    public void shouldCureDiseaseWithRemoveEnchantment() {
        authenticateAndStart();
        castDiseaseOnSecond();


        removeEnchantmentOnSecond();

        //wait 6 turns
        sendGestures("_____","_____","_____","_____");
        assertNotBroadcastedStartingWith("380 second "); // second shouldn't die from disease
    }

    private void removeEnchantmentOnSecond() {
        sendGestures("_","_","P","_");
        sendSecond("ANSWER left second");

        sendGestures("___","___","DWP","___");
        sendSecond("ANSWER left removeenchantment");
        sendSecond("ANSWER left second");

        assertBroadcasted("351 second CASTS removeenchantment AT second WITH left");
    }

    @Test
    public void shouldCureDiseaseWithDispelMagic() {
        authenticateAndStart();
        castDiseaseOnSecond();


        dispelMagicOnSecond();
                
        //wait 6 turns
        sendGestures("_____","_____","_____","_____");
        assertNotBroadcastedStartingWith("380 second "); // second shouldn't die from disease
    }

    private void dispelMagicOnSecond() {

        sendGestures("___","___","CDP","___");
        sendSecond("ANSWER left second");
        sendGestures("_","_","W","_");
        sendSecond("ANSWER left second");

        assertBroadcasted("351 second CASTS dispelmagic AT second WITH left");
    }


    @Test
    public void shouldDieFromPoison() {
        authenticateAndStart();
        sendGestures("DWWFWD", "______", "______", "______");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS poison AT second WITH left");

        //wait 6 turns
        sendGestures("_____", "_____", "_____", "_____");

        assertBroadcasted("355 m1001.12 second :second clutches their throat and gasping dies from poison");
        assertBroadcastedStartingWith("380 second "); // second dies
        assertBroadcastedStartingWith("390 "+server.getCurrentMatchId()+" first "); // first wins
    }

    @Test
    public void shouldCurePoisonWithRemoveEnchantment() {
        authenticateAndStart();
        sendGestures("DWWFWD", "______", "______", "______");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS poison AT second WITH left");

        removeEnchantmentOnSecond();

        assertNotBroadcastedStartingWith("380 second "); // second does not die
    }

    @Test
    public void shouldCurePoisonWithDispelMagic() {
        authenticateAndStart();
        sendGestures("DWWFWD", "______", "______", "______");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS poison AT second WITH left");

        dispelMagicOnSecond();

        assertNotBroadcastedStartingWith("380 second "); // second does not die
    }

    @Test
    public void shouldDieFromPoisonEvenWithCureHeavy() {
        authenticateAndStart();
        sendGestures("DWWFWD", "______", "______", "______");
        sendFirst("ANSWER left second");
        assertBroadcasted("351 first CASTS poison AT second WITH left");

        cureHeavyWoundsOnSecond();
        //wait 6 turns
        sendGestures("_____", "_____", "_____", "_____");

        assertBroadcasted("355 m1001.12 second :second clutches their throat and gasping dies from poison");
        assertBroadcastedStartingWith("380 second "); // second dies
        assertBroadcastedStartingWith("390 " + server.getCurrentMatchId() + " first "); // first wins
    }


    @Test
    public void shouldKillMonsterWithBlindness() {
        authenticateAndStart();

        //second: summon goblin
        sendGestures("DWFF","___P", "_SFW","____");
        sendFirst("ANSWER right first"); // shield on first
        sendSecond("ANSWER left second");
        sendSecond("ANSWER left$summongoblin.target first");
        String mob = second.getMonsters().iterator().next().getNickname();

        //first: blindness on monster
        sendGestures("d","d", "_","_");
        sendFirst("ANSWER both " + mob);
        sendSecond("ANSWER "+mob+" first");

        assertBroadcasted("351 first CASTS blindness AT "+mob+" WITH both");
        assertBroadcastedStartingWith("380 " + mob + " ");

        assertTookNoDamage(first);
    }

    @Test
    public void shouldCastBlindness() {
        authenticateAndStart();

        sendGestures("DWFFd","____d","_____","____S");
        sendFirst("ANSWER both second");

        assertBroadcasted("351 first CASTS blindness AT second WITH both");

        sendGestures("_","_","K","D");
        sendSecond("ANSWER left first"); // stab first
        sendSecond("ANSWER right first"); // missile first

        assertBroadcastedStartingWith("353 first BLOCKS second "); //attack missed
        assertTookDamage(first, 1); // but missile still connected

        //TODO: second should not see the results through the blindness though
//        assertDoesNotContainStartingWith("357 second missile first ", secondChannel);
    }

    @Test
    public void shouldCastInvisibility() {
        authenticateAndStart();

        //first: invisibility on self
        sendGestures("P","_","_","_");
        sendFirst("ANSWER left first");
        sendGestures("P","_","_","_");
        sendFirst("ANSWER left first");
        sendGestures("ws","ws","__","_K");

        sendFirst("ANSWER both first");
        sendSecond("ANSWER right first"); // second attack first

        assertBroadcastedStartingWith("353 first BLOCKS second "); //attack missed
        assertTookNoDamage(first);

        sendGestures("WWWW","SSSS","____","____");

        //second can not see firsts gestures for the next 3 rounds
        for (int i=5; i<8; i++) {
            assertChannelContains("331 "+server.getCurrentMatchId()+"."+i+" first * *", secondChannel);
        }
        assertChannelContains("331 "+server.getCurrentMatchId()+".8 first W S", secondChannel);
    }

    @Test
    public void shouldDestroyMonsterWithInvisibility() {
        authenticateAndStart();

        //second summon goblin
        sendGestures("P","_","S","_");
        sendFirst("ANSWER left first");
        sendGestures("P","_","F","_");
        sendFirst("ANSWER left first");
        sendGestures("w","w","W","_");
        sendSecond("ANSWER left second");
        sendSecond("ANSWER left$summongoblin.target first");

        String mob = second.getMonsters().iterator().next().getNickname();

        //first: invisibility on monser
        sendGestures("s","s","_","_");
        sendFirst("ANSWER both "+mob);
        sendSecond("ANSWER "+mob+" first");

        assertBroadcasted("351 first CASTS invisibility AT "+mob+" WITH both");
        assertBroadcastedStartingWith("380 "+mob);

        assertTookDamage(first,1);

    }
}
