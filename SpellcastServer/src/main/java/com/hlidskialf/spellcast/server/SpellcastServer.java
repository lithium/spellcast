package com.hlidskialf.spellcast.server;


import com.hlidskialf.spellcast.server.effect.ShieldEffect;
import com.hlidskialf.spellcast.server.spell.Spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by wiggins on 1/11/15.
 */
public abstract class SpellcastServer<ChannelType> implements SpellcastMatchState {
    private HashMap<ChannelType, SpellcastClient> clients;
    private String serverName;
    private String serverVersion;

    private int matchIdSeed;
    private String currentMatchId;
    private int currentRoundNumber;
    private MatchState currentMatchState;
    private RoundState currentRoundState;


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

    public SpellcastServer() {}

    public SpellcastServer(String serverName, String serverVersion) {
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        clients = new HashMap<ChannelType, SpellcastClient>();
        matchIdSeed = 1000;
        currentMatchState = MatchState.WaitingForPlayers;
        currentRoundState = RoundState.NotPlaying;
    }


    public void addChannel(ChannelType channel) {
        SpellcastClient newClient = new SpellcastClient(channel);
        clients.put(channel, newClient);
        hello(newClient);
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
                    client.setState(SpellcastClient.ClientState.Identified);

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
                if (client.getState() == SpellcastClient.ClientState.Identified) {
                    client.setReady(true);
                    broadcast(client.get301(), null);
                    checkForMatchStart();
                }
            } else if (command.equals("YIELD")) {
                if (client.getState() == SpellcastClient.ClientState.Identified) {
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
                    if (isAllClientsReady()) {
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
        if (ValidationHelper.isHandValid(hand) && (isTargetValid(answer) || ValidationHelper.isSpellValid(answer))) {
            client.answerQuestion(hand, answer);
            return true;
        }
        return false;
    }

    private void disconnectClient(SpellcastClient client, boolean close) {
        broadcast("303 "+client.getNickname()+" :Quits", client);
        clients.remove(client.getChannel());
        if (currentMatchState == MatchState.Playing) {
            SpellcastClient winner = checkForWinner();
            if (winner != null) {
                declareWinner(winner);
            }
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

    public void setClients(HashMap<ChannelType, SpellcastClient> clients) {
        this.clients = clients;
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

    public Target getTargetByNickname(String nick) {
        Target target = getClientByNickname(nick);
        if (target != null) {
            return target;
        }
        for (SpellcastClient client : clients.values()) {
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

    private SpellcastClient checkForWinner() {
        SpellcastClient lastSeen=null;
        if (currentMatchState == MatchState.Playing) {
            for (SpellcastClient c : clients.values()) {
                if (lastSeen != null) { // more than one client still playing
                    return null;
                }
                if (c.getState() == SpellcastClient.ClientState.Playing) {
                    lastSeen = c;
                }
            }
        }
        if (lastSeen != null) { //only one client left playing
            return lastSeen;
        }
        return null;
    }
    private boolean isAllClientsAnswered() {
        for (SpellcastClient c : clients.values()) {
            if (c.hasUnansweredQuestions()) {
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
            if (!c.isReady()) {
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

        currentMatchState = MatchState.Playing;
        currentMatchId = generateMatchId();
        currentRoundNumber = 0;
        broadcast("250 "+currentMatchId+ " :Match start");
        for (SpellcastClient c : clients.values()) {
            c.resetHistory();
            c.setHitpoints(15);
            c.setMaxHitpoints(15);
            c.setState(SpellcastClient.ClientState.Playing);
        }
        broadcast_stats();
        startNewRound();
    }

    private void startNewRound() {
        currentRoundNumber += 1;
        currentRoundState = RoundState.WaitingForGestures;
        broadcast("251 "+currentMatchId+"."+currentRoundNumber+" :Round start");
        for (SpellcastClient client : clients.values()) {
            client.setReady(false);
            client.resetQuestions();
            ArrayList<String> expireEffects = client.expireEffects(currentMatchId, currentRoundNumber);
            for (String expireMsg : expireEffects) {
                broadcast("355 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" :"+expireMsg);
            }
            if (client.canGestureThisRound(currentRoundNumber)) {
                broadcast("320 " + currentMatchId + "." + currentRoundNumber + " " + client.getNickname() + " :What are your gestures");
            }
        }
    }
    private void startRoundQuestions() {

        broadcast("330 Gestures:");
        for (SpellcastClient client : clients.values()) {
            broadcast("331 "+currentMatchId+"."+currentRoundNumber+" "+client.getNickname()+" "+client.getLeftGesture()+" "+client.getRightGesture());
        }
        broadcast("332 End of Gestures");

        boolean anyQuestions = false;

        // ask each client their questions
        for (SpellcastClient client : clients.values()) {
            client.setReady(false);
            client.performGestures();
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

    private void resolveRound() {

        for (SpellcastClient client : clients.values()) {
            resolveSpells(client, client.getLeftSpellQuestions(), "left");
            resolveSpells(client, client.getRightSpellQuestions(), "right");
        }

	    // TODO: resolve death effects

        for (SpellcastClient client : clients.values()) {
            resolveStabs(client, client.getLeftSpellQuestions(), "left");
            resolveStabs(client, client.getRightSpellQuestions(), "right");
        }

        broadcast("350 "+currentMatchId+"."+currentRoundNumber+" :Round complete");
        broadcast_stats();

        startNewRound();
    }

    private void resolveSpells(SpellcastClient client, ArrayList<SpellQuestion> questions, String hand) {
        for (SpellQuestion q : questions) {
            Spell spell = q.getSpell();
            Target target = getTargetByNickname(q.getTarget());
            if (!spell.getSlug().equals("stab")) {
                broadcast("351 "+client.getNickname()+" CASTS "+spell.getSlug()+" AT "+target.getNickname()+" WITH "+hand);
                spell.fireSpell(this, client, target);
            }
        }
    }
    private void resolveStabs(SpellcastClient client, ArrayList<SpellQuestion> questions, String hand) {
        for (SpellQuestion q : questions) {
            Spell spell = q.getSpell();
            Target target = getTargetByNickname(q.getTarget());
            if (spell.getSlug().equals("stab")) {
                broadcast("352 " + client.getNickname() + " STABS " + target.getNickname() + " WITH " + hand);

                if (target.hasEffect(ShieldEffect.Name)) {
                    broadcast("353 "+target.getNickname()+" BLOCKS "+client.getNickname()+" :the attack is blocked by a shield around "+target.getVisibleName());
                } else {
                    target.takeDamage(1);
                    if (target.isDead()) {
                        broadcast("380 "+target.getNickname()+" :"+target.getVisibleName()+" dies");
                    }
                }
            }
        }

    }



    private void declareWinner(SpellcastClient winner) {
        broadcast("390 "+currentMatchId+" "+winner.getNickname()+" :Wins the match!");
        currentMatchState = MatchState.WaitingForPlayers;
        for (SpellcastClient client : clients.values()) {
            if (client.getState() == SpellcastClient.ClientState.Playing) {
                client.setReady(false);
                client.setHitpoints(0);
                client.setMaxHitpoints(0);
                client.resetHistory();
                client.resetQuestions();
                client.setState(SpellcastClient.ClientState.Identified);
            }
        }
        broadcast_stats();
    }

    private boolean askClientQuestions(SpellcastClient client) {
        if (client.hasUnansweredQuestions()) {
            sendToClient(client, "340 "+currentMatchId+"."+currentRoundNumber+" :Questions");
            questions(client, client.getLeftSpellQuestions(), "left");
            questions(client, client.getRightSpellQuestions(), "right");
            //TODO: ask for targets of monsters
            sendToClient(client, "349 :End of Questions");
            return true;
        }
        return false;
    }

    public void questions(SpellcastClient client, ArrayList<SpellQuestion> spellQuestions, String hand) {
        int left = spellQuestions.size();
        if (left > 1) {
            sendToClient(client, "341 "+hand+" :Cast which spell with "+hand+" hand");
            for (SpellQuestion q : spellQuestions) {
                sendToClient(client, "342 "+hand+" " + q.getSpell().getSlug() + " :" + q.getSpell().getName());
            }
            sendToClient(client, "343 End of spells");
        } else if (left > 0) { // one spell to resolve, needs a target
            SpellQuestion q = spellQuestions.get(0);
            if (!q.hasTarget()) {
                sendToClient(client, "345 "+hand+" "+ q.getSpell().getSlug() + " :Which target for "+hand+" hand");
                for (SpellcastClient t : clients.values()) {
                   sendToClient(client, "346 "+hand+" "+t.getNickname()+" :"+t.getVisibleName());
                }
                sendToClient(client, "347 End of targets");
            }
        }


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
        for (SpellcastClient c : clients.values()) {
            wizards(c);
            monsters(c);
        }
    }
    public void wizards(SpellcastClient client) {
        sendToClient(client, "300 Wizards:");
        for (SpellcastClient c : clients.values()) {
            sendToClient(client, c.get301());
        }
        sendToClient(client, "302 End of Wizards");
    }

    public void monsters(SpellcastClient client) {
        sendToClient(client, "310 Monsters:");
        for (SpellcastClient c : clients.values()) {
            for (Monster monster : c.getMonsters()) {
                sendToClient(client, monster.get311());
            }
        }
        sendToClient(client, "312 End of Monsters");

    }


    public void error_nickname_used(SpellcastClient client) {
        sendToClient(client, "400 Nickname in use");
    }
    public void error_invalid_nickname(SpellcastClient client) {
        sendToClient(client, "401 Invalid nickname");
    }
    public void error_invalid_gesture(SpellcastClient client) {
        sendToClient(client, "402 Invalid gesture");
    }
    public void error_invalid_answer(SpellcastClient client) {
        sendToClient(client, "403 Invalid answer");
    }
}
