// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.behaviours.NoSuchBehaviourException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.behaviours.BehaviourDispatcher;
import com.wurmonline.server.Features;
import java.util.logging.Level;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class RemoveItemQuestion extends Question
{
    private static final Logger logger;
    private static final int MAXNUMS = Integer.MAX_VALUE;
    private long moveTarget;
    
    public RemoveItemQuestion(final Creature aResponder, final long aTarget) {
        super(aResponder, "Removing items", "How many items do you wish to remove?", 84, aTarget);
        this.moveTarget = 0L;
    }
    
    public RemoveItemQuestion(final Creature aResponder, final long aTarget, final long aMoveTarget) {
        super(aResponder, "Removing items", "How many items do you wish to remove?", 84, aTarget);
        this.moveTarget = 0L;
        this.moveTarget = aMoveTarget;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        final String numstext = aAnswers.getProperty("numstext");
        String nums = aAnswers.getProperty("items");
        if (numstext != null && numstext.length() > 0) {
            nums = numstext;
        }
        if (nums != null && nums.length() > 0) {
            if (nums.equals(String.valueOf(Integer.MAX_VALUE))) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You selected max.");
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You selected " + nums + ".");
            }
            try {
                int i = Integer.parseInt(nums);
                if (i > 0) {
                    try {
                        final Item bulkitem = Items.getItem(this.target);
                        final long topParentId = bulkitem.getTopParent();
                        final Item topParent = Items.getItem(topParentId);
                        float maxDist = 4.0f;
                        if (topParent.isVehicle()) {
                            final Vehicle vehicle = Vehicles.getVehicle(topParent);
                            if (vehicle != null) {
                                maxDist = Math.max(maxDist, vehicle.getMaxAllowedLoadDistance());
                            }
                        }
                        if (!this.getResponder().isWithinDistanceTo(topParent.getPosX(), topParent.getPosY(), topParent.getPosZ(), maxDist) && bulkitem.getTopParent() != this.getResponder().getVehicle()) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("You are too far away from the " + bulkitem.getName() + " now.");
                            return;
                        }
                        boolean full = false;
                        Item parent = null;
                        try {
                            parent = bulkitem.getParent();
                            full = parent.isFull();
                        }
                        catch (NoSuchItemException ex) {}
                        final boolean max = i == Integer.MAX_VALUE;
                        int bnums = 0;
                        if (bulkitem.getRealTemplate() != null && bulkitem.getRealTemplate().isCombine()) {
                            bnums = (int)Math.ceil(bulkitem.getBulkNumsFloat(false));
                        }
                        else {
                            bnums = bulkitem.getBulkNums();
                        }
                        Item toInsert = null;
                        final int current = this.getResponder().getInventory().getNumItemsNotCoins();
                        final int maxCapac = Math.max(0, 100 - current);
                        if (i > maxCapac) {
                            i = Math.min(bnums, maxCapac);
                        }
                        if (bnums < i) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The " + bulkitem.getName() + " does not contain " + i + " items.");
                            return;
                        }
                        int weightReduced = 0;
                        final ItemTemplate template = bulkitem.getRealTemplate();
                        if (template != null) {
                            final int volume = template.getVolume();
                            int tweight = template.getWeightGrams();
                            Item targetInventory = null;
                            try {
                                if (this.moveTarget != 0L) {
                                    targetInventory = Items.getItem(this.moveTarget);
                                }
                            }
                            catch (NoSuchItemException nsi) {
                                final String message = StringUtil.format("Unable to find item: %d.", this.moveTarget);
                                RemoveItemQuestion.logger.log(Level.WARNING, message, nsi);
                                return;
                            }
                            if (template.isFish() && template.getTemplateId() != 369) {
                                final double ql = bulkitem.getCurrentQualityLevel() / 100.0f;
                                tweight *= (int)ql;
                            }
                            if (max) {
                                i = Math.min(maxCapac, this.getResponder().getCarryCapacityFor(tweight));
                                if (i <= 0) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You can not even carry one of those.");
                                    return;
                                }
                                i = Math.min(i, bnums);
                            }
                            else if (!this.getResponder().canCarry(tweight * i)) {
                                if (targetInventory == null || !targetInventory.isBulkContainer()) {
                                    if (i > 1 || bulkitem.getWeightGrams() >= volume) {
                                        this.getResponder().getCommunicator().sendNormalServerMessage("You may not carry that weight.");
                                        return;
                                    }
                                }
                            }
                            if (targetInventory.isContainerLiquid() && (targetInventory.getSizeX() < template.getSizeX() || targetInventory.getSizeY() < template.getSizeY() || targetInventory.getSizeZ() < template.getSizeZ())) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The " + template.getName() + " will not fit inside the " + targetInventory.getName() + ".");
                                return;
                            }
                            final byte auxdata = bulkitem.getAuxData();
                            int toMake = bulkitem.getRealTemplateId();
                            String aName = bulkitem.getActualName();
                            if (toMake == 129 && auxdata == 0) {
                                toMake = 92;
                                aName = "meat";
                            }
                            if (Features.Feature.MOVE_BULK_TO_BULK.isEnabled() && targetInventory.isBulkContainer() && this.moveTarget > 0L) {
                                try {
                                    BehaviourDispatcher.action(this.getResponder(), this.getResponder().getCommunicator(), this.target, this.moveTarget, (short)914);
                                    final Action act = ((Player)this.getResponder()).getActions().getLastSlowAction();
                                    if (act != null) {
                                        act.setData(Integer.parseInt(nums));
                                    }
                                    else {
                                        this.getResponder().getCommunicator().sendAlertServerMessage("ERROR: Action was null, could not set amount!");
                                    }
                                }
                                catch (NoSuchPlayerException e) {
                                    RemoveItemQuestion.logger.fine("No such player Ex");
                                }
                                catch (NoSuchCreatureException e2) {
                                    RemoveItemQuestion.logger.fine("No such creature Ex");
                                }
                                catch (NoSuchBehaviourException e3) {
                                    RemoveItemQuestion.logger.fine("No such behaviour Ex :(");
                                }
                                catch (NoSuchWallException e4) {
                                    RemoveItemQuestion.logger.fine("no such wall ex");
                                }
                                catch (FailedException e5) {
                                    RemoveItemQuestion.logger.fine("Failed EX?");
                                }
                                return;
                            }
                            for (int created = 0; created < i; ++created) {
                                try {
                                    int weight = bulkitem.getWeightGrams() - weightReduced;
                                    float percent = 1.0f;
                                    if (weight < volume) {
                                        percent = weight / volume;
                                    }
                                    else {
                                        weight = Math.min(bulkitem.getWeightGrams(), volume);
                                    }
                                    if (weight > 0) {
                                        toInsert = ItemFactory.createItem(toMake, bulkitem.getCurrentQualityLevel(), bulkitem.getMaterial(), (byte)0, null);
                                        if (!toInsert.isFish()) {
                                            toInsert.setCreator(this.getResponder().getName());
                                        }
                                        toInsert.setLastOwnerId(this.getResponder().getWurmId());
                                        if (toInsert.isRepairable()) {
                                            toInsert.setCreationState((byte)0);
                                        }
                                        if (toInsert.usesFoodState()) {
                                            toInsert.setAuxData(auxdata);
                                            final ItemMealData imd = ItemMealData.getItemMealData(bulkitem.getWurmId());
                                            if (imd != null) {
                                                ItemMealData.save(toInsert.getWurmId(), imd.getRecipeId(), imd.getCalories(), imd.getCarbs(), imd.getFats(), imd.getProteins(), imd.getBonus(), imd.getStages(), imd.getIngredients());
                                            }
                                        }
                                        if (template.isFish() && template.getTemplateId() != 369) {
                                            toInsert.setSizes(tweight);
                                            toInsert.setWeight(tweight, true);
                                        }
                                        else {
                                            toInsert.setWeight((int)(percent * template.getWeightGrams()), true);
                                        }
                                        if (bulkitem.getData1() != -1) {
                                            toInsert.setRealTemplate(bulkitem.getData1());
                                        }
                                        if (!bulkitem.getActualName().equalsIgnoreCase("bulk item")) {
                                            toInsert.setName(aName);
                                        }
                                        if (this.moveTarget == 0L) {
                                            this.getResponder().getInventory().insertItem(toInsert);
                                        }
                                        else if (targetInventory.isBulkContainer()) {
                                            try {
                                                if ((targetInventory.isCrate() || !targetInventory.hasSpaceFor(toInsert.getVolume())) && (!targetInventory.isCrate() || !targetInventory.canAddToCrate(toInsert))) {
                                                    final String message2 = "The %s will not fit in the %s.";
                                                    this.getResponder().getCommunicator().sendNormalServerMessage(StringUtil.format("The %s will not fit in the %s.", toInsert.getName(), targetInventory.getName()));
                                                    Items.destroyItem(toInsert.getWurmId());
                                                    break;
                                                }
                                                if (!toInsert.moveToItem(this.getResponder(), targetInventory.getWurmId(), false)) {
                                                    Items.destroyItem(toInsert.getWurmId());
                                                    break;
                                                }
                                            }
                                            catch (NoSuchPlayerException ex2) {}
                                            catch (NoSuchCreatureException ex3) {}
                                        }
                                        else {
                                            if (!targetInventory.testInsertItem(toInsert) || !targetInventory.mayCreatureInsertItem()) {
                                                final String message2 = "There is not enough space for any more items.";
                                                this.getResponder().getCommunicator().sendNormalServerMessage("There is not enough space for any more items.");
                                                Items.destroyItem(toInsert.getWurmId());
                                                break;
                                            }
                                            targetInventory.insertItem(toInsert);
                                        }
                                        weightReduced += weight;
                                    }
                                }
                                catch (NoSuchTemplateException nst) {
                                    RemoveItemQuestion.logger.log(Level.WARNING, nst.getMessage(), nst);
                                }
                                catch (FailedException fe) {
                                    RemoveItemQuestion.logger.log(Level.WARNING, fe.getMessage(), fe);
                                }
                            }
                            this.getResponder().achievement(167, -i);
                            if (!bulkitem.setWeight(bulkitem.getWeightGrams() - weightReduced, true)) {}
                        }
                        if (parent != null && (full != parent.isFull() || parent.isCrate())) {
                            parent.updateModelNameOnGroundItem();
                        }
                    }
                    catch (NoSuchItemException nsc) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("No such item.");
                    }
                }
            }
            catch (NumberFormatException ne) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Not a number.");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        Item temp;
        try {
            if (this.moveTarget > 0L) {
                temp = Items.getItem(this.moveTarget);
            }
            else {
                temp = null;
            }
        }
        catch (NoSuchItemException e) {
            temp = null;
        }
        final Item moveTargetItem = temp;
        try {
            final Item bulkitem = Items.getItem(this.target);
            final String carryString = (moveTargetItem != null && (moveTargetItem.isBulk() || moveTargetItem.isBulkContainer())) ? "All items" : "As many as I can carry";
            final StringBuilder buf = new StringBuilder();
            final int nums = bulkitem.getBulkNums();
            buf.append(this.getBmlHeader());
            if (nums > 0) {
                buf.append("text{text=\"How many items do you wish to remove?\"};");
                buf.append("text{text=''}");
                buf.append("input{text='';id='numstext';maxlength='2'};");
                buf.append("text{text=''}");
                buf.append("radio{ group='items'; id='2147483647';selected='true';text='" + carryString + "'}");
                buf.append("radio{ group='items'; id='0';text='None'}");
                if (nums < 100 && nums != 1) {
                    buf.append("radio{ group='items'; id='" + nums + "';text='" + nums + "'}");
                }
                buf.append("radio{ group='items'; id='1';text='1'}");
                if (nums > 2 && nums != 2) {
                    buf.append("radio{ group='items'; id='2';text='2'}");
                }
                if (nums > 5 && nums != 5) {
                    buf.append("radio{ group='items'; id='5';text='5'}");
                }
                if (nums > 10 && nums != 10) {
                    buf.append("radio{ group='items'; id='10';text='10'}");
                }
                if (nums > 20 && nums != 20) {
                    buf.append("radio{ group='items'; id='20';text='20'}");
                }
                if (nums > 50 && nums != 50) {
                    buf.append("radio{ group='items'; id='50';text='50'}");
                }
            }
            else {
                buf.append("text{text=\"The " + bulkitem.getName() + " is empty.\"}");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 340, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (NoSuchItemException ex) {}
    }
    
    static {
        logger = Logger.getLogger(RemoveItemQuestion.class.getName());
    }
}
