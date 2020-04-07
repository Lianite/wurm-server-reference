// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.sql.PreparedStatement;
import java.sql.Connection;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.Mailer;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.Random;
import java.util.logging.Logger;

public class WishQuestion extends Question
{
    private static final Logger logger;
    private final long coinId;
    private static final String RESPONSE1 = ". Will the gods listen?";
    private static final String RESPONSE2 = ". Do you consider yourself lucky?";
    private static final String RESPONSE3 = ". Is this your turn?";
    private static final String RESPONSE4 = ". You get the feeling that someone listens.";
    private static final String RESPONSE5 = ". Good luck!";
    private static final String RESPONSE6 = ". Will it come true?";
    private static final Random rand;
    private static final String INSERT_WISH = "INSERT INTO WISHES (PLAYER,WISH,COIN,TOFULFILL) VALUES(?,?,?,?)";
    
    public WishQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final long coin) {
        super(aResponder, aTitle, aQuestion, 77, aTarget);
        this.coinId = coin;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        Item coin = null;
        Item targetItem = null;
        try {
            targetItem = Items.getItem(this.target);
        }
        catch (NoSuchItemException nsi) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You fail to locate the target!");
            return;
        }
        try {
            coin = Items.getItem(this.coinId);
            if (coin.getOwner() != this.getResponder().getWurmId() || coin.isBanked() || coin.mailed) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You are no longer in possesion of the " + coin.getName() + "!");
                return;
            }
            final String key = "data1";
            final String val = aAnswers.getProperty("data1");
            if (val != null && val.length() > 0) {
                String tstring = ". Will the gods listen?";
                final int x = WishQuestion.rand.nextInt(6);
                if (x == 1) {
                    tstring = ". Do you consider yourself lucky?";
                }
                else if (x == 2) {
                    tstring = ". Is this your turn?";
                }
                else if (x == 3) {
                    tstring = ". You get the feeling that someone listens.";
                }
                else if (x == 4) {
                    tstring = ". Good luck!";
                }
                else if (x == 5) {
                    tstring = ". Will it come true?";
                }
                this.getResponder().getCommunicator().sendNormalServerMessage("You wish for " + val + tstring);
                final long moneyVal = Economy.getValueFor(coin.getTemplateId());
                final float chance = moneyVal / 3.0E7f;
                final float chantLevel = targetItem.getSpellCourierBonus();
                final float timeBonus = WurmCalendar.isNight() ? 1.05f : 1.0f;
                final float newchance = chance * (targetItem.getCurrentQualityLevel() / 100.0f) * (1.0f + chantLevel / 100.0f) * (1.0f + coin.getCurrentQualityLevel() / 1000.0f) * timeBonus;
                WishQuestion.logger.log(Level.INFO, "New chance=" + newchance + " after coin=" + chance + ", chant=" + chantLevel + " ql=" + targetItem.getCurrentQualityLevel());
                boolean toFulfill = WishQuestion.rand.nextFloat() < newchance;
                if (this.getResponder().getPower() >= 5) {
                    toFulfill = true;
                }
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getPlayerDbCon();
                    ps = dbcon.prepareStatement("INSERT INTO WISHES (PLAYER,WISH,COIN,TOFULFILL) VALUES(?,?,?,?)");
                    ps.setLong(1, this.getResponder().getWurmId());
                    ps.setString(2, val);
                    ps.setLong(3, moneyVal);
                    ps.setBoolean(4, toFulfill);
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    WishQuestion.logger.log(Level.WARNING, sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
                Items.destroyItem(coin.getWurmId());
                if (toFulfill) {
                    try {
                        Mailer.sendMail(WebInterfaceImpl.mailAccount, "rolf@wurmonline.com", this.getResponder().getName() + " made a wish!", this.getResponder().getName() + " wants the wish " + val + " to be fulfilled!");
                    }
                    catch (Exception ex) {
                        WishQuestion.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You make no wish this time.");
            }
        }
        catch (NoSuchItemException nsi) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You are no longer in possesion of the coin!");
        }
        catch (NotOwnedException no) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You are no longer in possesion of the coin!");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text='What is your wish?'};input{maxchars='40';id='data1'; text=''}}");
        buf.append("label{text=\"Just leave it blank if you don't want to lose your coin.\"}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(WishQuestion.class.getName());
        rand = new Random();
    }
}
