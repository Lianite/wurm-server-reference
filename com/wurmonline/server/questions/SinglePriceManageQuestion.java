// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.economy.Change;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;

public final class SinglePriceManageQuestion extends Question
{
    private static final Logger logger;
    private final Item[] items;
    
    public SinglePriceManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 23, aTarget);
        this.items = new Item[0];
    }
    
    public SinglePriceManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final Item[] aTargets) {
        super(aResponder, aTitle, aQuestion, 23, -10L);
        this.items = aTargets;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseSinglePriceQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        if (this.target == -10L) {
            final StringBuilder buf = new StringBuilder(this.getBmlHeader());
            buf.append("header{text=\"Price for multiple items\"}");
            buf.append("table{rows=\"2\";cols=\"8\";");
            buf.append("label{text=\" \"};label{text='Gold'};label{text=\" \"};label{text='Silver'};label{text=\" \"};label{text='Copper'};label{text=\" \"};label{text='Iron'};");
            buf.append("label{text=\" \"};input{maxchars=\"2\";id=\"gold\";text=\"0\"};label{text=\" \"};input{maxchars=\"2\";id=\"silver\";text=\"0\"};label{text=\" \"};input{maxchars=\"2\";id=\"copper\";text=\"0\"};label{text=\" \"};input{maxchars=\"2\";id=\"iron\";;text=\"0\"}");
            buf.append("}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(400, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        else {
            try {
                final Item item = Items.getItem(this.target);
                if (item.getOwnerId() == this.getResponder().getWurmId()) {
                    final StringBuilder buf2 = new StringBuilder(this.getBmlHeader());
                    buf2.append("header{text=\"Price for " + item.getName().replace("\"", "''") + "\"}");
                    buf2.append("table{rows=\"2\";cols=\"5\";");
                    buf2.append("label{text=\"Item name\"};label{text='Gold'};label{text='Silver'};label{text='Copper'};label{text='Iron'};");
                    final long wid = item.getWurmId();
                    final Change change = Economy.getEconomy().getChangeFor(item.getPrice());
                    buf2.append("label{text=\"" + item.getName().replace("\"", "''") + "\"};harray{label{text=\" \"};input{maxchars=\"3\";id=\"" + wid + "gold\";text=\"" + change.getGoldCoins() + "\"};};harray{label{text=\" \"};input{maxchars=\"3\";id=\"" + wid + "silver\";text=\"" + change.getSilverCoins() + "\"};};harray{label{text=\" \"};input{maxchars=\"3\";id=\"" + wid + "copper\";text=\"" + change.getCopperCoins() + "\"};};harray{label{text=\" \"};input{maxchars=\"3\";id=\"" + wid + "iron\";text=\"" + change.getIronCoins() + "\"};};");
                    buf2.append("}");
                    buf2.append(this.createAnswerButton2());
                    this.getResponder().getCommunicator().sendBml(400, 300, true, true, buf2.toString(), 200, 200, 200, this.title);
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You don't own that item.");
                }
            }
            catch (NoSuchItemException nsc) {
                this.getResponder().getCommunicator().sendNormalServerMessage("No such item.");
                SinglePriceManageQuestion.logger.log(Level.WARNING, this.getResponder().getName(), nsc);
            }
        }
    }
    
    Item[] getItems() {
        return this.items;
    }
    
    static {
        logger = Logger.getLogger(SinglePriceManageQuestion.class.getName());
    }
}
