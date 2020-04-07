// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.shared.util.StringUtilities;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class SummonSoulQuestion extends Question
{
    private boolean properlySent;
    private static final Logger logger;
    
    public SummonSoulQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 79, aTarget);
        this.properlySent = false;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        if (!this.properlySent) {
            return;
        }
        final String name = aAnswers.getProperty("name");
        Creature soul = null;
        if (name != null && name.length() > 1) {
            soul = acquireSoul(StringUtilities.raiseFirstLetter(name));
        }
        if (soul == null || soul.getPower() > this.getResponder().getPower()) {
            this.getResponder().getCommunicator().sendNormalServerMessage("No such soul found.");
        }
        else {
            final SummonSoulAcceptQuestion ssaq = new SummonSoulAcceptQuestion(soul, "Accept Summon?", "Would you like to accept a summon from " + this.getResponder().getName() + "?", this.getResponder().getWurmId(), this.getResponder());
            ssaq.sendQuestion();
        }
    }
    
    private static Creature acquireSoul(final String name) {
        final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
        if (pinf != null && pinf.loaded) {
            try {
                return Server.getInstance().getCreature(pinf.wurmId);
            }
            catch (NoSuchPlayerException | NoSuchCreatureException ex3) {
                final WurmServerException ex2;
                final WurmServerException ex = ex2;
                SummonSoulQuestion.logger.log(Level.WARNING, ex.getMessage());
            }
        }
        return null;
    }
    
    @Override
    public void sendQuestion() {
        this.properlySent = true;
        final String sb = this.getBmlHeader() + "text{text='Which soul do you wish to summon?'};label{text='Name:'};input{id='name';maxchars='40';text=\"\"};" + this.createAnswerButton2();
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, sb, 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(SummonSoulQuestion.class.getName());
    }
}
