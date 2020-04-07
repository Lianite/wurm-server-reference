// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.spells.SpellEffect;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.shared.util.MaterialUtilities;
import java.util.Arrays;
import com.wurmonline.server.spells.Spells;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;

public class GmSetEnchants extends Question
{
    private static final Logger logger;
    private final Item item;
    private final Spell[] spells;
    
    public GmSetEnchants(final Creature aResponder, final Item aTarget) {
        super(aResponder, "Item Enchants", itemNameWithDescription(aTarget), 104, aTarget.getWurmId());
        this.item = aTarget;
        Arrays.sort(this.spells = Spells.getSpellsEnchantingItems());
    }
    
    private static String itemNameWithDescription(final Item litem) {
        final StringBuilder sb = new StringBuilder();
        final String name = (litem.getActualName().length() == 0) ? litem.getTemplate().getName() : litem.getActualName();
        MaterialUtilities.appendNameWithMaterialSuffix(sb, name, litem.getMaterial());
        if (litem.getDescription().length() > 0) {
            sb.append(" (" + litem.getDescription() + ")");
        }
        return "Enchants of " + sb.toString();
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            GmSetEnchants.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 104 && this.getResponder().getPower() >= 4) {
            boolean somethingChanged = false;
            final byte itemEnch = this.item.enchantment;
            for (int x = 0; x < this.spells.length; ++x) {
                final boolean newsel = Boolean.parseBoolean(aAnswer.getProperty("newsel" + x));
                int newpow = 50;
                try {
                    newpow = Math.min(Integer.parseInt(aAnswer.getProperty("newpow" + x)), (this.spells[x].getEnchantment() == 45) ? 10000 : 104);
                }
                catch (NumberFormatException ex) {}
                final byte ench = this.spells[x].getEnchantment();
                final SpellEffect eff = this.item.getSpellEffect(ench);
                boolean oldsel = false;
                int oldpow = 50;
                if (eff != null) {
                    oldsel = true;
                    oldpow = (int)eff.power;
                }
                else if (ench == itemEnch) {
                    oldsel = true;
                }
                if (newsel != oldsel || (oldsel && newpow != oldpow)) {
                    somethingChanged = true;
                    if (oldsel) {
                        if (this.spells[x].singleItemEnchant) {
                            this.item.enchant((byte)0);
                        }
                        else if (eff != null) {
                            this.item.getSpellEffects().removeSpellEffect(eff.type);
                        }
                    }
                    if (newsel) {
                        this.spells[x].castSpell(newpow, this.getResponder(), this.item);
                        GmSetEnchants.logger.log(Level.INFO, this.getResponder().getName() + " enchanting " + this.spells[x].getName() + " " + this.item.getName() + ", " + this.item.getWurmId() + ", " + newpow);
                        this.getResponder().getLogger().log(Level.INFO, " enchanting " + this.spells[x].getName() + " " + this.item.getName() + ", " + this.item.getWurmId() + ", " + newpow);
                    }
                }
            }
            if (somethingChanged) {
                final GmSetEnchants gt = new GmSetEnchants(this.getResponder(), this.item);
                gt.sendQuestion();
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 4) {
            final byte itemEnch = this.item.enchantment;
            buf.append("table{rows=\"" + this.spells.length + "\";cols=\"4\";label{text=\"\"};text{type=\"bold\";text=\"Power\"};text{type=\"bold\";text=\"Name\"};text{type=\"bold\";text=\"Description\"};");
            for (int x = 0; x < this.spells.length; ++x) {
                final byte ench = this.spells[x].getEnchantment();
                final SpellEffect eff = this.item.getSpellEffect(ench);
                boolean sel = false;
                String pow = "";
                if (eff != null) {
                    sel = true;
                    pow = String.valueOf((int)eff.power);
                }
                else if (ench == itemEnch) {
                    sel = true;
                }
                final int maxChars = (ench == 45) ? 5 : 3;
                buf.append("checkbox{id=\"newsel" + x + "\";selected=\"" + sel + "\"};" + (this.spells[x].singleItemEnchant ? "text{type=\"italic\";text=\"(none)\"};" : ("input{id=\"newpow" + x + "\";maxchars=\"" + maxChars + "\";text=\"" + pow + "\"};")) + "label{text=\"" + this.spells[x].getName() + "\"};label{text=\"" + this.spells[x].getDescription() + "\"};");
            }
            buf.append("}");
            buf.append("label{text=\"\"};");
            buf.append("text{type=\"bold\";text=\"--------------- Help -------------------\"}");
            buf.append("text{text=\"Can add or change or remove enchants to specific powers, it maybe necessary to remove an enchant before modifying its power. If the enchant requires a power, then if none is specified it will default to 50, also \"}");
            buf.append("text{text=\"Note: Checks to see if the item can have the enchantment are not performed.\"}");
            buf.append("text{text=\"If anything is changed, then once the change is applied it will show this screen again.\"}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(500, 500, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    static {
        logger = Logger.getLogger(GmSetEnchants.class.getName());
    }
}
