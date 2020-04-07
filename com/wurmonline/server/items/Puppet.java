// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.util.HashMap;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.combat.CombatEngine;
import java.util.logging.Level;
import com.wurmonline.server.creatures.NoArmourException;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.Server;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.behaviours.Action;
import java.util.Random;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class Puppet implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    private static final Map<Long, Long> puppetmasters;
    private static final Random random;
    private static final String[] FLSeventyPlus1;
    private static final String[] LFSeventyPlus1;
    private static final String[] FLSeventyPlus2;
    private static final String[] LFSeventyPlus2;
    private static final String[] VLSeventyPlus1;
    private static final String[] LVSeventyPlus1;
    private static final String[] VLSeventyPlus2;
    private static final String[] LVSeventyPlus2;
    private static final String[] MLSeventyPlus1;
    private static final String[] LMSeventyPlus1;
    private static final String[] MLSeventyPlus2;
    private static final String[] LMSeventyPlus2;
    private static final String[] VFSeventyPlus1;
    private static final String[] VFSeventyPlus2;
    private static final String[] FVSeventyPlus1;
    private static final String[] FVSeventyPlus2;
    private static final String[] VMSeventyPlus1;
    private static final String[] MVSeventyPlus1;
    private static final String[] VMSeventyPlus2;
    private static final String[] MVSeventyPlus2;
    private static final String[] FMSeventyPlus1;
    private static final String[] MFSeventyPlus1;
    private static final String[] FMSeventyPlus2;
    private static final String[] MFSeventyPlus2;
    private static final String[] AFSeventyPlus1;
    private static final String[] FASeventyPlus1;
    private static final String[] AFSeventyPlus2;
    private static final String[] FASeventyPlus2;
    private static final String[] AVSeventyPlus1;
    private static final String[] VASeventyPlus1;
    private static final String[] AVSeventyPlus2;
    private static final String[] VASeventyPlus2;
    private static final String[] ALSeventyPlus1;
    private static final String[] LASeventyPlus1;
    private static final String[] ALSeventyPlus2;
    private static final String[] LASeventyPlus2;
    private static final String[] AMSeventyPlus1;
    private static final String[] MASeventyPlus1;
    private static final String[] AMSeventyPlus2;
    private static final String[] MASeventyPlus2;
    private static final String[] VFSeventyMinus1;
    private static final String[] FVSeventyMinus1;
    private static final String[] VFSeventyMinus2;
    private static final String[] FVSeventyMinus2;
    private static final String[] MLSeventyMinus1;
    private static final String[] LMSeventyMinus1;
    private static final String[] MLSeventyMinus2;
    private static final String[] LMSeventyMinus2;
    private static final String[] FMSeventyMinus1;
    private static final String[] MFSeventyMinus1;
    private static final String[] FMSeventyMinus2;
    private static final String[] MFSeventyMinus2;
    private static final String[] VMSeventyMinus1;
    private static final String[] MVSeventyMinus1;
    private static final String[] VMSeventyMinus2;
    private static final String[] MVSeventyMinus2;
    private static final String[] FLSeventyMinus1;
    private static final String[] LFSeventyMinus1;
    private static final String[] FLSeventyMinus2;
    private static final String[] LFSeventyMinus2;
    private static final String[] VLSeventyMinus1;
    private static final String[] LVSeventyMinus1;
    private static final String[] VLSeventyMinus2;
    private static final String[] LVSeventyMinus2;
    private static final String[] item1;
    private static final String[] item2;
    private static final String[] item3;
    private static final String[] item4;
    private static final String[] item5;
    private static final String[] item6;
    private static final String[] item7;
    private static final String[] item8;
    private static final String[] item9;
    private static final String[] item10;
    private static final String[] item11;
    private static final String[] item12;
    private static final String[] item13;
    private static final String[] item14;
    private static final String[] item15;
    private static final String[] item16;
    private static final String[] FFbasic1;
    private static final String[] MMbasic1;
    private static final String[] LLbasic1;
    private static final String[] VVbasic1;
    private static final String[] FMbasic1;
    private static final String[] MFbasic1;
    private static final String[] FMbasic2;
    private static final String[] MFbasic2;
    private static final String[] FLbasic1;
    private static final String[] LFbasic1;
    private static final String[] FLbasic2;
    private static final String[] LFbasic2;
    private static final String[] FVbasic1;
    private static final String[] VFbasic1;
    private static final String[] FVbasic2;
    private static final String[] VFbasic2;
    private static final String[] MVbasic1;
    private static final String[] VMbasic1;
    private static final String[] MVbasic2;
    private static final String[] VMbasic2;
    private static final String[] MLbasic1;
    private static final String[] LMbasic1;
    private static final String[] MLbasic2;
    private static final String[] LMbasic2;
    private static final String[] VLbasic1;
    private static final String[] LVbasic1;
    private static final String[] VLbasic2;
    private static final String[] LVbasic2;
    
    public static final int getConversationLength(final boolean puppetOneStarted, final Action act, final Item puppetOne, final Item puppetTwo, final Creature performerOne, final Creature performerTwo, final int counter) {
        Puppet.random.setSeed(performerOne.getWurmId());
        final int performerTypeOne = Puppet.random.nextInt(24);
        Puppet.random.setSeed(performerTwo.getWurmId());
        final int performerTypeTwo = Puppet.random.nextInt(24);
        final int deityOne = getDeityFor(puppetOne);
        final int deityTwo = getDeityFor(puppetTwo);
        float courierOne = puppetOne.getSpellCourierBonus();
        float courierTwo = puppetTwo.getSpellCourierBonus();
        if (deityOne == 0) {
            courierOne = 0.0f;
        }
        if (deityTwo == 0) {
            courierTwo = 0.0f;
        }
        return getConversationArrayFor(act.hashCode(), puppetOne, puppetTwo, deityOne, performerTypeOne, courierOne, deityTwo, performerTypeTwo, courierTwo).length;
    }
    
    public static final boolean sendConversationString(final Action act, final Item puppetOne, final Item puppetTwo, final Creature performerOne, final Creature performerTwo, final int counter) {
        String toReturn = "";
        Puppet.random.setSeed(performerOne.getWurmId());
        final int performerTypeOne = Puppet.random.nextInt(24);
        Puppet.random.setSeed(performerTwo.getWurmId());
        final int performerTypeTwo = Puppet.random.nextInt(24);
        final int deityOne = getDeityFor(puppetOne);
        final int deityTwo = getDeityFor(puppetTwo);
        float courierOne = puppetOne.getSpellCourierBonus();
        float courierTwo = puppetTwo.getSpellCourierBonus();
        if (deityOne == 0) {
            courierOne = 0.0f;
        }
        if (deityTwo == 0) {
            courierTwo = 0.0f;
        }
        String[] toUse = Puppet.emptyStringArray;
        Skill puppeteering = null;
        try {
            puppeteering = performerOne.getSkills().getSkill(10087);
        }
        catch (NoSuchSkillException nss) {
            puppeteering = performerOne.getSkills().learn(10087, 1.0f);
        }
        toUse = getConversationArrayFor(act.hashCode(), puppetOne, puppetTwo, deityOne, performerTypeOne, courierOne, deityTwo, performerTypeTwo, courierTwo);
        if (puppeteering.skillCheck(counter * (3.0f - 0.2f * puppetOne.getRarity() - 0.2f * puppetTwo.getRarity()) + (courierOne + courierTwo) / 4.0f, puppetOne, 0.0, false, 5.0f) <= 0.0) {
            sendFailString(performerOne, puppetOne, courierOne, deityOne);
            if (counter < toUse.length) {
                return false;
            }
        }
        else if (counter < toUse.length) {
            if (counter == 0 && toUse[counter].equals("")) {
                return false;
            }
            if (counter == 0 && toUse[counter].startsWith("@")) {
                act.setFailSecond(1.0f);
            }
            final StringBuilder s = new StringBuilder();
            s.append(performerOne.getNamePossessive());
            s.append(" ");
            s.append(puppetOne.getName());
            s.append(": '");
            toUse[counter] = toUse[counter].replace("@", "");
            if (Server.rand.nextInt(100) == 0) {
                toUse[counter] = toUse[counter].replace("WHAT IS HAPPENING", "WHAT HAPPEN");
                toUse[counter] = toUse[counter].replace("How sad.", "We get signal.");
            }
            s.append(toUse[counter]);
            s.append("'");
            toReturn = s.toString();
            performerOne.getCommunicator().sendNormalServerMessage("Your " + puppetOne.getName() + ": '" + toUse[counter] + "'");
            Server.getInstance().broadCastAction(toReturn, performerOne, 5);
            return false;
        }
        return true;
    }
    
    private static final void sendFailString(final Creature performer, final Item puppet, final float enchantLevel, final int puppetDeity) {
        final int failType = Server.rand.nextInt(12);
        String toSendPerformer = "You fail to find words. How embarrassing!";
        String toSendBroadCast = performer.getName() + " is silent and blushes.";
        boolean red = false;
        switch (failType) {
            case 0: {
                toSendPerformer = "You forget what it was you were supposed to say!";
                toSendBroadCast = performer.getName() + " seems to look for words.";
                if (puppet.isPuppet() && Server.rand.nextInt(100) < enchantLevel) {
                    red = true;
                    final int x = Server.rand.nextInt(10);
                    if (x == 0) {
                        toSendPerformer = "The " + puppet.getName() + " glares at you!";
                        toSendBroadCast = performer.getName() + " looks shocked as " + performer.getHisHerItsString() + " " + puppet.getName() + " glares at " + performer.getHisHerItsString() + "!";
                    }
                    else if (x == 1) {
                        toSendPerformer = "Your " + puppet.getName() + " snores from boredom!";
                        toSendBroadCast = performer.getName() + " looks shocked as " + performer.getHisHerItsString() + " " + puppet.getName() + " snores from boredom!";
                    }
                    else if (x == 2) {
                        toSendPerformer = "Your " + puppet.getName() + " burps loudly!";
                        toSendBroadCast = performer.getName() + " looks at " + performer.getHisHerItsString() + " " + puppet.getName() + " with disbelief as it burps loudly!";
                    }
                    else if (x == 3) {
                        toSendPerformer = "The " + puppet.getName() + " gives you a menacing look. Watch out!";
                        toSendBroadCast = performer.getNamePossessive() + " " + puppet.getName() + " gives " + performer.getHimHerItString() + " a dark, menacing look!";
                    }
                    else if (x == 3) {
                        toSendPerformer = "The " + puppet.getName() + " points its finger and laughs at you!";
                        toSendBroadCast = performer.getNamePossessive() + " " + puppet.getName() + " points its finger and laughs at " + performer.getHimHerItString() + "!";
                    }
                    else if (x == 4) {
                        toSendPerformer = "The " + puppet.getName() + " suddenly bonks you on the head!";
                        toSendBroadCast = performer.getNamePossessive() + " " + puppet.getName() + " bonks " + performer.getHimHerItString() + " in the head!";
                    }
                    else if (x == 5) {
                        toSendPerformer = "The " + puppet.getName() + " makes a farting sound!";
                        toSendBroadCast = performer.getNamePossessive() + " farts loudly. Or was it the " + puppet.getName() + "?";
                    }
                    else if (puppetDeity == 4) {
                        toSendPerformer = "The " + puppet.getName() + " bites you!";
                        toSendBroadCast = performer.getName() + " is suddenly assaulted by " + performer.getHisHerItsString() + " " + puppet.getName() + "!";
                        float armourMod = 1.0f;
                        try {
                            final byte pos = performer.getBody().getRandomWoundPos();
                            try {
                                final byte bodyPosition = ArmourTemplate.getArmourPosition(pos);
                                final Item armour = performer.getArmour(bodyPosition);
                                armourMod = ArmourTemplate.calculateDR(armour, (byte)3);
                                armour.setDamage(armour.getDamage() + Math.max(0.05f, Math.min(1.0f, 7000.0f * ArmourTemplate.getArmourDamageModFor(armour, (byte)3) / 1200000.0f * armour.getDamageModifier())));
                            }
                            catch (NoArmourException ex2) {}
                            catch (NoSpaceException nsp) {
                                Puppet.logger.log(Level.WARNING, performer.getName() + " no armour space on loc " + pos);
                            }
                            final float poisdam = puppet.getSpellVenomBonus();
                            CombatEngine.addWound(null, performer, (byte)3, pos, 5000 + Server.rand.nextInt(10000), armourMod, "bites", null, 20.0f, Server.rand.nextInt((int)poisdam), false, false, false, false);
                        }
                        catch (Exception ex) {
                            Puppet.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                    else {
                        red = false;
                    }
                    break;
                }
                break;
            }
            case 1: {
                toSendPerformer = "You cough!";
                toSendBroadCast = performer.getName() + " coughs and excuses " + performer.getHimHerItString() + "self.";
                break;
            }
            case 2: {
                toSendPerformer = "You find yourself mumbling and nobody hears what you say!";
                toSendBroadCast = "You can't hear what " + performer.getName() + " says because " + performer.getHeSheItString() + " mumbles.";
                break;
            }
            case 3: {
                toSendPerformer = "You suddenly think about something totally different than puppeteering.";
                toSendBroadCast = performer.getName() + " seems far away in " + performer.getHisHerItsString() + " own thoughts.";
                break;
            }
            case 4: {
                toSendPerformer = "Your mind goes totally blank.";
                toSendBroadCast = "The eyes of " + performer.getName() + " suddenly lose focus.";
                break;
            }
            case 5: {
                toSendPerformer = "You can't help yourself! You scream loudly!";
                toSendBroadCast = performer.getName() + " emits a high-pitched shriek! Was that part of the show?";
                break;
            }
            case 6: {
                toSendPerformer = "You drop something and pick it up without thinking, missing a line.";
                toSendBroadCast = performer.getName() + " drops something and picks it up.";
                break;
            }
            case 7: {
                toSendPerformer = "You wonder if a large audience is better than a small one.";
                toSendBroadCast = performer.getName() + " looks at you hesitantly.";
                break;
            }
            case 8: {
                toSendPerformer = "You think, 'Is this really a good show?'";
                toSendBroadCast = performer.getName() + " silently moves " + performer.getHisHerItsString() + " mouth like a fish.";
                break;
            }
            case 9: {
                toSendPerformer = "You overplay and say the line really quickly. Did anyone understand that?";
                toSendBroadCast = "You don't understand what " + performer.getName() + " say since " + performer.getHeSheItString() + " speaks too fast.";
                break;
            }
            case 10: {
                toSendPerformer = "You just said something totally different than you were supposed to.";
                toSendBroadCast = performer.getName() + " seems to just be shilly-shallying now.";
                break;
            }
            default: {
                toSendPerformer = "You fail to find words. How embarrassing!";
                toSendBroadCast = performer.getName() + " is silent and blushes.";
                break;
            }
        }
        if (red) {
            performer.getCommunicator().sendAlertServerMessage(toSendPerformer);
        }
        else {
            performer.getCommunicator().sendNormalServerMessage(toSendPerformer);
        }
        Server.getInstance().broadCastAction(toSendBroadCast, performer, 5);
    }
    
    private static final String[] getConversationArrayFor(final int actSeed, final Item puppetOne, final Item puppetTwo, final int deityOne, final int performerTypeOne, final float courierOne, final int deityTwo, final int performerTypeTwo, final float courierTwo) {
        if (puppetOne.isPuppet() && puppetTwo.isPuppet() && courierOne > 70.0f && courierTwo > 70.0f) {
            return getSeventyPlusArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (puppetOne.isPuppet() && puppetTwo.isPuppet() && courierOne > 0.0f && courierTwo > 0.0f) {
            return getSeventyMinusArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (!puppetOne.isPuppet() && puppetTwo.isPuppet() && courierTwo > 70.0f) {
            return getSeventyPlusItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (!puppetOne.isPuppet() && puppetTwo.isPuppet()) {
            return getBasicItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (puppetOne.isPuppet() && !puppetTwo.isPuppet() && courierOne > 70.0f) {
            return getSeventyPlusItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (puppetOne.isPuppet() && !puppetTwo.isPuppet() && courierOne > 0.0f) {
            return getSeventyMinusItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (puppetOne.isPuppet() && puppetTwo.isPuppet() && courierOne == 0.0f) {
            return getBasicDeityArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        if (puppetOne.isPuppet() && !puppetTwo.isPuppet() && courierOne == 0.0f) {
            return getRandomDeityItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
        }
        return Puppet.emptyStringArray;
    }
    
    private static final String[] getSeventyPlusArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        Puppet.random.setSeed(actSeed);
        final int x = Puppet.random.nextInt(4);
        switch (performerTypeOne) {
            case 1: {
                if (deityOne == 1 && (performerTypeTwo == 19 || x == 0)) {
                    return Puppet.FLSeventyPlus1;
                }
                break;
            }
            case 2: {
                if (deityOne == 1 && (performerTypeTwo == 20 || x == 0)) {
                    return Puppet.FLSeventyPlus2;
                }
                break;
            }
            case 3: {
                if (deityOne == 1 && (performerTypeTwo == 7 || x == 0)) {
                    return Puppet.FVSeventyPlus1;
                }
                break;
            }
            case 4: {
                if (deityOne == 1 && (performerTypeTwo == 8 || x == 0)) {
                    return Puppet.FVSeventyPlus2;
                }
                break;
            }
            case 5: {
                if (deityOne == 1 && (performerTypeTwo == 13 || x == 0)) {
                    return Puppet.FMSeventyPlus1;
                }
                break;
            }
            case 6: {
                if (deityOne == 1 && (performerTypeTwo == 14 || x == 0)) {
                    return Puppet.FMSeventyPlus2;
                }
                break;
            }
            case 7: {
                if (deityOne == 3 && (performerTypeTwo == 3 || x == 0)) {
                    return Puppet.VFSeventyPlus1;
                }
                break;
            }
            case 8: {
                if (deityOne == 3 && (performerTypeTwo == 4 || x == 0)) {
                    return Puppet.VFSeventyPlus2;
                }
                break;
            }
            case 9: {
                if (deityOne == 3 && (performerTypeTwo == 21 || x == 0)) {
                    return Puppet.VLSeventyPlus1;
                }
                break;
            }
            case 10: {
                if (deityOne == 3 && (performerTypeTwo == 22 || x == 0)) {
                    return Puppet.VLSeventyPlus2;
                }
                break;
            }
            case 11: {
                if (deityOne == 3 && (performerTypeTwo == 17 || x == 0)) {
                    return Puppet.VMSeventyPlus1;
                }
                break;
            }
            case 12: {
                if (deityOne == 3 && (performerTypeTwo == 18 || x == 0)) {
                    return Puppet.VMSeventyPlus2;
                }
                break;
            }
            case 13: {
                if (deityOne == 2 && (performerTypeTwo == 5 || x == 0)) {
                    return Puppet.MFSeventyPlus1;
                }
                break;
            }
            case 14: {
                if (deityOne == 2 && (performerTypeTwo == 6 || x == 0)) {
                    return Puppet.MFSeventyPlus2;
                }
                break;
            }
            case 15: {
                if (deityOne == 2 && (performerTypeTwo == 23 || x == 0)) {
                    return Puppet.MLSeventyPlus1;
                }
                break;
            }
            case 16: {
                if (deityOne == 2 && (performerTypeTwo == 24 || x == 0)) {
                    return Puppet.MLSeventyPlus2;
                }
                break;
            }
            case 17: {
                if (deityOne == 2 && (performerTypeTwo == 11 || x == 0)) {
                    return Puppet.MVSeventyPlus1;
                }
                break;
            }
            case 18: {
                if (deityOne == 2 && (performerTypeTwo == 12 || x == 0)) {
                    return Puppet.MVSeventyPlus2;
                }
                break;
            }
            case 19: {
                if (deityOne == 4 && (performerTypeTwo == 1 || x == 0)) {
                    return Puppet.LFSeventyPlus1;
                }
                break;
            }
            case 20: {
                if (deityOne == 4 && (performerTypeTwo == 2 || x == 0)) {
                    return Puppet.LFSeventyPlus2;
                }
                break;
            }
            case 21: {
                if (deityOne == 4 && (performerTypeTwo == 9 || x == 0)) {
                    return Puppet.LVSeventyPlus1;
                }
                break;
            }
            case 22: {
                if (deityOne == 4 && (performerTypeTwo == 10 || x == 0)) {
                    return Puppet.LVSeventyPlus2;
                }
                break;
            }
            case 23: {
                if (deityOne == 4 && (performerTypeTwo == 15 || x == 0)) {
                    return Puppet.LMSeventyPlus1;
                }
                break;
            }
            case 24: {
                if (deityOne == 4 && (performerTypeTwo == 16 || x == 0)) {
                    return Puppet.LMSeventyPlus2;
                }
                break;
            }
            default: {
                return getSeventyMinusArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
            }
        }
        return getSeventyMinusArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
    }
    
    private static final String[] getSeventyMinusArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        if (Server.rand.nextInt(2) == 1) {
            switch (performerTypeOne) {
                case 1: {
                    if (deityOne == 1) {
                        return Puppet.FLSeventyMinus1;
                    }
                    break;
                }
                case 2: {
                    if (deityOne == 1) {
                        return Puppet.FLSeventyMinus2;
                    }
                    break;
                }
                case 3: {
                    if (deityOne == 1) {
                        return Puppet.FVSeventyMinus1;
                    }
                    break;
                }
                case 4: {
                    if (deityOne == 1) {
                        return Puppet.FVSeventyMinus2;
                    }
                    break;
                }
                case 5: {
                    if (deityOne == 1) {
                        return Puppet.FMSeventyMinus1;
                    }
                    break;
                }
                case 6: {
                    if (deityOne == 1) {
                        return Puppet.FMSeventyMinus2;
                    }
                    break;
                }
                case 7: {
                    if (deityOne == 3) {
                        return Puppet.VFSeventyMinus1;
                    }
                    break;
                }
                case 8: {
                    if (deityOne == 3) {
                        return Puppet.VFSeventyMinus2;
                    }
                    break;
                }
                case 9: {
                    if (deityOne == 3) {
                        return Puppet.VLSeventyMinus1;
                    }
                    break;
                }
                case 10: {
                    if (deityOne == 3) {
                        return Puppet.VLSeventyMinus2;
                    }
                    break;
                }
                case 11: {
                    if (deityOne == 3) {
                        return Puppet.VMSeventyMinus1;
                    }
                    break;
                }
                case 12: {
                    if (deityOne == 3) {
                        return Puppet.VMSeventyMinus2;
                    }
                    break;
                }
                case 13: {
                    if (deityOne == 2) {
                        return Puppet.MFSeventyMinus1;
                    }
                    break;
                }
                case 14: {
                    if (deityOne == 2) {
                        return Puppet.MFSeventyMinus2;
                    }
                    break;
                }
                case 15: {
                    if (deityOne == 2) {
                        return Puppet.MLSeventyMinus1;
                    }
                    break;
                }
                case 16: {
                    if (deityOne == 2) {
                        return Puppet.MLSeventyMinus2;
                    }
                    break;
                }
                case 17: {
                    if (deityOne == 2) {
                        return Puppet.MVSeventyMinus1;
                    }
                    break;
                }
                case 18: {
                    if (deityOne == 2) {
                        return Puppet.MVSeventyMinus2;
                    }
                    break;
                }
                case 19: {
                    if (deityOne == 4) {
                        return Puppet.LFSeventyMinus1;
                    }
                    break;
                }
                case 20: {
                    if (deityOne == 4) {
                        return Puppet.LFSeventyMinus2;
                    }
                    break;
                }
                case 21: {
                    if (deityOne == 4) {
                        return Puppet.LVSeventyMinus1;
                    }
                    break;
                }
                case 22: {
                    if (deityOne == 4) {
                        return Puppet.LVSeventyMinus2;
                    }
                    break;
                }
                case 23: {
                    if (deityOne == 4) {
                        return Puppet.LMSeventyMinus1;
                    }
                    break;
                }
                case 24: {
                    if (deityOne == 4) {
                        return Puppet.LMSeventyMinus2;
                    }
                    break;
                }
                default: {
                    return getBasicDeityArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
                }
            }
        }
        return getBasicDeityArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
    }
    
    private static final String[] getSeventyPlusItemArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        switch (performerTypeOne) {
            case 1: {
                if (deityOne == 0 && performerTypeTwo == 3) {
                    return Puppet.AFSeventyPlus1;
                }
                break;
            }
            case 2: {
                if (deityOne == 0 && performerTypeTwo == 4) {
                    return Puppet.AFSeventyPlus2;
                }
                break;
            }
            case 3: {
                if (deityOne == 1 && performerTypeTwo == 1) {
                    return Puppet.FASeventyPlus1;
                }
                break;
            }
            case 4: {
                if (deityOne == 1 && performerTypeTwo == 2) {
                    return Puppet.FASeventyPlus2;
                }
                break;
            }
            case 5: {
                if (deityOne == 0 && performerTypeTwo == 77) {
                    return Puppet.AVSeventyPlus1;
                }
                break;
            }
            case 6: {
                if (deityOne == 0 && performerTypeTwo == 8) {
                    return Puppet.AVSeventyPlus2;
                }
                break;
            }
            case 7: {
                if (deityOne == 3 && performerTypeTwo == 5) {
                    return Puppet.VASeventyPlus1;
                }
                break;
            }
            case 8: {
                if (deityOne == 3 && performerTypeTwo == 6) {
                    return Puppet.VASeventyPlus2;
                }
                break;
            }
            case 9: {
                if (deityOne == 0 && performerTypeTwo == 11) {
                    return Puppet.AMSeventyPlus1;
                }
                break;
            }
            case 10: {
                if (deityOne == 0 && performerTypeTwo == 12) {
                    return Puppet.AMSeventyPlus2;
                }
                break;
            }
            case 11: {
                if (deityOne == 2 && performerTypeTwo == 9) {
                    return Puppet.MASeventyPlus1;
                }
                break;
            }
            case 12: {
                if (deityOne == 2 && performerTypeTwo == 10) {
                    return Puppet.MASeventyPlus2;
                }
                break;
            }
            case 13: {
                if (deityOne == 0 && performerTypeTwo == 15) {
                    return Puppet.ALSeventyPlus1;
                }
                break;
            }
            case 14: {
                if (deityOne == 0 && performerTypeTwo == 16) {
                    return Puppet.ALSeventyPlus2;
                }
                break;
            }
            case 15: {
                if (deityOne == 4 && performerTypeTwo == 13) {
                    return Puppet.LASeventyPlus1;
                }
                break;
            }
            case 16: {
                if (deityOne == 4 && performerTypeTwo == 14) {
                    return Puppet.LASeventyPlus2;
                }
                break;
            }
            default: {
                return getSeventyMinusItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
            }
        }
        return getSeventyMinusItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
    }
    
    private static final String[] getSeventyMinusItemArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        return getBasicItemArray(actSeed, deityOne, performerTypeOne, deityTwo, performerTypeTwo);
    }
    
    private static final String[] getBasicDeityArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        Puppet.random.setSeed(actSeed);
        final int x = Puppet.random.nextInt(2);
        switch (deityOne) {
            case 1: {
                if (deityTwo == 1) {
                    return Puppet.FFbasic1;
                }
                if (deityTwo == 4) {
                    if (x == 0) {
                        return Puppet.FLbasic1;
                    }
                    return Puppet.FLbasic2;
                }
                else if (deityTwo == 3) {
                    if (x == 0) {
                        return Puppet.FVbasic1;
                    }
                    return Puppet.FVbasic2;
                }
                else {
                    if (deityTwo != 2) {
                        break;
                    }
                    if (x == 0) {
                        return Puppet.FMbasic1;
                    }
                    return Puppet.FMbasic2;
                }
                break;
            }
            case 3: {
                if (deityTwo == 3) {
                    return Puppet.VVbasic1;
                }
                if (deityTwo == 1) {
                    if (x == 0) {
                        return Puppet.VFbasic1;
                    }
                    return Puppet.VFbasic2;
                }
                else if (deityTwo == 4) {
                    if (x == 0) {
                        return Puppet.VLbasic1;
                    }
                    return Puppet.VLbasic2;
                }
                else {
                    if (deityTwo != 2) {
                        break;
                    }
                    if (x == 0) {
                        return Puppet.VMbasic1;
                    }
                    return Puppet.VMbasic2;
                }
                break;
            }
            case 2: {
                if (deityTwo == 2) {
                    return Puppet.MMbasic1;
                }
                if (deityTwo == 1) {
                    if (x == 0) {
                        return Puppet.MFbasic1;
                    }
                    return Puppet.MFbasic2;
                }
                else if (deityTwo == 4) {
                    if (x == 0) {
                        return Puppet.MLbasic1;
                    }
                    return Puppet.MLbasic2;
                }
                else {
                    if (deityTwo != 3) {
                        break;
                    }
                    if (x == 0) {
                        return Puppet.MVbasic1;
                    }
                    return Puppet.MVbasic2;
                }
                break;
            }
            case 4: {
                if (deityTwo == 4) {
                    return Puppet.LLbasic1;
                }
                if (deityTwo == 1) {
                    if (x == 0) {
                        return Puppet.LFbasic1;
                    }
                    return Puppet.LFbasic2;
                }
                else if (deityTwo == 3) {
                    if (x == 0) {
                        return Puppet.LVbasic1;
                    }
                    return Puppet.LVbasic2;
                }
                else {
                    if (deityTwo != 2) {
                        break;
                    }
                    if (x == 0) {
                        return Puppet.LMbasic1;
                    }
                    return Puppet.LMbasic2;
                }
                break;
            }
            default: {
                return Puppet.FFbasic1;
            }
        }
        return Puppet.FFbasic1;
    }
    
    private static final String[] getRandomDeityItemArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        Puppet.random.setSeed(actSeed);
        final int x = Puppet.random.nextInt(7);
        switch (deityOne) {
            case 1: {
                if (x == 0) {
                    return Puppet.FFbasic1;
                }
                if (x == 1) {
                    return Puppet.FLbasic1;
                }
                if (x == 2) {
                    return Puppet.FLbasic2;
                }
                if (x == 3) {
                    return Puppet.FVbasic1;
                }
                if (x == 4) {
                    return Puppet.FVbasic2;
                }
                if (x == 5) {
                    return Puppet.FMbasic1;
                }
                if (x == 6) {
                    return Puppet.FMbasic2;
                }
                break;
            }
            case 3: {
                if (x == 0) {
                    return Puppet.VVbasic1;
                }
                if (x == 1) {
                    return Puppet.VFbasic1;
                }
                if (x == 2) {
                    return Puppet.VFbasic2;
                }
                if (x == 3) {
                    return Puppet.VLbasic1;
                }
                if (x == 4) {
                    return Puppet.VLbasic2;
                }
                if (x == 5) {
                    return Puppet.VMbasic1;
                }
                if (x == 6) {
                    return Puppet.VMbasic2;
                }
                break;
            }
            case 2: {
                if (x == 0) {
                    return Puppet.MMbasic1;
                }
                if (x == 1) {
                    return Puppet.MFbasic1;
                }
                if (x == 2) {
                    return Puppet.MFbasic2;
                }
                if (x == 3) {
                    return Puppet.MLbasic1;
                }
                if (x == 4) {
                    return Puppet.MLbasic2;
                }
                if (x == 5) {
                    return Puppet.MVbasic1;
                }
                if (x == 6) {
                    return Puppet.MVbasic2;
                }
                break;
            }
            case 4: {
                if (x == 0) {
                    return Puppet.LLbasic1;
                }
                if (x == 1) {
                    return Puppet.LFbasic1;
                }
                if (x == 2) {
                    return Puppet.LFbasic2;
                }
                if (x == 3) {
                    return Puppet.LMbasic1;
                }
                if (x == 4) {
                    return Puppet.LMbasic2;
                }
                if (x == 5) {
                    return Puppet.LVbasic1;
                }
                if (x == 6) {
                    return Puppet.LVbasic2;
                }
                break;
            }
            default: {
                return Puppet.FFbasic1;
            }
        }
        return Puppet.FFbasic1;
    }
    
    private static final String[] getBasicItemArray(final int actSeed, final int deityOne, final int performerTypeOne, final int deityTwo, final int performerTypeTwo) {
        Puppet.random.setSeed(actSeed);
        final int x = Puppet.random.nextInt(100);
        if (x < 7) {
            return Puppet.item1;
        }
        if (x < 14) {
            return Puppet.item2;
        }
        if (x < 21) {
            return Puppet.item3;
        }
        if (x < 28) {
            return Puppet.item4;
        }
        if (x < 35) {
            return Puppet.item5;
        }
        if (x < 42) {
            return Puppet.item6;
        }
        if (x < 49) {
            return Puppet.item7;
        }
        if (x < 54) {
            return Puppet.item8;
        }
        if (x < 60) {
            return Puppet.item9;
        }
        if (x < 66) {
            return Puppet.item10;
        }
        if (x < 72) {
            return Puppet.item11;
        }
        if (x < 78) {
            return Puppet.item12;
        }
        if (x < 84) {
            return Puppet.item13;
        }
        if (x < 90) {
            return Puppet.item14;
        }
        if (x < 95) {
            return Puppet.item15;
        }
        if (x < 100) {
            return Puppet.item16;
        }
        return Puppet.item16;
    }
    
    private static int getDeityFor(final Item item) {
        switch (item.getTemplateId()) {
            case 640: {
                return 1;
            }
            case 641: {
                return 2;
            }
            case 642: {
                return 3;
            }
            case 643: {
                return 4;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static final boolean mayPuppetMaster(final Creature performer) {
        final Long last = Puppet.puppetmasters.get(performer.getWurmId());
        return last == null || System.currentTimeMillis() - last > 900000L;
    }
    
    public static final void startPuppeteering(final Creature performer) {
        Puppet.puppetmasters.put(performer.getWurmId(), System.currentTimeMillis());
    }
    
    public static final long getLastPuppeteered(final long performerId) {
        final Long last = Puppet.puppetmasters.get(performerId);
        if (last == null) {
            return 0L;
        }
        if (System.currentTimeMillis() - last > 900000L) {
            return 0L;
        }
        return last;
    }
    
    public static final void addPuppetTime(final long performerId, final long lastPuppeteered) {
        Puppet.puppetmasters.put(performerId, lastPuppeteered);
    }
    
    static {
        logger = Logger.getLogger(Puppet.class.getName());
        puppetmasters = new HashMap<Long, Long>();
        random = new Random();
        FLSeventyPlus1 = new String[] { "@It was better before.", "Not for me. I feel alone.", "So am I. Yet I feel their suspicion and awe.", "You are always so kind and forgiving." };
        LFSeventyPlus1 = new String[] { "", "This is better.", "I am friends with everyone now.", "Oh, I don't mind. Let them be suspicious.", "How nicely said of you. " };
        FLSeventyPlus2 = new String[] { "@Come on!", "You said you wanted to. Now I am disappointed.", "Why not?", "It won't.", "I just know.", "Don't be afraid." };
        LFSeventyPlus2 = new String[] { "", "I don't want to!", "Yes but now I don't want to.", "What if something happens?", "How do YOU know?", "Please! You make me sad! Stop torturing me!", "All that is left is fear." };
        VLSeventyPlus1 = new String[] { "@Have you done it yet?", "Coward.", "I will say what I want.", "Stop that!", "Well do as you please then. You are ridiculous.", "Do as you please." };
        LVSeventyPlus1 = new String[] { "", "No. I don't dare to.", "You shouldn't call me that! It's not fair!", "I will say what I want.", "Stop that!", "I'm not! Stop this!" };
        VLSeventyPlus2 = new String[] { "@We have decided that you have to walk that path again.", "You are the youngest.", "It is a reason. It is our reason.", "Frankly I do not care. Maybe if things were different.", "No YOU are! Egotistical little furball! You don't seem to care about anything! " };
        LVSeventyPlus2 = new String[] { "", "Why me? Why is it always me?", "Is that a good reason?", "You have no idea how this hurts.", "You are heartless!" };
        MLSeventyPlus1 = new String[] { "@I wonder what would help.", "All the time.", "You could do it for me.", "Do not fear. I will be there." };
        LMSeventyPlus1 = new String[] { "", "Does it hurt a lot?", "I could try I guess.", "It scares me.", "I have nobody else now." };
        MLSeventyPlus2 = new String[] { "@I don't see why you shouldn't!", "Of course it is!", "Because I am older?", "You always say that. Stop once and for all.", "If all goes well nobody will ever make fun of you again." };
        LMSeventyPlus2 = new String[] { "", "It's not your decision!", "How can you say that?", "That's not fair!", "You're not the one who has to do this." };
        VFSeventyPlus1 = new String[] { "@She actually enters now!", "I start to feel weird.", "Oh my god. Oh dear.", "Magranon! Don't enter!", "Of course. Of course." };
        VFSeventyPlus2 = new String[] { "", "Yes, I can't believe it. How... fascinating.", "Yes.. it is. I didn't expect to feel this way.", "NO! It can not be!", "He must. Don't you see?" };
        FVSeventyPlus1 = new String[] { "", "A lot is lost in the pain. But euforia. Visions of mother.", "I wonder if we would survive. Do you?", "We could just touch it!" };
        FVSeventyPlus2 = new String[] { "@How much do you remember?", "What happens if we try again?", "I would not take that chance.", "Do not speak of it any more please. And not in front of the others." };
        VMSeventyPlus1 = new String[] { "@Magranon! What did you see? Where is she?", "Gone?", "Oh love. Oh Magranon! Come here!", "We could not know. We could not... Oh Magranon.", "Magranon! You agreed to the plan! You accepted!" };
        MVSeventyPlus1 = new String[] { "", "There was a change.. but she is dead.. gone..", "Yes.. her body is... shattered.", "My sister! My poor poor sister! What have we done?", "No.. I did not do this! You did! You both!", "No! No! This must be undone!" };
        VMSeventyPlus2 = new String[] { "", "That is weird. I feel only empty.", "Your rage is feeding rage. You should lie down with me.", "Who knows what would happen? What bad could come of it?", "Do not be afraid. Could you be harmed?" };
        MVSeventyPlus2 = new String[] { "@I feel pain - all sorts of pain.", "I burn. I am a forest fire.", "I do not think that it will help.", "All sorts. I am not in control. Neither are you.", "How dare you patronize me over things that you know nothing about?" };
        FMSeventyPlus1 = new String[] { "@So how do you feel about this?", "She will be allright.", "You know I can not make such a promise.", "Yes. I know." };
        MFSeventyPlus1 = new String[] { "", "Not good honestly. I do not think that it is a good idea.", "Do you promise?", "If anything happens to her I do not know what I will do." };
        FMSeventyPlus2 = new String[] { "@I visited the source recently. It was gone.", "Oh, you know what happened to it?", "How?", "What kind of construction?", "Did they say where? What kind?", "What about the prisoners?" };
        MFSeventyPlus2 = new String[] { "", "Yes.. I guess you have the right to know about it.", "She moved it. I think to her domains.", "I think humans helped her. Some prisoners spoke about a construction.", "They spoke of a basin.", "That was all I could make them say. They did not know where. Only that it was below ground.", "What do YOU think? That I left them alive?" };
        AFSeventyPlus1 = new String[] { "@What did you do?", "What happened?", "How?", "Are you asking me?", "You became too powerful." };
        FASeventyPlus1 = new String[] { "", "She entered.", "She died.", "I do not know. What happened to us?", "I guess I am." };
        AFSeventyPlus2 = new String[] { "@I am scared now. Something is wrong.", "Are you afraid?", "He is broken.", "I wish we could help.", "That is not the right way." };
        FASeventyPlus2 = new String[] { "", "Yes, it disappeared.", "Not me. No I am not. I am strong - I have to be.", "He is.", "This should all be forgotten." };
        AVSeventyPlus1 = new String[] { "@Should you not have known better?", "It was pretty obvious I think.", "So you do not know everything after all?", "I fear that one day you will have to pay the price." };
        VASeventyPlus1 = new String[] { "", "There was no way of knowing!", "Thinking clear is not always easy.", "Not yet. Not yet.", "I will look to the stars. Maybe you are right." };
        AVSeventyPlus2 = new String[] { "", "Nobody said it was.", "We understand.", "No.. What do you mean?", "Did you?", "Do not start any rumours now." };
        VASeventyPlus2 = new String[] { "@It was not me! Not me!", "She did it to herself I say!", "I never meant to.. You know.", "Push her or anything.", "No.. I mean.. no.", "You are right. I should watch my tongue." };
        ALSeventyPlus1 = new String[] { "@Oh, to find you here. After all this time!", "I almost did not recognize you.", "You look... horrible... larvae.", "I will not. Disgusting!", "I will. I can not stand this any longer." };
        LASeventyPlus1 = new String[] { "", "Help... Help.", "Neither... I.", "Not.. eat.. me...", "Leave.. leave..", "Mok... Kill it!" };
        ALSeventyPlus2 = new String[] { "", "No! What has happened to you?", "Tricked? I heard of greed.", "I do not believe you.", "For not believing? That is.. a joke?", "No. Not now." };
        LASeventyPlus2 = new String[] { "@So. Will you retrieve my eye?", "Something horrible. I was tricked.", "That is a lie!", "Then I will slay you.", "Do I look like I make jokes now?", "Goodbye then." };
        AMSeventyPlus1 = new String[] { "@What did you do?", "Are you sure? You could have stopped it!", "It is your fault as much as anybody elses.", "Oh, stand up! Do not dare to blame someone else.", "Yes. It is a blessing." };
        MASeventyPlus1 = new String[] { "", "I did nothing. It was them!", "Quiet! Quiet! Do not torment me!", "Stop it already! I was also tricked.", "I am glad mother did not have to see this." };
        AMSeventyPlus2 = new String[] { "@We blame you now. And for the recent fires.", "Do not count on us.", "That will be hard. Not many of us like your ways.", "You should reconsider.", "Delusions." };
        MASeventyPlus2 = new String[] { "", "You will stay with me will you not?", "I will find new followers then.", "I will find a way.", "The winds whisper of new times. Something is coming." };
        VFSeventyMinus1 = new String[] { "", "Who? Libila?", "How?", "A wolf? She would never kill anything.", "Well. There is little we can do unless we find her." };
        FVSeventyMinus1 = new String[] { "@There is a rumour among the wolves that she is alive.", "Yes. She has changed.", "They say that she ordered one of them dead.", "Yes. One would think so but I believe them.", "Obviously she does not want to be found." };
        VFSeventyMinus2 = new String[] { "@These.. newcomers.. their presence is related to the second visit.", "More leaked when she moved it.", "Please do. I will as well, and I will talk to Magranon.", "So will I.", "Nothing. She was moved far away." };
        FVSeventyMinus2 = new String[] { "", "Yes. Something opened up and they entered our forest.", "Oh. I will see if I can find a way to stop it.", "I will welcome them for now.", "Have you heard anything about her whereabouts?" };
        MLSeventyMinus1 = new String[] { "@Speak, wretch! What is your name and what do you do here?", "Hahaha! I heard she is nothing more than some kind of larvae!", "How?", "Is that so? What more?", "An altar? An altar.." };
        LMSeventyMinus1 = new String[] { "", "I am Baghmot. I speak on behalf of Her. I am to tell you that you will be undone!", "That may be. But she has significant strength already, and is gaining more quickly.", "Please ... the newcomers. Their faith feeds her.", "Aiii! She ordered them to build an altar!" };
        MLSeventyMinus2 = new String[] { "@Do you like my fire?", "That would be the point.", "I don't mind.", "We will see, won't we? Now where were we..", "Yes, what about it?", "Alive? How do you mean alive?", "I must see this for myself.", "I am sure I won't." };
        LMSeventyMinus2 = new String[] { "", "It hurts! Aiii!", "You are cruel. More cruel than her!", "Will I even survive this?", "You asked about my skin.", "It is something in her lands that changes it.. the lands seem alive with something.", "Like.. snakes. Something in the ground is moving slowly.", "I like it." };
        FMSeventyMinus1 = new String[] { "", "Me and Vynora is going to welcome them while we figure out a way.", "Build an altar?", "I gathered some of it that was dropped as she left. Maybe it will protect our altar.", "Vynora learned their language. She can make inscriptions.", "Please reconsider. We need to unite.", "So be it then." };
        MFSeventyMinus1 = new String[] { "@She is building an altar. The newcomers give her strength.", "We should do the same then.", "Yes. I tried to approach hers. It is full with her tainted source. It hurts to approach.", "Yes.. Maybe it will. We can try.", "I have as well, and I have my own plans. I will write my own inscription.", "I have had time." };
        FMSeventyMinus2 = new String[] { "@Spirits come to me daily now.", "Yes. What can I say?", "Why? He will not love you anyways.", "I will not cast him out. I fear that it would ruin him.", "On another note - what about those lava fiends?", "They disturb the balance.", "I sense aggression.", "Do I sense a threat?", "So have I. So have I." };
        MFSeventyMinus2 = new String[] { "", "I have noticed such a change. Also my brother no longer speaks with me. He loves you instead.", "It hurts. You could refuse him.", "You do not know that.", "I made them from spirits. What about them?", "They do not! There are worse matters that YOU are the cause of!", "I sense ignorance and insinuation. I will react negatively.", "I have found other powers. You have no idea." };
        VMSeventyMinus1 = new String[] { "@The dragons told me that she moved the source.", "You should have told me yourself.", "I also have the right to know.", "Yes I did - no thanks to you.", "Things surely have changed, haven't they?" };
        MVSeventyMinus1 = new String[] { "", "Yes I am aware that she did. They should not have told you. It is treason.", "Why?", "No you do not. In any case you found out.", "That is fine with me.", "Nothing ever stays the same, does it?" };
        VMSeventyMinus2 = new String[] { "", "Yes what about it? I heard Fo saved it.", "Oh, how?", "Abundant? That sounds bad.", "Ouch.", "The source is powerful. If those humans come in contact with it they will be affected as well.", "I am aware. They are probably doomed, then." };
        MVSeventyMinus2 = new String[] { "@That wretched wolf I told you about.", "He claimed that her lands are alive somehow.", "I had to go see for myself. Seems they are abundant with corrupt source.", "Yes I am sure it is. It is also spreading.", "What would you think can come from it?", "She has started to attract them.", "Let's hope not." };
        FLSeventyMinus1 = new String[] { "@What have you done?", "I saved your friend.", "Well, whatever. He is my friend now.", "That is not my intention.", "Poor thing.", "We meant no harm.", "Can I do anything for you?" };
        LFSeventyMinus1 = new String[] { "", "How did you find me?", "Haha. Friend you say?", "Of course. You manage to fool everyone.", "So you say. I know better.", "I will have no mercy with you, you know that.", "You are only regretful. You game went awry and now you are scared. Cowards.", "Guess what? LEAVE ME ALONE! ALONE!" };
        FLSeventyMinus2 = new String[] { "@Did we actually meet afterwards?", "Did you really say those things?", "I love you, don't I?", "I don't understand why they didn't stop me.", "Am I still?", "And mine, of course." };
        LFSeventyMinus2 = new String[] { "", "Who knows?", "Most probably.", "So they say.", "They probably couldn't. You were too powerful.", "I wouldn't think so. Depends on my followers.", "Yes. Yours as well." };
        VLSeventyMinus1 = new String[] { "@You are sleeping now aren't you?", "So I can reach you in your dreams only.", "Will you not end this then?", "We apologize.", "I can foresee the destruction you will cause.", "What can we offer?" };
        LVSeventyMinus1 = new String[] { "", "Yes I am.", "Yes. I will hide.", "Give me one good reason.", "So I heard. You will still suffer.", "So be it. I will let death and desolation reign.", "Nothing. We will slowly dismember you." };
        VLSeventyMinus2 = new String[] { "@I am back again.", "Can you lock me out do you think?", "Fascinating. Anyways, we have decided to offer you all the newcomers.", "Yes we would.", "If you say so. Anything else?", "We will be prepared." };
        LVSeventyMinus2 = new String[] { "", "You are not welcome. This is the last time.", "Yes. You are here only because I allow you to.", "That is not enough. Really sacrifice them?", "That is truly ruthless.", "No. This was the last time.", "I don't think so." };
        item1 = new String[] { "@Hello.", "Pleased to meet you.", "How are we today?", "Oh, nice.", "Yes yes YES!", "Hahaha!", "Take that!", "Bang!", "Tjoff!" };
        item2 = new String[] { "@Hi.", "Pleased to meet you too.", "Fine thanks?", "You were one ugly doll.", "Silence, please!", "Why do you say that?", "Ouch!", "Buhuhu!", "Now you made me sad!" };
        item3 = new String[] { "@Woof!", "Bongo bongo bey.", "Lamisika!", "Hoodeladi hoppsan sa.", "You don't fool me, biscuit eater!", "Your ugly twin was by yesterday.", "He said he looked better than you.", "Don't hit me!", "Please calm down.", "I never said that!" };
        item4 = new String[] { "@How are you doing, friend?", "Nice to see you here.", "I really like the weather today. Do you think it will rain?", "How have you been lately?", "Glad to hear that.", "I am fine thank you. Very fine indeed." };
        item5 = new String[] { "@Aren't we the madhatter today?", "No I mean you!", "Help, guards!", "Guards!", "I am sure they will catch the perpetrator eventually.", "Who said that?", "No that's YOU!" };
        item6 = new String[] { "@Where are we?", "What are we doing here?", "You can't be serious.", "Who are you talking to?", "Please calm down!", "I am not upset but I can see that you are.", "That is your problem." };
        item7 = new String[] { "@Hello, dear friend.", "Yes it was too long.", "No I haven't.", "I have no idea what you are talking about.", "Are you accusing me of something?", "I would like to see you do that.", "Hahaha" };
        item8 = new String[] { "@What is this place?", "Why do you look so strange?", "Are you Fo?", "Of course I see that.", "Are you afraid of the spirits?", "What's all this talk about mycelium anyways?", "I am not afraid at all." };
        item9 = new String[] { "@Hello, you look like Libila today!", "No it was a joke.", "Actually I am really happy to see you.", "Where are you going? To the altar?", "They say the altar can be destroyed once the deities gain enough followers." };
        item10 = new String[] { "@Bla hello bla.", "I actually like Magranon.", "Magranon is the best.", "Magranon can kill ANYTHING.", "Magranon blows fire from his ass.", "I didn't mean to say that last thing. Now I blush.", "Who are you to question me anyways?" };
        item11 = new String[] { "@Hey, you!", "Are you Vynora?", "They say you know eeeverything.", "So it's true?", "Where's my hat at?", "Is the world flat?", "Can I travel to Seris?", "Where's my wombat cheesecake anyways?" };
        item12 = new String[] { "@Who was that?", "Oh it was you!", "No", "That is none of your business.", "Oh, please. Please please please.", "I am always here.", "I have never seen you before.", "That's all for me folks!" };
        item13 = new String[] { "@Oh hi", "No I don't.", "Who are you anyways?", "That's me in a nutshell.", "Hey wonderboy, don't you grow tired of those remarks?", "I took old Sadking on a ride yesterday.", "He buckled so much and I fell off.", "I don't feel any differenk.", "Thank you, thank you very much." };
        item14 = new String[] { "@Hello again.", "Haven't we met?", "Take that, infidel!", "Who said that?", "Now, where did that come from? Did anyone see that one coming?", "I am so sad today.", "Well, at least the weather is good.", "Allright, catch you later folks!" };
        item15 = new String[] { "@Good day to you.", "Well, how nice.", "Ho ho ho.", "Is that my dandelion over there?", "Oh, jolly.", "Jeeves! Jeeves!", "Please pour my cup of tea, dear.", "Most sorry if I upset anyone. Now take care, folks!" };
        item16 = new String[] { "@Wild thing! You make everything swing!", "Yeah!", "To the bottom, girls!", "Yumba, yumba - toot!", "That's right! Work it harder, dangerous one!", "You should meet my friend Spoon.", "That's all folks!" };
        FFbasic1 = new String[] { "@Fo.", "Yes.", "How am I today?", "Fine thank you.", "Am I sure?", "Yes I am.", "What am I thinking about?", "A fresh wind blowing in the trees and the grass I suppose. Keeping children and mothers safe from danger.", "Those are nice thoughts.", "Yes, I think so too." };
        MMbasic1 = new String[] { "@Oh, hello good old friend!", "Hello to me too!", "Mighty fine day, isn't it?", "Oh yes most assuredly.", "How are business?", "Thriving, thriving.", "How are my followers doing?", "Good I hope. I try to keep them safe.", "And how are my enemies doing?", "They should fear me and my people!", "And who are your people?", "The Mol Rehans are my people!" };
        LLbasic1 = new String[] { "@I am Libila, slayer of good!", "I am the bottom and the end.", "I am the stone you crash upon and split open.", "No enemy survives me.", "Nothing endures me.", "I turn all to black and blood.", "In all my work you will see malevolence.", "I promise power and wealth to those who trust me.", "Would you trust me?" };
        VVbasic1 = new String[] { "@Vynora is my name.", "There is nothing I do not know!", "Except the secrets of the other gods, of course.", "They will not be able to hide them forever.", "Why do I feel that there is something going on?", "Me and my people will search the truth!" };
        FMbasic1 = new String[] { "@Hello Magranon.", "Are you strong today?", "The lava creatures that you like so much scare the other animals.", "To scare animals?", "Protection against what?", "Oh, of course.", "We can agree on that." };
        MFbasic1 = new String[] { "", "Fo.", "Always strong. Always on the hunt!", "Hah! That's why I created them!", "No. For protection.", "Against Libila!", "She must be stopped." };
        FMbasic2 = new String[] { "", "Go ahead.", "Are you? Oh love is all you need, really.", "You should be gentle and benevolent. Do not rush things.", "In this case it may be a poor idea.", "Surely, you must be kidding? I invented love!" };
        MFbasic2 = new String[] { "@I need your advice, Fo!", "I am in love with Vynora!", "So they say. How should I approach her?", "But I am prone to rushing things! I can't wait!", "What do you know? My love is larger than any you have ever known!" };
        FLbasic1 = new String[] { "@Vile creature, begone!", "Do not come near me!", "Nooo! Aaah!", "Aaah!", "My creatures will stop you.", "You have no idea.", "No, they have strong hearts and souls!" };
        LFbasic1 = new String[] { "", "I will have you first.", "Hahaha!", "Hahaha!", "Hahaha!", "Now you are joking. They do not stand a chance.", "Oh, but I think I do. They are weak and die easily.", "That does not matter. My creatures have no heart or soul and cannot die!" };
        FLbasic2 = new String[] { "@Libila! You will stay put! Bow before my might!", "I am the light! You will suffer in my presence!", "Change your ways, Libila.", "Do not hate. Hate will consume you.", "Then you will perish." };
        LFbasic2 = new String[] { "", "Yield! Yield to me!", "I am darkness! You will go blind and weak while with me.", "I will never! I hate you!", "Little do you know. I have mastered hate.", "I laugh at you. Hahaha!" };
        FVbasic1 = new String[] { "@Vynora, my friend.", "What have you learnt today?", "That is good.", "What would that be?", "This world is full of mysteries. I did not create them all.", "So you say. Good luck then!" };
        VFbasic1 = new String[] { "", "Dear Fo.", "I know that the sun shines always beyond the clouds.", "You hide something from me, do you?", "I do not know. But my priests speak about it.", "Nevertheless, I will find them out." };
        FVbasic2 = new String[] { "", "I have met her.", "She was very forthcoming, if I may say.", "I will not elaborate. What do you insinuate?", "Don't get into deep water now!" };
        VFbasic2 = new String[] { "@Have you met the Lady of the lake yet?", "Did she please you?", "Ooh, now what does that mean?", "Oh, nothing. Nothing at all!", "Oh I know who was in deep water! Haha!" };
        MVbasic1 = new String[] { "", "Creatures?", "Yes. I made them.", "They are animated fire spirits.", "You should.", "Not from what I hear. Her creations are made some other way.", "You should. Then tell me." };
        VMbasic1 = new String[] { "@Magranon! What are these creatures I hear about?", "Made from lava?", "How do you create life?", "I may have to try that some day.", "Is that what Libila does as well?", "I must figure that out as well then." };
        MVbasic2 = new String[] { "@Have you figured out how Libila creates her Zombies yet?", "I wonder if what she uses can be used some other way?", "That would be fun, haha!", "Armies of cottages to crush her zombies!", "Now you're talking!", "It sure would." };
        VMbasic2 = new String[] { "", "Not yet.", "Maybe. Maybe we could animate tables!", "Yes, and cottages.", "I could animate the sea and drown her lands!", "Well, it would suit me, wouldn't it?", "One day maybe. One day." };
        MLbasic1 = new String[] { "@Hello Libila. What are you doing here?", "Hah! I am the only hunter here!", "Now, try not to scare the children.", "Why do you hate us so much?", "Please tell me!", "Would that make you want to kill us?", "Not that much." };
        LMbasic1 = new String[] { "", "I am here to hunt you down!", "Are you sure about that?", "Scaring is not my aim.", "Surely you must know?", "Maybe you laughed at me?", "Have you never wanted to hurt someone who laughs at you?", "You know nothing about me." };
        MLbasic2 = new String[] { "@Take this!", "And this!", "Here! That hurt, didn't it?", "Haha! Smack!", "Eat that!", "I win!" };
        LMbasic2 = new String[] { "", "Ouch!", "Eek!", "Ow!", "Yes, please stop!", "Buhuhuu.", "Noo, noo! Have mercy!", "Yes Magranon! You win! You win!" };
        VLbasic1 = new String[] { "@Tell me your secrets!", "Then I will hurt you. Here!", "Now will you tell me?", "Aiee!", "Maybe this time.", "Aooo! Aiie!", "Noo please stop!", ".. please.." };
        LVbasic1 = new String[] { "", "Is this a joke? I will not!", "Ouch!", "No. But I will poke you in the eye!", "Now, will you leave me alone?", "Wait. I will hit you again!", "And again!", "... until you are dead!", "Haha! Hahaha!" };
        VLbasic2 = new String[] { "", "Yes. Here we are. This time I will have you!", "Friends? What trap is this?", "Oh.. interesting. But it smells funny?", "That would explain it. I am sure it is delicious.", "Argh! What is this? My skin goes pale! WHAT IS HAPPENING?", "You tricked me!", "I will return." };
        LVbasic2 = new String[] { "@Aaand here we are again.", "No. Let's be friends.", "It is not a trap. Here, have some apple juice.", "Do not think about that. I used apples from the ground.", "Yes it still tastes wonderful - I tried it myself.", "Oh, look. You die. How sad.", "Yes, didn't I? Don't I always?", "I think not." };
    }
}
