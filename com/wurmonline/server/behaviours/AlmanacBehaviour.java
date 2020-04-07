// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.TextInputQuestion;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.villages.DeadVillage;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.items.FragmentUtilities;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Server;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import java.util.logging.Level;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.questions.ShowHarvestableInfo;
import com.wurmonline.server.questions.ShowArchReport;
import java.util.LinkedList;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class AlmanacBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    public AlmanacBehaviour() {
        super((short)57);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForAlmanac(performer, null, target));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForAlmanac(performer, source, target));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean[] ans = this.almanacAction(act, performer, null, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        final boolean[] ans = this.almanacAction(act, performer, source, target, action, counter);
        if (ans[0]) {
            return ans[1];
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private List<ActionEntry> getBehavioursForAlmanac(final Creature performer, @Nullable final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (target.getTemplateId() == 1127) {
            toReturn.add(new ActionEntry((short)17, "Read Almanac summary", "reading summary"));
            toReturn.add(Actions.actionEntrys[854]);
        }
        else if (target.getTemplateId() == 1128) {
            toReturn.add(new ActionEntry((short)17, "Read " + target.getName() + " summary", "reading summary"));
            if (target.isEmpty(false)) {
                toReturn.add(Actions.actionEntrys[855]);
            }
        }
        else if (target.getTemplateId() == 1403) {
            if (target.getAuxBit(0) && target.getAuxBit(1) && target.getAuxBit(2) && target.getAuxBit(3)) {
                toReturn.add(new ActionEntry((short)118, "Get direction", "getting direction"));
            }
            if (target.getData() != -1L) {
                toReturn.add(new ActionEntry((short)17, "Read report", "reading report"));
            }
        }
        return toReturn;
    }
    
    public boolean[] almanacAction(final Action act, final Creature performer, @Nullable final Item source, final Item target, final short action, final float counter) {
        if (action == 17) {
            if (target.getTemplateId() == 1403) {
                final ShowArchReport sar = new ShowArchReport(performer, target);
                sar.sendQuestion();
                return new boolean[] { true, true };
            }
            final ShowHarvestableInfo shi = new ShowHarvestableInfo(performer, target);
            shi.sendQuestion();
            return new boolean[] { true, true };
        }
        else {
            if (target.getTemplateId() == 1127 && action == 854) {
                try {
                    final Item newItem = ItemFactory.createItem(1128, 100.0f, null);
                    if (target.insertItem(newItem)) {
                        renameFolder(newItem, performer);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("no space to add folder into almanac.");
                    }
                }
                catch (FailedException e) {
                    performer.getCommunicator().sendNormalServerMessage("Problem adding folder into almanac.");
                    AlmanacBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                }
                catch (NoSuchTemplateException e2) {
                    performer.getCommunicator().sendNormalServerMessage("Problem adding folder into almanac.");
                    AlmanacBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
                }
                return new boolean[] { true, true };
            }
            if (target.getTemplateId() == 1128 && action == 59) {
                renameFolder(target, performer);
                return new boolean[] { true, true };
            }
            if (target.getTemplateId() == 1128 && target.isEmpty(false) && action == 855) {
                performer.getCommunicator().sendNormalServerMessage("Removing empty folder (" + target.getName() + ") from almanac.");
                Items.destroyItem(target.getWurmId());
                return new boolean[] { true, true };
            }
            if (target.getTemplateId() == 1403) {
                if (action == 118) {
                    if (target.getData() != -1L && target.getAuxBit(0) && target.getAuxBit(1) && target.getAuxBit(2) && target.getAuxBit(3)) {
                        final DeadVillage dv = Villages.getDeadVillage(target.getData());
                        if (dv != null) {
                            if (Math.abs(dv.getCenterX() - performer.getTileX()) < 1 && Math.abs(dv.getCenterY() - performer.getTileY()) < 1) {
                                int time = act.getTimeLeft();
                                if (counter == 1.0f) {
                                    performer.getCommunicator().sendNormalServerMessage("You spot something glinting in the ground and start pulling it free...", (byte)2);
                                    Server.getInstance().broadCastAction(performer.getName() + " spots something in the ground.", performer, 5);
                                    time = Actions.getVariableActionTime(performer, performer.getSkills().getSkillOrLearn(10069), null, 0.0, 250, 100, 2500);
                                    act.setTimeLeft(time);
                                    performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
                                    performer.getStatus().modifyStamina(-2500.0f);
                                }
                                else if (counter * 10.0f > time) {
                                    final Item cache = FragmentUtilities.createVillageCache((Player)performer, target, dv, performer.getSkills().getSkillOrLearn(10069));
                                    if (cache != null) {
                                        cache.setPosXY(dv.getCenterX() * 4 + 2, dv.getCenterY() * 4 + 2);
                                        final VolaTile t = Zones.getOrCreateTile(dv.getCenterX(), dv.getCenterY(), true);
                                        t.addItem(cache, false, false);
                                        performer.getCommunicator().sendNormalServerMessage("As you discover a " + cache.getName() + " the report is crumpled up and ruined.", (byte)2);
                                        Server.getInstance().broadCastAction(performer.getName() + " pulls a " + cache.getName() + " from the ground.", performer, 5);
                                        Items.destroyItem(target.getWurmId());
                                        performer.achievement(580);
                                        return new boolean[] { true, true };
                                    }
                                    performer.getCommunicator().sendNormalServerMessage("An error occured. Please try again later.", (byte)2);
                                    return new boolean[] { true, true };
                                }
                                return new boolean[] { true, false };
                            }
                            performer.getCommunicator().sendNormalServerMessage("Reading details from the report, " + dv.getDeedName() + " looks like it may have been " + dv.getDistanceFrom(performer.getTileX(), performer.getTileY()) + " to the " + dv.getDirectionFrom(performer.getTileX(), performer.getTileY()) + ".");
                        }
                        else if (WurmId.getOrigin(target.getData()) != Servers.localServer.getId()) {
                            performer.getCommunicator().sendNormalServerMessage("The details of this report seem to lead you to distant lands.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You're unable to make sense of this report.");
                        }
                        return new boolean[] { true, true };
                    }
                }
                else {
                    if (action == 1) {
                        if (target.getData() == -1L) {
                            performer.getCommunicator().sendNormalServerMessage("A blank archaeological report. Investigating a tile and discovering information about a village will let you report it in this report.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("An archaeological report of a village. Reading this will let you recall information that you have discovered about this village.");
                        }
                        return new boolean[] { true, true };
                    }
                    if (action == 7) {
                        return new boolean[] { true, true };
                    }
                }
            }
            return new boolean[] { false, false };
        }
    }
    
    private static void renameFolder(final Item folder, final Creature performer) {
        final TextInputQuestion tiq = new TextInputQuestion(performer, "Setting name for folder.", "Set the new name:", 1, folder.getWurmId(), 20, false);
        tiq.setOldtext(folder.getName());
        tiq.sendQuestion();
    }
    
    static {
        logger = Logger.getLogger(MarkerBehaviour.class.getName());
    }
}
