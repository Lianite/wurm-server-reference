// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.io.IOException;
import com.wurmonline.server.behaviours.CargoTransportationMethods;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.behaviours.CreatureBehaviour;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Server;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

class ChickenCoops implements TimeConstants, MiscConstants
{
    private static final Logger logger;
    private static int cretCount;
    
    static void poll(final Item theItem) {
        getCreatureCountAndContinue(theItem);
    }
    
    private static void getCreatureCountAndContinue(final Item theItem) {
        if (theItem.getTemplateId() == 1436) {
            try {
                final long delay = System.currentTimeMillis() - 3600000L;
                if (delay > theItem.getParent().getData()) {
                    if (theItem.getParent().getDamage() >= 80.0f) {
                        for (final Item item : theItem.getParent().getAllItems(true)) {
                            if (item.getTemplateId() == 1436) {
                                for (final Item chickens : item.getAllItems(true)) {
                                    unload(chickens);
                                }
                            }
                        }
                    }
                    ChickenCoops.cretCount = theItem.getAllItems(true).length;
                    if (ChickenCoops.cretCount > 0) {
                        for (final Item item : theItem.getParent().getAllItems(true)) {
                            pollFeeder(item);
                            pollDrinker(item);
                            eggPoller(item);
                        }
                    }
                    theItem.getParent().setData(System.currentTimeMillis());
                }
            }
            catch (NoSuchItemException ex) {
                ChickenCoops.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    private static void pollFeeder(final Item theItem) {
        if (theItem.getTemplateId() == 1434) {
            final long delay = System.currentTimeMillis() - 14400000L;
            if (delay > theItem.getData()) {
                Label_0188: {
                    if (!theItem.isEmpty(true)) {
                        if (theItem.getAllItems(true).length >= ChickenCoops.cretCount) {
                            final Item[] foodEaten = theItem.getAllItems(true);
                            for (int x = 0; x < ChickenCoops.cretCount; ++x) {
                                Items.destroyItem(foodEaten[x].getWurmId());
                            }
                            break Label_0188;
                        }
                    }
                    try {
                        for (final Item item : theItem.getParent().getAllItems(true)) {
                            if (item.getTemplateId() == 1436) {
                                for (final Item chickens : item.getAllItems(true)) {
                                    unload(chickens);
                                }
                            }
                        }
                    }
                    catch (NoSuchItemException ex) {
                        ChickenCoops.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
                theItem.setData(System.currentTimeMillis());
            }
        }
    }
    
    private static void pollDrinker(final Item theItem) {
        if (theItem.getTemplateId() == 1435) {
            final long delay = System.currentTimeMillis() - 14400000L;
            if (delay > theItem.getData()) {
                final int cretKG = ChickenCoops.cretCount * 250;
                for (final Item water : theItem.getAllItems(true)) {
                    Label_0209: {
                        if (!theItem.isEmpty(true)) {
                            if (water.getWeightGrams() >= cretKG) {
                                water.setWeight(water.getWeightGrams() - cretKG, true);
                                break Label_0209;
                            }
                        }
                        try {
                            for (final Item item : theItem.getParent().getAllItems(true)) {
                                if (item.getTemplateId() == 1436) {
                                    for (final Item chickens : item.getAllItems(true)) {
                                        unload(chickens);
                                    }
                                }
                            }
                        }
                        catch (NoSuchItemException ex) {
                            ChickenCoops.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
                theItem.setData(System.currentTimeMillis());
            }
        }
    }
    
    private static void eggPoller(final Item theItem) {
        if (theItem.getTemplateId() == 1433) {
            final long delay = System.currentTimeMillis() - 43200000L;
            if (delay > theItem.getData()) {
                try {
                    for (int x = 1; x <= ChickenCoops.cretCount; ++x) {
                        if (theItem.getAllItems(true).length < 100) {
                            final Item egg = ItemFactory.createItem(464, theItem.getQualityLevel(), null);
                            theItem.insertItem(egg);
                            if (Server.rand.nextInt(20) == 0) {
                                egg.setData1(48);
                                egg.setName("fertile egg");
                            }
                        }
                        else {
                            final Item[] overflow = theItem.getAllItems(true);
                            for (int y = 1; y <= overflow.length - 100; ++y) {
                                Items.destroyItem(overflow[y].getWurmId());
                            }
                        }
                    }
                }
                catch (FailedException | NoSuchTemplateException ex3) {
                    final WurmServerException ex2;
                    final WurmServerException ex = ex2;
                    ChickenCoops.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                theItem.setData(System.currentTimeMillis());
            }
        }
    }
    
    private static void unload(final Item theItem) {
        try {
            final Item parent = theItem.getParent();
            final Creature creature = Creatures.getInstance().getCreature(theItem.getData());
            int layer;
            if (parent.isOnSurface()) {
                layer = 0;
            }
            else {
                layer = -1;
            }
            final Creatures cstat = Creatures.getInstance();
            creature.getStatus().setDead(false);
            cstat.removeCreature(creature);
            cstat.addCreature(creature, false);
            creature.putInWorld();
            final float px = ((int)parent.getParent().getPosX() >> 2) * 4 + 2;
            final float py = ((int)parent.getParent().getPosY() >> 2) * 4 + 2;
            CreatureBehaviour.blinkTo(creature, px, py, layer, parent.getPosZ(), parent.getBridgeId(), parent.getFloorLevel());
            final Item coop = parent.getParent();
            DbCreatureStatus.setLoaded(0, creature.getWurmId());
            Items.destroyItem(theItem.getWurmId());
            CargoTransportationMethods.updateItemModel(coop);
        }
        catch (IOException | NoSuchItemException | NoSuchCreatureException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            ChickenCoops.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    static {
        logger = Logger.getLogger(ChickenCoops.class.getName());
    }
}
