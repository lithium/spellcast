package com.hlidskialf.spellcast.server;


import com.hlidskialf.spellcast.server.effect.DeathEffect;
import com.hlidskialf.spellcast.server.effect.ResistElementEffect;
import com.hlidskialf.spellcast.server.effect.ShieldEffect;
import com.hlidskialf.spellcast.server.spell.CounterspellSpell;
import com.hlidskialf.spellcast.server.spell.DispelMagicSpell;
import com.hlidskialf.spellcast.server.spell.MagicMirrorSpell;
import com.hlidskialf.spellcast.server.spell.RemoveEnchantmentSpell;
import com.hlidskialf.spellcast.server.spell.Spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastServer<ChannelType> implements SpellcastMatchState {
    private String serverName;
    private String serverVersion;

    private HashMap<ChannelType, SpellcastClient> clients;

    private ArrayList<SpellcastClient> players;
    private ArrayList<Tombstone> tombstones;
    private int matchIdSeed;
    private String currentMatchId;
    private int currentRoundNumber;
    private MatchState currentMatchState;
    private RoundState currentRoundState;
	private ArrayList<ResolvingSpell> resolvingSpells;
    private ArrayList<ResolvingAttack> resolvingAttacks;


	private enum MatchState {
        WaitingForPlayers,
        Playing
    }
    private enum RoundState {
        WaitingForGestures,
        WaitingForAnswers,
        NotPlaying
    }



    /* abstract methods */
    abstract public void sendToClient(SpellcastClient client, String message);
    abstract public void closeClient(SpellcastClient client);

    public SpellcastServer() {
        clients = new HashMap<ChannelType, SpellcastClient>();
        players = new ArrayList<SpellcastClient>();
        tombstones = new ArrayList<Tombstone>();
        resolvingSpells = new ArrayList<ResolvingSpell>();
        resolvingAttacks = new ArrayList<ResolvingAttack>();
        matchIdSeed = 1000;
        currentMatchState = MatchState.WaitingForPlayers;
        currentRoundState = RoundState.NotPlaying;
    }

    public SpellcastServer(String serverName, String serverVersion) {
        this();
        this.serverName = serverName;
        this.serverVersion = serverVersion;
    }


    public SpellcastClient addChannel(ChannelType channel) {
        SpellcastClient newClient = new SpellcastClient(channel);
        clients.put(channel, newClient);
        hello(newClient);
        return newClient;
    }
    public void removeChannel(ChannelType channel) {
        SpellcastClient client = clients.get(channel);
        if (client != null)
            disconnectClient(client, false);
    }



    public void processChannelMessage(ChannelType channel, String message) {
        SpellcastClient client = clients.get(channel);
        String[] parts = message.split(" ");
        String command = parts[0].toUpperCase();

        if (command.equals("QUIT")) {
            disconnectClient(client, true);
        }
        else
        if (client.getState() == SpellcastClient.ClientState.WaitingForName) {
            if (command.equals("NAME") && parts.length > 1) {
                if (isNicknameTaken(parts[1], client)) {
                    error_nickname_used(client);
                } else if (!ValidationHelper.isNicknameValid(parts[1])) {
                    error_invalid_nickname(client);
                } else {

                    client.setNickname(parts[1]);
                    if (parts.length > 2)
                        client.setGender(parts[2]);

                    int idx = message.lastIndexOf(':');
                    if (idx != -1) {
                        client.setVisibleName(message.substring(idx + 1));
                    } else if (parts.length > 3) {
                        client.setVisibleName(parts[3]);
                    }
                    client.setState(SpellcastClient.ClientState.Watching);

                    welcome(client);
                    broadcast(client.get301(), client);
                }
            }
        }
        else {
            if (command.equals("NAME") && parts.length > 2) {
                client.setGender(parts[1]);
                int idx = message.lastIndexOf(':');
                if (idx != -1) {
                    client.setVisibleName(message.substring(idx + 1));
                } else {
                    client.setVisibleName(parts[2]);
                }
                broadcast(client.get301(), null);
            } else if (command.equals("SAY")) {
                String txt = message.substring(4);
                broadcast("201 " + client.getNickname() + " :" + txt, null);
            } else if (currentMatchState == MatchState.WaitingForPlayers && command.equals("READY")) {
                if (client.getState() == SpellcastClient.ClientState.Watching) {
                    client.setReady(true);
                    broadcast(client.get301(), null);
                    checkForMatchStart();
                }
            } else if (command.equals("YIELD")) {
                if (client.getState() == SpellcastClient.ClientState.Watching) {
                    // client indicates they aren't ready for the match to start
                    client.setReady(false);
                    broadcast(client.get301(), null);
                }
                else if (client.getState() == SpellcastClient.ClientState.Playing) {
                    // TODO: client loses
                }
            } else if (currentRoundState == RoundState.WaitingForGestures && command.equals("GESTURE") && parts.length > 2) {
                if (ValidationHelper.isGestureValid(parts[1]) && ValidationHelper.isGestureValid(parts[2])) {
                    client.readyGestures(parts[1], parts[2]);
                    broadcast("321 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" :Gestures ready");
                    if (isAllPlayersGestured()) {
                        startRoundQuestions();
                    }
                } else {
                    error_invalid_gesture(client);
                }
            } else if (currentRoundState == RoundState.WaitingForAnswers &&
                       command.equals("ANSWER") &&
                       parts.length > 2 &&
                       client.hasUnansweredQuestions()) {
                // ANSWER <hand> <answer>
                // ANSWER <hand> <answer> <hand> <answer>

                if (!answer_question(client, parts[1], parts[2])) {
                    error_invalid_answer(client);
                }
                if (parts.length == 5) {
                    if (!answer_question(client, parts[3], parts[4])) {
                        error_invalid_answer(client);
                    }
                }

                if (client.hasUnansweredQuestions()) {
                    askClientQuestions(client);
                } else {
                    //answered their last question
                    broadcast("348 " + currentMatchId + "." + currentRoundNumber + " " + client.getNickname() + " :Finished answering");

                    if (isAllClientsAnswered()) {
                        resolveRound();
                    }
                }

            } else if (command.equals("WHO")) {
                wizards(client);
                monsters(client);
            }
        }
    }

    private boolean answer_question(SpellcastClient client, String hand, String answer) {
        if (ValidationHelper.isHandValid(hand) && (isTargetValid(answer) ||
                                                   ValidationHelper.isSpellValid(answer))) {
            client.answerQuestion(hand, answer);
            return true;
        }
        return false;
    }

    private void disconnectClient(SpellcastClient client, boolean close) {
        broadcast("303 " + client.getNickname() + " :Quits", client);
        clients.remove(client.getChannel());
        if (currentMatchState == MatchState.Playing) {
            killTarget(client);
            displayTombstones();
            checkForWinner();
        }
        if (close) {
            closeClient(client);
        }

    }
    private boolean isNicknameTaken(String nickname, SpellcastClient client) {
        for (SpellcastClient c : clients.values()) {
            if ((client == null || !client.equals(c)) && (c.getNickname() != null && c.getNickname().equals(nickname))) {
                return true;
            }
        }
        return false;
    }
    public boolean isTargetValid(String target) {
        if (target == null || target.isEmpty()) {
            return false;
        }
        return getTargetByNickname(target) != null;
    }

    public Collection<SpellcastClient> getAllClients() {
        return clients.values();
    }


    public String getCurrentMatchId() {
        return currentMatchId;
    }

    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    public MatchState getCurrentMatchState() {
        return currentMatchState;
    }

    public RoundState getCurrentRoundState() {
        return currentRoundState;
    }

    public Elemental getElemental() {
        for (SpellcastClient c : players) {
            Elemental e = c.getElemental();
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    public Target getTargetByNickname(String nick) {
        Target target = getClientByNickname(nick);
        if (target != null) {
            return target;
        }
        for (SpellcastClient client : players) {
            target = client.getMonsterById(nick);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    public SpellcastClient getClientByNickname(String nickname) {
        for (SpellcastClient c : clients.values()) {
            if (c.getNickname() != null && c.getNickname().equals(nickname)) {
                return c;
            }
        }
        return null;
    }

    /* return false if still playing, true if there was a winner */
    private boolean checkForWinner() {
        if (players.size() > 1) {
            return false;
        }
        if (players.size() == 1) {
            declareWinner(players.get(0));
            return true;
        }
        // no players left -- its a draw
        declareDraw();
        return true;
    }
    private boolean isAllClientsAnswered() {
        for (SpellcastClient c : players) {
            if (c.hasUnansweredQuestions()) {
                return false;
            }
        }
        return true;
    }
    private boolean isAllPlayersGestured() {
        for (SpellcastClient c : players) {
            if (!c.isReady()) {
                return false;
            }
        }
        return true;
    }
    private boolean isAllClientsReady() {
        if (clients.size() < 2) {
            return false;
        }
        for (SpellcastClient c : clients.values()) {
            if (c.getState() == SpellcastClient.ClientState.Watching && !c.isReady()) {
                return false;
            }
        }
        return true;
    }
    private void checkForMatchStart() {
        if (currentMatchState == MatchState.WaitingForPlayers && isAllClientsReady()) {
            startNewMatch();
        }
    }

    private String generateMatchId() {
        matchIdSeed += 1;
        return "m"+(matchIdSeed);
    }

    private void startNewMatch() {
        if (currentMatchState == MatchState.Playing) {
            return;
        }

        players.clear();
        tombstones.clear();
        currentMatchState = MatchState.Playing;
        currentMatchId = generateMatchId();
        currentRoundNumber = 0;
        broadcast("250 "+currentMatchId+ " :Match start");
        for (SpellcastClient c : clients.values()) {
            if (c.getState().equals(SpellcastClient.ClientState.Watching)) {
                c.resetHistory();
                c.setReady(false);
                c.setHitpoints(15);
                c.setMaxHitpoints(15);
                c.setState(SpellcastClient.ClientState.Playing);
                players.add(c);
            }
        }
        startNewRound();
    }

    private void startNewRound() {
        currentRoundNumber += 1;
        currentRoundState = RoundState.WaitingForGestures;
        broadcast("251 "+currentMatchId+"."+currentRoundNumber+" :Round start");

        //expire effects and reset players
        for (SpellcastClient client : players) {
            client.setReady(false);
            client.resetQuestions();
            for (String expireMsg : client.expireEffects(currentMatchId, currentRoundNumber)) {
                broadcast("355 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" :"+expireMsg);
            }
        }

        broadcast_stats();

        //ask for gestures
        for (SpellcastClient client : players) {
            if (client.canGestureThisRound(currentRoundNumber)) {
                broadcast("320 " + currentMatchId + "." + currentRoundNumber + " " + client.getNickname() + " :What are your gestures");
            }
        }
    }
    private void startRoundQuestions() {

        broadcast("330 :Gestures--");
        for (SpellcastClient client : players) {
            broadcast("331 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" "+client.getLeftGesture()+" "+client.getRightGesture());
        }
        broadcast("332 :End of Gestures");

        boolean anyQuestions = false;

        // ask each client their questions
        for (SpellcastClient client : players) {
            client.setReady(false);
            client.performGestures();
            client.askForMonsterAttacks();
            if (askClientQuestions(client)) {
                anyQuestions = true;
            }
        }

        if (anyQuestions) {
            currentRoundState = RoundState.WaitingForAnswers;
        } else {
            resolveRound();
        }
    }

	public ArrayList<ResolvingSpell> getResolvingSpells() {
		return resolvingSpells;
	}

    private void resolveRound() {

	    resolvingSpells.clear();
        resolvingAttacks.clear();


        // queue up all spells that resolved
        for (SpellcastClient client : players) {

            for (Map.Entry<Hand,ArrayList<SpellQuestion>> e : client.getSpellQuestions().entrySet()) {
                resolveSpells(client, e.getValue(), e.getKey(), resolvingSpells);
            }
        }

        // queue up all attacks that resolved
        for (SpellcastClient client : players) {
            //stabs
            for (Map.Entry<Hand,ArrayList<SpellQuestion>> e : client.getSpellQuestions().entrySet()) {
                resolveStabs(client, e.getValue(), e.getKey(), resolvingAttacks);
            }

            //monster attacks
            for (MonsterQuestion mq : client.getMonsterQuestions()) {
                Target target = getTargetByNickname(mq.getTarget());
                if (mq.getMonster() == null) {
                    resolvingAttacks.add(new ResolvingAttack(client, mq.getNickname(), target, mq.getDamage()));
                } else {
                    resolvingAttacks.add(new ResolvingAttack(mq.getMonster(), target, mq.getDamage()));
                }
            }

        }

	    /*
	     * Resolution order:
		 * resolve any dispel magic
	     * resolve any counterspell
	     * resolve magic mirrors
	     * resolve remove enchantment
	     * bestow any effects from resolving spells
	     * fire remaining spells
	     * resolve death effects
	     * stabs/monster attacks
	     * dispel monsters
	     * clean up the dead
	     */


        // resolve any counter/negation spells first in priority order
	    fireParticularSpell(resolvingSpells, DispelMagicSpell.Slug);
	    fireParticularSpell(resolvingSpells, CounterspellSpell.Slug);
	    fireParticularSpell(resolvingSpells, MagicMirrorSpell.Slug);
	    fireParticularSpell(resolvingSpells, RemoveEnchantmentSpell.Slug);

        //get effects from remaining spells
        for (ResolvingSpell rSpell : resolvingSpells) {
            if (!(rSpell.isCountered() || rSpell.isFired())) {
                broadcast(rSpell.get351());
                rSpell.effects(this);
            }
        }
        //fire any remaining un-countered spells
	    for (ResolvingSpell rSpell : resolvingSpells) {
		    if (!(rSpell.isCountered() || rSpell.isFired())) {
			    rSpell.fire(this);
		    }
	    }

	    // resolve death effects
        for (Target t: getAllTargets()) {
            if (t.hasEffect(DeathEffect.Name)) {
                killTarget(t);
            }
        }
        displayTombstones();


        //elemental attacks
        Elemental elemental = getElemental();
        if (elemental != null) {
            for (Target target : players) {
                if (target.hasEffect(ResistElementEffect.resistanceFor(elemental.getElement()))) {
                    broadcast(ResolvingAttack.get353(elemental, target, "resists "+elemental.getElement()));
                }
                else if (target.hasEffect(ShieldEffect.Name)) {
                    broadcast(ResolvingAttack.get353(elemental, target, "slides off shield"));
                } else {
                    broadcast(ResolvingAttack.get352(elemental, target));
                    target.takeDamage(elemental.getDamage());
                }
            }
        }

        //resolve stabs/monster attacks
        for (ResolvingAttack rAttack : resolvingAttacks) {
            if (rAttack.resolveAttack(this)) {
                broadcast(rAttack.get352());
            } else {
                broadcast(rAttack.get353("slides off shield"));
            }
        }

	    // dispel monsters
        for (SpellcastClient client : players) {
            for (Monster monster : client.getMonsters()) {
                if (monster.isDispelled()) {
                    client.loseControlOfMonster(monster);
                    broadcast(monster.get360());
                }
            }
        }

        // clean up the dead
        for (Target t : getAllTargets()) {
            if (t.getHitpoints() < 1) {
                killTarget(t);
            }
        }
        displayTombstones();


        broadcast("350 " + currentMatchId + "." + currentRoundNumber + " :Round complete");

        if (!checkForWinner()) {
            startNewRound();
        }
    }

    private void displayTombstones() {
        int last = tombstones.size();
        if (last <= 0)
            return;
        ListIterator<Tombstone> it = tombstones.listIterator(last);
        while (it.hasPrevious()) {
            Tombstone tomb = it.previous();
            if (!tomb.isDisplayed() && tomb.getDeathRound() == currentRoundNumber) {
                tomb.setDisplayed(true);
                broadcast("380 "+tomb.getNickname()+" :dies");
                SpellcastClient client = tomb.getClient();
                client.setState(SpellcastClient.ClientState.Watching);
                client.setReady(false);
                players.remove(client);
            } else {
                break;
            }
        }
    }

    private void killTarget(Target t) {
        if (t instanceof SpellcastClient) {
            SpellcastClient client = (SpellcastClient) t;
            tombstones.add(new Tombstone(client, currentMatchId, currentRoundNumber));
        } else if (t instanceof Monster) {
            Monster monster = (Monster) t;
            monster.getController().loseControlOfMonster(monster);
        }
    }

	private void fireParticularSpell(ArrayList<ResolvingSpell> resolvingSpells, String spellSlug) {
		for (ResolvingSpell rs : resolvingSpells) {
			if (!rs.isCountered() && rs.getSpell().getSlug().equals(spellSlug)) {
                broadcast(rs.get351());
                rs.effects(this);
				rs.fire(this);
			}
		}
	}

    private void resolveSpells(SpellcastClient client, ArrayList<SpellQuestion> questions, Hand hand, ArrayList<ResolvingSpell> spellBuffer) {
        for (SpellQuestion q : questions) {
            Spell spell = q.getSpell();
            Target target = getTargetByNickname(q.getTarget());
            if (!spell.getSlug().equals("stab")) {
	            spellBuffer.add(new ResolvingSpell(spell, client, target, hand));
            }
        }
    }
    private void resolveStabs(SpellcastClient client, ArrayList<SpellQuestion> questions, Hand hand, ArrayList<ResolvingAttack> attackBuffer) {
        for (SpellQuestion q : questions) {
            Spell spell = q.getSpell();
            Target target = getTargetByNickname(q.getTarget());
            if (spell.getSlug().equals("stab")) {
                attackBuffer.add(new ResolvingAttack(client, target, 1));
            }
        }
    }



    private void declareWinner(SpellcastClient winner) {
        broadcast("390 "+currentMatchId+" "+winner.getNickname()+" :Wins the match!");
        currentMatchState = MatchState.WaitingForPlayers;
        winner.setReady(false);
        winner.setHitpoints(0);
        winner.setMaxHitpoints(0);
        winner.resetHistory();
        winner.resetQuestions();
        winner.setState(SpellcastClient.ClientState.Watching);
        players.clear();
    }
    private void declareDraw() {

        // since there are no players left, a draw is declared between all wizards who died this round.
        broadcast("391 "+currentMatchId+" :is a draw between-");

        // we're assuming tombstones are in round order here
        ListIterator<Tombstone> it = tombstones.listIterator(tombstones.size());
        while (it.hasPrevious()) {
            Tombstone tomb = it.previous();
            if (tomb.getDeathRound() != currentRoundNumber) //we've gone back past anyone who died this round
                break;
            broadcast("392 "+tomb.getNickname());
        }
        broadcast("393 :End of draw");
        currentMatchState = MatchState.WaitingForPlayers;
    }

    private boolean askClientQuestions(SpellcastClient client) {
        if (client.hasUnansweredQuestions()) {
            sendToClient(client, "340 " + currentMatchId + "." + currentRoundNumber + " :Questions");


            for (Map.Entry<Hand,ArrayList<SpellQuestion>> e : client.getSpellQuestions().entrySet()) {
                questions(client, e.getValue(), e.getKey());
            }
            questions_monsters(client, client.getMonsterQuestions());
            sendToClient(client, "349 :End of Questions");
            return true;
        }
        return false;
    }

    public void questions(SpellcastClient client, ArrayList<SpellQuestion> spellQuestions, Hand hand) {
        int left = spellQuestions.size();
        if (left > 1) {
            sendToClient(client, "341 "+hand+" :Cast which spell with "+hand+" hand");
            for (SpellQuestion q : spellQuestions) {
                sendToClient(client, "342 "+hand+" " + q.getSpell().getSlug() + " :" + q.getSpell().getName());
            }
            sendToClient(client, "343 :End of spells");
        } else if (left > 0) { // one spell to resolve, needs a target
            SpellQuestion q = spellQuestions.get(0);
            if (!q.hasTarget()) {
                sendToClient(client, "345 "+hand+" "+ q.getSpell().getSlug() + " :Which target for "+hand+" hand");
                for (Target t : getVisibleTargets(client)) {
                   sendToClient(client, "346 "+hand+" "+t.getNickname()+" :"+t.getVisibleName());
                }
                sendToClient(client, "347 :End of targets");
            }
        }
    }

    public void questions_monsters(SpellcastClient client, ArrayList<MonsterQuestion> monsterQuestions) {
        int remaining = 0;
        for (MonsterQuestion mq : monsterQuestions) {
            if (!mq.hasTarget()) {
                remaining++;

                sendToClient(client, "335 "+mq.getMonsterNickname()+" :Which target should monster attack");
                for (Target t : getVisibleTargets(mq.getMonster())) {
                    sendToClient(client, "336 "+mq.getMonsterNickname()+" "+t.getNickname()+" :"+t.getVisibleName());
                }
                sendToClient(client, "337 :End of monster targets");
            }
        }
    }

    @Override
    public Iterable<Target> getAllTargets() {
        return getVisibleTargets(null);
    }

    public Iterable<Target> getVisibleTargets(Target origin) {
        ArrayList<Target> visible = new ArrayList<Target>();
        for (SpellcastClient client : players) {
            if (client.getState().equals(SpellcastClient.ClientState.Playing)) {
                visible.add(client);
                for (Monster monster : client.getMonsters()) {
                    visible.add(monster);
                }
            }
        }
        return visible;
    }


    public void broadcast(String message) {
        broadcast(message, null);
    }
    public void broadcast(String message, SpellcastClient except) {
        for (SpellcastClient client : clients.values()) {
            if (client.getState() != SpellcastClient.ClientState.WaitingForName && (except == null || !except.equals(client))) {
                sendToClient(client, message);
            }
        }
    }

    public void hello(SpellcastClient client) {
        sendToClient(client, "222 SPELLCAST " + serverName + " " + serverVersion);
    }

    public void welcome(SpellcastClient client) {
        sendToClient(client, "200 Hello " + client.getNickname());
        wizards(client);
        monsters(client);
    }


    public void broadcast_stats() {
        for (SpellcastClient c : players) {
            wizards(c);
            monsters(c);
        }
    }
    public void wizards(SpellcastClient client) {
        sendToClient(client, "300 Wizards");
        for (SpellcastClient c : players) {
            sendToClient(client, c.get301());
        }
        sendToClient(client, "302 :End of Wizards");
    }

    public void monsters(SpellcastClient client) {
        sendToClient(client, "310 Monsters");
        for (SpellcastClient c : players) {
            for (Monster monster : c.getMonsters()) {
                sendToClient(client, monster.get311());
            }
        }
        sendToClient(client, "312 :End of Monsters");

    }


    public void error_nickname_used(SpellcastClient client) {
        sendToClient(client, "400 :Nickname in use");
    }
    public void error_invalid_nickname(SpellcastClient client) {
        sendToClient(client, "401 :Invalid nickname");
    }
    public void error_invalid_gesture(SpellcastClient client) {
        sendToClient(client, "402 :Invalid gesture");
    }
    public void error_invalid_answer(SpellcastClient client) {
        sendToClient(client, "403 :Invalid answer");
    }
}
