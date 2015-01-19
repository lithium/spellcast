

Line-based protocol.  Lines are terminated by "\r\n". Max message length of 256 characters.

Nicknames consist of [a-zA-Z _'0-9].  A maximum of 32 characters.  Nickname can not be changed once authenticated.



Authentication
----

*server welcomes a new client*

    222 SPELLCAST <servername> <version>

*client identifies itself*

    NAME nickname <male | female | none> :visible name

*server replies to identity*

    200 Hello <nickname>
    400 Nickname in use
    401 Invalid nickname

*server notifies of active match state and current round if any*

    210 <match-id> <roundNumber>


Game State
-----
*server sends game state*

    300 Wizards list:
    301 <nick> <HP> <gender> :visible name
    302 End of Wizards

    310 Monster list:
    311 <owner-nick>+<monster-id> <HP> :monster name
    312 End of Monsters

    315 Effects list:
    316 <target> <spell> :effect description
    317 end of effects


Chat
----
*client wants to change their visible name*

    NAME <gender> :wizard name
    -- server sends a new 301 to each other client

*client greets the room*

    SAY Hello everyone!

*server broadcasts to all clients (including sender)*

    201 <nickname> :Hello everyone!

*server notifies all clients when a wizard quits*

    303 <nick> :Quits


*client wants to start playing the next match*

    READY
    YIELD
    WATCHING

*server broadcasts 301 to indicate readiness*

    301 <nick> "+" <gender> :visible name
    301 <nick> "-" <gender> :visible name
    301 <nick> "*" <gender> :visible name



*when all players have indicated ready, server broadcasts*

    250 <match-id> :Match start
    252 <match-id> :Waiting for players
    253 <match-id> :Match in-progress



Round
----
*round starts, server asks each client who can move this round for gestures*

    251 <match-id>.<roundNumber> :Round start
    320 <match-id>.<roundNumber> <nickname> :What are your gestures

*client submits their moves for the round*

    GESTURE <left hand gesture> <right hand gesture>

*server notifies other clients wizard has gestures ready*

    321 <match-id>.<roundNumber> <nickname> :Gestures ready

*once all wizards are ready, server reveals gestures for the round to everyone*

    330 Gestures for round:
    331 <match-id>.<roundNumber> <nickname> <left gesture> <right gesture>
    332 End of gestures

*server then asks questions to each client*

    340 <match-id>.<roundNumber> Questions:

    341 <left | right> :Which spell to cast with hand
    342 <left | right> <spell> :Spell Name
    343 end of spell options

    345 <left | right> <spell | stab> :Which target to <stab | cast spell> at with hand
    346 <left | right> <target> :Visible Name
    347 end of target options

    349 end of questions

*client answers questions*

    ANSWER <left | right> <spell | target>
    ANSWER left <answer> right <answer>

*server broadcasts that wizard has answered questions*

    348 <match-id>.<roundNumber> <nickname> :Finished answering

*after all wizards have answered all their questions the round is resolved*

    350 <match-id>.<roundNumber> :Round complete

    351 <nickname> CASTS <spell> AT <target> WITH <left | right>
    352 <nickname> STABS <target> WITH <left | right>

    353 <target> BLOCKS <nickname> :the attack is blocked
    355 <match-id>.<roundNumber> <nickname> :effect expiration message
    -- server then re-sends game state, 301s, and 311s for all players, and the next round starts*


Winning
-----

*server notifies all clients of a death*

    380 <wizard> dies
    381 <monster-id> dies

*server notifies all clients of a winner*

    390 <match-id> <nickname> :wins the match!
    391 <match-id> :is a draw between:
    392 <nickname>
    393 end of tied winners
