// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.KarmaQuestion;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.Player;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.spells.Spells;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.Servers;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Constants;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class BodyPartBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    BodyPartBehaviour() {
        super((short)10);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (object.isBodyPartRemoved()) {
            toReturn.addAll(super.getBehavioursFor(performer, object));
        }
        else if (object.isBodyPartAttached()) {
            if (performer.getCultist() != null && performer.getCultist().mayRefresh()) {
                toReturn.add(new ActionEntry((short)91, "Refresh", "refreshing", BodyPartBehaviour.emptyIntArr));
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartLoveEffect()) {
                toReturn.add(Actions.actionEntrys[389]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartDoubleWarDamage()) {
                toReturn.add(Actions.actionEntrys[390]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartDoubleStructDamage()) {
                toReturn.add(Actions.actionEntrys[391]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartFearEffect()) {
                toReturn.add(Actions.actionEntrys[392]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartNoElementalDamage()) {
                toReturn.add(Actions.actionEntrys[393]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayStartIgnoreTraps()) {
                toReturn.add(Actions.actionEntrys[394]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayFillup()) {
                toReturn.add(Actions.actionEntrys[396]);
            }
            if (performer.getCultist() != null && performer.getCultist().mayTeleport()) {
                toReturn.add(new ActionEntry((short)(-1), "Random Teleport", "Teleporting", new int[0]));
                toReturn.add(new ActionEntry((short)95, "Confirm", "Teleporting", new int[0]));
            }
            if (performer.getCultist() != null && performer.getCultist().mayRecall()) {
                toReturn.add(Actions.actionEntrys[489]);
            }
            if (Constants.isEigcEnabled) {
                toReturn.add(Actions.actionEntrys[494]);
            }
            if (performer.mayUseLastGasp()) {
                toReturn.add(Actions.actionEntrys[495]);
            }
            if (performer.getKarma() > 0) {
                toReturn.add(Actions.actionEntrys[511]);
            }
            toReturn.add(new ActionEntry((short)585, "Unequip all", "unequipping", BodyPartBehaviour.emptyIntArr));
            toReturn.addAll(ManageMenu.getBehavioursFor(performer));
            if (performer.getPower() <= 0 && Kingdoms.isCustomKingdom(performer.getKingdomId())) {
                toReturn.add(Actions.actionEntrys[89]);
            }
            Behaviour.addEmotes(toReturn);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(this.getBehavioursFor(performer, object));
        if (source.isHolyItem() && object.isBodyPartAttached()) {
            if (source.isHolyItem(performer.getDeity()) && (performer.isPriest() || performer.getPower() > 0)) {
                final float faith = performer.getFaith();
                final Spell[] spells = performer.getDeity().getSpellsTargettingCreatures((int)faith);
                if (spells.length > 0) {
                    toReturn.add(new ActionEntry((short)(-spells.length), "Spells", "spells"));
                    for (final Spell lSpell : spells) {
                        toReturn.add(Actions.actionEntrys[lSpell.number]);
                    }
                }
            }
        }
        else if (object.isBodyPartAttached() && (source.isMagicStaff() || (source.getTemplateId() == 176 && performer.getPower() >= 4 && Servers.isThisATestServer()))) {
            final List<ActionEntry> slist = new LinkedList<ActionEntry>();
            if (performer.knowsKarmaSpell(547)) {
                slist.add(Actions.actionEntrys[547]);
            }
            if (performer.knowsKarmaSpell(554)) {
                slist.add(Actions.actionEntrys[554]);
            }
            if (performer.knowsKarmaSpell(555)) {
                slist.add(Actions.actionEntrys[555]);
            }
            if (performer.knowsKarmaSpell(560)) {
                slist.add(Actions.actionEntrys[560]);
            }
            if (object.getOwnerId() == performer.getWurmId()) {
                if (performer.knowsKarmaSpell(548)) {
                    slist.add(Actions.actionEntrys[548]);
                }
                if (performer.knowsKarmaSpell(552)) {
                    slist.add(Actions.actionEntrys[552]);
                }
                if (performer.knowsKarmaSpell(553)) {
                    slist.add(Actions.actionEntrys[553]);
                }
                if (performer.knowsKarmaSpell(562)) {
                    slist.add(Actions.actionEntrys[562]);
                }
            }
            if (performer.getPower() >= 4) {
                toReturn.add(new ActionEntry((short)(-slist.size()), "Sorcery", "casting"));
            }
            toReturn.addAll(slist);
        }
        if (object.isBodyPartAttached() && source.getTemplate().isRune()) {
            final Skill soulDepth = performer.getSoulDepth();
            final double diff = 20.0f + source.getDamage() - (source.getCurrentQualityLevel() + source.getRarity() - 45.0);
            final double chance = soulDepth.getChance(diff, null, source.getCurrentQualityLevel());
            if (RuneUtilities.isSingleUseRune(source) && ((RuneUtilities.getSpellForRune(source) != null && RuneUtilities.getSpellForRune(source).isTargetCreature()) || RuneUtilities.getModifier(RuneUtilities.getEnchantForRune(source), RuneUtilities.ModifierEffect.SINGLE_REFRESH) > 0.0f)) {
                toReturn.add(new ActionEntry((short)945, "Use Rune: " + chance + "%", "using rune", BodyPartBehaviour.emptyIntArr));
            }
        }
        if (object.isBodyPartAttached() && source.canHaveInscription()) {
            toReturn.addAll(PapyrusBehaviour.getPapyrusBehavioursFor(performer, source));
        }
        if (performer.getPower() >= 4 && object.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[721]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (target.isBodyPartRemoved()) {
            return super.action(act, performer, source, target, action, counter);
        }
        if (action == 95) {
            if (performer.getCultist() != null && performer.getCultist().mayTeleport()) {
                return this.action(act, performer, target, action, counter);
            }
        }
        else {
            if (action == 489) {
                return this.action(act, performer, target, action, counter);
            }
            if (action == 721) {
                if (performer.getPower() >= 4 && target.getOwnerId() == performer.getWurmId()) {
                    Methods.sendGmSetMedpathQuestion(performer, performer);
                }
                return true;
            }
        }
        if (action == 945 && source.getTemplate().isRune() && RuneUtilities.isSingleUseRune(source) && ((RuneUtilities.getSpellForRune(source) != null && RuneUtilities.getSpellForRune(source).isTargetCreature()) || RuneUtilities.getModifier(RuneUtilities.getEnchantForRune(source), RuneUtilities.ModifierEffect.SINGLE_REFRESH) > 0.0f)) {
            return CreatureBehaviour.useRuneOnCreature(act, performer, source, performer, action, counter);
        }
        if ((action == 6 || action == 7) && target.isBodyPartAttached()) {
            return true;
        }
        if (act.isSpell() && target.isBodyPartAttached()) {
            boolean done = true;
            final Spell spell = Spells.getSpell(action);
            if (spell != null) {
                try {
                    final Creature targ = Server.getInstance().getCreature(target.getOwnerId());
                    if (spell.offensive) {
                        if (targ.equals(performer)) {
                            performer.getCommunicator().sendNormalServerMessage("You can not cast that upon yourself.");
                            return true;
                        }
                        if (targ.isInvulnerable()) {
                            performer.getCommunicator().sendNormalServerMessage(target.getName() + " seems to absorb the spell with no effect.");
                            return true;
                        }
                        if (performer.mayAttack(targ)) {
                            if ((performer.opponent != null || targ.isAggHuman()) && performer.opponent == null) {
                                performer.setOpponent(targ);
                            }
                            if (targ.opponent == null) {
                                targ.setOpponent(performer);
                                targ.setTarget(performer.getWurmId(), false);
                                targ.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " is attacking you with a spell!");
                            }
                        }
                        else {
                            if (!performer.isStunned() && !performer.isUnconscious()) {
                                performer.getCommunicator().sendNormalServerMessage("You are too weak to attack.");
                                return true;
                            }
                            return true;
                        }
                    }
                    if (spell.religious) {
                        if (performer.getDeity() != null) {
                            if (Methods.isActionAllowed(performer, (short)245)) {
                                done = Methods.castSpell(performer, spell, targ, counter);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You have no deity and cannot cast the spell.");
                            done = true;
                        }
                    }
                    else if (source.isMagicStaff() || (source.getTemplateId() == 176 && performer.getPower() >= 2 && Servers.isThisATestServer())) {
                        if (Methods.isActionAllowed(performer, (short)547)) {
                            done = Methods.castSpell(performer, spell, targ, counter);
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You need to use a magic staff.");
                        done = true;
                    }
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
            }
            return done;
        }
        if (action == 744 && source.canHaveInscription()) {
            return PapyrusBehaviour.addToCookbook(act, performer, source, target, action, counter);
        }
        if (action == 1 || action == 494 || action == 495 || action == 511 || action == 566) {
            return this.action(act, performer, target, action, counter);
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        final boolean toReturn = true;
        if (target.isBodyPartRemoved()) {
            return super.action(act, performer, target, action, counter);
        }
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine(performer));
            if (target.isBodyPartAttached()) {
                Creature owner = performer;
                try {
                    owner = Server.getInstance().getCreature(target.getOwnerId());
                    if (owner.equals(performer)) {
                        String kingdom = "a Jenn-Kellon.";
                        if (owner.getKingdomId() == 0) {
                            kingdom = "not allied to anyone.";
                        }
                        else if (owner.getKingdomId() == 2) {
                            kingdom = "a Mol Rehan.";
                        }
                        else if (owner.getKingdomId() == 3) {
                            kingdom = "with the Horde of the Summoned.";
                        }
                        else if (owner.getKingdomId() == 4) {
                            kingdom = "from the Freedom Isles.";
                        }
                        else if (owner.getKingdomId() != 0) {
                            kingdom = "a " + Kingdoms.getNameFor(owner.getKingdomId());
                        }
                        if (owner.isKing()) {
                            final King k = King.getKing(owner.getKingdomId());
                            if (k != null) {
                                performer.getCommunicator().sendNormalServerMessage("You are " + k.getFullTitle() + ".");
                            }
                        }
                        performer.getCommunicator().sendNormalServerMessage("You are " + kingdom);
                        final String appointments = owner.getAppointmentTitles();
                        if (appointments.length() > 0) {
                            performer.getCommunicator().sendNormalServerMessage(appointments);
                        }
                        if (performer.getPower() > 0) {
                            performer.getCommunicator().sendNormalServerMessage("Reputation: " + owner.getReputation() + " Alignment: " + owner.getAlignment());
                            if (performer.getPower() >= 5) {
                                performer.getCommunicator().sendNormalServerMessage("Mount speed mod=" + performer.getMovementScheme().getMountSpeed());
                            }
                        }
                        performer.getCommunicator().sendNormalServerMessage(StringUtilities.raiseFirstLetter(owner.getStatus().getBodyType()));
                        if (owner.isPlayer()) {
                            performer.getCommunicator().sendNormalServerMessage("Rank=" + ((Player)owner).getRank() + ", Max Rank=" + ((Player)owner).getMaxRank());
                        }
                        if (Servers.isThisATestServer() && performer.isPlayer()) {
                            final Player player = (Player)performer;
                            performer.getCommunicator().sendNormalServerMessage(String.format("Your current alcohol level is %.1f.", player.getAlcohol()));
                        }
                    }
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
                if (performer.getPower() >= 3) {
                    performer.getCommunicator().sendNormalServerMessage("Position x=" + owner.getPosX() + "(" + owner.getTileX() + "), y=" + owner.getPosY() + "(" + owner.getTileY() + "), z=" + owner.getPositionZ() + "(+" + owner.getAltOffZ() + "), floorlevel=" + owner.getFloorLevel());
                    if (performer.getCurrentTile() != null) {
                        performer.getCommunicator().sendNormalServerMessage("Tile position x=" + owner.getCurrentTile().tilex + ", " + owner.getCurrentTile().tiley);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage(owner.getName() + " has no current tile.");
                    }
                    performer.getCommunicator().sendNormalServerMessage("Layer=" + owner.getLayer() + ", Teleporting=" + owner.isTeleporting() + ", vehicle=" + owner.getVehicle() + " Origin server " + WurmId.getOrigin(performer.getWurmId()));
                    if (owner.getVisionArea() != null) {
                        performer.getCommunicator().sendNormalServerMessage("Visionarea initialized=" + owner.getVisionArea().isInitialized() + ". Surface startx=" + owner.getVisionArea().getSurface().getStartX() + ", startY=" + owner.getVisionArea().getSurface().getStartY() + ", endX=" + owner.getVisionArea().getSurface().getEndX() + ", endY=" + owner.getVisionArea().getSurface().getEndY());
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Visionarea not initialized");
                    }
                }
            }
        }
        else if (action == 5) {
            Server.getInstance().broadCastNormal(performer.getName() + " waves his " + target.getName() + " frantically.");
        }
        else if (action == 389) {
            if (performer.getCultist() != null && performer.getCultist().mayStartLoveEffect()) {
                performer.getCommunicator().sendNormalServerMessage("You feel the love stream towards you.");
                performer.getCultist().touchCooldown3();
                if (performer.isFighting() && performer.opponent != null && performer.opponent.opponent == performer) {
                    performer.opponent.setTarget(-10L, true);
                }
                performer.setTarget(-10L, true);
                performer.refreshAttitudes();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.LOVE_EFFECT, performer.getCultist().getLoveEffectTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 91) {
            if (performer.getCultist() != null && performer.getCultist().mayRefresh()) {
                final float nut = Math.min(50 + (performer.getCultist().getLevel() - 4) * 5, 99) / 100.0f;
                performer.getStatus().refresh(nut, true);
                performer.getCommunicator().sendNormalServerMessage("You send yourself a warm thought.");
                if (performer.getCultist() != null) {
                    performer.getCultist().touchCooldown1();
                }
            }
        }
        else if (action == 390) {
            if (performer.getCultist() != null && performer.getCultist().mayStartDoubleWarDamage()) {
                performer.getCommunicator().sendNormalServerMessage("You feel the rage pulsate within.");
                performer.getCultist().touchCooldown1();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.HATE_DOUBLE_WAR, performer.getCultist().getDoubleWarDamageTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 391) {
            if (performer.getCultist() != null && performer.getCultist().mayStartDoubleStructDamage()) {
                performer.getCommunicator().sendNormalServerMessage("You feel the rage pulsate within.");
                performer.getCultist().touchCooldown2();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.HATE_DOUBLE_STRUCT, performer.getCultist().getDoubleStructDamageTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 392) {
            if (performer.getCultist() != null && performer.getCultist().mayStartFearEffect()) {
                performer.getCommunicator().sendNormalServerMessage("Everything will fear you now, and rightly so.");
                performer.getCultist().touchCooldown3();
                performer.refreshAttitudes();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.HATE_FEAR_EFFECT, performer.getCultist().getFearEffectTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 393) {
            if (performer.getCultist() != null && performer.getCultist().mayStartNoElementalDamage()) {
                performer.getCommunicator().sendNormalServerMessage("You align yourself with the elements.");
                performer.getCultist().touchCooldown1();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POWER_NO_ELEMENTAL, performer.getCultist().getElementalImmunityTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 394) {
            if (performer.getCultist() != null && performer.getCultist().mayStartIgnoreTraps()) {
                performer.getCommunicator().sendNormalServerMessage("You alert yourself to traps.");
                performer.getCultist().touchCooldown3();
                performer.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POWER_IGNORE_TRAPS, performer.getCultist().getIgnoreTrapsTimeLeftSeconds(), 100.0f);
            }
        }
        else if (action == 396) {
            if (performer.getCultist() != null && performer.getCultist().mayFillup()) {
                performer.getCommunicator().sendNormalServerMessage("You relax and forget about your hunger.");
                performer.getCultist().touchCooldown2();
                performer.getStatus().modifyHunger(-60000, 0.98f);
            }
        }
        else {
            if (action == 95) {
                if (performer.getCultist() != null && performer.getCultist().mayTeleport()) {
                    if (performer.getEnemyPresense() > 0) {
                        performer.getCommunicator().sendNormalServerMessage("There is a blocking enemy presence nearby. Nothing happens.");
                        return true;
                    }
                    final int tilex = Zones.safeTileX(Server.rand.nextInt(Zones.worldTileSizeX));
                    final int tiley = Zones.safeTileY(Server.rand.nextInt(Zones.worldTileSizeY));
                    if (!MethodsCreatures.mayTelePortToTile(performer, tilex, tiley, true)) {
                        performer.getCommunicator().sendNormalServerMessage("Something blocked your attempt. Nothing happens.");
                        return true;
                    }
                    if (performer.getPower() <= 0) {
                        final Item[] allItems;
                        final Item[] inventoryItems = allItems = performer.getInventory().getAllItems(true);
                        for (final Item lInventoryItem : allItems) {
                            if (lInventoryItem.isArtifact()) {
                                performer.getCommunicator().sendNormalServerMessage("The " + lInventoryItem.getName() + " softly hums. Nothing happens.");
                                return true;
                            }
                        }
                        final Item[] allItems2;
                        final Item[] bodyItems = allItems2 = performer.getBody().getBodyItem().getAllItems(true);
                        for (final Item lInventoryItem2 : allItems2) {
                            if (lInventoryItem2.isArtifact()) {
                                performer.getCommunicator().sendNormalServerMessage("The " + lInventoryItem2.getName() + " softly hums. Nothing happens.");
                                return true;
                            }
                        }
                        if (performer.isInPvPZone()) {
                            performer.getCommunicator().sendNormalServerMessage("The magic of the pvp zone interferes. You can not teleport here.");
                            return true;
                        }
                    }
                    performer.setTeleportPoints((short)tilex, (short)tiley, 0, 0);
                    Server.getInstance().broadCastAction(performer.getName() + " shuts " + performer.getHisHerItsString() + " eyes, dreaming away.", performer, 5);
                    if (performer.startTeleporting()) {
                        performer.getCommunicator().sendNormalServerMessage("You close your eyes and let your spirit fly.");
                        performer.getCommunicator().sendTeleport(false);
                        performer.getCultist().touchCooldown3();
                    }
                }
                return true;
            }
            if (action == 489) {
                if (performer.getCultist() != null && performer.getCultist().mayRecall()) {
                    final Item[] allItems3;
                    final Item[] inventoryItems2 = allItems3 = performer.getInventory().getAllItems(true);
                    for (final Item lInventoryItem3 : allItems3) {
                        if (lInventoryItem3.isArtifact()) {
                            performer.getCommunicator().sendNormalServerMessage("The " + lInventoryItem3.getName() + " hums and disturbs the weave. You can not teleport right now.");
                            return true;
                        }
                    }
                    final Item[] allItems4;
                    final Item[] bodyItems2 = allItems4 = performer.getBody().getBodyItem().getAllItems(true);
                    for (final Item lInventoryItem4 : allItems4) {
                        if (lInventoryItem4.isArtifact()) {
                            performer.getCommunicator().sendNormalServerMessage("The " + lInventoryItem4.getName() + " hums and disturbs the weave. You can not teleport right now.");
                            return true;
                        }
                    }
                    if (performer.getCitizenVillage() != null) {
                        if (performer.mayChangeVillageInMillis() > 0L) {
                            performer.getCommunicator().sendNormalServerMessage("You are still too new to this village. Nothing happens.");
                            return true;
                        }
                        if (performer.getEnemyPresense() > 0) {
                            performer.getCommunicator().sendNormalServerMessage("There is a blocking enemy presence nearby. Nothing happens.");
                            return true;
                        }
                        if (performer.isInPvPZone()) {
                            performer.getCommunicator().sendNormalServerMessage("The magic of the pvp zone interferes. You can not teleport here.");
                            return true;
                        }
                        performer.setTeleportPoints((short)performer.getCitizenVillage().getTokenX(), (short)performer.getCitizenVillage().getTokenY(), 0, 0);
                        if (performer.startTeleporting()) {
                            performer.getCommunicator().sendNormalServerMessage("You close your eyes and let your spirit fly home.");
                            Server.getInstance().broadCastAction(performer.getName() + " shuts " + performer.getHisHerItsString() + " eyes and suddenly disappears.", performer, 5);
                            performer.getCommunicator().sendTeleport(false);
                            performer.getCultist().touchCooldown4();
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You need to be part of a settlement you can call home.");
                    }
                }
                return true;
            }
            if (action == 494) {
                Methods.sendVoiceChatQuestion(performer);
            }
            else if (action == 495) {
                if (performer.mayUseLastGasp()) {
                    performer.useLastGasp();
                }
            }
            else if (action == 511) {
                if (performer.getKarma() > 0) {
                    final KarmaQuestion kq = new KarmaQuestion(performer);
                    kq.sendQuestion();
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You have no karma to use.");
                }
            }
            else {
                if (ManageMenu.isManageAction(performer, action)) {
                    return ManageMenu.action(act, performer, action, counter);
                }
                if (action == 89 && performer.getPower() <= 0) {
                    return super.action(act, performer, target, action, counter);
                }
            }
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(BodyPartBehaviour.class.getName());
    }
}
