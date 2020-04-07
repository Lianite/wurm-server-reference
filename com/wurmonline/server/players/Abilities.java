// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.creatures.Creature;
import java.util.BitSet;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class Abilities implements MiscConstants, TimeConstants
{
    private static final String[] abilityDescs;
    private static final Logger logger;
    
    static void initialiseAbilities() {
        for (int x = 0; x < 64; ++x) {
            Abilities.abilityDescs[x] = "";
            if (x == 1) {
                Abilities.abilityDescs[x] = "Witch";
            }
            else if (x == 2) {
                Abilities.abilityDescs[x] = "Hag";
            }
            else if (x == 3) {
                Abilities.abilityDescs[x] = "Crone";
            }
            else if (x == 4) {
                Abilities.abilityDescs[x] = "Night Hag";
            }
            else if (x == 5) {
                Abilities.abilityDescs[x] = "Enchantress";
            }
            else if (x == 6) {
                Abilities.abilityDescs[x] = "Norn";
            }
            else if (x == 11) {
                Abilities.abilityDescs[x] = "Siren";
            }
            else if (x == 8) {
                Abilities.abilityDescs[x] = "Mesmeriser";
            }
            else if (x == 9) {
                Abilities.abilityDescs[x] = "Soothsayer";
            }
            else if (x == 10) {
                Abilities.abilityDescs[x] = "Medium";
            }
            else if (x == 7) {
                Abilities.abilityDescs[x] = "Fortune Teller";
            }
            else if (x == 12) {
                Abilities.abilityDescs[x] = "Diviner";
            }
            else if (x == 13) {
                Abilities.abilityDescs[x] = "Inquisitor";
            }
            else if (x == 14) {
                Abilities.abilityDescs[x] = "Witch Doctor";
            }
            else if (x == 15) {
                Abilities.abilityDescs[x] = "Necromancer";
            }
            else if (x == 16) {
                Abilities.abilityDescs[x] = "Occultist";
            }
            else if (x == 17) {
                Abilities.abilityDescs[x] = "Death Knight";
            }
            else if (x == 18) {
                Abilities.abilityDescs[x] = "Diabolist";
            }
            else if (x == 19) {
                Abilities.abilityDescs[x] = "Hypnotist";
            }
            else if (x == 20) {
                Abilities.abilityDescs[x] = "Evocator";
            }
            else if (x == 21) {
                Abilities.abilityDescs[x] = "Thaumaturg";
            }
            else if (x == 22) {
                Abilities.abilityDescs[x] = "Warlock";
            }
            else if (x == 23) {
                Abilities.abilityDescs[x] = "Magician";
            }
            else if (x == 24) {
                Abilities.abilityDescs[x] = "Conjurer";
            }
            else if (x == 25) {
                Abilities.abilityDescs[x] = "Magus";
            }
            else if (x == 26) {
                Abilities.abilityDescs[x] = "Arch Mage";
            }
            else if (x == 27) {
                Abilities.abilityDescs[x] = "Witch Hunter";
            }
            else if (x == 28) {
                Abilities.abilityDescs[x] = "Wizard";
            }
            else if (x == 29) {
                Abilities.abilityDescs[x] = "Summoner";
            }
            else if (x == 30) {
                Abilities.abilityDescs[x] = "Spellbinder";
            }
            else if (x == 31) {
                Abilities.abilityDescs[x] = "Illusionist";
            }
            else if (x == 32) {
                Abilities.abilityDescs[x] = "Enchanter";
            }
            else if (x == 33) {
                Abilities.abilityDescs[x] = "Druid";
            }
            else if (x == 34) {
                Abilities.abilityDescs[x] = "Sorceror";
            }
            else if (x == 35) {
                Abilities.abilityDescs[x] = "Sorceress";
            }
            else if (x == 36) {
                Abilities.abilityDescs[x] = "Demon Queen";
            }
            else if (x == 37) {
                Abilities.abilityDescs[x] = "Mage";
            }
            else if (x == 38) {
                Abilities.abilityDescs[x] = "Shadowmage";
            }
            else if (x == 39) {
                Abilities.abilityDescs[x] = "Ascended";
            }
            else if (x == 40) {
                Abilities.abilityDescs[x] = "Planeswalker";
            }
            else if (x == 41) {
                Abilities.abilityDescs[x] = "Worgmaster";
            }
            else if (x == 42) {
                Abilities.abilityDescs[x] = "Valkyrie";
            }
            else if (x == 43) {
                Abilities.abilityDescs[x] = "Berserker";
            }
            else if (x == 44) {
                Abilities.abilityDescs[x] = "Incinerator";
            }
        }
    }
    
    static BitSet setTraitBits(final long bits, final BitSet toSet) {
        for (int x = 0; x < 64; ++x) {
            if (x == 0) {
                if ((bits & 0x1L) == 0x1L) {
                    toSet.set(x, true);
                }
                else {
                    toSet.set(x, false);
                }
            }
            else if ((bits >> x & 0x1L) == 0x1L) {
                toSet.set(x, true);
            }
            else {
                toSet.set(x, false);
            }
        }
        return toSet;
    }
    
    static long getTraitBits(final BitSet bitsprovided) {
        long ret = 0L;
        for (int x = 0; x <= 64; ++x) {
            if (bitsprovided.get(x)) {
                ret += 1 << x;
            }
        }
        return ret;
    }
    
    public static final String getAbilityString(final int ability) {
        if (ability >= 0 && ability < 64) {
            return Abilities.abilityDescs[ability];
        }
        return "";
    }
    
    public static final boolean isWitch(final Creature creature) {
        return creature.hasAbility(1);
    }
    
    public static final boolean isHag(final Creature creature) {
        return creature.hasAbility(2);
    }
    
    public static final boolean isCrone(final Creature creature) {
        return creature.hasAbility(3);
    }
    
    public static final boolean isNightHag(final Creature creature) {
        return creature.hasAbility(4);
    }
    
    public static final boolean isEnchantress(final Creature creature) {
        return creature.hasAbility(5);
    }
    
    public static final boolean isNorn(final Creature creature) {
        return creature.hasAbility(6);
    }
    
    public static final boolean isSiren(final Creature creature) {
        return creature.hasAbility(11);
    }
    
    public static final boolean isMesmeriser(final Creature creature) {
        return creature.hasAbility(8);
    }
    
    public static final boolean isSoothSayer(final Creature creature) {
        return creature.hasAbility(9);
    }
    
    public static final boolean isMedium(final Creature creature) {
        return creature.hasAbility(10);
    }
    
    public static final boolean isFortuneTeller(final Creature creature) {
        return creature.hasAbility(7);
    }
    
    public static final boolean isDiviner(final Creature creature) {
        return creature.hasAbility(12);
    }
    
    public static final boolean isInquisitor(final Creature creature) {
        return creature.hasAbility(13);
    }
    
    public static final boolean isIncinerator(final Creature creature) {
        return creature.hasAbility(44);
    }
    
    public static final boolean isWitchDoctor(final Creature creature) {
        return creature.hasAbility(14);
    }
    
    public static final boolean isNecromancer(final Creature creature) {
        return creature.hasAbility(15);
    }
    
    public static final boolean isOccultist(final Creature creature) {
        return creature.hasAbility(16);
    }
    
    public static final boolean isDeathKnight(final Creature creature) {
        return creature.hasAbility(17);
    }
    
    public static final boolean isDiabolist(final Creature creature) {
        return creature.hasAbility(18);
    }
    
    public static final boolean isHypnostist(final Creature creature) {
        return creature.hasAbility(19);
    }
    
    public static final boolean isEvocator(final Creature creature) {
        return creature.hasAbility(20);
    }
    
    public static final boolean isThaumaturg(final Creature creature) {
        return creature.hasAbility(21);
    }
    
    public static final boolean isWarlock(final Creature creature) {
        return creature.hasAbility(22);
    }
    
    public static final boolean isMagician(final Creature creature) {
        return creature.hasAbility(23);
    }
    
    public static final boolean isConjurer(final Creature creature) {
        return creature.hasAbility(24);
    }
    
    public static final boolean isMagus(final Creature creature) {
        return creature.hasAbility(25);
    }
    
    public static final boolean isArchmage(final Creature creature) {
        return creature.hasAbility(26);
    }
    
    public static final boolean isWitchHunter(final Creature creature) {
        return creature.hasAbility(27);
    }
    
    public static final boolean isWizard(final Creature creature) {
        return creature.hasAbility(28);
    }
    
    public static final boolean isSummoner(final Creature creature) {
        return creature.hasAbility(29);
    }
    
    public static final boolean isSpellbinder(final Creature creature) {
        return creature.hasAbility(30);
    }
    
    public static final boolean isIllusionist(final Creature creature) {
        return creature.hasAbility(31);
    }
    
    public static final boolean isEnchanter(final Creature creature) {
        return creature.hasAbility(32);
    }
    
    public static final boolean isDruid(final Creature creature) {
        return creature.hasAbility(33);
    }
    
    public static final boolean isSorceror(final Creature creature) {
        return creature.hasAbility(34);
    }
    
    public static final boolean isSorceress(final Creature creature) {
        return creature.hasAbility(35);
    }
    
    public static final boolean isDemonQueen(final Creature creature) {
        return creature.hasAbility(36);
    }
    
    public static final boolean isMage(final Creature creature) {
        return creature.hasAbility(37);
    }
    
    public static final boolean isShadowMage(final Creature creature) {
        return creature.hasAbility(38);
    }
    
    public static final boolean isAscended(final Creature creature) {
        return creature.hasAbility(39);
    }
    
    public static final boolean isPlanesWalker(final Creature creature) {
        return creature.hasAbility(40);
    }
    
    public static final boolean isWorgMaster(final Creature creature) {
        return creature.hasAbility(41);
    }
    
    public static final boolean isValkyrie(final Creature creature) {
        return creature.hasAbility(42);
    }
    
    public static final boolean isBerserker(final Creature creature) {
        return creature.hasAbility(43);
    }
    
    private static final boolean ascend(final Creature responder, final byte deityId, final int data) {
        final LoginServerWebConnection lsw = new LoginServerWebConnection();
        try {
            Players.getInstance().sendGlobalNonPersistantTimedEffect(0L, (short)19, responder.getTileX(), responder.getTileY(), responder.getPositionZ(), System.currentTimeMillis() + 600000L);
            Players.getInstance().sendGlobalNonPersistantTimedEffect(0L, (short)4, responder.getTileX(), responder.getTileY(), responder.getPositionZ(), System.currentTimeMillis() + 600000L);
            Players.getInstance().sendGlobalNonPersistantTimedEffect(0L, (short)22, responder.getTileX(), responder.getTileY(), responder.getPositionZ(), System.currentTimeMillis() + 600000L);
            responder.getMusicPlayer().checkMUSIC_UNLIMITED_SND();
        }
        catch (Exception ex) {
            Abilities.logger.log(Level.WARNING, responder.getName(), ex.getMessage());
        }
        responder.setAbility(39, true);
        for (int x = 0; x < 3; ++x) {
            final int num = Server.rand.nextInt(8);
            if (responder.getSex() == 0) {
                switch (num) {
                    case 0: {
                        responder.setAbility(15, true);
                        break;
                    }
                    case 1: {
                        responder.setAbility(41, true);
                        break;
                    }
                    case 2: {
                        responder.setAbility(16, true);
                        break;
                    }
                    case 3: {
                        responder.setAbility(29, true);
                        break;
                    }
                    case 4: {
                        responder.setAbility(33, true);
                        break;
                    }
                    case 5: {
                        responder.setAbility(12, true);
                        break;
                    }
                    case 6: {
                        responder.setAbility(30, true);
                        break;
                    }
                    case 7: {
                        responder.setAbility(13, true);
                        break;
                    }
                    default: {
                        assert false : num + " is not possible";
                        break;
                    }
                }
            }
            else {
                switch (num) {
                    case 0: {
                        responder.setAbility(1, true);
                        break;
                    }
                    case 1: {
                        responder.setAbility(3, true);
                        break;
                    }
                    case 2: {
                        responder.setAbility(42, true);
                        break;
                    }
                    case 3: {
                        responder.setAbility(10, true);
                        break;
                    }
                    case 4: {
                        responder.setAbility(33, true);
                        break;
                    }
                    case 5: {
                        responder.setAbility(35, true);
                        break;
                    }
                    case 6: {
                        responder.setAbility(11, true);
                        break;
                    }
                    case 7: {
                        responder.setAbility(2, true);
                        break;
                    }
                    default: {
                        assert false : num + " is not possible";
                        break;
                    }
                }
            }
        }
        if (data == 577) {
            responder.achievement(322);
        }
        HistoryManager.addHistory(responder.getName(), "has ascended to immortality as a demigod!");
        Server.getInstance().broadCastSafe(responder.getName() + " has ascended to immortality as a demigod!");
        final Skills s = responder.getSkills();
        final float bodyStr = (float)(s.getSkillOrLearn(102).getKnowledge(0.0) - 20.0);
        final float bodySta = (float)(s.getSkillOrLearn(103).getKnowledge(0.0) - 20.0);
        final float bodyCon = (float)(s.getSkillOrLearn(104).getKnowledge(0.0) - 20.0);
        final float mindLog = (float)(s.getSkillOrLearn(100).getKnowledge(0.0) - 20.0);
        final float mindSpe = (float)(s.getSkillOrLearn(101).getKnowledge(0.0) - 20.0);
        final float soulStr = (float)(s.getSkillOrLearn(105).getKnowledge(0.0) - 20.0);
        final float soulDep = (float)(s.getSkillOrLearn(106).getKnowledge(0.0) - 20.0);
        responder.getCommunicator().sendNormalServerMessage(lsw.ascend(Deities.getNextDeityNum(), responder.getName(), responder.getWurmId(), deityId, responder.getSex(), (byte)2, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep));
        Abilities.logger.log(Level.INFO, responder.getName() + " ascends to demigod!");
        return true;
    }
    
    private static final boolean isBlack(final int ability) {
        switch (ability) {
            case 1:
            case 2:
            case 3:
            case 13:
            case 15:
            case 16: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static final boolean isRed(final int ability) {
        switch (ability) {
            case 6:
            case 7:
            case 8:
            case 20:
            case 24:
            case 34:
            case 35:
            case 44: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static final boolean isOther(final int ability) {
        switch (ability) {
            case 9:
            case 10:
            case 11:
            case 12:
            case 27:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 41:
            case 42:
            case 43: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static final int getNewAbilityTitle(final Creature performer) {
        int black = 0;
        int red = 0;
        int rest = 0;
        int nums = 0;
        int lastAbility = 0;
        for (int x = 0; x <= 44; ++x) {
            if (performer.hasAbility(x)) {
                ++nums;
                if (isBlack(x)) {
                    ++black;
                }
                else if (isRed(x)) {
                    ++red;
                }
                else if (isOther(x)) {
                    ++rest;
                }
                lastAbility = x;
            }
        }
        if (nums <= 1) {
            return lastAbility;
        }
        boolean isBlack = false;
        boolean isRed = false;
        boolean isOther = false;
        if (isMayorBlack(black, red, rest)) {
            isBlack = true;
        }
        else if (isMayorRed(black, red, rest)) {
            isRed = true;
        }
        else if (isMayorRest(black, red, rest)) {
            isOther = true;
        }
        if (nums <= 5 && performer.hasAbility(39)) {
            return 39;
        }
        if (nums > 9 && performer.hasAbility(39)) {
            performer.achievement(328);
            return 40;
        }
        if (nums >= 9) {
            performer.achievement(327);
            return 26;
        }
        if (nums < 6) {
            if (isBlack) {
                if (nums == 4) {
                    performer.achievement(331);
                    return 18;
                }
                if (black != 2 && black != 3) {
                    performer.achievement(329);
                    return 38;
                }
                if (performer.getSex() == 1) {
                    if (black == 2) {
                        return 4;
                    }
                    if (black == 3) {
                        return 36;
                    }
                }
                else {
                    if (black == 2) {
                        return 14;
                    }
                    if (black == 3) {
                        return 17;
                    }
                }
            }
            else if (isRed) {
                if (red == 2) {
                    return 21;
                }
                if (red == 3) {
                    return 22;
                }
                performer.achievement(332);
                return 25;
            }
            else if (isOther) {
                if (rest == 2) {
                    if (performer.getSex() == 1) {
                        return 5;
                    }
                    return 28;
                }
                else {
                    if (rest == 3) {
                        performer.achievement(333);
                        return 37;
                    }
                    performer.achievement(333);
                    return 37;
                }
            }
            else if (nums > 3) {
                return 19;
            }
            return performer.getAbilityTitleVal();
        }
        if (isBlack) {
            performer.achievement(329);
            return 38;
        }
        performer.achievement(330);
        return 23;
    }
    
    private static final boolean isMayorBlack(final int black, final int red, final int rest) {
        return black > red && black > rest;
    }
    
    private static final boolean isMayorRed(final int black, final int red, final int rest) {
        return red > black && red > rest;
    }
    
    private static final boolean isMayorRest(final int black, final int red, final int rest) {
        return rest > black && rest > red;
    }
    
    public static final boolean alreadyHasAbilityForItem(final Item item, final Creature performer) {
        return performer.hasAbility(getAbilityForItem(item.getTemplateId(), performer));
    }
    
    public static final int getAbilityForItem(final int itemTemplate, final Creature performer) {
        byte ability = 0;
        switch (itemTemplate) {
            case 808: {
                if (performer.getSex() == 1) {
                    ability = 1;
                    break;
                }
                ability = 15;
                break;
            }
            case 796: {
                if (performer.getSex() == 1) {
                    ability = 3;
                    break;
                }
                ability = 16;
                break;
            }
            case 806: {
                if (performer.getSex() == 1) {
                    ability = 2;
                    break;
                }
                ability = 13;
                break;
            }
            case 809: {
                if (performer.getSex() == 1) {
                    ability = 12;
                    break;
                }
                ability = 31;
                break;
            }
            case 797: {
                if (performer.getSex() == 1) {
                    ability = 9;
                    break;
                }
                ability = 27;
                break;
            }
            case 799: {
                if (performer.getSex() == 1) {
                    ability = 11;
                    break;
                }
                ability = 30;
                break;
            }
            case 810: {
                if (performer.getSex() == 1) {
                    ability = 10;
                    break;
                }
                ability = 29;
                break;
            }
            case 801: {
                if (performer.getSex() == 1) {
                    ability = 7;
                    break;
                }
                ability = 20;
                break;
            }
            case 798: {
                if (performer.getSex() == 1) {
                    ability = 35;
                    break;
                }
                ability = 34;
                break;
            }
            case 800: {
                if (performer.getSex() == 1) {
                    ability = 6;
                    break;
                }
                ability = 32;
                break;
            }
            case 795: {
                if (performer.getSex() == 1) {
                    ability = 8;
                    break;
                }
                ability = 24;
                break;
            }
            case 803: {
                ability = 33;
                break;
            }
            case 807: {
                ability = 41;
                break;
            }
            case 804: {
                ability = 44;
                break;
            }
            case 802: {
                if (performer.getSex() == 1) {
                    ability = 42;
                    break;
                }
                ability = 43;
                break;
            }
            case 794: {
                ability = 39;
                break;
            }
            default: {
                ability = 0;
                break;
            }
        }
        return ability;
    }
    
    public static final boolean useItem(final Creature performer, final Item item, final Action act, final float counter) {
        if (!item.isAbility()) {
            performer.getCommunicator().sendNormalServerMessage("The " + item.getName() + " makes no sense to you.");
            return true;
        }
        if (item.isStreetLamp()) {
            performer.getCommunicator().sendNormalServerMessage("You need to plant the " + item.getName() + " for it to have effect.");
            return true;
        }
        if (alreadyHasAbilityForItem(item, performer)) {
            performer.getCommunicator().sendNormalServerMessage("You already know the secrets that the " + item.getName() + " contains.");
            return true;
        }
        if (item.getAuxData() >= 3 && item.getTemplateId() != 794) {
            performer.getCommunicator().sendNormalServerMessage("The " + item.getName() + " is all used up.");
            return true;
        }
        if (!isInProperLocation(item, performer)) {
            return true;
        }
        int time = act.getTimeLeft();
        boolean toReturn = false;
        if (counter == 1.0f) {
            time = 200;
            if (item.getTemplateId() == 794) {
                performer.getCommunicator().sendNormalServerMessage("You try to solve the puzzle of the " + item.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts solving the puzzle of the " + item.getName() + ".", performer, 5);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You start using the " + item.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts using " + item.getNameWithGenus() + ".", performer, 5);
            }
            performer.sendActionControl(Actions.actionEntrys[118].getVerbString(), true, time);
            act.setTimeLeft(time);
        }
        if (act.currentSecond() == 10) {
            act.setManualInvulnerable(true);
            sendUseMessage(item, performer);
            act.setManualInvulnerable(false);
        }
        else if (act.currentSecond() > time / 10) {
            toReturn = true;
            if (item.getTemplateId() != 794) {
                item.setAuxData((byte)(item.getAuxData() + 1));
            }
            if (item.getTemplateId() == 794) {
                try {
                    if (performer.getSkills().getSkill(100).skillCheck(25.0, 0.0, false, 30.0f) > 0.0) {
                        ascend(performer, item.getAuxData(), item.getData1());
                        Items.destroyItem(item.getWurmId());
                        performer.setAbilityTitle(getNewAbilityTitle(performer));
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You fail to solve the puzzle this time. You may have to train your logical skills.");
                    }
                }
                catch (NoSuchSkillException nsc) {
                    performer.getSkills().learn(100, 19.0f);
                }
            }
            else {
                final int newAbility = getAbilityForItem(item.getTemplateId(), performer);
                performer.setAbility(newAbility, true);
                performer.setAbilityTitle(getNewAbilityTitle(performer));
                sendEffectsToCreature(performer);
            }
            if (performer.isFrozen()) {
                performer.toggleFrozen(performer);
            }
            if (item.getAuxData() >= 3 && item.getTemplateId() != 794) {
                performer.getCommunicator().sendNormalServerMessage("The " + item.getName() + " crumbles to dust.");
                Items.destroyItem(item.getWurmId());
            }
        }
        return toReturn;
    }
    
    private static final void sendUseMessage(final Item item, final Creature performer) {
        switch (item.getTemplateId()) {
            case 808: {
                performer.getCommunicator().sendNormalServerMessage("You stare into the darkness of the Abyss. You step into it and fall. You fall..");
                performer.setDisease((byte)50);
                performer.getStatus().modifyStamina(-65535.0f);
                performer.getStatus().modifyThirst(65535.0f);
                performer.getStatus().modifyHunger(65535, 0.0f);
                break;
            }
            case 796: {
                performer.getCommunicator().sendNormalServerMessage("The pain! The horror! The horror!");
                performer.getStatus().modifyStamina(-65535.0f);
                performer.getStatus().modifyThirst(65535.0f);
                performer.getStatus().modifyHunger(65535, 0.0f);
                performer.addWoundOfType(null, (byte)6, 21, false, 1.0f, true, 10000.0, 10.0f, 0.0f, false, false);
                break;
            }
            case 806: {
                performer.getCommunicator().sendNormalServerMessage("You never thought that something could be this dark.. so.. vicious and hopeless..");
                performer.getStatus().modifyStamina(-65535.0f);
                performer.getStatus().modifyThirst(65535.0f);
                performer.getStatus().modifyHunger(65535, 0.0f);
                performer.addWoundOfType(null, (byte)6, 21, false, 1.0f, true, 10000.0, 10.0f, 0.0f, false, false);
                break;
            }
            case 809: {
                performer.getCommunicator().sendNormalServerMessage("The tome sparkles with weird energies!");
                if (!performer.addWoundOfType(null, (byte)4, 14, false, 1.0f, true, 5000.0, 0.0f, 0.0f, false, false)) {
                    performer.addWoundOfType(null, (byte)4, 13, false, 1.0f, true, 5000.0, 0.0f, 0.0f, false, false);
                    break;
                }
                break;
            }
            case 797: {
                if (!performer.addWoundOfType(null, (byte)10, 1, false, 1.0f, false, 10000.0, 0.0f, 0.0f, false, false)) {
                    performer.toggleFrozen(performer);
                    break;
                }
                break;
            }
            case 799: {
                performer.getCommunicator().sendNormalServerMessage("The pages all seem to be water... waves.. flowing..");
                if (performer.addWoundOfType(null, (byte)7, 2, false, 1.0f, false, 20000.0, 0.0f, 0.0f, false, false)) {
                    return;
                }
                performer.toggleFrozen(performer);
                performer.getStatus().modifyStamina(-65535.0f);
                performer.getStatus().modifyThirst(65535.0f);
                performer.getStatus().modifyHunger(65535, 0.0f);
                break;
            }
            case 810: {
                performer.getCommunicator().sendNormalServerMessage("The strong light emanating from the pages make you wonder if you really read those symbols!");
                performer.addWoundOfType(null, (byte)4, 1, false, 1.0f, false, 10000.0, 0.0f, 0.0f, false, false);
                break;
            }
            case 794: {
                performer.getCommunicator().sendNormalServerMessage("You get the feeling that you are dissolving!");
                performer.toggleFrozen(performer);
                break;
            }
            case 801: {
                performer.getCommunicator().sendNormalServerMessage("This cherry is the best thing you've ever tasted!");
                performer.getStatus().refresh(0.99f, true);
                break;
            }
            case 798: {
                performer.getCommunicator().sendNormalServerMessage("Your inner eye sees fires as you enter a feverish trance.");
                performer.getStatus().refresh(0.99f, true);
                break;
            }
            case 800: {
                performer.getCommunicator().sendNormalServerMessage("The cherry is pretty tasteless.");
                break;
            }
            case 795: {
                performer.getCommunicator().sendNormalServerMessage("Woah! This.. is.. good!");
                performer.getStatus().refresh(0.99f, true);
                break;
            }
            case 803: {
                performer.getCommunicator().sendNormalServerMessage("The walnut is surprisingly sweet.");
                performer.getStatus().refresh(0.99f, true);
                break;
            }
            case 807: {
                performer.getCommunicator().sendNormalServerMessage("It's confusing. You read something about 'Source alignment, karma pulse'...");
                if (performer.getKarma() < 100) {
                    performer.setKarma(100);
                    break;
                }
                break;
            }
            case 802: {
                performer.getCommunicator().sendNormalServerMessage("The cherry is very bitter.");
                CombatEngine.addWound(null, performer, (byte)1, 5, 10000.0, 1.0f, "", null, 0.0f, 1.0f, false, false, false, false);
                break;
            }
        }
    }
    
    private static final boolean isInProperLocation(final Item item, final Creature performer) {
        if (performer.getPower() >= 2) {
            return true;
        }
        boolean ok = true;
        boolean onlySand = true;
        boolean tree = false;
        boolean water = false;
        float height = 0.0f;
        boolean hasAltar = false;
        final int tilex = performer.getTileX();
        final int tiley = performer.getTileY();
        final int sx = Zones.safeTileX(tilex - 1);
        final int ex = Zones.safeTileX(tilex + 1);
        final int sy = Zones.safeTileY(tiley - 1);
        final int ey = Zones.safeTileY(tiley + 1);
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                int tile = Server.surfaceMesh.getTile(x, y);
                if (!performer.isOnSurface()) {
                    tile = Server.caveMesh.getTile(x, y);
                }
                if (!Terraforming.isFlat(x, y, performer.isOnSurface(), 1)) {
                    if (ok) {
                        performer.getCommunicator().sendNormalServerMessage("You need to be standing in a 3x3 flat area in order to use this.");
                    }
                    ok = false;
                }
                height = Tiles.decodeHeightAsFloat(tile);
                water = (height <= 0.0f && height > -1.0f);
                final Tiles.Tile theTile = Tiles.getTile(Tiles.decodeType(tile));
                if (theTile.isNormalTree()) {
                    final byte data = Tiles.decodeData(tile);
                    if (!theTile.getTreeType(data).isFruitTree()) {
                        final byte age = FoliageAge.getAgeAsByte(data);
                        if (age >= FoliageAge.MATURE_SPROUTING.getAgeId() && age <= FoliageAge.OVERAGED.getAgeId()) {
                            tree = true;
                        }
                    }
                }
                if (Tiles.decodeType(tile) != Tiles.Tile.TILE_SAND.id) {
                    onlySand = false;
                }
                final VolaTile t = Zones.getTileOrNull(x, y, performer.isOnSurface());
                if (t != null) {
                    final Item[] items2;
                    final Item[] items = items2 = t.getItems();
                    for (final Item i : items2) {
                        if (i.isAltar()) {
                            hasAltar = true;
                        }
                    }
                }
            }
        }
        if (onlySand) {
            int tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex - 20), Zones.safeTileY(tiley - 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex + 20), Zones.safeTileX(tiley + 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex - 20), Zones.safeTileX(tiley + 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex + 20), Zones.safeTileX(tiley - 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex + 20), Zones.safeTileX(tiley));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex - 20), Zones.safeTileX(tiley));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex), Zones.safeTileX(tiley - 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
            tile2 = Server.surfaceMesh.getTile(Zones.safeTileX(tilex), Zones.safeTileX(tiley + 20));
            if (Tiles.decodeType(tile2) != Tiles.Tile.TILE_SAND.id) {
                onlySand = false;
            }
        }
        if (!hasAltar) {
            performer.getCommunicator().sendNormalServerMessage("You need to be in the vicinity of a holy altar.");
            ok = false;
        }
        switch (item.getTemplateId()) {
            case 796:
            case 806:
            case 808: {
                if (performer.isOnSurface()) {
                    ok = false;
                    performer.getCommunicator().sendNormalServerMessage("You need to be in the darkness of caves, sheltered from sight.");
                    break;
                }
                break;
            }
            case 797:
            case 799:
            case 809: {
                if (!water) {
                    ok = false;
                    performer.getCommunicator().sendNormalServerMessage("You need to be standing in the cleansing shallow water.");
                    break;
                }
                break;
            }
            case 794:
            case 801:
            case 810: {
                if (height < 175.0f) {
                    ok = false;
                    performer.getCommunicator().sendNormalServerMessage("You need to be high up towards the heavens, closer to the gods.");
                    break;
                }
                break;
            }
            case 795:
            case 798:
            case 800: {
                if (!onlySand) {
                    ok = false;
                    performer.getCommunicator().sendNormalServerMessage("You need to be deep in the barren desert, where nothing ever grows.");
                    break;
                }
                break;
            }
            case 802:
            case 803:
            case 807: {
                if (!tree) {
                    ok = false;
                    performer.getCommunicator().sendNormalServerMessage("You need to be close to a strong plant, so that you may connect to its life force.");
                    break;
                }
                break;
            }
        }
        return ok;
    }
    
    public static final void sendEffectsToCreature(final Creature c) {
        if (!c.hasAnyAbility()) {
            return;
        }
        if (c.getFireResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.FIRE_RESIST, 100000, c.getFireResistance() * 100.0f);
        }
        if (c.getColdResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.COLD_RESIST, 100000, c.getColdResistance() * 100.0f);
        }
        if (c.getDiseaseResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.DISEASE_RESIST, 100000, c.getDiseaseResistance() * 100.0f);
        }
        if (c.getPhysicalResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.PHYSICAL_RESIST, 100000, c.getPhysicalResistance() * 100.0f);
        }
        if (c.getPierceResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.PIERCE_RESIST, 100000, c.getPierceResistance() * 100.0f);
        }
        if (c.getSlashResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.SLASH_RESIST, 100000, c.getSlashResistance() * 100.0f);
        }
        if (c.getCrushResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.CRUSH_RESIST, 100000, c.getCrushResistance() * 100.0f);
        }
        if (c.getBiteResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.BITE_RESIST, 100000, c.getBiteResistance() * 100.0f);
        }
        if (c.getPoisonResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POISON_RESIST, 100000, c.getPoisonResistance() * 100.0f);
        }
        if (c.getWaterResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.WATER_RESIST, 100000, c.getWaterResistance() * 100.0f);
        }
        if (c.getAcidResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.ACID_RESIST, 100000, c.getAcidResistance() * 100.0f);
        }
        if (c.getInternalResistance() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.INTERNAL_RESIST, 100000, c.getInternalResistance() * 100.0f);
        }
        if (c.getFireVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.FIRE_VULNERABILITY, 100000, -100.0f + c.getFireVulnerability() * 100.0f);
        }
        if (c.getColdVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.COLD_VULNERABILITY, 100000, -100.0f + c.getColdVulnerability() * 100.0f);
        }
        if (c.getDiseaseVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.DISEASE_VULNERABILITY, 100000, -100.0f + c.getDiseaseVulnerability() * 100.0f);
        }
        if (c.getPhysicalVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.PHYSICAL_VULNERABILITY, 100000, -100.0f + c.getPhysicalVulnerability() * 100.0f);
        }
        if (c.getPierceVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.PIERCE_VULNERABILITY, 100000, -100.0f + c.getPierceVulnerability() * 100.0f);
        }
        if (c.getSlashVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.SLASH_VULNERABILITY, 100000, -100.0f + c.getSlashVulnerability() * 100.0f);
        }
        if (c.getCrushVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.CRUSH_VULNERABILITY, 100000, -100.0f + c.getCrushVulnerability() * 100.0f);
        }
        if (c.getBiteVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.BITE_VULNERABILITY, 100000, -100.0f + c.getBiteVulnerability() * 100.0f);
        }
        if (c.getPoisonVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POISON_VULNERABILITY, 100000, -100.0f + c.getPoisonVulnerability() * 100.0f);
        }
        if (c.getWaterVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.WATER_VULNERABILITY, 100000, -100.0f + c.getWaterVulnerability() * 100.0f);
        }
        if (c.getAcidVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.ACID_VULNERABILITY, 100000, -100.0f + c.getAcidVulnerability() * 100.0f);
        }
        if (c.getInternalVulnerability() > 0.0f) {
            c.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.INTERNAL_VULNERABILITY, 100000, -100.0f + c.getInternalVulnerability() * 100.0f);
        }
    }
    
    static {
        abilityDescs = new String[64];
        logger = Logger.getLogger(Abilities.class.getName());
        initialiseAbilities();
    }
}
