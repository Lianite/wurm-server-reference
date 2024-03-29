// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.creatures.Traits;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.Items;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.NoSuchItemException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.spells.RiteEvent;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.Servers;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.questions.ConvertQuestion;
import com.wurmonline.server.questions.AskConvertQuestion;
import com.wurmonline.server.questions.QuestionParser;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.questions.RechargeQuestion;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.QuestionTypes;
import com.wurmonline.server.MiscConstants;

public final class MethodsReligion implements MiscConstants, QuestionTypes, ItemTypes, CounterTypes, ItemMaterials, SoundNames, VillageStatus, TimeConstants, MonetaryConstants
{
    public static final String cvsversion = "$Id: MethodsReligion.java,v 1.22 2007-04-19 23:05:18 root Exp $";
    private static final Logger logger;
    public static final Map<Long, Long> listenedTo;
    private static final Map<Long, Long> lastReceivedAlignment;
    private static final Map<Long, Long> lastHeldSermon;
    private static final float MILLION = 1000000.0f;
    private static final float KTHOUS = 100000.0f;
    
    public static final boolean mayReceiveAlignment(final Creature c) {
        final Long lastReceived = MethodsReligion.lastReceivedAlignment.get(c.getWurmId());
        return lastReceived == null || System.currentTimeMillis() - lastReceived > 1800000L;
    }
    
    public static final void setReceivedAlignment(final Creature c) {
        MethodsReligion.lastReceivedAlignment.put(c.getWurmId(), System.currentTimeMillis());
    }
    
    static final boolean sendRechargeQuestion(final Creature responder, final Item rechargeable) {
        final RechargeQuestion spm = new RechargeQuestion(responder, "Recharging " + rechargeable.getName(), "Do you want to ", rechargeable.getWurmId());
        spm.sendQuestion();
        return true;
    }
    
    static final void sendAskConvertQuestion(final Creature asker, final Creature responder, final Item holyItem) {
        if (((Player)asker).lastSentQuestion > 0) {
            asker.getCommunicator().sendNormalServerMessage("You must wait another " + ((Player)asker).lastSentQuestion + " seconds before asking again.");
            return;
        }
        if (asker.getDeity() == null) {
            asker.getCommunicator().sendNormalServerMessage("You have no deity!");
            return;
        }
        if (!holyItem.isHolyItem(asker.getDeity())) {
            asker.getCommunicator().sendNormalServerMessage("The " + holyItem.getName() + " is not blessed by your deity.");
            return;
        }
        if (!responder.isPlayer()) {
            asker.getCommunicator().sendNormalServerMessage("Only players may be converted for the moment.");
            return;
        }
        if (responder.isNewbie()) {
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too inexperienced to be converted.");
            return;
        }
        if (!responder.mayChangeDeity(asker.getDeity().getNumber())) {
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " changed faith too recently to consider a new deity.");
            return;
        }
        if (!responder.isWithinTileDistanceTo(asker.getTileX(), asker.getTileY(), (int)(asker.getPositionZ() + asker.getAltOffZ()) >> 2, 4)) {
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too far away now.");
            return;
        }
        if (responder.isChampion()) {
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is a champion, and may not convert.");
            return;
        }
        if (responder.getDeity() != null && responder.getDeity() == asker.getDeity()) {
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " already follows " + asker.getDeity().getName() + ".");
            return;
        }
        if (QuestionParser.doesKingdomTemplateAcceptDeity(responder.getKingdomTemplateId(), asker.getDeity())) {
            final AskConvertQuestion spm = new AskConvertQuestion(responder, "The words of " + asker.getDeity().name, "Lecture:", asker.getWurmId(), holyItem);
            spm.sendQuestion();
            ((Player)asker).lastSentQuestion = 60;
            return;
        }
        asker.getCommunicator().sendNormalServerMessage("Following that deity would expel " + responder.getName() + " from their kingdom.");
    }
    
    private static final void sendConvertQuestion(final Creature asker, final Creature responder, final Item holyItem, final float counter) {
        if (holyItem.isHolyItem(asker.getDeity())) {
            final Deity deity = asker.getDeity();
            if (deity != null) {
                if (responder instanceof Player) {
                    if (responder.getDeity() != deity) {
                        if (!responder.isNewbie()) {
                            if (responder.mayChangeDeity(deity.number)) {
                                if (responder.isWithinTileDistanceTo(asker.getTileX(), asker.getTileY(), (int)(asker.getPositionZ() + asker.getAltOffZ()) >> 2, 4)) {
                                    final ConvertQuestion spm = new ConvertQuestion(responder, "Converting to " + asker.getDeity().name, "Convert:", asker.getWurmId(), holyItem);
                                    spm.sendQuestion();
                                    spm.setSkillcounter(counter);
                                }
                                else {
                                    asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too far away now.");
                                }
                            }
                            else {
                                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " changed faith too recently to consider a new deity.");
                            }
                        }
                        else {
                            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too inexperienced to be converted.");
                        }
                    }
                    else {
                        asker.getCommunicator().sendNormalServerMessage(responder.getName() + " already prays to " + responder.getDeity().name + "!");
                        responder.getCommunicator().sendNormalServerMessage("You already pray to " + responder.getDeity().name + "!");
                    }
                }
                else {
                    asker.getCommunicator().sendNormalServerMessage("Only players may be converted for the moment.");
                }
            }
            else {
                asker.getCommunicator().sendNormalServerMessage("You have no deity!");
            }
        }
        else {
            asker.getCommunicator().sendNormalServerMessage("The " + holyItem.getName() + " is not blessed by your deity.");
        }
    }
    
    static boolean performRitual(final Creature performer, final Item source, final Item target, final float counter, final int action, final Action act) {
        boolean done = false;
        boolean ok = false;
        if (target.isKingdomMarker() || target.isEpicTargetItem() || target.getTemplateId() == 236) {
            ok = true;
        }
        if (act != null && ok) {
            final String riteName = Actions.actionEntrys[action].getActionString();
            if (counter == 1.0f) {
                performer.getCommunicator().sendNormalServerMessage("You start to perform the " + riteName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to perform the " + riteName + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[216].getVerbString(), true, 360);
            }
            else {
                short emote = 2010;
                if (act.currentSecond() == 3) {
                    if (action >= 496) {
                        emote = 2002;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() == 9) {
                    if (action >= 496 && action <= 497) {
                        emote = 2014;
                    }
                    else {
                        emote = 2016;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() == 15) {
                    if (action >= 496 && action <= 498) {
                        emote = 2022;
                    }
                    else {
                        emote = 2015;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() == 21) {
                    if (action >= 496 && action <= 499) {
                        emote = 2021;
                    }
                    else {
                        emote = 2024;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() == 27) {
                    if (action >= 496 && action <= 500) {
                        emote = 2023;
                    }
                    else {
                        emote = 2028;
                    }
                    Emotes.emoteAt(emote, performer, target);
                }
                else if (act.currentSecond() == 33) {
                    if (action >= 496 && action <= 501) {
                        emote = 2013;
                    }
                    else {
                        emote = 2005;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() >= 36) {
                    performer.getStatus().modifyStamina(-1000.0f);
                    if (action >= 496 && action <= 500) {
                        emote = 2006;
                    }
                    else {
                        emote = 2029;
                    }
                    Emotes.emoteAt(emote, performer, target);
                    performer.getCommunicator().sendNormalServerMessage("Your ritual is complete.");
                    Server.getInstance().broadCastAction(performer.getName() + " ends " + performer.getHisHerItsString() + " ritual.", performer, 5);
                    if (action == 496) {
                        try {
                            performer.setFavor(performer.getFavor() + 0.1f);
                        }
                        catch (IOException iox) {
                            MethodsReligion.logger.log(Level.WARNING, iox.getMessage());
                        }
                    }
                    else if (action == 501) {
                        performer.healRandomWound(1);
                        performer.setWounded();
                    }
                    else if (action == 498) {
                        Server.getWeather().modifyFogTarget(0.01f);
                    }
                    else if (action == 497) {
                        Server.getWeather().modifyRainTarget(0.01f);
                    }
                    else if (action == 499) {
                        Server.getWeather().modifyCloudTarget(-0.01f);
                    }
                    else if (action == 502) {
                        performer.addWoundOfType(performer, (byte)9, 1, false, 1.0f, false, 2000.0, 0.0f, 0.0f, false, true);
                    }
                    else if (action == 500) {
                        performer.healRandomWound(100);
                    }
                    boolean trigger = true;
                    final boolean epicHomeServer = Servers.localServer.EPIC && Servers.localServer.HOMESERVER;
                    if (target.isKingdomMarker()) {
                        trigger = false;
                        if (epicHomeServer) {
                            trigger = true;
                        }
                        else if (!Servers.localServer.HOMESERVER) {
                            trigger = true;
                        }
                    }
                    if (trigger) {
                        MissionTriggers.activateTriggers(performer, source, action, target.getWurmId(), 1);
                    }
                    done = true;
                }
            }
        }
        else {
            done = true;
            performer.getCommunicator().sendNormalServerMessage("You can't perform the ritual now.");
        }
        return done;
    }
    
    static boolean preach(final Creature performer, final Creature responder, final Item holyItem, final float counter) {
        boolean done = false;
        final Deity deity = performer.getDeity();
        if (deity == null) {
            performer.getCommunicator().sendNormalServerMessage("You have no deity and stop preaching.");
            done = true;
        }
        else if (!holyItem.isHolyItem(deity)) {
            performer.getCommunicator().sendNormalServerMessage("Your holy item is not blessed by " + deity.name + "!");
            done = true;
        }
        else {
            Action act = null;
            try {
                act = performer.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                done = true;
            }
            if (act != null && act.getNumber() == 216) {
                if (counter == 1.0f) {
                    performer.getCommunicator().sendNormalServerMessage("You start to preach about " + deity.name + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to preach about " + deity.name + ".", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[216].getVerbString(), true, 360);
                }
                else if (act.currentSecond() == 3) {
                    performer.getCommunicator().sendNormalServerMessage("You clear your throat.");
                    Server.getInstance().broadCastAction(performer.getName() + " clears " + performer.getHisHerItsString() + " throat.", performer, 5);
                }
                else if (act.currentSecond() == 6) {
                    Methods.sendSound(performer, "sound.religion.preach");
                    performer.say(deity.convertText1[0]);
                }
                else if (act.currentSecond() == 9) {
                    performer.say(deity.convertText1[1]);
                }
                else if (act.currentSecond() == 12) {
                    performer.say(deity.convertText1[2]);
                }
                else if (act.currentSecond() == 15) {
                    performer.say(deity.convertText1[3]);
                }
                else if (act.currentSecond() == 18) {
                    Methods.sendSound(performer, "sound.religion.preach");
                    performer.say(deity.convertText1[4]);
                }
                else if (act.currentSecond() == 21) {
                    performer.say(deity.convertText1[5]);
                }
                else if (act.currentSecond() == 24) {
                    performer.say(deity.convertText1[6]);
                }
                else if (act.currentSecond() == 27) {
                    Methods.sendSound(performer, "sound.religion.preach");
                    performer.say(deity.convertText1[7]);
                }
                else if (act.currentSecond() == 30) {
                    performer.say(deity.convertText1[8]);
                }
                else if (act.currentSecond() == 33) {
                    Methods.sendSound(performer, "sound.religion.preach");
                    performer.say(deity.convertText1[9]);
                }
                else if (act.currentSecond() >= 36) {
                    performer.getCommunicator().sendNormalServerMessage("You end your speech.");
                    Server.getInstance().broadCastAction(performer.getName() + " ends " + performer.getHisHerItsString() + " speech.", performer, 5);
                    done = true;
                    sendConvertQuestion(performer, responder, holyItem, counter);
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("You stop preaching.");
            }
        }
        return done;
    }
    
    private static final void prayResult(final Creature performer, final float power, final Deity deity, final int rarity) {
        final int num = Server.rand.nextInt(50 - rarity);
        String message = "Nothing special seems to happen.";
        boolean effect = false;
        switch (num) {
            case 0: {
                if (performer.getBody().getWounds() != null) {
                    final Wound[] wounds = performer.getBody().getWounds().getWounds();
                    if (wounds.length > 0) {
                        message = deity.name + " heals you.";
                        wounds[0].heal();
                        effect = true;
                    }
                    break;
                }
                break;
            }
            case 1: {
                if (performer.isPriest()) {
                    for (int x = Zones.safeTileX(performer.getTileX() - 1); x <= Zones.safeTileX(performer.getTileX() + 1); ++x) {
                        for (int y = Zones.safeTileY(performer.getTileY() - 1); y <= Zones.safeTileY(performer.getTileY() + 1); ++y) {
                            final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                            if (t != null) {
                                final Creature[] crets = t.getCreatures();
                                if (crets.length > 0) {
                                    for (int c = 0; c < crets.length; ++c) {
                                        if (crets[c] != performer && crets[c].getAttitude(performer) == 1) {
                                            final Wounds w = crets[c].getBody().getWounds();
                                            if (w != null) {
                                                final Wound[] wounds2 = w.getWounds();
                                                if (wounds2.length > 0) {
                                                    message = deity.name + " heals " + crets[c].getName() + ".";
                                                    crets[c].getCommunicator().sendNormalServerMessage("You feel a hot feeling near your wound and it suddenly goes away. Praise be " + deity.name + "!");
                                                    wounds2[Server.rand.nextInt(wounds2.length)].heal();
                                                    effect = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 2: {
                effect = true;
                if (performer.getFaith() > 50.0f) {
                    final Item gem = TileRockBehaviour.createRandomGem(40.0f);
                    if (performer.getInventory().getNumItemsNotCoins() < 100) {
                        performer.getInventory().insertItem(gem, true);
                    }
                    else {
                        try {
                            gem.putItemInfrontof(performer);
                        }
                        catch (Exception nsz) {
                            MethodsReligion.logger.log(Level.INFO, nsz.getMessage(), nsz);
                        }
                    }
                    performer.achievement(622);
                }
                else {
                    final int templateId = 246 + Server.rand.nextInt(6);
                    try {
                        final Item mushroom = ItemFactory.createItem(templateId, power, deity.name);
                        if (performer.getInventory().getNumItemsNotCoins() < 100) {
                            performer.getInventory().insertItem(mushroom, true);
                        }
                        else {
                            mushroom.putItemInfrontof(performer);
                        }
                    }
                    catch (Exception ex) {
                        MethodsReligion.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
                message = deity.name + " puts something in your pocket.";
                performer.achievement(609);
                break;
            }
            case 3: {
                if (performer.isPriest()) {
                    effect = true;
                    try {
                        performer.setFavor(performer.getFavor() + 10.0f);
                    }
                    catch (IOException iox) {
                        MethodsReligion.logger.log(Level.WARNING, iox.getMessage(), iox);
                    }
                    message = deity.name + " gives you some favor.";
                    break;
                }
                break;
            }
            case 4: {
                if (performer.isPlayer() && performer.isPriest() && performer.getVehicle() == -10L) {
                    effect = true;
                    ((Player)performer).setFarwalkerSeconds((byte)45);
                    performer.getMovementScheme().setFarwalkerMoveMod(true);
                    performer.getStatus().sendStateString();
                    message = "Your legs tingle and you feel unstoppable.";
                    break;
                }
                break;
            }
            case 5: {
                if (performer.isPlayer()) {
                    effect = true;
                    for (int x = 0; x < power / 10.0f; ++x) {
                        try {
                            if (performer.getInventory().getItemCount() >= 100) {
                                message = "You think you feel a faint tug in your inventory but perhaps because it's full nothing happens.";
                                break;
                            }
                            final Item coin = ItemFactory.createItem(51, Server.rand.nextFloat() * power, deity.name);
                            performer.getInventory().insertItem(coin, true);
                            message = "Your pocket suddenly feels heavier.";
                        }
                        catch (Exception ex) {
                            MethodsReligion.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                    performer.achievement(609);
                    break;
                }
                break;
            }
            case 6: {
                if (performer.isPlayer()) {
                    effect = true;
                    int skillNum = 100;
                    if (deity.number == 1) {
                        skillNum = 103;
                    }
                    if (deity.number == 4 || deity.number == 2) {
                        skillNum = 102;
                    }
                    if (deity.number == 3) {
                        skillNum = 100;
                    }
                    try {
                        final Skill toImprove = performer.getSkills().getSkill(skillNum);
                        toImprove.setKnowledge(toImprove.getKnowledge() + 1.0E-4, false);
                        message = "You are blessed with some " + toImprove.getName() + ".";
                    }
                    catch (NoSuchSkillException ex2) {}
                    break;
                }
                break;
            }
            case 7: {
                if (performer.isPlayer() && power > 50.0f && ((Player)performer).getAlcoholAddiction() > 0L && Server.rand.nextInt(100) == 0) {
                    effect = true;
                    ((Player)performer).setAlcohol(0.0f);
                    ((Player)performer).getSaveFile().setAlcoholTime(0L);
                    message = "You sober up and your addiction is removed.";
                    break;
                }
                break;
            }
            case 8: {
                if (!performer.isPlayer() || performer.getPet() == null) {
                    break;
                }
                effect = true;
                if (performer.getPower() > 0) {
                    performer.getCommunicator().sendNormalServerMessage("Loyalty before: " + performer.getPet().getLoyalty());
                }
                performer.getPet().setLoyalty(performer.getPet().getLoyalty() + 2.0f);
                message = performer.getPet().getName() + " suddenly seems more loyal.";
                if (performer.getPower() > 0) {
                    performer.getCommunicator().sendNormalServerMessage("Loyalty after: " + performer.getPet().getLoyalty());
                    break;
                }
                break;
            }
            case 9: {
                effect = true;
                performer.setPrayerSeconds(300);
                message = deity.name + " grants you faster favor gain.";
                break;
            }
            default: {
                effect = false;
                message = "You send your prayers to " + deity.name + ".";
                break;
            }
        }
        performer.getCommunicator().sendNormalServerMessage(message);
    }
    
    static boolean pray(final Action act, final Creature performer, final float counter) {
        final float faith = performer.getFaith();
        if (performer.getDeity() == null) {
            performer.getCommunicator().sendNormalServerMessage("You have no deity, so you cannot pray here.");
            return true;
        }
        final Deity deity = performer.getDeity();
        boolean done = false;
        int time = act.getTimeLeft();
        if (act.mayPlaySound()) {
            Methods.sendSound(performer, "sound.religion.prayer");
        }
        if (counter == 1.0f) {
            Skill prayer = null;
            try {
                prayer = performer.getSkills().getSkill(10066);
            }
            catch (NoSuchSkillException nss) {
                prayer = performer.getSkills().learn(10066, 1.0f);
            }
            final double mod = prayer.getKnowledge(0.0);
            time = (int)(300.0 - mod);
            performer.getCommunicator().sendNormalServerMessage("You start to pray.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to pray.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[141].getVerbString(), true, time);
            act.setTimeLeft(time);
            performer.getStatus().modifyStamina(-1000.0f);
        }
        else if (counter * 10.0f >= time) {
            if (act.getRarity() > 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            performer.getStatus().modifyStamina(-3000.0f);
            Skill prayer = null;
            try {
                prayer = performer.getSkills().getSkill(10066);
            }
            catch (NoSuchSkillException nss) {
                prayer = performer.getSkills().learn(10066, 1.0f);
            }
            final double result = prayer.skillCheck(prayer.getKnowledge(0.0) - (30.0f + Server.rand.nextFloat() * 60.0f), faith, false, counter / 3.0f);
            if (result > 0.0) {
                prayResult(performer, (float)result, deity, act.getRarity());
            }
            if (performer.isPlayer()) {
                RiteEvent.checkRiteRewards((Player)performer);
            }
            final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
            templateDeity.increaseFavor();
            done = true;
            performer.getCommunicator().sendNormalServerMessage("You finish your prayer to " + deity.name + ".");
            Server.getInstance().broadCastAction(performer.getName() + " finishes " + performer.getHisHerItsString() + " prayer to " + deity.name + ".", performer, 5);
        }
        return done;
    }
    
    static boolean pray(final Action act, final Creature performer, final Item altar, final float counter) {
        final float faith = performer.getFaith();
        if (performer.getDeity() == null) {
            performer.getCommunicator().sendNormalServerMessage("You have no deity, so you cannot pray here.");
            return true;
        }
        final Deity deity = altar.getBless();
        if (deity == null) {
            performer.getCommunicator().sendNormalServerMessage("The " + altar.getName() + " has no deity, so you cannot pray here.");
            return true;
        }
        if (performer.getDeity() != deity && ((!performer.getDeity().isHateGod() && altar.getTemplateId() != 327) || (altar.getTemplateId() != 328 && performer.getDeity().isHateGod()))) {
            performer.getCommunicator().sendNormalServerMessage("You cannot pray at altars belonging to " + deity.name + ".");
            return true;
        }
        if (altar.getParentId() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("The altar needs to be on the ground to be used.");
            return true;
        }
        boolean done = false;
        int time = act.getTimeLeft();
        if (counter == 1.0f) {
            Skill prayer = null;
            try {
                prayer = performer.getSkills().getSkill(10066);
            }
            catch (NoSuchSkillException nss) {
                prayer = performer.getSkills().learn(10066, 1.0f);
            }
            final double mod = prayer.getKnowledge(0.0);
            time = (int)(300.0 - mod - altar.getCurrentQualityLevel() / 10.0f);
            try {
                performer.getSkills().getSkill(10066);
            }
            catch (NoSuchSkillException nss2) {
                performer.getSkills().learn(10066, 1.0f);
            }
            performer.getCommunicator().sendNormalServerMessage("You start to pray at the " + altar.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to pray at the " + altar.getName() + ".", performer, 5);
            act.setTimeLeft(time);
            performer.sendActionControl(Actions.actionEntrys[141].getVerbString(), true, time);
            performer.getStatus().modifyStamina(-1000.0f);
        }
        if (counter * 10.0f >= time) {
            if (act.getRarity() > 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            performer.getStatus().modifyStamina(-3000.0f);
            Methods.sendSound(performer, "sound.religion.prayer");
            Skill prayer = null;
            try {
                prayer = performer.getSkills().getSkill(10066);
            }
            catch (NoSuchSkillException nss) {
                prayer = performer.getSkills().learn(10066, 1.0f);
            }
            final double result = prayer.skillCheck(prayer.getKnowledge(0.0) - (30.0f + Server.rand.nextFloat() * 60.0f) - altar.getCurrentQualityLevel() / 10.0f, faith, false, counter / 3.0f);
            if (result > 0.0) {
                prayResult(performer, (float)result, deity, act.getRarity() + altar.getRarity());
            }
            if (performer.isPlayer()) {
                RiteEvent.checkRiteRewards((Player)performer);
            }
            final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
            templateDeity.increaseFavor();
            if (altar.isHugeAltar()) {
                if (performer.getDeity().isHateGod()) {
                    performer.maybeModifyAlignment(-3 - altar.getRarity());
                }
                else {
                    performer.maybeModifyAlignment(3 + altar.getRarity());
                }
            }
            done = true;
            performer.getCommunicator().sendNormalServerMessage("You finish your prayer to " + performer.getDeity().name + ".");
            Server.getInstance().broadCastAction(performer.getName() + " finishes " + performer.getHisHerItsString() + " prayer to " + performer.getDeity().name + ".", performer, 5);
            if (performer.checkPrayerFaith()) {
                if (performer.getFaith() < 10.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You feel calm and solemn.");
                }
                else if (performer.getFaith() < 20.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You feel sincere devotion to " + performer.getDeity().name + ".");
                }
                else if (performer.getFaith() < 30.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You are relying on " + performer.getDeity().name + " to help you.");
                }
                else if (performer.getFaith() < 40.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("Was that a sudden gust of wind? Maybe " + performer.getDeity().name + " is pleased with your devotion?");
                }
                else if (performer.getFaith() < 60.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You can almost feel that an envoy of " + performer.getDeity().name + " is watching you.");
                }
                else if (performer.getFaith() < 80.0f || Server.rand.nextInt(5) == 0) {
                    performer.getCommunicator().sendNormalServerMessage("You have a feeling that " + performer.getDeity().name + " is here with you now.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("Deep in your heart, you feel certain that " + performer.getDeity().name + " is pleased with you and will protect you.");
                }
                if (Server.rand.nextInt(10) == 0 && performer.getMusicPlayer() != null) {
                    performer.getMusicPlayer().playPrayer();
                }
            }
        }
        return done;
    }
    
    public static boolean canBeSacrificed(final Item item) {
        return !item.isArtifact() && !item.isUnique() && ((!item.isNoDrop() && !item.isNoTrade() && !item.isLocked() && !item.isIndestructible()) || item.getTemplateId() == 168 || item.isCoin() || item.isLock());
    }
    
    public static float getFavorModifier(@Nullable final Deity deity, @Nonnull final Item item) {
        float mod = 1.0f;
        if (deity != null) {
            if (item.isWood() && deity.isWoodAffinity()) {
                mod = 2.0f;
            }
            else if (item.isMetal() && deity.isMetalAffinity()) {
                mod = 2.0f;
            }
            else if (item.isCloth() && deity.isClothAffinity()) {
                mod = 2.0f;
            }
            else if ((item.getAlchemyType() > 0 || item.getTemplateId() == 272) && deity.isMeatAffinity()) {
                if (item.getAlchemyType() > 0) {
                    if (item.isFood() && item.isChopped()) {
                        mod = 2.5f;
                    }
                    else {
                        mod = 2.0f;
                    }
                }
            }
            else if (item.isFood() && deity.isFoodAffinity()) {
                if (item.isHighNutrition()) {
                    mod = 3.0f;
                }
                else if (item.isGoodNutrition()) {
                    mod = 2.5f;
                }
                else if (item.isMediumNutrition()) {
                    mod = 2.0f;
                }
                else if (item.isLowNutrition()) {
                    mod = 1.0f;
                }
            }
            else if (item.isPottery() && deity.isClayAffinity()) {
                mod = 2.0f;
            }
        }
        if (mod <= 1.0f && item.isFood() && item.isChopped()) {
            mod = 2.0f;
        }
        return mod;
    }
    
    public static float getFavorValue(@Nullable final Deity deity, @Nonnull final Item item) {
        float newVal = item.getValue();
        if (deity != null) {
            if ((item.getAlchemyType() > 0 || item.getTemplateId() == 272) && deity.isMeatAffinity()) {
                if (item.getTemplateId() == 272) {
                    newVal = (int)(10000.0f * item.getCurrentQualityLevel() / 100.0f);
                }
                else if (item.getAlchemyType() > 0) {
                    if (item.isFood() && item.isChopped()) {
                        newVal = (int)(2500.0f * item.getCurrentQualityLevel() / 100.0f);
                    }
                    else {
                        newVal = (int)(1500 * item.getAlchemyType() * item.getCurrentQualityLevel() / 100.0f);
                    }
                }
            }
            else if (item.isFood() && deity.isFoodAffinity() && item.isDish() && item.getFoodComplexity() > 0.0f) {
                newVal = (int)(2500.0f * item.getFoodComplexity() * item.getCurrentQualityLevel() / 100.0f);
            }
        }
        if (item.isFood() && item.isChopped()) {
            newVal = (int)(2500.0f * item.getCurrentQualityLevel() / 100.0f);
        }
        return newVal;
    }
    
    static final boolean sacrifice(final Action action, final Creature performer, final Item altar) {
        boolean done = false;
        if (performer.getDeity() == null) {
            performer.getCommunicator().sendNormalServerMessage("You have no deity, so you cannot sacrifice here.");
            return true;
        }
        if (performer.getCurrentVillage() != null && performer.getCurrentVillage().isEnemy(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You can not sacrifice here.");
            return true;
        }
        final Deity deity = altar.getBless();
        if (deity == null) {
            performer.getCommunicator().sendNormalServerMessage("The " + altar.getName() + " has no deity, so you cannot perform rituals here.");
            return true;
        }
        if (performer.getDeity() != deity && (performer.getDeity().number == 4 || altar.getTemplateId() != 327)) {
            performer.getCommunicator().sendNormalServerMessage("You cannot perform rituals at altars belonging to " + deity.name + ".");
            return true;
        }
        if (altar.getParentId() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("The altar needs to be on the ground to be used.");
            return true;
        }
        if (action.currentSecond() == 1) {
            performer.getCommunicator().sendNormalServerMessage("You start to sacrifice at the " + altar.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to sacrifice at the " + altar.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[142].getVerbString(), true, 300);
        }
        else if (action.currentSecond() == 5) {
            performer.getCommunicator().sendNormalServerMessage("You call out to " + deity.name + " by all " + deity.getHisHerItsString() + " names.");
            Server.getInstance().broadCastAction(performer.getName() + " calls out to " + deity.name + " by all " + deity.getHisHerItsString() + " names.", performer, 5);
        }
        else if (action.currentSecond() == 10) {
            performer.getCommunicator().sendNormalServerMessage("You beg " + deity.name + " to accept your offerings.");
            Server.getInstance().broadCastAction(performer.getName() + " begs " + deity.name + " to accept " + performer.getHisHerItsString() + " offerings.", performer, 5);
        }
        else if (action.currentSecond() == 15) {
            performer.getCommunicator().sendNormalServerMessage("You ask " + deity.name + " for forgiveness if some of the items do not please " + deity.getHimHerItString() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " asks " + deity.name + " for forgiveness if some of the items do not please " + deity.getHimHerItString() + ".", performer, 5);
        }
        else if (action.currentSecond() == 20) {
            performer.getCommunicator().sendNormalServerMessage("You ask " + deity.name + " to bless you with " + deity.getHisHerItsString() + " favors.");
            Server.getInstance().broadCastAction(performer.getName() + " asks " + deity.name + " to bless " + performer.getHimHerItString() + " with " + deity.getHisHerItsString() + " favors.", performer, 5);
        }
        else if (action.currentSecond() == 25) {
            performer.getCommunicator().sendNormalServerMessage("You kneel at the altar in silent hope.");
            Server.getInstance().broadCastAction(performer.getName() + " kneels at the altar in silence.", performer, 5);
        }
        else if (action.currentSecond() > 30) {
            done = true;
            final Item[] items = altar.getAllItems(false);
            float favor = 0.0f;
            boolean corpse = false;
            if (items.length > 0) {
                int value = 0;
                final Village v = performer.getCitizenVillage();
                int bestRarity = 0;
                float bestQL = 0.0f;
                int startNut = 0;
                for (int x = 0; x < items.length; ++x) {
                    if (!canBeSacrificed(items[x])) {
                        performer.getCommunicator().sendNormalServerMessage(deity.name + " does not accept " + items[x].getNameWithGenus() + ".");
                    }
                    else if (items[x].isBanked()) {
                        performer.getCommunicator().sendNormalServerMessage(deity.name + " does not accept " + items[x].getNameWithGenus() + ".");
                        MethodsReligion.logger.log(Level.WARNING, performer.getName() + " tried to sac banked item!");
                        try {
                            final Item parent = items[x].getParent();
                            parent.dropItem(items[x].getWurmId(), false);
                        }
                        catch (NoSuchItemException nsi) {
                            MethodsReligion.logger.log(Level.WARNING, "Coin lacks parent when sacrificed.");
                        }
                    }
                    else if (!items[x].isPlacedOnParent()) {
                        if (!items[x].isInsidePlacedContainer()) {
                            final float newVal = getFavorValue(deity, items[x]);
                            final float mod = getFavorModifier(deity, items[x]);
                            value += (int)Math.max(10.0f, newVal * mod);
                            if (items[x].getTemplateId() == 272) {
                                corpse = true;
                            }
                            if (performer.getFaith() <= performer.getFavor() && v != null) {
                                float enchVal = Math.max(10, items[x].getValue()) * mod;
                                if (items[x].getTemplateId() == 683 || items[x].getTemplateId() == 737) {
                                    enchVal = 200000.0f * mod;
                                }
                                final ItemSpellEffects sp = items[x].getSpellEffects();
                                if (sp != null) {
                                    final SpellEffect[] spefs = sp.getEffects();
                                    for (int s = 0; s < spefs.length; ++s) {
                                        if (spefs[s].getSpellInfluenceType() != 1) {
                                            enchVal += spefs[s].power * 100.0f;
                                        }
                                    }
                                }
                                if (items[x].isWeapon() || items[x].isArmour() || items[x].isShield() || items[x].isWeaponBow() || items[x].isBowUnstringed()) {
                                    v.setFaithWar(v.getFaithWarValue() + enchVal / 100000.0f);
                                    if (enchVal > 100000.0f) {
                                        v.addHistory(performer.getName(), " adds " + enchVal / 100000.0f + " to war faith bonus");
                                    }
                                }
                                else if (items[x].isFood() || items[x].isHealing() || items[x].getMaterial() == 22 || items[x].getMaterial() == 55) {
                                    if (items[x].isLowNutrition()) {
                                        enchVal *= 10.0f;
                                    }
                                    else if (items[x].isMediumNutrition()) {
                                        enchVal *= 20.0f;
                                    }
                                    else {
                                        enchVal *= 30.0f;
                                    }
                                    v.setFaithHeal(v.getFaithHealValue() + enchVal / 100000.0f);
                                    if (enchVal > 100000.0f) {
                                        v.addHistory(performer.getName(), " adds " + enchVal / 100000.0f + " to healing faith bonus");
                                    }
                                }
                                else {
                                    if (items[x].isStone() || items[x].isWood() || items[x].isMetal()) {
                                        enchVal = Math.max(100.0f, Math.max(enchVal, items[x].getWeightGrams() / 20));
                                    }
                                    else {
                                        enchVal = Math.max(enchVal, 200.0f);
                                    }
                                    v.setFaithCreate(v.getFaithCreateValue() + enchVal / 100000.0f);
                                    if (enchVal > 100000.0f) {
                                        v.addHistory(performer.getName(), " adds " + enchVal / 1000000.0f + " to enchant faith bonus");
                                    }
                                }
                            }
                            if ((items[x].getTemplateId() == 683 || items[x].getTemplateId() == 737) && performer.getFavor() < performer.getFaith()) {
                                try {
                                    performer.setFavor(performer.getFavor() + performer.getFaith() / 10.0f);
                                }
                                catch (IOException iox) {
                                    MethodsReligion.logger.log(Level.WARNING, performer.getName() + ", " + iox.getMessage());
                                }
                            }
                            if (items[x].getRarity() > 0 && items[x].getTemplate() != null && (items[x].getWeightGrams() >= items[x].getTemplate().getWeightGrams() || items[x].getTemplate().isFish())) {
                                if (items[x].getRarity() == 1) {
                                    performer.healRandomWound(100);
                                    if (startNut < 50) {
                                        startNut = 50;
                                    }
                                }
                                else if (items[x].getRarity() == 2) {
                                    final Skills skills = performer.getSkills();
                                    final Skill[] skillarr = skills.getSkills();
                                    final Skill s2 = skillarr[Server.rand.nextInt(skillarr.length)];
                                    final double diffToMax = 100.0 - s2.getKnowledge();
                                    float divider = 1000.0f;
                                    if (s2.getType() == 1 || s2.getType() == 0) {
                                        divider *= 20.0f;
                                    }
                                    s2.setKnowledge(s2.getKnowledge() + diffToMax / divider, false);
                                    if (startNut < 70) {
                                        startNut = 70;
                                    }
                                }
                                else if (items[x].getRarity() == 3) {
                                    final Skills skills = performer.getSkills();
                                    final Skill[] skillarr = skills.getSkills();
                                    final Skill s2 = skillarr[Server.rand.nextInt(skillarr.length)];
                                    final double diffToMax = 100.0 - s2.getKnowledge();
                                    float divider = 300.0f;
                                    if (s2.getType() == 1 || s2.getType() == 0) {
                                        divider *= 20.0f;
                                    }
                                    s2.setKnowledge(s2.getKnowledge() + Math.min(1.0, diffToMax / divider), false);
                                    performer.getBody().healFully();
                                    if (startNut < 90) {
                                        startNut = 90;
                                    }
                                }
                                if (items[x].getRarity() > bestRarity) {
                                    bestRarity = items[x].getRarity();
                                }
                                if (items[x].getCurrentQualityLevel() > bestQL) {
                                    bestQL = items[x].getCurrentQualityLevel();
                                }
                            }
                            if (items[x].isCoin()) {
                                try {
                                    final Item parent2 = items[x].getParent();
                                    parent2.dropItem(items[x].getWurmId(), false);
                                }
                                catch (NoSuchItemException nsi2) {
                                    MethodsReligion.logger.log(Level.WARNING, "Coin lacks parent when sacrificed.");
                                }
                                Economy.getEconomy().returnCoin(items[x], "Sacrificed");
                            }
                            else {
                                if (items[x].getTemplate().isMissionItem() && (items[x].getQualityLevel() >= 30.0f || items[x].getTemplateId() == 683 || items[x].getTemplateId() == 737)) {
                                    MissionTriggers.activateTriggers(performer, items[x], 142, -10L, 1);
                                }
                                Items.destroyItem(items[x].getWurmId());
                            }
                            performer.achievement(607);
                            if (newVal * mod / 1000.0f >= 30.0f) {
                                performer.achievement(620);
                            }
                        }
                    }
                }
                if (bestRarity > 0) {
                    final float nut = (startNut + bestQL * (100 - startNut) / 100.0f) / 100.0f;
                    performer.getStatus().refresh(nut, true);
                }
                favor = value / 1000.0f;
                if (value < 100 && value > 0) {
                    performer.getCommunicator().sendNormalServerMessage(deity.name + " kindly accepts your offerings.");
                    Server.getInstance().broadCastAction(performer.getName() + " looks pleased as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
                }
                else if (value > 0) {
                    if (value < 50000) {
                        if (value > 10000) {
                            if (Server.rand.nextInt(20) == 0) {
                                try {
                                    final Item i = ItemFactory.createItem(5, Server.rand.nextInt(100), deity.name);
                                    performer.getInventory().insertItem(i);
                                }
                                catch (NoSuchTemplateException mso) {
                                    MethodsReligion.logger.log(Level.WARNING, performer.getName() + ": " + mso.getMessage(), mso);
                                }
                                catch (FailedException fe) {
                                    MethodsReligion.logger.log(Level.WARNING, performer.getName() + ": " + fe.getMessage(), fe);
                                }
                            }
                            if (performer.getDeity().isHateGod()) {
                                performer.maybeModifyAlignment(-1.0f);
                            }
                            else {
                                performer.maybeModifyAlignment(1.0f);
                            }
                        }
                        SoundPlayer.playSound("sound.fx.humm", altar, 1.0f);
                        performer.getCommunicator().sendNormalServerMessage("A strange sound is emitted from the " + altar.getName() + " as " + deity.name + " gladly accepts your offerings.");
                        Server.getInstance().broadCastAction(performer.getName() + " looks awed as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
                    }
                    else {
                        performer.getBody().healFully();
                        String sn;
                        if (deity.sex == 1) {
                            sn = "sound.fx.ooh.female";
                        }
                        else {
                            sn = "sound.fx.ooh.male";
                        }
                        SoundPlayer.playSound(sn, altar, 1.0f);
                        if (value < 100000) {
                            performer.getCommunicator().sendNormalServerMessage(deity.name + " is very pleased with your offerings.");
                            Server.getInstance().broadCastAction(performer.getName() + " looks very happy as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
                        }
                        else {
                            EndGameItems.locateRandomEndGameItem(performer);
                            if (value < 1000000) {
                                performer.getCommunicator().sendNormalServerMessage(deity.name + " is surprised by your generous offerings.");
                                Server.getInstance().broadCastAction(performer.getName() + " looks very happy as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
                            }
                            else {
                                performer.getCommunicator().sendNormalServerMessage(deity.name + " is extremely satisfied with your generous offerings.");
                                Server.getInstance().broadCastAction(performer.getName() + " looks very happy as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
                                final Skill channeling = performer.getChannelingSkill();
                                if (channeling != null) {
                                    final double knowledge = channeling.getKnowledge();
                                    if (knowledge < 100.0) {
                                        channeling.setKnowledge(knowledge + (100.0 - knowledge) / 3000.0, false);
                                    }
                                }
                            }
                        }
                    }
                }
                if (corpse && altar.isHugeAltar() && performer.getDeity().isHateGod()) {
                    performer.maybeModifyAlignment(-1.0f);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage(deity.name + " seems upset as the altar is empty!");
            }
            try {
                performer.setFavor(performer.getFavor() + favor);
            }
            catch (IOException iox2) {
                MethodsReligion.logger.log(Level.WARNING, performer.getName() + " " + iox2.getMessage(), iox2);
            }
        }
        return done;
    }
    
    static final boolean sacrifice(final Creature performer, final Creature target, final Item source, final Action action, final float counter) {
        boolean done = true;
        if (target.isDead()) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is dead! How can you sacrifice a dead parrot?");
            return true;
        }
        if (performer.getDeity() == null) {
            performer.getCommunicator().sendNormalServerMessage("You have no deity, so you cannot sacrifice here.");
            return true;
        }
        final double diff = performer.getFightingSkill().getKnowledge(0.0) / 4.0 - target.getBaseCombatRating();
        final int damageMin = (int)Math.min(32767.0, 65535.0 - 65535.0f / target.getBaseCombatRating() * diff);
        final int damageDiff = target.getStatus().damage - damageMin + 32767;
        if (target.getStatus().damage < damageMin) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is still strong enough to resist your attempts.");
            return true;
        }
        if (performer.getCurrentVillage() != null && performer.getCurrentVillage().isEnemy(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You can not sacrifice here.");
            return true;
        }
        Deity deity = null;
        try {
            final FaithZone z = Zones.getFaithZone(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (z == null) {
                performer.getCommunicator().sendNormalServerMessage("You cannot feel the presence of " + performer.getDeity().getName() + " here. Sacrificing the " + target.getName() + " here would do nothing.");
                return true;
            }
            deity = z.getCurrentRuler();
        }
        catch (NoSuchZoneException e) {
            performer.getCommunicator().sendNormalServerMessage("This area feels weird. Sacrificing the " + target.getName() + " here would do nothing.");
            return true;
        }
        if (deity == null) {
            performer.getCommunicator().sendNormalServerMessage("Something is wrong with this location. You think sacrificng in another spot would be better.");
            return true;
        }
        done = false;
        if (action.currentSecond() == 1) {
            done = true;
            final MissionTrigger[] missionTriggersWith;
            final MissionTrigger[] triggers = missionTriggersWith = MissionTriggers.getMissionTriggersWith(-target.getTemplate().getTemplateId(), 142, -10L);
            for (final MissionTrigger t : missionTriggersWith) {
                if (!t.isInactive()) {
                    done = false;
                }
            }
            if (!done || target.getTemplate().isEpicMissionSlayable()) {
                performer.getCommunicator().sendNormalServerMessage("You start to sacrifice the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to sacrifice the " + target.getName() + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[142].getVerbString(), true, 120);
                done = false;
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot sacrifice the " + target.getName() + " right now.");
            }
        }
        else if (action.currentSecond() == 2) {
            performer.getCommunicator().sendNormalServerMessage("You call out to " + deity.name + " by all " + deity.getHisHerItsString() + " names.");
            Server.getInstance().broadCastAction(performer.getName() + " calls out to " + deity.name + " by all " + deity.getHisHerItsString() + " names.", performer, 5);
        }
        else if (action.currentSecond() == 4) {
            if (Server.rand.nextInt(65535) > damageDiff) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " hits you and interrupts your offering to " + deity.getName() + ". This may be easier if " + target.getHeSheItString() + " was closer to death's door.");
                Server.getInstance().broadCastAction(performer.getName() + " is interrupted by the " + target.getName() + ".", performer, 5);
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("You beg " + deity.name + " to accept your offerings.");
            Server.getInstance().broadCastAction(performer.getName() + " begs " + deity.name + " to accept " + performer.getHisHerItsString() + " offerings.", performer, 5);
        }
        else if (action.currentSecond() == 6) {
            performer.getCommunicator().sendNormalServerMessage("You ask " + deity.name + " for forgiveness if the " + target.getName() + " does not please " + deity.getHimHerItString() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " asks " + deity.name + " for forgiveness if the " + target.getName() + " does not please " + deity.getHimHerItString() + ".", performer, 5);
        }
        else if (action.currentSecond() == 8) {
            if (Server.rand.nextInt(65535) > damageDiff) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " hits you and interrupts your offering to " + deity.getName() + ". This may be easier if " + target.getHeSheItString() + " was closer to death's door.");
                Server.getInstance().broadCastAction(performer.getName() + " is interrupted by the " + target.getName() + ".", performer, 5);
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("You ask " + deity.name + " to bless you with " + deity.getHisHerItsString() + " favors.");
            Server.getInstance().broadCastAction(performer.getName() + " asks " + deity.name + " to bless " + performer.getHimHerItString() + " with " + deity.getHisHerItsString() + " favors.", performer, 5);
        }
        else if (action.currentSecond() == 10) {
            performer.getCommunicator().sendNormalServerMessage("You kneel in silent hope.");
            Server.getInstance().broadCastAction(performer.getName() + " kneels in silence.", performer, 5);
        }
        else if (action.currentSecond() >= 12) {
            done = true;
            performer.achievement(611);
            if (target.getStatus().isChampion()) {
                performer.achievement(634);
            }
            float favor = 5.0f;
            if (target.hasTraits()) {
                for (int x = 0; x < 64; ++x) {
                    if (target.getStatus().isTraitBitSet(x)) {
                        if (!Traits.isTraitNegative(x) && !Traits.isTraitNeutral(x)) {
                            ++favor;
                        }
                        else if (Traits.isTraitNegative(x)) {
                            --favor;
                        }
                    }
                }
            }
            favor *= target.getBaseCombatRating();
            if (deity != performer.getDeity()) {
                favor = 0.0f;
            }
            MissionTriggers.activateTriggers(performer, -target.getTemplate().getTemplateId(), 142, -10L, 1);
            target.die(false, "Religious Sacrifice Action");
            if (favor >= 30.0f) {
                if (performer.getDeity().isHateGod()) {
                    performer.maybeModifyAlignment(-1.0f);
                }
                else {
                    performer.maybeModifyAlignment(1.0f);
                }
                SoundPlayer.playSound("sound.fx.humm", target, 1.0f);
                performer.getCommunicator().sendNormalServerMessage("You hear a strange sound as " + deity.name + " gladly accepts your offerings.");
                Server.getInstance().broadCastAction(performer.getName() + " looks awed as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
            }
            else if (favor > 0.0f) {
                performer.getCommunicator().sendNormalServerMessage(deity.name + " kindly accepts your offerings.");
                Server.getInstance().broadCastAction(performer.getName() + " looks pleased as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage(deity.name + " is silent.");
                Server.getInstance().broadCastAction(performer.getName() + " looks worried as " + deity.name + " accepts " + performer.getHisHerItsString() + " offerings.", performer, 5);
            }
            try {
                performer.setFavor(performer.getFavor() + favor);
            }
            catch (IOException iox) {
                MethodsReligion.logger.log(Level.WARNING, performer.getName() + " " + iox.getMessage(), iox);
            }
        }
        return done;
    }
    
    static boolean desecrate(final Action act, final Creature performer, @Nullable final Item item, final Item altar) {
        boolean done = false;
        final Deity deity = performer.getDeity();
        final Deity altarDeity = altar.getBless();
        if (item != null && (item.isBodyPart() || item.getTemplateId() == 0 || item.getTemplateId() == 824)) {
            performer.getCommunicator().sendNormalServerMessage("You cannot use that.");
            return true;
        }
        if (altarDeity == null) {
            performer.getCommunicator().sendNormalServerMessage("The " + altar.getName() + " has no deity!");
            return true;
        }
        boolean usingHolyItem = false;
        if (deity != null) {
            if (altarDeity.equals(deity)) {
                if (performer.faithful || performer.isChampion()) {
                    performer.getCommunicator().sendNormalServerMessage("How can you even think of desecrating " + altar.getNameWithGenus() + "?");
                    return true;
                }
            }
            else if (altarDeity.alignment > 0 && deity.alignment > 0 && performer.faithful) {
                performer.getCommunicator().sendNormalServerMessage(deity.name + " would not approve of you desecrating " + altar.getNameWithGenus() + ".");
                return true;
            }
            if (!Methods.isActionAllowed(performer, (short)83)) {
                return true;
            }
            if (item != null && item.isHolyItem() && item.isHolyItem(deity)) {
                usingHolyItem = true;
            }
        }
        Skill exorcism = null;
        try {
            exorcism = performer.getSkills().getSkill(10068);
        }
        catch (NoSuchSkillException nss) {
            if (deity != null) {
                exorcism = performer.getSkills().learn(10068, 1.0f);
            }
        }
        if (act.currentSecond() == 1) {
            if (deity != null && item != null && item.isHolyItem(deity)) {
                performer.getCommunicator().sendNormalServerMessage("You will use the power of " + deity.name + " to desecrate the altar!");
            }
            performer.getCommunicator().sendNormalServerMessage("You start to desecrate the " + altar.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to desecrate the " + altar.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[143].getVerbString(), true, 900);
        }
        else {
            float bonus = performer.zoneBonus;
            if (act.currentSecond() % 5 == 0 && act.currentSecond() != 90) {
                performer.getStatus().modifyStamina(-3000.0f);
                if (deity != null && altarDeity.equals(deity)) {
                    performer.getCommunicator().sendAlertServerMessage("This is a great risk. " + deity.name + " may notice you and become furious!");
                }
            }
            if (act.currentSecond() == 10) {
                performer.getCommunicator().sendNormalServerMessage("You think of ways to defile the " + altar.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " gives the " + altar.getName() + " a stern look.", performer, 5);
            }
            else if (act.currentSecond() == 20) {
                performer.getCommunicator().sendNormalServerMessage("You spit on the " + altar.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " spits on the " + altar.getName() + ".", performer, 5);
            }
            else if (act.currentSecond() == 30) {
                if (deity != null) {
                    performer.getCommunicator().sendNormalServerMessage("You call out to " + deity.name + " for strength.");
                    Server.getInstance().broadCastAction(performer.getName() + " calls out to " + deity.name + " for strength.", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You call " + altarDeity.name + " names.");
                    Server.getInstance().broadCastAction(performer.getName() + " calls " + altarDeity.name + " names.", performer, 5);
                }
            }
            else if (act.currentSecond() == 40 || act.currentSecond() == 75) {
                if (usingHolyItem) {
                    Methods.sendSound(performer, "sound.religion.channel");
                    performer.getCommunicator().sendNormalServerMessage("You channel the power of " + deity.name + " through your " + item.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " channels the power of " + deity.name + " through " + performer.getHisHerItsString() + " " + item.getName() + ".", performer, 5);
                }
                else if (item != null) {
                    performer.getCommunicator().sendNormalServerMessage("You bang the " + item.getName() + " on the " + altar.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " bangs " + item.getNameWithGenus() + " on the " + altar.getName() + ".", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You hit the " + altar.getName() + " hard.");
                    Server.getInstance().broadCastAction(performer.getName() + " hits the " + altar.getName() + " hard.", performer, 5);
                }
            }
            else if (act.currentSecond() == 50) {
                performer.getCommunicator().sendNormalServerMessage("You relieve yourself on the " + altar.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " relieves " + performer.getHimHerItString() + "self on the " + altar.getName() + "!", performer, 5);
            }
            else if (act.currentSecond() == 60) {
                performer.getCommunicator().sendNormalServerMessage("You try to push the " + altar.getName() + " over.");
                Server.getInstance().broadCastAction(performer.getName() + " tries to push the " + altar.getName() + " over.", performer, 5);
            }
            else if (act.currentSecond() == 70) {
                if (deity != null) {
                    Methods.sendSound(performer, "sound.religion.desecrate");
                    performer.getCommunicator().sendNormalServerMessage("In the name of " + deity.name + " you demand the presence of " + altarDeity.name + " to leave this place.");
                    Server.getInstance().broadCastAction(performer.getName() + " demands that the presence of " + altarDeity.name + " leaves this place in the name of " + deity.name + ".", performer, 5);
                }
                else if (item != null) {
                    if (item.isFood() || item.isCloth()) {
                        performer.getCommunicator().sendNormalServerMessage("You throw the " + item.getName() + " on the " + altar.getName() + " in disgust.");
                        Server.getInstance().broadCastAction(performer.getName() + " throws " + item.getNameWithGenus() + " on the " + altar.getName() + " in disgust.", performer, 5);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You try to scratch the " + altar.getName() + " with the " + item.getName() + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " tries to scratch the " + altar.getName() + " with the " + item.getName() + ".", performer, 5);
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You kick the " + altar.getName() + " hard.");
                    Server.getInstance().broadCastAction(performer.getName() + " kick the " + altar.getName() + " hard.", performer, 5);
                }
            }
            else if (act.currentSecond() == 80) {
                performer.getCommunicator().sendNormalServerMessage("You notice a weird silence.");
                Server.getInstance().broadCastAction(performer.getName() + " stops and listens.", performer, 5);
            }
            else if (act.currentSecond() >= 90) {
                Methods.sendSound(performer, "sound.religion.desecrate");
                done = true;
                if (item != null) {
                    bonus = performer.zoneBonus + item.getCurrentQualityLevel() / 5.0f;
                    if (!usingHolyItem) {
                        bonus = performer.zoneBonus + item.getCurrentQualityLevel() / 10.0f;
                    }
                    if (item.getTemplateId() == 340) {
                        bonus += 50.0f;
                    }
                }
                double power = 0.0;
                if (exorcism != null) {
                    power = exorcism.skillCheck(altar.getCurrentQualityLevel(), bonus, false, act.getCounterAsFloat());
                }
                else {
                    power = bonus - Server.rand.nextInt(200);
                }
                float alignMod = 0.0f;
                if (altarDeity.alignment > 0) {
                    alignMod = -2.0f;
                }
                else if (altarDeity.alignment < 0) {
                    alignMod = 2.0f;
                }
                final boolean hugeAltar = altar.isHugeAltar();
                if (power > 0.0 && !hugeAltar) {
                    performer.getCommunicator().sendNormalServerMessage("The " + altar.getName() + " crumbles to dust!");
                    Server.getInstance().broadCastAction("The " + altar.getName() + " crumbles to dust!", performer, 5);
                    Items.destroyItem(altar.getWurmId());
                    if (deity != null) {
                        if (altarDeity.equals(deity)) {
                            performer.getCommunicator().sendNormalServerMessage(deity.name + " has noticed you and is furious!");
                            byte type = 6;
                            final int rand = Server.rand.nextInt(3);
                            if (rand != 0) {
                                if (rand == 1) {
                                    type = 5;
                                }
                                else if (rand == 2) {
                                    type = 2;
                                }
                            }
                            try {
                                performer.addWoundOfType(null, type, 0, true, 1.0f, true, (float)Math.abs(power) * 200.0f, (float)Math.abs(power), (float)Math.abs(power), false, false);
                            }
                            catch (Exception ex) {
                                MethodsReligion.logger.log(Level.WARNING, performer.getName(), ex);
                            }
                            try {
                                performer.setFaith(performer.getFaith() - 5.0f);
                            }
                            catch (IOException iox) {
                                MethodsReligion.logger.log(Level.WARNING, performer.getName(), iox);
                            }
                        }
                        else if (altarDeity.alignment > 0 && deity.alignment > 0 && Server.rand.nextInt(120) > performer.getFaith()) {
                            performer.getCommunicator().sendNormalServerMessage(deity.name + " has noticed you and is very upset!");
                            performer.modifyFaith(-1.0f);
                            try {
                                performer.setFavor(performer.getFavor() - 20.0f);
                            }
                            catch (IOException iox2) {
                                MethodsReligion.logger.log(Level.WARNING, performer.getName(), iox2);
                            }
                        }
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You are hit by a sudden pain!");
                    Server.getInstance().broadCastAction(performer.getName() + " suddenly writhes in pain!", performer, 5);
                    byte type = 6;
                    final int rand = Server.rand.nextInt(3);
                    double isev = 0.0;
                    double psev = 0.0;
                    double pow = -power;
                    if (rand == 1) {
                        type = 5;
                        psev = pow;
                    }
                    else if (rand == 2) {
                        type = 2;
                    }
                    else {
                        isev = pow;
                    }
                    final EndGameItem three = EndGameItems.getGoodAltar();
                    if (three != null && three.getItem() != null && three.getItem().getWurmId() == altar.getWurmId() && performer.getKingdomTemplateId() != 0 && performer.getKingdomTemplateId() != 3) {
                        performer.setReputation(performer.getReputation() - 70);
                    }
                    if (hugeAltar && power > 0.0) {
                        pow = power;
                        altar.setDamage(altar.getDamage() + 0.1f);
                    }
                    performer.addWoundOfType(null, type, 1, true, 1.0f, true, pow * 200.0, (float)psev, (float)isev, false, false);
                }
                performer.maybeModifyAlignment(alignMod);
            }
        }
        return done;
    }
    
    public static final boolean listen(final Creature performer, final Creature target, final Action act, final float counter) {
        boolean done = false;
        final Deity deity = performer.getDeity();
        if (target.isInvulnerable()) {
            target.getCommunicator().sendNormalServerMessage("You must not be invulnerable in order to confess.");
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " must not be invulnerable!");
            return true;
        }
        if (target.isMoving()) {
            target.getCommunicator().sendNormalServerMessage("You must stand still in order to confess.");
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " must stand still!");
            return true;
        }
        final Long last = MethodsReligion.listenedTo.get(target.getWurmId());
        if (last != null && System.currentTimeMillis() - last < 86400000L) {
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " apparently confessed recently. That will have to do for another " + Server.getTimeFor(last + 86400000L - System.currentTimeMillis()) + ".");
            target.getCommunicator().sendNormalServerMessage("You confessed recently. That will have to do for another " + Server.getTimeFor(last + 86400000L - System.currentTimeMillis()) + ".");
            return true;
        }
        if (counter == 1.0f) {
            target.getCommunicator().sendNormalServerMessage(performer.getName() + " focuses on you and awaits your confession.");
            performer.getCommunicator().sendNormalServerMessage("You start to listen to the confession of " + target.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to hear the confessions of " + target.getName() + ".", performer, target, 5);
            performer.sendActionControl(Actions.actionEntrys[115].getVerbString(), true, 400);
        }
        else {
            if (act.currentSecond() == 5) {
                if (deity.isHateGod()) {
                    target.getCommunicator().sendNormalServerMessage("You open up with something fairly harmless.");
                    performer.getCommunicator().sendNormalServerMessage("You nod and think of ways to humiliate " + target.getName() + ".");
                }
                else {
                    target.getCommunicator().sendNormalServerMessage("You open up with something fairly harmless.");
                    performer.getCommunicator().sendNormalServerMessage("You nod and hope that " + target.getName() + " hasn't done too many bad things.");
                }
                Server.getInstance().broadCastAction(performer.getName() + " nods and mumbles something as " + target.getName() + " confesses.", performer, target, 5);
            }
            if (act.currentSecond() == 10) {
                if (deity.isLibila()) {
                    target.getCommunicator().sendNormalServerMessage("You try to distract by sounding very worried that you may not do enough in her service.");
                    performer.getCommunicator().sendNormalServerMessage("Aha! The foolish " + target.getName() + " shows weakness!");
                }
                else {
                    target.getCommunicator().sendNormalServerMessage("You show ridiculous amounts of grief over the trifle you just told.");
                    performer.getCommunicator().sendNormalServerMessage(target.getName() + " plays over. Surely " + target.getHeSheItString() + " can't be that remorseful. This is a sin if anything!");
                }
                Server.getInstance().broadCastAction(performer.getName() + " sits silent and listens sincerely while " + target.getName() + " looks very worried.", performer, target, 5);
            }
            else if (act.currentSecond() == 20) {
                if (deity.isHateGod()) {
                    target.getCommunicator().sendNormalServerMessage("You try to hide your weakness in a subordinate clause but alas! " + performer.getName() + " stabs at it like a snake!");
                    performer.getCommunicator().sendNormalServerMessage("Hold it! " + target.getName() + " tries to hide something!");
                }
                else {
                    target.getCommunicator().sendNormalServerMessage("Elaborately you tell about a recent failure of yours, trying to smear out the importance with many words. It doesn't work at all and " + performer.getName() + " reprimands you.");
                    performer.getCommunicator().sendNormalServerMessage(target.getName() + " tells you a story, the length of which only upsets you more. You have to reprimand " + target.getHimHerItString() + " sternly. This has gone too far!");
                }
                Server.getInstance().broadCastAction(performer.getName() + " suddenly asks a stern question, and " + target.getName() + " goes bleak.", performer, target, 5);
            }
            else if (act.currentSecond() == 30) {
                if (deity.isHateGod()) {
                    target.getCommunicator().sendNormalServerMessage("You apologize sincerely, practically begging for your life!");
                    performer.getCommunicator().sendNormalServerMessage(target.getName() + " whimpers beneath the might of Libila!");
                }
                else {
                    target.getCommunicator().sendNormalServerMessage("You regret your ways and ask " + performer.getName() + " what you can do to repent.");
                    performer.getCommunicator().sendNormalServerMessage(target.getName() + " asks what " + target.getHeSheItString() + " should do for absolution.");
                }
                Server.getInstance().broadCastAction("Through tearfilled eyes, " + target.getName() + " asks " + performer.getName() + " a question.", performer, target, 5);
            }
            else if (act.currentSecond() >= 40) {
                if (deity.isLibila()) {
                    target.getCommunicator().sendNormalServerMessage(performer.getName() + " scorns you and tells you to give " + performer.getHimHerItString() + " 10 copper coins for Libila to forgive you.");
                    performer.getCommunicator().sendNormalServerMessage("You decide that you can probably fool " + target.getName() + " to give you 10 copper pieces and say that it is the penance " + target.getHeSheItString() + " has to pay.");
                }
                else {
                    int templateId = 29;
                    if (deity.isMagranon()) {
                        templateId = 204;
                    }
                    else if (deity.isFo()) {
                        templateId = 436;
                    }
                    String name = "10 copper";
                    try {
                        final ItemTemplate it = ItemTemplateFactory.getInstance().getTemplate(templateId);
                        name = it.getNameWithGenus();
                    }
                    catch (NoSuchTemplateException ex) {}
                    target.getCommunicator().sendNormalServerMessage(performer.getName() + " thinks for a while and asks you to sacrifice " + name + ".");
                    performer.getCommunicator().sendNormalServerMessage("You decide that a good penance is for " + target.getName() + " to sacrifice " + name + ". That will surely please " + deity.name + ".");
                }
                Server.getInstance().broadCastAction(target.getName() + " finishes " + target.getHisHerItsString() + " confession and " + performer.getName() + " seems to tell " + target.getHimHerItString() + " what to do.", performer, target, 5);
                done = true;
                MethodsReligion.listenedTo.put(target.getWurmId(), System.currentTimeMillis());
                performer.achievement(612);
                try {
                    final Skill preaching = performer.getSkills().getSkill(10065);
                    preaching.skillCheck(1.0, 0.0, false, 30.0f);
                }
                catch (NoSuchSkillException nss) {
                    performer.getSkills().learn(10065, 1.0f);
                }
                if (deity.isHateGod()) {
                    performer.maybeModifyAlignment(-1.0f);
                    if (target.isPriest()) {
                        target.maybeModifyAlignment(-1.0f);
                    }
                    else {
                        target.maybeModifyAlignment(-5.0f);
                    }
                }
                else {
                    performer.maybeModifyAlignment(1.0f);
                    if (target.isPriest()) {
                        target.maybeModifyAlignment(1.0f);
                    }
                    else {
                        target.maybeModifyAlignment(5.0f);
                    }
                }
            }
        }
        return done;
    }
    
    public static final boolean holdSermon(final Creature performer, final Item altar, final Item holySymbol, final Action act, final float counter) {
        boolean done = false;
        final Deity deity = performer.getDeity();
        final Long last = MethodsReligion.lastHeldSermon.get(performer.getWurmId());
        if (last != null && System.currentTimeMillis() - last < 10800000L) {
            performer.getCommunicator().sendNormalServerMessage("You held a sermon recently and you are still recovering psychically. You need to wait another " + Server.getTimeFor(last + 10800000L - System.currentTimeMillis()) + ".");
            return true;
        }
        if (!holySymbol.isHolyItem() || !holySymbol.isHolyItem(performer.getDeity())) {
            performer.getCommunicator().sendNormalServerMessage("You need to use your own holy symbol to initate the sermon.");
            return true;
        }
        if (counter == 1.0f) {
            final int tilex = performer.getCurrentTile().tilex;
            final int tiley = performer.getCurrentTile().tiley;
            int numfollowers = 0;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    final VolaTile t = Zones.getTileOrNull(tilex + x, tiley + y, performer.isOnSurface());
                    if (t != null) {
                        final Creature[] crets = t.getCreatures();
                        for (int c = 0; c < crets.length; ++c) {
                            if (crets[c].getWurmId() != performer.getWurmId() && crets[c].isPlayer() && crets[c].getDeity() != null && crets[c].getKingdomId() == performer.getKingdomId()) {
                                ++numfollowers;
                            }
                        }
                    }
                }
            }
            if (numfollowers <= 0) {
                performer.getCommunicator().sendNormalServerMessage("You need at least one follower to attend the sermon.");
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage("You initiate the sermon by brandishing your " + holySymbol.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " initiates the sermon by brandishing " + performer.getHisHerItsString() + " " + holySymbol.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[216].getVerbString(), true, 600);
        }
        else {
            if (act.currentSecond() == 5) {
                performer.getCommunicator().sendNormalServerMessage("You clear your throat and evaluate your audience.");
                Server.getInstance().broadCastAction(performer.getName() + " clears " + performer.getHisHerItsString() + " throat and looks at the audience sternly.", performer, 5);
            }
            if (act.currentSecond() == 10) {
                if (deity.isHateGod()) {
                    performer.getCommunicator().sendNormalServerMessage("The rabble you have to deal with these days! Let's scare them into submission.");
                    Server.getInstance().broadCastAction(performer.getName() + " screams something about how infidels will be slaughtered and fed to the wolves. You suddenly realize that " + performer.getHeSheItString() + " means you, the audience.", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("These people need guidance and motivation in their hardship. You praise " + deity.name + " for " + deity.getHisHerItsString() + " glory.");
                    Server.getInstance().broadCastAction(performer.getName() + " speaks about " + deity.name + " and how " + deity.getHeSheItString() + " will help you through any hardships.", performer, 5);
                }
            }
            else if (act.currentSecond() == 20) {
                if (Server.rand.nextBoolean()) {
                    if (deity.isLibila()) {
                        performer.getCommunicator().sendNormalServerMessage("Clearly that didn't do the trick. More gore!");
                        Server.getInstance().broadCastAction(performer.getName() + " keeps on about how Libila will boil you alive and chew on your loose skin unless you assault the enemy in her name.", performer, 5);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You explain how we all have our doubts and feel lost some times but in the end we will be saved by " + deity.name + "!");
                        Server.getInstance().broadCastAction(performer.getName() + " comforts you and explains how " + deity.name + " will always come to your aid in the end.", performer, 5);
                    }
                }
            }
            else if (act.currentSecond() == 30) {
                if (deity.getFavor() > 100000) {
                    performer.getCommunicator().sendNormalServerMessage("You sense that " + deity.getName() + " is brimming with power. Maybe you can channel it somehow?");
                    Server.getInstance().broadCastAction(performer.getName() + " speaks of miracles soon to come!", performer, 5);
                }
                else if (deity.getFavor() > 50000) {
                    performer.getCommunicator().sendNormalServerMessage("You explain how " + deity.name + " is strong but still needs you to work faithfully so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.");
                    Server.getInstance().broadCastAction(performer.getName() + " explains how " + deity.name + " is strong but still needs you to work faithfully so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.", performer, 5);
                }
                else if (deity.getFavor() > 25000) {
                    performer.getCommunicator().sendNormalServerMessage("You explain how " + deity.name + " is still waning and needs you to keep working for " + deity.getHimHerItString() + " so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.");
                    Server.getInstance().broadCastAction(performer.getName() + " explains how " + deity.name + " is still waning and needs you to keep working for " + deity.getHimHerItString() + " so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You explain how " + deity.name + " is in the waning crescent and needs you to do a lot more for " + deity.getHimHerItString() + " so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.");
                    Server.getInstance().broadCastAction(performer.getName() + " explains how " + deity.name + " is in the waning crescent and needs you to do more for " + deity.getHimHerItString() + " so that " + deity.getHeSheItString() + " may manifest " + deity.getHisHerItsString() + " powers.", performer, 5);
                }
            }
            else if (act.currentSecond() == 40) {
                String todo = "sacrifice something";
                if (deity.isHateGod()) {
                    final int num = Server.rand.nextInt(5);
                    if (num == 0) {
                        todo = "desecrate an enemy altar";
                    }
                    else if (num == 1) {
                        todo = "confess";
                    }
                    else if (num == 2) {
                        todo = "butcher enemy corpses";
                    }
                    else if (num == 3) {
                        todo = "pray regularly";
                    }
                    else if (num == 4) {
                        todo = "attend sermon";
                    }
                }
                else {
                    final int num = Server.rand.nextInt(5);
                    if (num == 0) {
                        todo = "desecrate an enemy altar";
                    }
                    else if (num == 1) {
                        todo = "confess";
                    }
                    else if (num == 2) {
                        todo = "bury a corpse";
                    }
                    else if (num == 3) {
                        todo = "heal your next of kin";
                    }
                    else if (num == 4) {
                        todo = "pray regularly";
                    }
                    else if (num == 5) {
                        todo = "attend sermon";
                    }
                }
                performer.getCommunicator().sendNormalServerMessage("Today you decide to suggest that they " + todo + ".");
                Server.getInstance().broadCastAction(performer.getName() + " suggests that you " + todo + ".", performer, 5);
            }
            else if (act.currentSecond() == 50) {
                if (Deities.hasValreiStatuses() && Server.rand.nextBoolean()) {
                    String status = Deities.getRandomStatusFor(deity.getNumber());
                    if (status.length() == 0 || Server.rand.nextBoolean()) {
                        status = Deities.getRandomStatus();
                    }
                    if (status.length() > 0) {
                        performer.getCommunicator().sendNormalServerMessage("You have a vision: " + status);
                        Server.getInstance().broadCastAction(performer.getName() + " claims to have a vision: " + status, performer, 5);
                    }
                }
                else if (EpicServerStatus.getCurrentScenario() != null && EpicServerStatus.getCurrentScenario().isCurrent()) {
                    performer.getCommunicator().sendNormalServerMessage("You have the vision that " + EpicServerStatus.getCurrentScenario().getScenarioQuest() + '.');
                    Server.getInstance().broadCastAction(performer.getName() + " claims that " + EpicServerStatus.getCurrentScenario().getScenarioQuest() + '.', performer, 5);
                }
                if (Servers.localServer.PVPSERVER) {
                    performer.getCommunicator().sendNormalServerMessage("There are enemies of the faith and these must be stopped. You urge the flock to be vigilant, and wary of strangers and infidels.");
                    Server.getInstance().broadCastAction(performer.getName() + " explains that the enemies of " + deity.name + " must be summarily slaughtered.", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("In distant lands there are enemies of the faith and people struggle to keep them at bay. You urge the flock to send these people a thought and a prayer.");
                    Server.getInstance().broadCastAction(performer.getName() + " explains that far away other people of the faith fight horrible enemies of " + deity.name + ". You are urged to send a thought and a prayer to these valiant protectors.", performer, 5);
                }
            }
            else if (act.currentSecond() >= 60) {
                performer.getCommunicator().sendNormalServerMessage("You finish this sermon by yet again praising " + deity.name + " and ask everyone to pray together with you.");
                Server.getInstance().broadCastAction(performer.getName() + " finishes the sermon by asking you to join " + performer.getHimHerItString() + " in a prayer to " + deity.name + ".", performer, 5);
                done = true;
                MethodsReligion.lastHeldSermon.put(performer.getWurmId(), System.currentTimeMillis());
                Skill preaching = null;
                try {
                    preaching = performer.getSkills().getSkill(10065);
                }
                catch (NoSuchSkillException nss) {
                    preaching = performer.getSkills().learn(10065, 1.0f);
                }
                final Set<Creature> listeners = new HashSet<Creature>();
                final int tilex2 = performer.getCurrentTile().tilex;
                final int tiley2 = performer.getCurrentTile().tiley;
                int numfollowers2 = 0;
                for (int x2 = -5; x2 < 5; ++x2) {
                    for (int y2 = -5; y2 < 5; ++y2) {
                        final VolaTile t2 = Zones.getTileOrNull(tilex2 + x2, tiley2 + y2, performer.isOnSurface());
                        if (t2 != null) {
                            final Creature[] crets2 = t2.getCreatures();
                            for (int c2 = 0; c2 < crets2.length; ++c2) {
                                if (crets2[c2].getWurmId() != performer.getWurmId()) {
                                    listeners.add(crets2[c2]);
                                    if (crets2[c2].isPaying() && crets2[c2].isPlayer() && crets2[c2].getDeity() != null && crets2[c2].getKingdomId() == performer.getKingdomId()) {
                                        ++numfollowers2;
                                    }
                                }
                            }
                        }
                    }
                }
                int numsTouched = 0;
                if (listeners.size() > 0) {
                    for (final Creature c3 : listeners) {
                        if (c3.isPlayer() && c3.getDeity() != null && c3.getKingdomId() == performer.getKingdomId()) {
                            if (deity.isHateGod()) {
                                if (c3.maybeModifyAlignment(Math.max(-4 - altar.getRarity(), -numfollowers2 - altar.getRarity())) && c3.isPaying()) {
                                    ++numsTouched;
                                }
                            }
                            else if (c3.maybeModifyAlignment(Math.min(4 + altar.getRarity(), numfollowers2 + altar.getRarity())) && c3.isPaying()) {
                                ++numsTouched;
                            }
                            c3.achievement(610);
                        }
                    }
                    if (preaching.skillCheck(Math.max(1, numsTouched * 3), altar, 0.0, false, 10.0f) > 0.0) {
                        if (deity.isHateGod()) {
                            performer.maybeModifyAlignment(Math.max(-4, Math.min(-1, -numsTouched)));
                        }
                        else {
                            performer.maybeModifyAlignment(Math.min(4, Math.max(1, numsTouched)));
                        }
                    }
                    float rarityMod = 1.0f;
                    if (altar.getRarity() > 0) {
                        rarityMod += altar.getRarity() * 0.01f;
                    }
                    final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
                    templateDeity.setFavor(templateDeity.getFavor() + numsTouched + altar.getRarity());
                    if (numsTouched > 3) {
                        performer.getCommunicator().sendNormalServerMessage(deity.name + " is mighty pleased with you!");
                        try {
                            performer.setFaith(performer.getFaith() + numsTouched * rarityMod / 50.0f);
                        }
                        catch (IOException iox) {
                            MethodsReligion.logger.log(Level.WARNING, performer.getName() + " " + iox.getMessage());
                        }
                    }
                    if (numsTouched > 5) {
                        try {
                            ((Player)performer).getSaveFile().setNumFaith((byte)0, System.currentTimeMillis());
                        }
                        catch (IOException ex) {}
                    }
                    performer.achievement(639);
                }
            }
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(MethodsReligion.class.getName());
        listenedTo = new HashMap<Long, Long>();
        lastReceivedAlignment = new HashMap<Long, Long>();
        lastHeldSermon = new HashMap<Long, Long>();
    }
}
