// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class FriendQuestion extends Question
{
    private static final Logger logger;
    
    public FriendQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 25, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseFriendQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Player sender = Players.getInstance().getPlayer(this.target);
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            buf.append("text{text='" + sender.getName() + " wants to add you to " + sender.getHisHerItsString() + " friends list.'}");
            buf.append("text{text='This will mean " + sender.getHeSheItString() + " will see you log on and off, and be able to allow you into structures " + sender.getHeSheItString() + " controls.'}");
            buf.append("text{text='Do you accept?'}");
            buf.append("radio{ group='join'; id='accept';text='Accept'}");
            buf.append("radio{ group='join'; id='decline';text='Decline';selected='true'}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (NoSuchPlayerException nsp) {
            FriendQuestion.logger.log(Level.WARNING, "Player with id " + this.target + " trying to send a question, but cant be found?", nsp);
        }
    }
    
    static {
        logger = Logger.getLogger(FriendQuestion.class.getName());
    }
}
