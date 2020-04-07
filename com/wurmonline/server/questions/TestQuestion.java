// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.items.WurmColor;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.behaviours.MethodsItems;
import java.util.logging.Level;
import com.wurmonline.server.players.PlayerJournal;
import com.wurmonline.server.players.JournalTier;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.deities.Deities;
import java.io.IOException;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class TestQuestion extends Question implements TimeConstants
{
    private static final Logger logger;
    private static final ConcurrentHashMap<Long, Long> armourCreators;
    
    public TestQuestion(final Creature aResponder, final long aTarget) {
        super(aResponder, "Testing", "What do you want to do?", 96, aTarget);
    }
    
    public boolean checkIfMayCreateArmour() {
        if (this.getResponder().getPower() > 0) {
            return true;
        }
        Long last = TestQuestion.armourCreators.get(this.getResponder().getWurmId());
        if (last != null && System.currentTimeMillis() - last < 300000L) {
            return false;
        }
        last = new Long(System.currentTimeMillis());
        TestQuestion.armourCreators.put(this.getResponder().getWurmId(), last);
        return true;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        if (Servers.localServer.testServer) {
            this.getResponder().getBody().healFully();
            this.getResponder().getStatus().modifyStamina2(100.0f);
            final String priestTypeString = aAnswers.getProperty("priestType");
            final String faithLevelString = aAnswers.getProperty("faithLevel");
            if (priestTypeString != null) {
                final int priestType = Integer.parseInt(priestTypeString);
                switch (priestType) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        if (this.getResponder().getDeity() != null) {
                            try {
                                this.getResponder().setFaith(0.0f);
                                this.getResponder().setFavor(0.0f);
                                this.getResponder().setDeity(null);
                                this.getResponder().getCommunicator().sendNormalServerMessage("You follow no deity.");
                            }
                            catch (IOException e) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Could not remove deity.");
                            }
                            break;
                        }
                        break;
                    }
                    default: {
                        int count = 2;
                        for (final Deity d : Deities.getDeities()) {
                            if (count == priestType) {
                                try {
                                    this.getResponder().setDeity(d);
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You are now a follower of " + d.getName() + ".");
                                }
                                catch (IOException e2) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("Could not set deity.");
                                }
                            }
                            ++count;
                        }
                        break;
                    }
                }
            }
            if (faithLevelString != null) {
                int faithLevel = Integer.parseInt(faithLevelString);
                if (faithLevel > 0) {
                    faithLevel = Math.min(100, faithLevel);
                    if (this.getResponder().getDeity() != null) {
                        try {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Faith set to " + faithLevel + ".");
                            if (faithLevel >= 30 && !this.getResponder().isPriest()) {
                                this.getResponder().setPriest(true);
                                this.getResponder().getCommunicator().sendNormalServerMessage("You are now a priest of " + this.getResponder().getDeity().getName() + ".");
                                if (this.getResponder().isPlayer()) {
                                    PlayerJournal.sendTierUnlock((Player)this.getResponder(), PlayerJournal.getAllTiers().get((byte)10));
                                }
                            }
                            else if (faithLevel < 30 && this.getResponder().isPriest()) {
                                this.getResponder().setPriest(false);
                                this.getResponder().getCommunicator().sendNormalServerMessage("You are no longer a priest of " + this.getResponder().getDeity().getName() + ".");
                            }
                            this.getResponder().setFaith(faithLevel);
                        }
                        catch (IOException e) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Could not set faith.");
                        }
                    }
                }
            }
            final String skillLevel = aAnswers.getProperty("skillLevel");
            if (skillLevel != null) {
                try {
                    double slevel = Double.parseDouble(skillLevel);
                    slevel = Math.min(slevel, 90.0);
                    if (slevel > 0.0) {
                        final Skills s = this.getResponder().getSkills();
                        if (s != null) {
                            final Skill[] skills3;
                            final Skill[] skills = skills3 = s.getSkills();
                            for (final Skill sk : skills3) {
                                if (sk.getType() != 0 && sk.getType() != 1) {
                                    sk.setKnowledge(slevel, false);
                                }
                            }
                        }
                    }
                }
                catch (Exception e3) {
                    if (TestQuestion.logger.isLoggable(Level.FINE)) {
                        TestQuestion.logger.fine("skill bug?");
                    }
                }
            }
            final String alignLevel = aAnswers.getProperty("alignmentLevel");
            if (alignLevel != null) {
                try {
                    float alignment = Float.parseFloat(alignLevel);
                    if (alignment != 0.0f) {
                        if (alignment > 99.0f) {
                            alignment = 99.0f;
                        }
                        if (alignment < -99.0f) {
                            alignment = -99.0f;
                        }
                        this.getResponder().setAlignment(alignment);
                    }
                }
                catch (Exception e4) {
                    if (TestQuestion.logger.isLoggable(Level.FINE)) {
                        TestQuestion.logger.fine("alignment update issue");
                    }
                }
            }
            final String charLevel = aAnswers.getProperty("characteristicsLevel");
            if (charLevel != null) {
                try {
                    double slevel2 = Double.parseDouble(charLevel);
                    slevel2 = Math.min(slevel2, 90.0);
                    if (slevel2 > 0.0) {
                        final Skills s2 = this.getResponder().getSkills();
                        if (s2 != null) {
                            final Skill[] skills4;
                            final Skill[] skills2 = skills4 = s2.getSkills();
                            for (final Skill sk2 : skills4) {
                                if (sk2.getType() == 0 || sk2.getType() == 1) {
                                    sk2.setKnowledge(slevel2, false);
                                }
                            }
                        }
                    }
                }
                catch (Exception e5) {
                    if (TestQuestion.logger.isLoggable(Level.FINE)) {
                        TestQuestion.logger.fine("skill bug?");
                    }
                }
            }
            final String itemtype = aAnswers.getProperty("itemtype");
            if (itemtype != null) {
                final String quantity = aAnswers.getProperty("quantity");
                int qty = 0;
                try {
                    qty = Integer.parseInt(quantity);
                }
                catch (NumberFormatException nfs) {
                    qty = 0;
                }
                if (qty < 0) {
                    qty = 0;
                }
                final String materialType = aAnswers.getProperty("materialtype");
                byte matType = -1;
                try {
                    matType = Byte.parseByte(materialType);
                }
                catch (NumberFormatException nfs2) {
                    matType = -1;
                }
                if (matType < 0) {
                    matType = -1;
                }
                if (matType > 0) {
                    --matType;
                    if (matType < MethodsItems.getAllNormalWoodTypes().length) {
                        matType = MethodsItems.getAllNormalWoodTypes()[matType];
                    }
                    else {
                        matType -= (byte)MethodsItems.getAllNormalWoodTypes().length;
                        if (matType > MethodsItems.getAllMetalTypes().length) {
                            matType = -1;
                        }
                        else {
                            matType = MethodsItems.getAllMetalTypes()[matType];
                        }
                    }
                }
                else {
                    matType = -1;
                }
                final String qualityLevel = aAnswers.getProperty("qualitylevel");
                if (qualityLevel != null) {
                    try {
                        int ql = Integer.parseInt(qualityLevel);
                        if (ql > 0) {
                            ql = Math.min(ql, 90);
                            try {
                                int num = Integer.parseInt(itemtype);
                                if (num == 0) {
                                    return;
                                }
                                --num;
                                if (num <= 6 && !this.checkIfMayCreateArmour()) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You may only create items every 5 minutes in order to save the database.");
                                }
                                switch (num) {
                                    case 0: {
                                        createAndInsertItems(this.getResponder(), 109, 114, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 109, 109, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 114, 114, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 111, 111, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 779, 779, ql, false, matType);
                                        break;
                                    }
                                    case 1: {
                                        createAndInsertItems(this.getResponder(), 103, 108, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 103, 103, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 105, 105, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 106, 106, ql, false, matType);
                                        break;
                                    }
                                    case 2: {
                                        createAndInsertItems(this.getResponder(), 115, 120, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 119, 119, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 116, 116, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 115, 115, ql, false, matType);
                                        break;
                                    }
                                    case 3: {
                                        createAndInsertItems(this.getResponder(), 274, 279, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 278, 278, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 274, 274, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 277, 277, ql, false, matType);
                                        break;
                                    }
                                    case 4: {
                                        createAndInsertItems(this.getResponder(), 280, 287, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 284, 284, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 280, 280, ql, false, matType);
                                        createAndInsertItems(this.getResponder(), 283, 283, ql, false, matType);
                                        break;
                                    }
                                    case 5: {
                                        final int drakeColor = this.getRandomDragonColor();
                                        createAndInsertItems(this.getResponder(), 468, 473, ql, drakeColor, false);
                                        createAndInsertItems(this.getResponder(), 472, 472, ql, drakeColor, false);
                                        createAndInsertItems(this.getResponder(), 469, 469, ql, drakeColor, false);
                                        createAndInsertItems(this.getResponder(), 468, 468, ql, drakeColor, false);
                                        break;
                                    }
                                    case 6: {
                                        final int scaleColor = this.getRandomDragonColor();
                                        createAndInsertItems(this.getResponder(), 474, 478, ql, scaleColor, false);
                                        createAndInsertItems(this.getResponder(), 478, 478, ql, scaleColor, false);
                                        createAndInsertItems(this.getResponder(), 474, 474, ql, scaleColor, false);
                                        createAndInsertItems(this.getResponder(), 477, 477, ql, scaleColor, false);
                                        break;
                                    }
                                    case 7: {
                                        createAndInsertItems(this.getResponder(), 80, 80, ql, false, matType);
                                        break;
                                    }
                                    case 8: {
                                        createAndInsertItems(this.getResponder(), 21, 21, ql, false, matType);
                                        break;
                                    }
                                    case 9: {
                                        createAndInsertItems(this.getResponder(), 81, 81, ql, false, matType);
                                        break;
                                    }
                                    case 10: {
                                        createAndInsertItems(this.getResponder(), 291, 291, ql, false, matType);
                                        break;
                                    }
                                    case 11: {
                                        createAndInsertItems(this.getResponder(), 292, 292, ql, false, matType);
                                        break;
                                    }
                                    case 12: {
                                        createAndInsertItems(this.getResponder(), 290, 290, ql, false, matType);
                                        break;
                                    }
                                    case 13: {
                                        createAndInsertItems(this.getResponder(), 3, 3, ql, false, matType);
                                        break;
                                    }
                                    case 14: {
                                        createAndInsertItems(this.getResponder(), 90, 90, ql, false, matType);
                                        break;
                                    }
                                    case 15: {
                                        createAndInsertItems(this.getResponder(), 87, 87, ql, false, matType);
                                        break;
                                    }
                                    case 16: {
                                        createAndInsertItems(this.getResponder(), 706, 706, ql, false, matType);
                                        break;
                                    }
                                    case 17: {
                                        createAndInsertItems(this.getResponder(), 705, 705, ql, false, matType);
                                        break;
                                    }
                                    case 18: {
                                        createAndInsertItems(this.getResponder(), 707, 707, ql, false, matType);
                                        break;
                                    }
                                    case 19: {
                                        createAndInsertItems(this.getResponder(), 86, 86, ql, false, matType);
                                        break;
                                    }
                                    case 20: {
                                        createAndInsertItems(this.getResponder(), 4, 4, ql, false, matType);
                                        break;
                                    }
                                    case 21: {
                                        createAndInsertItems(this.getResponder(), 85, 85, ql, false, matType);
                                        break;
                                    }
                                    case 22: {
                                        createAndInsertItems(this.getResponder(), 82, 82, ql, false, matType);
                                        break;
                                    }
                                    case 23: {
                                        this.createMultiple(this.getResponder(), 25, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 20, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 24, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 480, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 8, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 143, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 7, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 62, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 63, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 493, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 97, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 313, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 296, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 388, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 421, 1, ql, matType);
                                        break;
                                    }
                                    case 24: {
                                        final ItemTemplate[] templates;
                                        final ItemTemplate[] itemtemps = templates = ItemTemplateFactory.getInstance().getTemplates();
                                        for (final ItemTemplate temp : templates) {
                                            if (temp.isCombine() && !temp.isFood() && temp.getTemplateId() != 683 && temp.getTemplateId() != 737 && (temp.getDecayTime() == 86401L || temp.getDecayTime() == 28800L || temp.destroyOnDecay) && !temp.getModelName().startsWith("model.resource.scrap.")) {
                                                for (int x = 0; x < 5; ++x) {
                                                    createAndInsertItems(this.getResponder(), temp.getTemplateId(), temp.getTemplateId(), 1 + Server.rand.nextInt(ql), 0, true, false, (byte)(-1));
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    case 25: {
                                        this.createOnGround(this.getResponder(), 132, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 26: {
                                        this.createOnGround(this.getResponder(), 492, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 27: {
                                        this.createOnGround(this.getResponder(), 146, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 28: {
                                        this.createOnGround(this.getResponder(), 860, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 29: {
                                        this.createOnGround(this.getResponder(), 188, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 30: {
                                        this.createOnGround(this.getResponder(), 217, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 31: {
                                        this.createOnGround(this.getResponder(), 218, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 32: {
                                        this.createOnGround(this.getResponder(), 22, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 33: {
                                        this.createOnGround(this.getResponder(), 23, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 34: {
                                        this.createOnGround(this.getResponder(), 9, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 35: {
                                        this.createOnGround(this.getResponder(), 557, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 36: {
                                        this.createOnGround(this.getResponder(), 558, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 37: {
                                        this.createOnGround(this.getResponder(), 559, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 38: {
                                        this.createOnGround(this.getResponder(), 319, (qty == 0) ? 4 : qty, ql, matType);
                                        break;
                                    }
                                    case 39: {
                                        this.createOnGround(this.getResponder(), 786, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 40: {
                                        this.createOnGround(this.getResponder(), 785, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 41: {
                                        this.createOnGround(this.getResponder(), 26, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 42: {
                                        this.createOnGround(this.getResponder(), 130, (qty == 0) ? 10 : qty, ql, matType);
                                        break;
                                    }
                                    case 43: {
                                        this.createMultiple(this.getResponder(), 903, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 901, 1, ql, matType);
                                        break;
                                    }
                                    case 44: {
                                        this.createMultiple(this.getResponder(), 711, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 213, 4, ql, matType);
                                        this.createMultiple(this.getResponder(), 439, 8, ql, matType);
                                        break;
                                    }
                                    case 45: {
                                        this.createMultiple(this.getResponder(), 221, 5, ql, matType);
                                        this.createMultiple(this.getResponder(), 223, 5, ql, matType);
                                        this.createMultiple(this.getResponder(), 480, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 23, 3, ql, matType);
                                        this.createMultiple(this.getResponder(), 64, 1, ql, matType);
                                        break;
                                    }
                                    case 46: {
                                        if (matType != 8 || matType != 7) {
                                            matType = 8;
                                        }
                                        this.createMultiple(this.getResponder(), 505, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 507, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 508, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 506, 1, ql, matType);
                                        break;
                                    }
                                    case 47: {
                                        this.createMultiple(this.getResponder(), 376, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 374, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 380, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 382, 1, ql, matType);
                                        this.createMultiple(this.getResponder(), 378, 1, ql, matType);
                                        break;
                                    }
                                }
                            }
                            catch (NumberFormatException nfs3) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Error: input was " + itemtype + " - failed to parse.");
                            }
                        }
                        else {
                            this.getResponder().getCommunicator().sendNormalServerMessage("No quality level selected so not creating.");
                        }
                    }
                    catch (NumberFormatException nfs4) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Error: input was " + itemtype + " - failed to parse.");
                    }
                }
            }
        }
    }
    
    private void createOnGround(final Creature receiver, final int itemTemplate, final int howMany, final float qualityLevel, final byte materialType) {
        for (int x = 0; x < howMany; ++x) {
            createAndInsertItems(receiver, itemTemplate, itemTemplate, qualityLevel, false, materialType);
        }
    }
    
    private void createMultiple(final Creature receiver, final int itemTemplate, final int howMany, final float qualityLevel, final byte materialType) {
        for (int x = 0; x < howMany; ++x) {
            createAndInsertItems(receiver, itemTemplate, itemTemplate, qualityLevel, false, materialType);
        }
    }
    
    public static final void createAndInsertItems(final Creature receiver, final int itemStart, final int itemEnd, final float qualityLevel, final boolean newbieItem, final byte materialType) {
        createAndInsertItems(receiver, itemStart, itemEnd, qualityLevel, 0, false, newbieItem, materialType);
    }
    
    public static final void createAndInsertItems(final Creature receiver, final int itemStart, final int itemEnd, final float qualityLevel, final int color, final boolean newbieItem) {
        createAndInsertItems(receiver, itemStart, itemEnd, qualityLevel, color, false, newbieItem, (byte)(-1));
    }
    
    private static final void createAndInsertItems(final Creature receiver, final int itemStart, final int itemEnd, final float qualityLevel, final int color, final boolean onGround, final boolean newbieItem, final byte material) {
        if (itemStart > itemEnd) {
            receiver.getCommunicator().sendNormalServerMessage("Error: Bugged test case.");
            return;
        }
        for (int x = itemStart; x <= itemEnd; ++x) {
            if (x != 110) {
                if (onGround) {
                    try {
                        ItemFactory.createItem(x, qualityLevel, receiver.getPosX(), receiver.getPosY(), Server.rand.nextFloat() * 180.0f, receiver.isOnSurface(), (byte)0, -10L, receiver.getName());
                    }
                    catch (Exception ex) {
                        receiver.getCommunicator().sendAlertServerMessage(ex.getMessage());
                    }
                }
                else {
                    try {
                        final Item i = ItemFactory.createItem(x, qualityLevel, receiver.getName());
                        if (newbieItem) {
                            i.setAuxData((byte)1);
                        }
                        if (i.isGem()) {
                            i.setData1((qualityLevel <= 0.0f) ? 0 : ((int)(qualityLevel * 2.0f)));
                            i.setDescription("v");
                        }
                        if (i.isDragonArmour()) {
                            i.setMaterial((byte)16);
                            i.setColor(color);
                            final String dName = i.getDragonColorNameByColor(color);
                            if (dName != "") {
                                i.setName(dName + " " + i.getName());
                            }
                        }
                        if (material != -1) {
                            i.setMaterial(material);
                        }
                        receiver.getInventory().insertItem(i);
                    }
                    catch (Exception ex) {
                        receiver.getCommunicator().sendAlertServerMessage(ex.getMessage());
                    }
                }
            }
        }
        receiver.wearItems();
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{text='Create an armour set or a weapon and set skills:'}");
        buf.append("harray{label{text='Item'}dropdown{id='itemtype';options=\"");
        buf.append("Nothing,");
        buf.append("Cloth,");
        buf.append("Leather,");
        buf.append("Studded,");
        buf.append("Chain,");
        buf.append("Plate,");
        buf.append("Drake (random color),");
        buf.append("Dragon Scale (random color),");
        buf.append("Shortsword,");
        buf.append("Longsword,");
        buf.append("Twohanded sword,");
        buf.append("Small maul,");
        buf.append("Med maul,");
        buf.append("Large maul,");
        buf.append("Small axe,");
        buf.append("Large axe,");
        buf.append("Twohanded axe,");
        buf.append("Halberd,");
        buf.append("Long spear,");
        buf.append("Steel spear,");
        buf.append("Large Metal Shield,");
        buf.append("Medium Metal Shield,");
        buf.append("Large Wooden Shield,");
        buf.append("Small Wooden Shield,");
        buf.append("Basic Tools,");
        buf.append("Raw materials,");
        buf.append("#10 Stone Bricks,");
        buf.append("#10 Mortar,");
        buf.append("#10 Rock Shards,");
        buf.append("#4 Wood Beams,");
        buf.append("#10 Iron Ribbons,");
        buf.append("#10 Large Nails,");
        buf.append("#10 Small Nails,");
        buf.append("#10 Planks,");
        buf.append("#10 Shafts,");
        buf.append("#4 Logs,");
        buf.append("#4 Thick Ropes,");
        buf.append("#4 Mooring Ropes,");
        buf.append("#4 Cordage Ropes,");
        buf.append("#4 Normal Ropes,");
        buf.append("#10 Marble Bricks,");
        buf.append("#10 Marble Shards,");
        buf.append("#10 Dirt,");
        buf.append("#10 Clay,");
        buf.append("Bridge Tools,");
        buf.append("Make your own RangePole,");
        buf.append("Make your own Dioptra,");
        buf.append("Statuette Set,");
        buf.append("Vesseled Gems Set,");
        buf.append("\";default=\"0\"}}");
        buf.append("text{text='Select material:'}");
        buf.append("harray{label{text='Material'}dropdown{id='materialtype';options=\"");
        buf.append("Standard.,");
        for (final byte material : MethodsItems.getAllNormalWoodTypes()) {
            buf.append(StringUtilities.raiseFirstLetter(Item.getMaterialString(material)) + ",");
        }
        for (final byte material : MethodsItems.getAllMetalTypes()) {
            buf.append(StringUtilities.raiseFirstLetter(Item.getMaterialString(material)) + ",");
        }
        buf.append("\";default=\"0\"}}");
        buf.append("harray{label{text='Item qualitylevel (Max 90)'};input{maxchars='2'; id='qualitylevel'; text='50'}}");
        buf.append("harray{label{text='Set skills to (Max 90, 0=no change)'};input{maxchars='2'; id='skillLevel'; text='0'}}");
        buf.append("harray{label{text='Set characteristics to (Max 90, 0=no change)'};input{maxchars='2'; id='characteristicsLevel'; text='0'}}");
        buf.append("harray{label{text='Set Alignment to (Max 99, Min -99, 0=no change)'};input{maxchars='3'; id='alignmentLevel'; text='0'}}");
        buf.append("harray{label{text='Item quantity (0..99, 0 = use default)'};input{maxchars='2'; id='quantity'; text='0'}}");
        buf.append("text{text='Quantity is only used for items with a # before their name, if 0 then the default number after the # is used.'};");
        buf.append("text{text='Set Deity:'}");
        buf.append("harray{label{text='Deity'}dropdown{id='priestType';options=\"");
        buf.append("No Change,");
        buf.append("No Deity,");
        for (final Deity d : Deities.getDeities()) {
            buf.append(d.getName() + ",");
        }
        buf.append("\";default=\"0\"}}");
        buf.append("harray{label{text='Faith (Max 100, 0=no change)'};input{maxchars='3'; id='faithLevel'; text='0'}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    final int getRandomDragonColor() {
        final int c = Server.rand.nextInt(5);
        switch (c) {
            case 0: {
                return WurmColor.createColor(215, 40, 40);
            }
            case 1: {
                return WurmColor.createColor(10, 10, 10);
            }
            case 2: {
                return WurmColor.createColor(10, 210, 10);
            }
            case 3: {
                return WurmColor.createColor(255, 255, 255);
            }
            case 4: {
                return WurmColor.createColor(40, 40, 215);
            }
            default: {
                return WurmColor.createColor(100, 100, 100);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(TestQuestion.class.getName());
        armourCreators = new ConcurrentHashMap<Long, Long>();
    }
}
