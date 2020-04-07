// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Hashtable;
import com.wurmonline.server.kingdom.Appointment;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.kingdom.Appointments;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.players.Spawnpoint;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.behaviours.NoSuchBehaviourException;
import com.wurmonline.server.behaviours.BehaviourDispatcher;
import com.wurmonline.server.webinterface.WcKingdomChat;
import com.wurmonline.server.Message;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.players.PlayerJournal;
import com.wurmonline.server.players.JournalTier;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.villages.Reputation;
import com.wurmonline.server.villages.KosWarning;
import com.wurmonline.server.utils.NameCountList;
import java.util.Map;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.villages.RecruitmentAd;
import com.wurmonline.server.villages.RecruitmentAds;
import com.wurmonline.server.villages.WarDeclaration;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.players.Friend;
import com.wurmonline.server.villages.GuardPlan;
import java.util.ListIterator;
import java.util.LinkedList;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.NoSuchRoleException;
import java.util.StringTokenizer;
import com.wurmonline.server.Features;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.villages.Citizen;
import java.util.Set;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.epic.EpicServerStatus;
import java.util.HashSet;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.creatures.CreaturePos;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import java.util.Collection;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.players.Cults;
import com.wurmonline.server.players.Cultist;
import com.wurmonline.server.skills.Affinities;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.skills.SkillTemplate;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.behaviours.WurmPermissions;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.items.AdvancedCreationEntry;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.BushData;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.WurmColor;
import java.util.Iterator;
import java.util.Properties;
import java.io.IOException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Items;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.shared.constants.CounterTypes;

public final class QuestionParser implements QuestionTypes, CounterTypes, ItemTypes, VillageStatus, TimeConstants, MonetaryConstants, MiscConstants, CreatureTemplateIds
{
    private static Logger logger;
    public static final String legalChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!() ";
    private static final String numbers = "1234567890";
    public static final String villageLegalChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- ";
    
    static final boolean containsIllegalCharacters(final String name) {
        final char[] chars = name.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!() ".indexOf(chars[x]) < 0) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean containsIllegalVillageCharacters(final String name) {
        final char[] chars = name.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- ".indexOf(chars[x]) < 0) {
                return true;
            }
        }
        return false;
    }
    
    static void parseShutdownQuestion(final ShutDownQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 13) {
            if (WurmId.getType(target) != 2 && WurmId.getType(target) != 19) {
                if (WurmId.getType(target) != 20) {
                    return;
                }
            }
            try {
                final Item item = Items.getItem(target);
                if (item.getTemplateId() == 176 && responder.getPower() >= 3) {
                    final String minutest = question.getAnswer().getProperty("minutes");
                    final String secondst = question.getAnswer().getProperty("seconds");
                    final String reason = question.getAnswer().getProperty("reason");
                    try {
                        final int minutes = Integer.parseInt(minutest);
                        final int seconds = Integer.parseInt(secondst);
                        final String globalStr = question.getAnswer().getProperty("global");
                        boolean global = false;
                        if (globalStr != null && globalStr.equals("true")) {
                            global = true;
                        }
                        if (global) {
                            Servers.startShutdown(responder.getName(), minutes * 60 + seconds, reason);
                        }
                        if (Servers.isThisLoginServer() || !global) {
                            Server.getInstance().startShutdown(minutes * 60 + seconds, reason);
                            QuestionParser.logger.log(Level.INFO, responder.getName() + " shutting down server in " + minutes + " minutes and " + seconds + " seconds, reason: " + reason);
                            if (responder.getLogger() != null) {
                                responder.getLogger().log(Level.INFO, responder.getName() + " shutting down server in " + minutes + " minutes and " + seconds + " seconds, reason: " + reason);
                            }
                        }
                    }
                    catch (NumberFormatException nfe) {
                        responder.getCommunicator().sendNormalServerMessage("Failed to parse " + minutest + " or " + secondst + " to a number. Please try again.");
                    }
                }
                else {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " managed to try to shutdown with item " + item + ".");
                    responder.getCommunicator().sendNormalServerMessage("You can't shutdown with that. In fact you should not even manage to try. This has been logged.");
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which shutdown was intended.");
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
        }
    }
    
    static final boolean containsNonNumber(final String name) {
        final char[] chars = name.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            if ("1234567890".indexOf(chars[x]) < 0) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean isMultiplierName(final String answer) {
        if ((answer.toLowerCase().startsWith("x") || answer.toLowerCase().endsWith("x")) && answer.length() > 1) {
            String rest;
            if (answer.toLowerCase().startsWith("x")) {
                rest = answer.substring(1, answer.length());
            }
            else {
                rest = answer.substring(0, answer.length() - 1);
            }
            return !containsNonNumber(rest);
        }
        return false;
    }
    
    static void parseTextInputQuestion(final TextInputQuestion question, final Item liquid) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 2) {
            if (liquid != null && !Items.exists(liquid)) {
                responder.getCommunicator().sendNormalServerMessage("Your " + liquid.getName() + " you started inscribing with has vanished.");
                return;
            }
            try {
                final Item item = Items.getItem(target);
                if (item.getInscription() != null && responder.getPower() < 2) {
                    responder.getCommunicator().sendNormalServerMessage("The " + item.getName() + " already have an inscription.");
                    return;
                }
                final String answer = question.getAnswer().getProperty("answer").trim();
                if (InscriptionData.containsIllegalCharacters(answer)) {
                    responder.getCommunicator().sendNormalServerMessage("The inscription contains some characters that are too complex for you to inscribe.");
                    return;
                }
                if (answer.length() == 0) {
                    responder.getCommunicator().sendNormalServerMessage("You decide not to inscribe the " + item.getName() + " at the moment.");
                    return;
                }
                int colour = 0;
                if (liquid != null && liquid.getTemplateId() != 753) {
                    colour = liquid.color;
                }
                item.setInscription(answer, responder.getName(), colour);
                responder.getCommunicator().sendNormalServerMessage("You carefully inscribe the " + item.getName() + " with " + answer.length() + " printed letters.");
                if (liquid != null) {
                    liquid.setWeight(liquid.getWeightGrams() - 10, true);
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which an inscription was intended.");
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
        }
        else if (type == 1 && target != -10L) {
            if (WurmId.getType(target) != 2 && WurmId.getType(target) != 19) {
                if (WurmId.getType(target) != 20) {
                    if (WurmId.getType(target) == 4) {
                        try {
                            final Structure structure = Structures.getStructure(target);
                            if (structure.isTypeHouse()) {
                                final Item writ = Items.getItem(structure.getWritId());
                                if (responder.getPower() < 2 && writ.getOwnerId() != responder.getWurmId()) {
                                    return;
                                }
                            }
                            QuestionParser.logger.log(Level.INFO, "Setting structure " + structure.getName() + " to " + question.getAnswer().getProperty("answer"));
                            final String answer = question.getAnswer().getProperty("answer");
                            if (containsIllegalCharacters(answer)) {
                                responder.getCommunicator().sendNormalServerMessage("The name contains illegal characters.");
                                return;
                            }
                            if (!structure.getName().equals(answer)) {
                                structure.setName(answer, true);
                            }
                        }
                        catch (NoSuchStructureException nss) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to locate structure with id=" + target + " for which name change was intended.");
                        }
                        catch (NoSuchItemException nsi2) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to locate writ for structure with id=" + target + ".");
                        }
                    }
                    return;
                }
            }
            try {
                final Item item = Items.getItem(target);
                if (!item.isNoRename()) {
                    final int templateId = item.getTemplateId();
                    String answer2 = question.getAnswer().getProperty("answer");
                    if (containsIllegalCharacters(answer2)) {
                        responder.getCommunicator().sendNormalServerMessage("The name contains illegal characters.");
                        return;
                    }
                    boolean updated = false;
                    if (item.isSign()) {
                        int mod = 1;
                        if (templateId == 209) {
                            mod = 2;
                        }
                        final int maxSize = Math.max(5, (int)(item.getRarity() * 3 + item.getCurrentQualityLevel() * mod));
                        if (answer2.length() > maxSize) {
                            responder.getCommunicator().sendSafeServerMessage("The text is too long. Only " + maxSize + " letters can be imprinted on this sign.");
                        }
                        answer2 = answer2.substring(0, Math.min(answer2.length(), maxSize));
                        final String stype = question.getAnswer().getProperty("data1");
                        if (stype != null && stype.length() > 0) {
                            final byte bt = Byte.parseByte(stype);
                            if (bt > 0 && bt <= 22) {
                                item.setAuxData(bt);
                                if (!item.setDescription(answer2) && item.getZoneId() > 0 && item.getParentId() == -10L) {
                                    final VolaTile t = Zones.getTileOrNull(item.getTileX(), item.getTileY(), item.isOnSurface());
                                    if (t != null) {
                                        t.renameItem(item);
                                    }
                                }
                                updated = true;
                            }
                        }
                    }
                    if (templateId == 521 && responder.getPower() > 0) {
                        item.setName(answer2);
                    }
                    else if (templateId == 651) {
                        if (question.getOldtext().length() > 0 && !item.getCreatorName().toLowerCase().equals(responder.getName().toLowerCase())) {
                            responder.getCommunicator().sendNormalServerMessage("You can't change the recipient of the gift.");
                            return;
                        }
                        item.setDescription(answer2);
                        item.setName("From " + item.getSignature() + " to " + answer2);
                    }
                    else if (templateId == 824) {
                        if (answer2.length() == 0) {
                            responder.getCommunicator().sendNormalServerMessage("Groups must have a name.");
                        }
                        item.setName(answer2);
                    }
                    else if (templateId == 1128) {
                        if (answer2.length() == 0) {
                            responder.getCommunicator().sendNormalServerMessage("Almanac folders must have a name.");
                        }
                        item.setName(answer2);
                    }
                    else {
                        if (isMultiplierName(answer2)) {
                            responder.getCommunicator().sendNormalServerMessage("Starting or ending the description with x indicates a multiplier, which is not allowed.");
                            return;
                        }
                        if (!updated) {
                            item.setDescription(answer2);
                        }
                    }
                }
                else {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " managed to try to rename item " + item.getName() + " which is non-renamable.");
                    responder.getCommunicator().sendNormalServerMessage("You can't rename that.");
                }
            }
            catch (NoSuchItemException nsi2) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which name change was intended.");
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
        }
        else if (type == 1 && target == -10L) {
            final String answer3 = question.getAnswer().getProperty("answer").trim();
            if (containsIllegalCharacters(answer3)) {
                responder.getCommunicator().sendNormalServerMessage("The name contains illegal characters.");
                return;
            }
            if (isMultiplierName(answer3)) {
                responder.getCommunicator().sendNormalServerMessage("Starting or ending the description with x indicates a multiplier, which is not allowed.");
                return;
            }
            for (final Item item2 : question.getItems()) {
                if (!item2.isNoRename()) {
                    final int templateId2 = item2.getTemplateId();
                    if (templateId2 != 521 && templateId2 != 651 && templateId2 != 824 && !item2.isCoin()) {
                        item2.setDescription(answer3);
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("Cannot rename " + item2.getName() + ".");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("Cannot rename " + item2.getName() + ".");
                }
            }
        }
    }
    
    static void parseStructureManagement(final StructureManagement question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (WurmId.getType(target) == 4) {
            try {
                final Structure structure = Structures.getStructure(target);
                Item writ = Items.getItem(structure.getWritId());
                if (writ.getOwnerId() != responder.getWurmId()) {
                    return;
                }
                final Properties props = question.getAnswer();
                for (final String key : ((Hashtable<Object, V>)props).keySet()) {
                    if (key.equals("demolish") && ((Hashtable<K, Object>)props).get(key).equals("true")) {
                        structure.totallyDestroy();
                        return;
                    }
                    if (key.equals("allowAllies")) {
                        if (props.get(key) != null && ((Hashtable<K, Object>)props).get(key).equals("true")) {
                            structure.setAllowAllies(true);
                        }
                        else {
                            structure.setAllowAllies(false);
                        }
                    }
                    else if (key.equals("allowVillagers")) {
                        if (props.get(key) != null && ((Hashtable<K, Object>)props).get(key).equals("true")) {
                            structure.setAllowVillagers(true);
                        }
                        else {
                            structure.setAllowVillagers(false);
                        }
                    }
                    else if (key.equals("allowKingdom")) {
                        if (props.get(key) != null && ((Hashtable<K, Object>)props).get(key).equals("true")) {
                            structure.setAllowKingdom(true);
                        }
                        else {
                            structure.setAllowKingdom(false);
                        }
                    }
                    else if (key.charAt(0) == 'f') {
                        boolean set = false;
                        try {
                            final String val = props.getProperty(key);
                            set = val.equals("true");
                        }
                        catch (Exception ex2) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to set " + props.getProperty(key) + " to a boolean.");
                        }
                        if (!set) {
                            continue;
                        }
                        final String fis = key.substring(1, key.length());
                        try {
                            final long fid = Long.parseLong(fis);
                            structure.addGuest(fid, 42);
                        }
                        catch (Exception ex) {
                            QuestionParser.logger.log(Level.WARNING, "Faiiled to add guest " + fis, ex);
                        }
                    }
                    else if (key.charAt(0) == 'g') {
                        boolean set = false;
                        try {
                            final String val = props.getProperty(key);
                            set = val.equals("true");
                        }
                        catch (Exception ex2) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to set " + props.getProperty(key) + " to a boolean.");
                        }
                        if (!set) {
                            continue;
                        }
                        final String gis = key.substring(1, key.length());
                        try {
                            final long gid = Long.parseLong(gis);
                            structure.removeGuest(gid);
                        }
                        catch (Exception ex) {
                            QuestionParser.logger.log(Level.WARNING, "Faiiled to remove guest " + gis, ex);
                        }
                    }
                    else if (key.equals("lock")) {
                        boolean set = false;
                        try {
                            final String val = props.getProperty(key);
                            set = val.equals("true");
                        }
                        catch (Exception ex2) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to set " + props.getProperty(key) + " to a boolean.");
                        }
                        if (!set) {
                            continue;
                        }
                        structure.lockAllDoors();
                    }
                    else if (key.equals("unlock")) {
                        boolean set = false;
                        try {
                            final String val = props.getProperty(key);
                            set = val.equals("true");
                        }
                        catch (Exception ex2) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to set " + props.getProperty(key) + " to a boolean.");
                        }
                        if (!set) {
                            continue;
                        }
                        structure.unlockAllDoors();
                    }
                    else {
                        if (!key.equals("sname")) {
                            continue;
                        }
                        String name = props.getProperty("sname");
                        if (name == null || name.equals(structure.getName())) {
                            continue;
                        }
                        if (name.length() >= 41) {
                            name = name.substring(0, 39);
                            responder.getCommunicator().sendNormalServerMessage("The name has been truncated to " + name + ".");
                        }
                        else if (name.length() < 3) {
                            responder.getCommunicator().sendSafeServerMessage("Please select a longer name.");
                        }
                        else if (containsIllegalCharacters(name)) {
                            responder.getCommunicator().sendSafeServerMessage("The name " + name + " contain illegal characters. Please select another name.");
                        }
                        else {
                            structure.setName(name, false);
                            try {
                                writ = Items.getItem(structure.getWritId());
                                writ.setDescription(name);
                            }
                            catch (NoSuchItemException nsi) {
                                QuestionParser.logger.log(Level.WARNING, "Structure " + target + " has no writ with id " + structure.getWritId() + "?", nsi);
                            }
                        }
                    }
                }
                try {
                    structure.save();
                }
                catch (IOException iox) {
                    QuestionParser.logger.log(Level.WARNING, "Failed to save structure " + target, iox);
                }
            }
            catch (NoSuchStructureException nss) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate structure with id=" + target + " for which name change was intended.", nss);
            }
            catch (NoSuchItemException nsi2) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate writ for structure with id=" + target + ".", nsi2);
            }
        }
    }
    
    static void parseItemDataQuestion(final ItemDataQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 4 && (WurmId.getType(target) == 2 || WurmId.getType(target) == 19 || WurmId.getType(target) == 20)) {
            final String name = question.getAnswer().getProperty("itemName");
            final String d1 = question.getAnswer().getProperty("data1");
            final String d2 = question.getAnswer().getProperty("data2");
            final String e1 = question.getAnswer().getProperty("extra1");
            final String e2 = question.getAnswer().getProperty("extra2");
            String aux = "?";
            try {
                final Item item = Items.getItem(target);
                String extra = "";
                if (name != null) {
                    item.setName(name, true);
                }
                final int data1 = (d1 == null) ? -1 : Integer.parseInt(d1);
                final int data2 = (d2 == null) ? -1 : Integer.parseInt(d2);
                final int extra2 = (e1 == null) ? -1 : Integer.parseInt(e1);
                final int extra3 = (e2 == null) ? -1 : Integer.parseInt(e2);
                if (item.hasData()) {
                    item.setAllData(data1, data2, extra2, extra3);
                }
                byte auxd = 0;
                if (item.usesFoodState()) {
                    final String raux = question.getAnswer().getProperty("raux");
                    auxd = Byte.parseByte(raux);
                    if (Boolean.parseBoolean(question.getAnswer().getProperty("chopped"))) {
                        auxd += 16;
                    }
                    if (Boolean.parseBoolean(question.getAnswer().getProperty("mashed"))) {
                        auxd += 32;
                    }
                    if (Boolean.parseBoolean(question.getAnswer().getProperty("wrap"))) {
                        auxd += 64;
                    }
                    if (Boolean.parseBoolean(question.getAnswer().getProperty("fresh"))) {
                        auxd += 128;
                    }
                }
                else {
                    aux = question.getAnswer().getProperty("aux");
                    auxd = (byte)Integer.parseInt(aux);
                }
                item.setAuxData(auxd);
                String val = question.getAnswer().getProperty("dam");
                if (val != null) {
                    try {
                        item.setDamage(Float.parseFloat(val));
                        extra = extra + ", dam=" + val;
                    }
                    catch (Exception ex3) {}
                }
                val = question.getAnswer().getProperty("temp");
                if (val != null) {
                    try {
                        item.setTemperature(Short.parseShort(val));
                        extra = extra + ", temp=" + val;
                    }
                    catch (Exception ex4) {}
                }
                val = question.getAnswer().getProperty("weight");
                if (val != null) {
                    try {
                        if (Integer.parseInt(val) <= 0) {
                            responder.getCommunicator().sendNormalServerMessage("Weight cannot be below 1.");
                        }
                        else {
                            item.setWeight(Integer.parseInt(val), false);
                            extra = extra + ", weight=" + val;
                        }
                    }
                    catch (Exception ex5) {}
                }
                val = question.getAnswer().getProperty("rarity");
                if (val != null) {
                    try {
                        byte vals = Byte.parseByte(val);
                        if (vals < 0) {
                            vals = 0;
                        }
                        if (vals > 3) {
                            vals = 3;
                        }
                        extra = extra + ", rarity=" + vals;
                        item.setRarity(vals);
                    }
                    catch (Exception ex6) {}
                }
                final String fruit = question.getAnswer().getProperty("fruit");
                if (fruit != null) {
                    final ItemTemplate template = question.getTemplate(Integer.parseInt(fruit));
                    if (template != null && template.getTemplateId() != item.getRealTemplateId()) {
                        item.setRealTemplate(template.getTemplateId());
                        extra = " and " + template.getName();
                        responder.getCommunicator().sendUpdateInventoryItem(item);
                    }
                    if (template == null && item.getRealTemplateId() != -10 && item.getTemplateId() != 1307) {
                        item.setRealTemplate(-10);
                        item.setName("fruit juice");
                        responder.getCommunicator().sendUpdateInventoryItem(item);
                    }
                }
                final String red = question.getAnswer().getProperty("c_red");
                final String green = question.getAnswer().getProperty("c_green");
                final String blue = question.getAnswer().getProperty("c_blue");
                final String tickp = question.getAnswer().getProperty("primary");
                try {
                    final int r = Integer.parseInt(red);
                    final int g = Integer.parseInt(green);
                    final int b = Integer.parseInt(blue);
                    final boolean tick = Boolean.parseBoolean(tickp);
                    if (tick) {
                        item.setColor(WurmColor.createColor((r < 0) ? 0 : r, (g < 0) ? 0 : g, (b < 0) ? 0 : b));
                        extra = extra + ", color=[R:" + r + " G:" + g + " B:" + b + "]";
                    }
                    else {
                        item.setColor(-1);
                        extra += ", color=none";
                    }
                }
                catch (NumberFormatException | NullPointerException ex7) {
                    final RuntimeException ex;
                    final RuntimeException e3 = ex;
                    item.setColor(-1);
                    extra += ", color=none";
                }
                final String tick2 = question.getAnswer().getProperty("secondary");
                if (tick2 != null) {
                    final String red2 = question.getAnswer().getProperty("c2_red");
                    final String green2 = question.getAnswer().getProperty("c2_green");
                    final String blue2 = question.getAnswer().getProperty("c2_blue");
                    try {
                        final int r2 = Integer.parseInt(red2);
                        final int g2 = Integer.parseInt(green2);
                        final int b2 = Integer.parseInt(blue2);
                        final boolean tick3 = Boolean.parseBoolean(tick2);
                        if (tick3) {
                            item.setColor2(WurmColor.createColor((r2 < 0) ? 0 : r2, (g2 < 0) ? 0 : g2, (b2 < 0) ? 0 : b2));
                            extra = extra + ", color2=[R:" + r2 + " G:" + g2 + " B:" + b2 + "]";
                        }
                        else {
                            item.setColor2(-1);
                            extra += ", color2=none";
                        }
                    }
                    catch (NumberFormatException | NullPointerException ex8) {
                        final RuntimeException ex2;
                        final RuntimeException e4 = ex2;
                        item.setColor2(-1);
                        extra += ", color2=none";
                    }
                }
                if (responder.getPower() >= 5) {
                    final long lastMaintained = Long.parseLong(question.getAnswer().getProperty("lastMaintained"));
                    extra = extra + ", lastMaintained=" + lastMaintained;
                    item.setLastMaintained(lastMaintained);
                }
                final String lastOwner = question.getAnswer().getProperty("lastowner");
                try {
                    final long lastOwnerId = Long.parseLong(lastOwner);
                    if (lastOwnerId != item.getLastOwnerId()) {
                        extra = extra + ", lastowner=" + lastOwnerId;
                        item.setLastOwnerId(lastOwnerId);
                    }
                }
                catch (NumberFormatException ex9) {}
                catch (NullPointerException ex10) {}
                responder.getCommunicator().sendNormalServerMessage("You quietly mumble: " + data1 + ", " + data2 + " as well as " + auxd + extra);
                if (responder.getLogger() != null) {
                    responder.getLogger().info("Sets item data of " + target + " (" + item.getName() + ") to : " + data1 + ", " + data2 + ", and aux: " + auxd + extra);
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which data change was intended.", nsi);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendNormalServerMessage("You realize that something doesn't match your requirements. Did you mistype a number?");
                question.getAnswer().forEach((k, v) -> responder.getCommunicator().sendNormalServerMessage(k + " = " + v));
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that data1: " + d1 + ", data2: " + d2 + " or aux: " + aux + " doesn't match their requirements. " + nfe, nfe);
                }
            }
        }
    }
    
    static void parseTileDataQuestion(final TileDataQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        final int tilex = question.getTilex();
        final int tiley = question.getTiley();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 35 && (WurmId.getType(target) == 2 || WurmId.getType(target) == 19 || WurmId.getType(target) == 20)) {
            final String d1 = question.getAnswer().getProperty("surf");
            final String d2 = question.getAnswer().getProperty("cave");
            final boolean bot = Boolean.parseBoolean(question.getAnswer().getProperty("bot"));
            final boolean forage = Boolean.parseBoolean(question.getAnswer().getProperty("forage"));
            final boolean collect = Boolean.parseBoolean(question.getAnswer().getProperty("collect"));
            final boolean transforming = Boolean.parseBoolean(question.getAnswer().getProperty("transforming"));
            final boolean transformed = Boolean.parseBoolean(question.getAnswer().getProperty("transformed"));
            final boolean hive = Boolean.parseBoolean(question.getAnswer().getProperty("hive"));
            final boolean hasGrubs = Boolean.parseBoolean(question.getAnswer().getProperty("hasGrubs"));
            final byte serverCaveFlags = Byte.parseByte(question.getAnswer().getProperty("caveserverflag"));
            final byte clientCaveFlags = Byte.parseByte(question.getAnswer().getProperty("caveclientflag"));
            final short sHeight = Short.parseShort(question.getAnswer().getProperty("surfaceheight"));
            final short rHeight = Short.parseShort(question.getAnswer().getProperty("rockheight"));
            final short cHeight = Short.parseShort(question.getAnswer().getProperty("caveheight"));
            final byte cceil = Byte.parseByte(question.getAnswer().getProperty("caveceiling"));
            final int surfMesh = Server.surfaceMesh.getTile(tilex, tiley);
            final byte surfType = Tiles.decodeType(surfMesh);
            short surfHeight = Tiles.decodeHeight(surfMesh);
            short rockHeight = Tiles.decodeHeight(Server.rockMesh.getTile(tilex, tiley));
            final int caveMesh = Server.caveMesh.getTile(tilex, tiley);
            short caveHeight = Tiles.decodeHeight(caveMesh);
            byte caveCeiling = Tiles.decodeData(caveMesh);
            boolean updateCave = false;
            final Tiles.Tile tile = Tiles.getTile(surfType);
            try {
                final int data1 = Integer.parseInt(d1);
                final int data2 = Integer.parseInt(d2);
                if (data1 >= 0 && (((surfType == 7 || surfType == 43) && data1 <= 2047) || data1 <= 65535)) {
                    if (surfType == 7 || surfType == 43) {
                        final int count = Integer.parseInt(question.getAnswer().getProperty("count"));
                        Server.setWorldResource(tilex, tiley, (count & 0x1F) << 11 | data1);
                    }
                    else if (surfType == 1 || surfType == 2 || surfType == 10 || surfType == 22 || surfType == 6 || surfType == 18 || surfType == 24) {
                        final int count = Integer.parseInt(question.getAnswer().getProperty("qlcnt"));
                        Server.setWorldResource(tilex, tiley, ((count & 0xFF) << 8) + (data1 & 0xFF));
                    }
                    else {
                        Server.setWorldResource(tilex, tiley, data1);
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("Surface resource must be 0-32767");
                }
                Server.setBotanizable(tilex, tiley, bot);
                Server.setForagable(tilex, tiley, forage);
                Server.setGatherable(tilex, tiley, collect);
                Server.setBeingTransformed(tilex, tiley, transforming);
                Server.setTransformed(tilex, tiley, transformed);
                Server.setCheckHive(tilex, tiley, hive);
                Server.setGrubs(tilex, tiley, hasGrubs);
                Server.setClientCaveFlags(tilex, tiley, clientCaveFlags);
                Server.setServerCaveFlags(tilex, tiley, serverCaveFlags);
                if (data2 <= 65535 && data2 >= 0) {
                    Server.setCaveResource(tilex, tiley, data2);
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("Cave resource must be 0-65535");
                }
                if (Math.abs(sHeight - surfHeight) <= 300) {
                    surfHeight = sHeight;
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("Unable to change the surface layer height by more than 300 dirt.");
                }
                if (Math.abs(rHeight - rockHeight) <= 300) {
                    rockHeight = rHeight;
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("Unable to change the rock layer height by more than 300 dirt.");
                }
                if (rockHeight > surfHeight) {
                    surfHeight = rockHeight;
                }
                if (caveHeight != cHeight || caveCeiling != cceil) {
                    caveHeight = cHeight;
                    caveCeiling = cceil;
                    updateCave = true;
                }
                byte surfaceTileData = 0;
                if (tile.isGrass() || surfType == 10) {
                    final int ggrowth = Integer.parseInt(question.getAnswer().getProperty("growth"));
                    final int gflower = Integer.parseInt(question.getAnswer().getProperty("flower"));
                    surfaceTileData = (byte)((ggrowth & 0x3) << 6 | (gflower & 0xF & 0xFF));
                }
                else if (surfType == 31 || surfType == 34 || surfType == 3 || surfType == 14 || surfType == 35 || surfType == 11) {
                    final int tage = Integer.parseInt(question.getAnswer().getProperty("age"));
                    final int ttype = Integer.parseInt(question.getAnswer().getProperty("type"));
                    if (surfType == 31 || surfType == 34 || surfType == 35) {
                        surfaceTileData = (byte)(BushData.BushType.encodeTileData(tage, ttype) & 0xFF);
                    }
                    else {
                        surfaceTileData = (byte)(TreeData.TreeType.encodeTileData(tage, ttype) & 0xFF);
                    }
                }
                else if (tile.usesNewData()) {
                    final byte tage2 = Byte.parseByte(question.getAnswer().getProperty("age"));
                    final int harvestable = Integer.parseInt(question.getAnswer().getProperty("harvestable"));
                    final int incentre = Integer.parseInt(question.getAnswer().getProperty("incentre"));
                    final int growth = Integer.parseInt(question.getAnswer().getProperty("growth"));
                    final FoliageAge age = FoliageAge.fromByte(tage2);
                    final GrassData.GrowthTreeStage grass = GrassData.GrowthTreeStage.fromInt(growth);
                    surfaceTileData = Tiles.encodeTreeData(age, harvestable != 0, incentre != 0, grass);
                }
                else if (surfType == 7 || surfType == 43) {
                    final boolean ftended = Boolean.parseBoolean(question.getAnswer().getProperty("tended"));
                    final int fage = Integer.parseInt(question.getAnswer().getProperty("age"));
                    final int fcrop = Integer.parseInt(question.getAnswer().getProperty("crop"));
                    surfaceTileData = (byte)((ftended ? 128 : 0) | (fage & 0x7) << 4 | (fcrop & 0xF & 0xFF));
                }
                else if (Tiles.isRoadType(surfType)) {
                    final int funused = Integer.parseInt(question.getAnswer().getProperty("unused"));
                    final int fdir = Integer.parseInt(question.getAnswer().getProperty("dir"));
                    surfaceTileData = (byte)((funused & 0x1F) << 3 | (fdir & 0x7 & 0xFF));
                }
                else {
                    final String d3 = question.getAnswer().getProperty("surftiledata");
                    surfaceTileData = Byte.parseByte(d3);
                }
                Server.setSurfaceTile(tilex, tiley, surfHeight, surfType, surfaceTileData);
                Server.rockMesh.setTile(tilex, tiley, Tiles.encode(rockHeight, (short)0));
                if (updateCave) {
                    final byte ctype = Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley));
                    Server.caveMesh.setTile(tilex, tiley, Tiles.encode(caveHeight, ctype, caveCeiling));
                }
                final int surf = Server.getWorldResource(tilex, tiley);
                final int flag = Server.getServerSurfaceFlags(tilex, tiley);
                responder.getCommunicator().sendNormalServerMessage("You quietly mumble: " + tilex + "," + tiley + ":flags:" + flag + ":d1:" + surf + ":d2:" + data2 + "," + (surfaceTileData & 0xFF));
                if (responder.getLogger() != null) {
                    responder.getLogger().info("Sets tile data of " + tilex + "," + tiley + " to flags:" + flag + ":d1:" + surf + ":d2:" + data2 + "," + (surfaceTileData & 0xFF));
                }
                Players.getInstance().sendChangedTile(tilex, tiley, true, true);
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendNormalServerMessage("You realize that " + d1 + " or " + d2 + " doesn't match your requirements.");
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that surface resource: " + d1 + " or cave resource: " + d2 + " doesn't match their requirements. " + nfe, nfe);
                }
            }
        }
    }
    
    static void parseTeleportQuestion(final TeleportQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 17 && (WurmId.getType(target) == 2 || WurmId.getType(target) == 19 || WurmId.getType(target) == 20)) {
            final String d1 = question.getAnswer().getProperty("data1");
            final String d2 = question.getAnswer().getProperty("data2");
            final String wid = question.getAnswer().getProperty("wurmid");
            final String vid = question.getAnswer().getProperty("villid");
            if (!d1.equals("-1")) {
                if (!d2.equals("-1")) {
                    try {
                        final Item item = Items.getItem(target);
                        final int data1 = Integer.parseInt(d1);
                        final int data2 = Integer.parseInt(d2);
                        final String surfaced = question.getAnswer().getProperty("layer");
                        byte layer = 0;
                        if (surfaced != null && surfaced.equals("1")) {
                            layer = -1;
                        }
                        responder.setTeleportLayer(layer);
                        if (layer < 0 && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(data1, data2)))) {
                            responder.getCommunicator().sendNormalServerMessage("The tile " + data1 + ", " + data2 + " is solid cave.");
                            return;
                        }
                        item.setData(data1, data2);
                        responder.getCommunicator().sendNormalServerMessage("You quietly mumble: " + data1 + ", " + data2 + " surfaced=" + (layer == 0));
                        if (responder.getPower() >= 5) {
                            try {
                                final Zone z = Zones.getZone(data1, data2, layer == 0);
                                responder.getCommunicator().sendNormalServerMessage("That zone is number " + z.getId() + " x=" + z.getStartX() + " to " + z.getEndX() + " and " + z.getStartY() + " to " + z.getEndY() + ". Size=" + z.getSize());
                            }
                            catch (Exception e) {
                                responder.getCommunicator().sendNormalServerMessage("Exception: " + e.getMessage());
                                QuestionParser.logger.warning(responder.getName() + " had problems getting zone information while teleporting, data1: " + data1 + ", data2: " + data2 + ", layer: " + layer + ", wurmid: " + wid + ", villageid: " + vid);
                            }
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which name change was intended.", nsi);
                        responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
                    }
                    catch (NumberFormatException nfe) {
                        responder.getCommunicator().sendNormalServerMessage("You realize that " + d1 + " or " + d2 + " doesn't match your requirements.");
                        if (QuestionParser.logger.isLoggable(Level.FINE)) {
                            QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that " + d1 + " or " + d2 + " doesn't match their requirements. " + nfe, nfe);
                        }
                    }
                    return;
                }
            }
            try {
                int vidd = Integer.parseInt(vid);
                if (vidd == 0) {
                    final int listid = Integer.parseInt(wid);
                    final Player p = question.getPlayer(listid);
                    if (p == null) {
                        responder.getCommunicator().sendNormalServerMessage("No player found.");
                        return;
                    }
                    final int tx = p.getTileX();
                    final int ty = p.getTileY();
                    responder.setTeleportLayer(p.isOnSurface() ? 0 : -1);
                    responder.setTeleportFloorLevel(p.getFloorLevel());
                    final Item item2 = Items.getItem(target);
                    if (!p.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tx, ty)))) {
                        responder.getCommunicator().sendNormalServerMessage("The tile " + tx + ", " + ty + " is solid cave.");
                        return;
                    }
                    item2.setData(tx, ty);
                    responder.getLogger().log(Level.INFO, "Located " + p.getName() + " at " + tx + ", " + ty);
                    responder.getCommunicator().sendNormalServerMessage("You quietly mumble: " + p.getName() + "... " + tx + ", " + ty);
                    if (responder.getPower() >= 5) {
                        try {
                            final Zone z2 = Zones.getZone(tx, ty, responder.getTeleportLayer() >= 0);
                            responder.getCommunicator().sendNormalServerMessage("That zone is number " + z2.getId() + " x=" + z2.getStartX() + " to " + z2.getEndX() + " and " + z2.getStartY() + " to " + z2.getEndY() + ". Size=" + z2.getSize());
                        }
                        catch (Exception e2) {
                            responder.getCommunicator().sendNormalServerMessage("Exception: " + e2.getMessage());
                            QuestionParser.logger.warning(responder.getName() + " had problems getting zone information while teleporting, data1: " + tx + ", data2: " + ty + ", layer: " + responder.getTeleportLayer() + ", wurmid: " + wid + ", villageid: " + vid);
                        }
                    }
                }
                else {
                    --vidd;
                    final Village village = question.getVillage(vidd);
                    if (village == null) {
                        responder.getCommunicator().sendNormalServerMessage("No village found.");
                        return;
                    }
                    final int tx2 = village.getTokenX();
                    final int ty2 = village.getTokenY();
                    final Item item3 = Items.getItem(target);
                    responder.setTeleportLayer(village.isOnSurface() ? 0 : -1);
                    if (!village.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tx2, ty2)))) {
                        responder.getCommunicator().sendNormalServerMessage("The tile " + tx2 + ", " + ty2 + " is solid cave.");
                        return;
                    }
                    item3.setData(tx2, ty2);
                    responder.getCommunicator().sendNormalServerMessage("You quietly mumble: " + village.getName() + "... " + tx2 + ", " + ty2);
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which name change was intended.", nsi);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendNormalServerMessage("You realize that the player id " + wid + " doesn't match your requirements.");
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that player id " + wid + " doesn't match their requirements. " + nfe, nfe);
                }
            }
        }
    }
    
    static void parseItemCreationQuestion(final ItemCreationQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 5 && (WurmId.getType(target) == 2 || WurmId.getType(target) == 19 || WurmId.getType(target) == 20)) {
            final String d1 = question.getAnswer().getProperty("data1");
            final String d2 = question.getAnswer().getProperty("data2");
            final String sm = question.getAnswer().getProperty("sizemod");
            final String number = question.getAnswer().getProperty("number");
            final String materialString = question.getAnswer().getProperty("material");
            final String alltypes = question.getAnswer().getProperty("alltypes");
            boolean allMaterialTypes = alltypes != null && alltypes.equals("true");
            final int maxWoodTypes = 16;
            final byte[] woodTypes = MethodsItems.getAllNormalWoodTypes();
            final byte[] metalTypes = MethodsItems.getAllMetalTypes();
            final String rareString = question.getAnswer().getProperty("rare");
            byte material = 0;
            try {
                final Item item = Items.getItem(target);
                final long now = System.currentTimeMillis();
                if (Servers.localServer.testServer || ((item.getTemplateId() == 176 || item.getTemplateId() == 301) && (responder.getPower() >= 2 || Players.isArtist(question.getResponder().getWurmId(), false, false)))) {
                    final int data1 = Integer.parseInt(d1);
                    int data2 = Integer.parseInt(d2);
                    int num = Integer.parseInt(number);
                    final byte rare = Byte.parseByte(rareString);
                    final String name = question.getAnswer().getProperty("itemName");
                    final String red = question.getAnswer().getProperty("c_red");
                    final String green = question.getAnswer().getProperty("c_green");
                    final String blue = question.getAnswer().getProperty("c_blue");
                    int colour = -1;
                    if (red != null && green != null && blue != null && red.length() > 0 && green.length() > 0 && blue.length() > 0) {
                        try {
                            final int r = Integer.parseInt(red);
                            final int g = Integer.parseInt(green);
                            final int b = Integer.parseInt(blue);
                            colour = WurmColor.createColor((r < 0) ? 0 : r, (g < 0) ? 0 : g, (b < 0) ? 0 : b);
                        }
                        catch (NumberFormatException | NullPointerException ex2) {
                            final RuntimeException ex;
                            final RuntimeException e = ex;
                            QuestionParser.logger.log(Level.WARNING, "Bad colours:" + red + "," + green + "," + blue);
                        }
                    }
                    try {
                        material = Byte.parseByte(materialString);
                    }
                    catch (NumberFormatException nfe2) {
                        QuestionParser.logger.log(Level.WARNING, "Material was " + materialString);
                    }
                    final ItemTemplate template = question.getTemplate(data1);
                    if (template == null) {
                        responder.getCommunicator().sendNormalServerMessage("You decide not to create anything.");
                        return;
                    }
                    float sizemod = 1.0f;
                    if (sm != null && sm.length() > 0) {
                        try {
                            sizemod = Math.abs(Float.parseFloat(sm));
                            if (template.getTemplateId() != 995) {
                                sizemod = Math.min(5.0f, sizemod);
                            }
                        }
                        catch (NumberFormatException nfen1) {
                            responder.getCommunicator().sendAlertServerMessage("The size mod " + sm + " is not a float value.");
                        }
                    }
                    if (template.getTemplateId() != 179 && template.getTemplateId() != 386) {
                        if (num != 1 || material != 0) {
                            allMaterialTypes = false;
                        }
                        if (!template.isWood() && !template.isMetal()) {
                            allMaterialTypes = false;
                        }
                        if (num == 1 && allMaterialTypes) {
                            if (template.isWood()) {
                                if (template.getTemplateId() == 65 || template.getTemplateId() == 413) {
                                    num = woodTypes.length;
                                }
                                else {
                                    num = maxWoodTypes;
                                }
                            }
                            else {
                                num = metalTypes.length;
                            }
                        }
                        for (int x = 0; x < num; ++x) {
                            data2 = Math.min(100, data2);
                            data2 = Math.max(1, data2);
                            Item newItem = null;
                            if (!Servers.localServer.testServer && responder.getPower() <= 3) {
                                if (material == 0) {
                                    material = template.getMaterial();
                                }
                                newItem = ItemFactory.createItem(387, data2, material, rare, responder.getName());
                                newItem.setRealTemplate(template.getTemplateId());
                                if (template.getTemplateId() == 729) {
                                    newItem.setName("This cake is a lie!");
                                }
                                else {
                                    newItem.setName("weird " + ItemFactory.generateName(template, material));
                                }
                            }
                            else {
                                int t = template.getTemplateId();
                                int color = colour;
                                if (template.isColor) {
                                    t = 438;
                                    if (colour == -1) {
                                        color = WurmColor.getInitialColor(template.getTemplateId(), Math.max(1, data2));
                                    }
                                }
                                if (allMaterialTypes && template.isWood()) {
                                    newItem = ItemFactory.createItem(t, data2, woodTypes[x], rare, responder.getName());
                                }
                                else if (allMaterialTypes && template.isMetal()) {
                                    newItem = ItemFactory.createItem(t, data2, metalTypes[x], rare, responder.getName());
                                }
                                else if (material != 0) {
                                    newItem = ItemFactory.createItem(t, data2, material, rare, responder.getName());
                                }
                                else {
                                    newItem = ItemFactory.createItem(t, data2, rare, responder.getName());
                                }
                                if (t != -1 && color != -1) {
                                    newItem.setColor(color);
                                }
                                if (name != null && name.length() > 0) {
                                    newItem.setName(name, true);
                                }
                                if (template.getTemplateId() == 175) {
                                    newItem.setAuxData((byte)2);
                                }
                            }
                            if (newItem.getTemplateId() == 995 && sizemod > 0.0f) {
                                newItem.setAuxData((byte)sizemod);
                                if (newItem.getAuxData() > 4) {
                                    newItem.setRarity((byte)2);
                                }
                                newItem.fillTreasureChest();
                            }
                            if (newItem.isCoin()) {
                                final long val = Economy.getValueFor(template.getTemplateId());
                                if (val * num > 500000000L) {
                                    responder.getCommunicator().sendNormalServerMessage("You aren't allowed to create that amount of money.");
                                    responder.getLogger().log(Level.WARNING, responder.getName() + " tried to create " + num + " " + newItem.getName() + " but wasn't allowed to.");
                                    return;
                                }
                                final Change change = new Change(val);
                                final long newGold = change.getGoldCoins();
                                if (newGold > 0L) {
                                    final long oldGold = Economy.getEconomy().getGold();
                                    Economy.getEconomy().updateCreatedGold(oldGold + newGold);
                                }
                                final long newCopper = change.getCopperCoins();
                                if (newCopper > 0L) {
                                    final long oldCopper = Economy.getEconomy().getCopper();
                                    Economy.getEconomy().updateCreatedCopper(oldCopper + newCopper);
                                }
                                final long newSilver = change.getSilverCoins();
                                if (newSilver > 0L) {
                                    final long oldSilver = Economy.getEconomy().getSilver();
                                    Economy.getEconomy().updateCreatedSilver(oldSilver + newSilver);
                                }
                                final long newIron = change.getIronCoins();
                                if (newIron > 0L) {
                                    final long oldIron = Economy.getEconomy().getIron();
                                    Economy.getEconomy().updateCreatedIron(oldIron + newIron);
                                }
                            }
                            if (responder.getLogger() != null) {
                                responder.getLogger().log(Level.INFO, responder.getName() + " created item " + newItem.getName() + ", item template: " + newItem.getTemplate().getName() + ", WurmID: " + newItem.getWurmId() + ", QL: " + newItem.getQualityLevel() + ", Rarity: " + newItem.getRarity());
                            }
                            else if (responder.getPower() != 0) {
                                QuestionParser.logger.log(Level.INFO, responder.getName() + " created item " + newItem.getName() + ", WurmID: " + newItem.getWurmId() + ", QL: " + newItem.getQualityLevel());
                            }
                            if (sizemod != 1.0f && template.getTemplateId() != 995) {
                                newItem.setWeight((int)Math.max(1.0f, sizemod * template.getWeightGrams()), false);
                                newItem.setSizes(newItem.getWeightGrams());
                            }
                            final Item inventory = responder.getInventory();
                            if (newItem.isKingdomMarker() || (newItem.isWind() && template.getTemplateId() == 579) || template.getTemplateId() == 578 || template.getTemplateId() == 999) {
                                newItem.setAuxData(responder.getKingdomId());
                            }
                            if (newItem.isLock() && newItem.getTemplateId() != 167) {
                                try {
                                    final Item key = ItemFactory.createItem(168, newItem.getQualityLevel(), responder.getName());
                                    key.setMaterial(newItem.getMaterial());
                                    inventory.insertItem(key);
                                    newItem.addKey(key.getWurmId());
                                    key.setLockId(newItem.getWurmId());
                                }
                                catch (NoSuchTemplateException nst) {
                                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to create key: " + nst.getMessage(), nst);
                                }
                                catch (FailedException fe) {
                                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to create key: " + fe.getMessage(), fe);
                                }
                            }
                            Item container = null;
                            if (newItem.isLiquid()) {
                                if (template.getTemplateId() == 654) {
                                    container = ItemFactory.createItem(653, 99.0f, responder.getName());
                                    responder.getInventory().insertItem(container, true);
                                    container.insertItem(newItem);
                                }
                                else {
                                    final Item[] allItems = inventory.getAllItems(false);
                                    for (int a = 0; a < allItems.length; ++a) {
                                        if (allItems[a].isContainerLiquid() && allItems[a].isEmpty(false) && allItems[a].insertItem(newItem)) {
                                            container = allItems[a];
                                            newItem.setWeight(container.getFreeVolume(), false);
                                            break;
                                        }
                                    }
                                }
                                if (container != null) {
                                    if (item.getTemplateId() == 301) {
                                        responder.getCommunicator().sendNormalServerMessage("You pour some " + newItem.getNameWithGenus() + " from the horn into the " + container.getName() + ".");
                                        Server.getInstance().broadCastAction(responder.getName() + " pours something out of a huge goat horn full of fruit and flowers.", responder, 5);
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("You wave your wand and create " + newItem.getName() + " in " + container.getNameWithGenus() + " [" + container.getDescription() + "].");
                                        Server.getInstance().broadCastAction(responder.getName() + " waves a black wand vividly.", responder, 5);
                                    }
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You need an empty container to put the " + newItem.getNameWithGenus() + " in!");
                                    Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                                }
                            }
                            else if (inventory.insertItem(newItem)) {
                                if (item.getTemplateId() == 301) {
                                    responder.getCommunicator().sendNormalServerMessage("You pull " + newItem.getNameWithGenus() + " out from the horn.");
                                    Server.getInstance().broadCastAction(responder.getName() + " pulls something out of a huge goat horn full of fruit and flowers.", responder, 5);
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You wave your wand and create " + newItem.getNameWithGenus() + ".");
                                    Server.getInstance().broadCastAction(responder.getName() + " waves a black wand vividly.", responder, 5);
                                }
                                if (newItem.isEpicTargetItem() && Servers.localServer.testServer) {
                                    newItem.setAuxData(responder.getKingdomTemplateId());
                                    AdvancedCreationEntry.onEpicItemCreated(responder, newItem, newItem.getTemplateId(), true);
                                }
                            }
                            else {
                                try {
                                    newItem.putItemInfrontof(responder);
                                    if (item.getTemplateId() == 301) {
                                        responder.getCommunicator().sendNormalServerMessage("You pull " + newItem.getNameWithGenus() + " out from the horn and puts it on the ground.");
                                        Server.getInstance().broadCastAction(responder.getName() + " pulls something out of a huge goat horn full of fruit and flowers.", responder, 5);
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("You wave your wand and create " + newItem.getNameWithGenus() + " in front of you.");
                                        Server.getInstance().broadCastAction(responder.getName() + " waves a black wand vividly.", responder, 5);
                                    }
                                }
                                catch (NoSuchPlayerException nsp) {
                                    responder.getCommunicator().sendAlertServerMessage("Could not locate your identity! Check the logs!");
                                    QuestionParser.logger.log(Level.WARNING, nsp.getMessage(), nsp);
                                }
                                catch (NoSuchCreatureException nsc) {
                                    responder.getCommunicator().sendAlertServerMessage("Could not locate your identity! Check the logs!");
                                    QuestionParser.logger.log(Level.WARNING, nsc.getMessage(), nsc);
                                }
                                catch (NoSuchZoneException nsz) {
                                    responder.getCommunicator().sendAlertServerMessage("You need to be in valid zones, since you cannot carry the item.");
                                }
                            }
                        }
                    }
                    else {
                        responder.getCommunicator().sendAlertServerMessage("Don't create these. They will lack important data.");
                    }
                }
                else {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " tries to create items by hacking the protocol. data1: " + d1 + ", data2: " + d2 + ", number: " + number);
                }
                if (responder.loggerCreature1 > 0L) {
                    responder.getCommunicator().sendNormalServerMessage("That took " + (System.currentTimeMillis() - now) + " ms.");
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, "Failed to locate item with id=" + target + " for which name change was intended.");
                responder.getCommunicator().sendNormalServerMessage("Failed to locate that item.");
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendNormalServerMessage("You realize that " + d1 + ", " + d2 + " or " + number + " doesn't match your requirements.");
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that " + d1 + ", " + d2 + " or " + number + " doesn't match their requirements. " + nfe, nfe);
                }
            }
            catch (FailedException fe2) {
                responder.getCommunicator().sendNormalServerMessage("Failed!: " + fe2.getMessage());
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, "Failed to create an Item " + fe2, fe2);
                }
            }
            catch (NoSuchTemplateException nst2) {
                responder.getCommunicator().sendNormalServerMessage("Failed!: " + nst2.getMessage());
                if (QuestionParser.logger.isLoggable(Level.FINE)) {
                    QuestionParser.logger.log(Level.FINE, "Failed to create an Item " + nst2, nst2);
                }
            }
        }
    }
    
    public static void parseLearnSkillQuestion(final LearnSkillQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 16) {
            final String d1 = question.getAnswer().getProperty("data1");
            final String value = question.getAnswer().getProperty("val");
            final String dec = question.getAnswer().getProperty("dec");
            final String aff = question.getAnswer().getProperty("aff");
            final String align = question.getAnswer().getProperty("align");
            final String karma = question.getAnswer().getProperty("karma");
            final String strPath = question.getAnswer().getProperty("path");
            final String strLevel = question.getAnswer().getProperty("level");
            if (WurmPermissions.mayUseDeityWand(responder)) {
                try {
                    final int data1 = Integer.parseInt(d1);
                    final Collection<SkillTemplate> temps = SkillSystem.templates.values();
                    final SkillTemplate[] templates = temps.toArray(new SkillTemplate[temps.size()]);
                    Arrays.sort(templates, new Comparator<SkillTemplate>() {
                        @Override
                        public int compare(final SkillTemplate o1, final SkillTemplate o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    final int sk = templates[data1].getNumber();
                    double skillval = Double.parseDouble(value + "." + dec);
                    final boolean changeSkill = skillval != 0.0;
                    final boolean changeAff = !aff.equals("-1");
                    final boolean changeAlign = align.length() > 0;
                    final boolean changeKarma = karma.length() > 0;
                    final boolean hasCult = strPath != null;
                    final int newAff = Integer.parseInt(aff);
                    final int newAlign = changeAlign ? Integer.parseInt(align) : 0;
                    final int newKarma = changeKarma ? Integer.parseInt(karma) : 0;
                    skillval = Math.min(100.0, skillval);
                    skillval = Math.max(1.0, skillval);
                    Skills skills = null;
                    if (WurmId.getType(target) == 1 || WurmId.getType(target) == 0) {
                        if (sk != 10086) {
                            try {
                                final Creature receiver = Server.getInstance().getCreature(target);
                                skills = receiver.getSkills();
                                try {
                                    final Skill skill = skills.getSkill(sk);
                                    if (changeSkill) {
                                        skill.setKnowledge(skillval, false);
                                        responder.getCommunicator().sendNormalServerMessage("You set the " + skill.getName() + " skill of " + receiver.getName() + " to " + skillval + ".");
                                        receiver.getCommunicator().sendNormalServerMessage(responder.getName() + " sets your " + skill.getName() + " skill to " + skillval + ".");
                                        QuestionParser.logger.log(Level.INFO, responder.getName() + " set " + skill.getName() + " skill of " + receiver.getName() + " to " + skillval + ".");
                                    }
                                    if (changeAff) {
                                        final int oldAff = skill.affinity;
                                        if (oldAff != newAff) {
                                            Affinities.setAffinity(receiver.getWurmId(), sk, newAff, false);
                                            if (oldAff < newAff) {
                                                responder.getCommunicator().sendNormalServerMessage("You increased affinities from " + oldAff + " to " + newAff + ".");
                                            }
                                            else {
                                                responder.getCommunicator().sendNormalServerMessage("You decrease affinities from " + oldAff + " to " + newAff + ".");
                                            }
                                            QuestionParser.logger.log(Level.INFO, responder.getName() + " set affinities for " + skill.getName() + " skill of " + receiver.getName() + " to " + newAff + ".");
                                        }
                                        if (receiver.isNpc() && skill.getNumber() == 1023) {
                                            receiver.setSkill(10056, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(10058, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(10081, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(10080, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(10079, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(1030, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(1002, newAff * 10 + Server.rand.nextFloat() * newAff * 10.0f);
                                            receiver.setSkill(103, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            receiver.setSkill(102, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            receiver.setSkill(10054, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            receiver.setSkill(10053, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            receiver.setSkill(10055, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            receiver.setSkill(10052, Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            final Item prim = receiver.getPrimWeapon();
                                            if (prim != null) {
                                                receiver.setSkill(prim.getPrimarySkill(), Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            }
                                            final Item shield = receiver.getShield();
                                            if (shield != null) {
                                                receiver.setSkill(shield.getPrimarySkill(), Math.max(1, newAff) * 10 + Server.rand.nextFloat() * Math.max(10, newAff * 10));
                                            }
                                        }
                                    }
                                    if (changeAlign) {
                                        final float oldAlign = receiver.getAlignment();
                                        if (oldAlign != newAlign) {
                                            receiver.setAlignment(newAlign);
                                            if (oldAlign < newAlign) {
                                                responder.getCommunicator().sendNormalServerMessage("You increased alignment from " + oldAlign + " to " + newAlign + ".");
                                            }
                                            else {
                                                responder.getCommunicator().sendNormalServerMessage("You decrease alignment from " + oldAlign + " to " + newAlign + ".");
                                            }
                                        }
                                    }
                                    if (changeKarma) {
                                        final float oldKarma = receiver.getKarma();
                                        if (oldKarma != newKarma) {
                                            receiver.setKarma(newKarma);
                                            if (oldKarma < newKarma) {
                                                responder.getCommunicator().sendNormalServerMessage("You increased karma from " + oldKarma + " to " + newKarma + ".");
                                            }
                                            else {
                                                responder.getCommunicator().sendNormalServerMessage("You decrease karma from " + oldKarma + " to " + newKarma + ".");
                                            }
                                            QuestionParser.logger.log(Level.INFO, responder.getName() + " set karma of " + receiver.getName() + " from " + oldKarma + " to " + newKarma + ".");
                                        }
                                    }
                                    if (hasCult) {
                                        Cultist cultist = Cultist.getCultist(target);
                                        byte path = 0;
                                        byte level = 0;
                                        if (cultist != null) {
                                            path = cultist.getPath();
                                            level = cultist.getLevel();
                                        }
                                        final byte newPath = Byte.parseByte(strPath);
                                        if (path != newPath) {
                                            if (path != 0) {
                                                try {
                                                    cultist.deleteCultist();
                                                    responder.getCommunicator().sendNormalServerMessage("You have removed " + receiver.getName() + " from " + Cults.getPathNameFor(path) + ".");
                                                    receiver.getCommunicator().sendNormalServerMessage(responder.getName() + " removed you from " + Cults.getPathNameFor(path) + ".");
                                                    QuestionParser.logger.log(Level.INFO, responder.getName() + " removed " + receiver.getName() + " from " + Cults.getPathNameFor(path) + ".");
                                                }
                                                catch (IOException e) {
                                                    responder.getCommunicator().sendNormalServerMessage("Problem leaving cultist path for " + receiver.getName() + ".");
                                                    QuestionParser.logger.log(Level.INFO, responder.getName() + " had problem resetting cultist path for " + receiver.getName() + ".");
                                                    return;
                                                }
                                            }
                                            if (newPath != 0) {
                                                cultist = new Cultist(target, newPath);
                                                responder.getCommunicator().sendNormalServerMessage("You have added " + receiver.getName() + " to " + Cults.getPathNameFor(newPath) + ".");
                                                receiver.getCommunicator().sendNormalServerMessage(responder.getName() + " added you to " + Cults.getPathNameFor(newPath) + ".");
                                                QuestionParser.logger.log(Level.INFO, responder.getName() + " added " + receiver.getName() + " to " + Cults.getPathNameFor(newPath) + ".");
                                            }
                                        }
                                        if (newPath != 0 && strLevel.length() > 0) {
                                            level = (byte)Math.min(Byte.parseByte(strLevel), 15);
                                            cultist.setLevel(level);
                                            responder.getCommunicator().sendNormalServerMessage("You have changes cult level for " + receiver.getName() + " to " + Cults.getNameForLevel(newPath, level) + ".");
                                            receiver.getCommunicator().sendNormalServerMessage(responder.getName() + " changed your cult level to " + Cults.getNameForLevel(newPath, level) + ".");
                                            QuestionParser.logger.log(Level.INFO, responder.getName() + " changed cult level of " + receiver.getName() + " to " + Cults.getNameForLevel(newPath, level) + ".");
                                        }
                                    }
                                }
                                catch (NoSuchSkillException nss) {
                                    skills.learn(sk, (float)skillval);
                                    responder.getCommunicator().sendNormalServerMessage("You teach " + receiver.getName() + " " + SkillSystem.getNameFor(sk) + " to " + skillval + ".");
                                    receiver.getCommunicator().sendNormalServerMessage(responder.getName() + " teaches you the " + SkillSystem.getNameFor(sk) + " skill to " + skillval + ".");
                                    QuestionParser.logger.log(Level.INFO, responder.getName() + " set " + SkillSystem.getNameFor(sk) + " skill of " + receiver.getName() + " to " + skillval + ".");
                                }
                                catch (IOException e2) {
                                    responder.getCommunicator().sendNormalServerMessage("Problem changing alignment for " + receiver.getName() + ".");
                                }
                            }
                            catch (NoSuchCreatureException nsc) {
                                responder.getCommunicator().sendNormalServerMessage("Failed to locate creature with id " + target + ".");
                            }
                            catch (NoSuchPlayerException nsp) {
                                responder.getCommunicator().sendNormalServerMessage("Failed to locate player with id " + target + ".");
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("This skill is impossible to learn like that.");
                        }
                    }
                    else {
                        skills = responder.getSkills();
                        try {
                            final Skill skill2 = skills.getSkill(sk);
                            if (changeSkill) {
                                skill2.setKnowledge(skillval, false);
                                responder.getCommunicator().sendNormalServerMessage("You set " + SkillSystem.getNameFor(sk) + " to " + skillval + ".");
                            }
                            if (changeAff) {
                                final int oldAff2 = skill2.affinity;
                                if (oldAff2 != newAff) {
                                    skill2.setAffinity(newAff);
                                    if (oldAff2 < newAff) {
                                        responder.getCommunicator().sendNormalServerMessage("You increased affinities from " + oldAff2 + " to " + newAff + ".");
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("You descrmented affinities from " + oldAff2 + " to " + newAff + ".");
                                    }
                                }
                            }
                        }
                        catch (NoSuchSkillException nss2) {
                            skills.learn(sk, (float)skillval);
                            responder.getCommunicator().sendNormalServerMessage("You learn " + SkillSystem.getNameFor(sk) + " to " + skillval + ".");
                        }
                        QuestionParser.logger.log(Level.INFO, responder.getName() + " learnt " + SkillSystem.getNameFor(sk) + " to " + skillval + ".");
                        if (changeKarma) {
                            final float oldKarma2 = responder.getKarma();
                            if (oldKarma2 != newKarma) {
                                responder.setKarma(newKarma);
                                if (oldKarma2 < newKarma) {
                                    responder.getCommunicator().sendNormalServerMessage("You increased your karma from " + oldKarma2 + " to " + newKarma + ".");
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You decrease your karma from " + oldKarma2 + " to " + newKarma + ".");
                                }
                                QuestionParser.logger.log(Level.INFO, responder.getName() + " set karma of " + responder.getName() + " from " + oldKarma2 + " to " + newKarma + ".");
                            }
                        }
                    }
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to interpret " + d1 + " or " + value + " as a number.");
                }
            }
            else {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tries to learn skills but their power is only " + responder.getPower() + ". data1: " + d1 + ", value: " + value);
            }
        }
    }
    
    public static void summon(final String pname, final Creature responder, final int tilex, final int tiley, final byte layer) {
        try {
            final Player player = Players.getInstance().getPlayer(StringUtilities.raiseFirstLetter(pname));
            responder.getCommunicator().sendNormalServerMessage("You summon " + player.getName() + ".");
            Server.getInstance().broadCastAction(responder.getName() + " makes a commanding gesture!", responder, 5);
            player.getCommunicator().sendNormalServerMessage("You are summoned by a great force.");
            Server.getInstance().broadCastAction(player.getName() + " suddenly disappears.", player, 5);
            if (responder.getLogger() != null) {
                responder.getLogger().log(Level.INFO, responder.getName() + " summons " + pname);
            }
            player.setTeleportPoints((short)tilex, (short)tiley, layer, responder.getFloorLevel());
            player.startTeleporting();
            Server.getInstance().broadCastAction(player.getName() + " suddenly appears.", player, 5);
            player.getCommunicator().sendTeleport(false);
            player.setBridgeId(responder.getBridgeId());
            QuestionParser.logger.log(Level.INFO, responder.getName() + " summoned player " + player.getName() + ", with ID: " + player.getWurmId() + " at coords " + tilex + ',' + tiley + " teleportlayer " + player.getTeleportLayer());
        }
        catch (NoSuchPlayerException nsp) {
            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
            try {
                pinf.load();
            }
            catch (IOException iox) {
                responder.getCommunicator().sendNormalServerMessage("Failed to load data for the player with name " + pname + ".");
                return;
            }
            if (pinf != null && pinf.wurmId > 0L) {
                final CreaturePos cs = CreaturePos.getPosition(pinf.wurmId);
                cs.setPosX((tilex << 2) + 2);
                cs.setPosY((tiley << 2) + 2);
                float z = 0.0f;
                try {
                    z = Zones.calculateHeight(cs.getPosX(), cs.getPosY(), layer == 0);
                }
                catch (NoSuchZoneException nsz) {
                    responder.getCommunicator().sendNormalServerMessage("No such zone: " + tilex + "," + tiley + ", surf=" + (layer == 0));
                    return;
                }
                cs.setLayer(layer);
                cs.setPosZ(z, true);
                cs.setRotation(responder.getStatus().getRotation() - 180.0f);
                try {
                    final int zoneid = Zones.getZoneIdFor(tilex, tiley, layer == 0);
                    cs.setZoneId(zoneid);
                    cs.setBridgeId(responder.getBridgeId());
                    cs.save(false);
                    responder.getCommunicator().sendNormalServerMessage("Okay, " + pname + " set to " + tilex + "," + tiley + " surfaced=" + (layer == 0));
                    QuestionParser.logger.log(Level.INFO, responder.getName() + " set " + pname + " to " + tilex + "," + tiley + " surfaced=" + (layer == 0));
                    if (responder.getLogger() != null) {
                        responder.getLogger().log(Level.INFO, "Set " + pname + " to " + tilex + "," + tiley + " surfaced=" + (layer == 0));
                    }
                }
                catch (NoSuchZoneException nsz) {
                    responder.getCommunicator().sendNormalServerMessage("No such zone: " + tilex + "," + tiley + ", surf=" + (layer == 0));
                }
            }
            else {
                responder.getCommunicator().sendNormalServerMessage("No player with the name " + pname + " found.");
            }
        }
    }
    
    static void parseCreatureCreationQuestion(final CreatureCreationQuestion question) {
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 6 && (WurmId.getType(target) == 2 || WurmId.getType(target) == 19 || WurmId.getType(target) == 20)) {
            final String pname = question.getAnswer().getProperty("pname");
            if (pname != null && pname.length() > 0) {
                if (pname.equalsIgnoreCase(responder.getName())) {
                    responder.getCommunicator().sendNormalServerMessage("You cannot summon yourself.");
                }
                else {
                    summon(pname, responder, responder.getTileX(), responder.getTileY(), (byte)responder.getLayer());
                }
            }
            else {
                final String d1 = question.getAnswer().getProperty("data1");
                final String number = question.getAnswer().getProperty("number");
                final String cname = question.getAnswer().getProperty("cname");
                final String gender = question.getAnswer().getProperty("gender");
                final String sage = question.getAnswer().getProperty("age");
                try {
                    final Item item = Items.getItem(target);
                    if ((item.getTemplateId() == 176 && responder.getPower() >= 3) || (Servers.localServer.testServer && responder.getPower() >= 1)) {
                        float posx = (question.getTileX() << 2) + 2;
                        float posy = (question.getTileY() << 2) + 2;
                        final int layer = question.getLayer();
                        final int floorLevel = responder.getFloorLevel();
                        float rot = responder.getStatus().getRotation();
                        rot -= 180.0f;
                        if (rot < 0.0f) {
                            rot += 360.0f;
                        }
                        else if (rot > 360.0f) {
                            rot -= 360.0f;
                        }
                        final int tId = Integer.parseInt(d1);
                        final int num = Integer.parseInt(number);
                        final int dage = Integer.parseInt(sage);
                        final CreatureTemplate template = question.getTemplate(tId);
                        if (template.isUnique() && responder.getPower() < 5 && !Servers.localServer.testServer) {
                            responder.getCommunicator().sendNormalServerMessage("You may not summon that creature.");
                            return;
                        }
                        if (template.getTemplateId() == 53 && !WurmCalendar.isEaster() && !Servers.localServer.testServer) {
                            responder.getCommunicator().sendNormalServerMessage("You may not summon that creature now.");
                            return;
                        }
                        final long start = System.nanoTime();
                        for (int x = 0; x < num; ++x) {
                            if (x > 0) {
                                posx += -2.0f + Server.rand.nextFloat() * 4.0f;
                                posy += -2.0f + Server.rand.nextFloat() * 4.0f;
                            }
                            try {
                                byte sex = 0;
                                if (gender.equals("female") || template.getSex() == 1) {
                                    sex = 1;
                                }
                                byte kingd = 0;
                                if (template.isHuman()) {
                                    kingd = responder.getKingdomId();
                                }
                                final long sid = question.getStructureId();
                                long bridgeId = -10L;
                                if (sid > 0L) {
                                    try {
                                        final Structure struct = Structures.getStructure(sid);
                                        if (struct.isTypeBridge()) {
                                            bridgeId = sid;
                                        }
                                    }
                                    catch (NoSuchStructureException ex2) {}
                                }
                                byte ttype = 0;
                                if (template.hasDen() || template.isRiftCreature() || Servers.localServer.testServer) {
                                    ttype = Byte.parseByte(question.getAnswer().getProperty("tid"));
                                }
                                Creature newCreature;
                                if (template.getTemplateId() != 69) {
                                    int age = dage - 1;
                                    if (dage < 2) {
                                        age = (int)(Server.rand.nextFloat() * 5.0f);
                                    }
                                    if (template.getTemplateId() == 65 || template.getTemplateId() == 48 || template.getTemplateId() == 98 || template.getTemplateId() == 101 || template.getTemplateId() == 50 || template.getTemplateId() == 117 || template.getTemplateId() == 118) {
                                        newCreature = Creature.doNew(template.getTemplateId(), true, posx, posy, rot, layer, cname, sex, kingd, ttype, false, (byte)age, floorLevel);
                                    }
                                    else {
                                        if (dage < 2) {
                                            age = (int)(Server.rand.nextFloat() * Math.min(48, template.getMaxAge()));
                                        }
                                        newCreature = Creature.doNew(template.getTemplateId(), true, posx, posy, rot, layer, cname, sex, kingd, ttype, false, (byte)age, floorLevel);
                                    }
                                }
                                else {
                                    newCreature = Creature.doNew(template.getTemplateId(), false, posx, posy, rot, layer, cname, sex, kingd, ttype, true, (byte)0, floorLevel);
                                }
                                if (sid > 0L && bridgeId > 0L) {
                                    newCreature.setBridgeId(bridgeId);
                                }
                                QuestionParser.logger.log(Level.INFO, responder.getName() + " created " + gender + " " + template.getName() + ", with ID: " + newCreature.getWurmId() + ", age: " + newCreature.getStatus().age + " at coords " + posx + ',' + posy);
                                responder.getCommunicator().sendNormalServerMessage("You wave your wand, demanding " + newCreature.getNameWithGenus() + " to appear from the mists of the void.");
                                Server.getInstance().broadCastAction(responder.getName() + " waves a black wand vividly and " + newCreature.getNameWithGenus() + " quickly appears from nowhere.", responder, 5);
                                if (newCreature.isHorse()) {
                                    newCreature.setVisible(false);
                                    Creature.setRandomColor(newCreature);
                                    newCreature.setVisible(true);
                                }
                                else if (newCreature.getTemplate().isColoured) {
                                    newCreature.setVisible(false);
                                    final int randCol = Server.rand.nextInt(newCreature.getTemplate().maxColourCount);
                                    if (randCol == 1) {
                                        newCreature.getStatus().setTraitBit(15, true);
                                    }
                                    else if (randCol == 2) {
                                        newCreature.getStatus().setTraitBit(16, true);
                                    }
                                    else if (randCol == 3) {
                                        newCreature.getStatus().setTraitBit(17, true);
                                    }
                                    else if (randCol == 4) {
                                        newCreature.getStatus().setTraitBit(18, true);
                                    }
                                    else if (randCol == 5) {
                                        newCreature.getStatus().setTraitBit(24, true);
                                    }
                                    else if (randCol == 6) {
                                        newCreature.getStatus().setTraitBit(25, true);
                                    }
                                    else if (randCol == 7) {
                                        newCreature.getStatus().setTraitBit(23, true);
                                    }
                                    else if (randCol == 8) {
                                        newCreature.getStatus().setTraitBit(30, true);
                                    }
                                    else if (randCol == 9) {
                                        newCreature.getStatus().setTraitBit(31, true);
                                    }
                                    else if (randCol == 10) {
                                        newCreature.getStatus().setTraitBit(32, true);
                                    }
                                    else if (randCol == 11) {
                                        newCreature.getStatus().setTraitBit(33, true);
                                    }
                                    else if (randCol == 12) {
                                        newCreature.getStatus().setTraitBit(34, true);
                                    }
                                    else if (randCol == 13) {
                                        newCreature.getStatus().setTraitBit(35, true);
                                    }
                                    else if (randCol == 14) {
                                        newCreature.getStatus().setTraitBit(36, true);
                                    }
                                    else if (randCol == 15) {
                                        newCreature.getStatus().setTraitBit(37, true);
                                    }
                                    else if (randCol == 16) {
                                        newCreature.getStatus().setTraitBit(38, true);
                                    }
                                    newCreature.setVisible(true);
                                }
                                SoundPlayer.playSound("sound.work.carpentry.carvingknife", newCreature, 1.0f);
                            }
                            catch (Exception ex) {
                                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to create creature but failed: " + ex.getMessage(), ex);
                            }
                        }
                        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                        QuestionParser.logger.info(responder.getName() + " created " + num + ' ' + template.getName() + ", which took " + lElapsedTime + " millis.");
                    }
                    else if (item.getTemplateId() == 315 && responder.getPower() >= 1) {
                        responder.getCommunicator().sendNormalServerMessage("Sorry, but you cannot summon creatures at this moment.");
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("Sorry, but you cannot summon creatures at this moment.");
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + ", power=" + responder.getPower() + " tried to summon a " + cname + " creature using a " + item);
                    }
                }
                catch (NoSuchItemException nsi) {
                    QuestionParser.logger.log(Level.INFO, responder.getName() + " tried to use a deitywand itemid to create a creature, but the item did not exist.", nsi);
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("You realize that " + d1 + ", or " + number + " doesn't match your requirements.");
                    if (QuestionParser.logger.isLoggable(Level.FINE)) {
                        QuestionParser.logger.log(Level.FINE, responder.getName() + " realises that " + d1 + " or " + number + " doesn't match their requirements. " + nfe, nfe);
                    }
                }
            }
        }
    }
    
    static void parseVillageExpansionQuestion(final VillageExpansionQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        try {
            final Item deed = Items.getItem(target);
            final Item token = question.getToken();
            final int villid = token.getData2();
            final Village village = Villages.getVillage(villid);
            final int tilex = village.getTokenX();
            final int tiley = village.getTokenY();
            final int oldSize = (village.getEndX() - village.getStartX()) / 2;
            final int size = Villages.getSizeForDeed(deed.getTemplateId());
            if (oldSize == size) {
                responder.getCommunicator().sendSafeServerMessage("There is no difference in the sizes of the deeds.");
                return;
            }
            final Structure[] newstructures = Zones.getStructuresInArea(tilex - size, tiley - size, tilex + size, tiley + size, responder.isOnSurface());
            final Set<Structure> notOverlaps = new HashSet<Structure>();
            final Structure[] oldstructures = Zones.getStructuresInArea(village.getStartX(), village.getStartY(), village.getEndX(), village.getEndY(), responder.isOnSurface());
            for (int o = 0; o < oldstructures.length; ++o) {
                boolean found = false;
                for (int n = 0; n < newstructures.length; ++n) {
                    if (newstructures[n] == oldstructures[o]) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    notOverlaps.add(oldstructures[o]);
                }
            }
            if (notOverlaps.size() > 0) {
                final Structure[] structures = notOverlaps.toArray(new Structure[notOverlaps.size()]);
                final Item[] writs = responder.getKeys();
                boolean error = false;
                for (int x = 0; x < structures.length; ++x) {
                    final long writid = structures[x].getWritId();
                    boolean found2 = false;
                    for (int k = 0; k < writs.length; ++k) {
                        if (writs[k].getWurmId() == writid) {
                            found2 = true;
                        }
                    }
                    if (!found2) {
                        error = true;
                        if (responder.getPower() > 0) {
                            responder.getLogger().log(Level.INFO, responder.getName() + " founding village over existing structure " + structures[x].getName());
                            responder.getCommunicator().sendSafeServerMessage("Skipping the writ requirement for the structure " + structures[x].getName() + " to expand the village. Are you sure you know what you are doing?");
                        }
                        else {
                            responder.getCommunicator().sendSafeServerMessage("You need the writ for the structure " + structures[x].getName() + " to expand the village.");
                        }
                    }
                }
                if (error) {
                    if (responder.getPower() <= 0) {
                        return;
                    }
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " founding village over existing structures.");
                }
            }
            final Item altar = Villages.isAltarOnDeed(size, size, size, size, tilex, tiley, responder.isOnSurface());
            if (altar != null) {
                if (!altar.isEpicTargetItem() || !Servers.localServer.PVPSERVER) {
                    responder.getCommunicator().sendSafeServerMessage("You cannot found the settlement here, since the " + altar.getName() + " makes it holy ground.");
                    return;
                }
                if (EpicServerStatus.getRitualMissionForTarget(altar.getWurmId()) != null) {
                    responder.getCommunicator().sendSafeServerMessage("You cannot found the settlement here, since the " + altar.getName() + " is currently required for an active mission.");
                    return;
                }
            }
            final Citizen mayor = village.getMayor();
            if (mayor != null) {
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(mayor.wurmId);
                long moneyToReimburse = 0L;
                if (pinf != null) {
                    try {
                        final Item olddeed = Items.getItem(village.deedid);
                        moneyToReimburse = olddeed.getValue() / 2;
                    }
                    catch (NoSuchItemException nsi) {
                        QuestionParser.logger.log(Level.WARNING, village.getName() + " No deed id with id=" + village.deedid, nsi);
                    }
                }
                if (moneyToReimburse > 0L) {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    if (!lsw.addMoney(mayor.wurmId, pinf.getName(), moneyToReimburse, "Expand " + village.getName())) {
                        QuestionParser.logger.log(Level.INFO, "Expanding did not yield money for " + village.getName() + " to " + pinf.getName() + ": " + moneyToReimburse + "?");
                        responder.getCommunicator().sendSafeServerMessage("You expand the village here, but the mayor could not be reimbursed.");
                    }
                }
            }
            village.setNewBounds(tilex - size, tiley - size, tilex + size, tiley + size);
            village.plan.updateGuardPlan(village.plan.type, village.plan.moneyLeft + village.plan.calculateMonthlyUpkeepTimeforType(1), village.plan.getNumHiredGuards());
            final String villageName = village.getName();
            if (responder.getPower() < 5) {
                final Shop shop = Economy.getEconomy().getKingsShop();
                shop.setMoney(shop.getMoney() - (int)(deed.getValue() * 0.4f));
            }
            int g = 0;
            if (village.getGuards() != null) {
                g = village.getGuards().length;
            }
            final int maxCitizens = (int)(size * size / 2.5f);
            final int numOldCitizens = village.getCitizens().length - g;
            final int citizToRemove = numOldCitizens - maxCitizens;
            if (citizToRemove > 0) {
                final Citizen[] citz = village.getCitizens();
                for (int x2 = 0; x2 < citizToRemove; ++x2) {
                    if (citz[x2].getRole().id != 2 && citz[x2].getRole().id != 4) {
                        village.removeCitizen(citz[x2]);
                    }
                }
            }
            responder.getCommunicator().sendNormalServerMessage(villageName + " has been expanded to size " + size + ".");
            Server.getInstance().broadCastSafe(WurmCalendar.getTime());
            Server.getInstance().broadCastSafe("The homestead of " + villageName + " has just been expanded by " + responder.getName() + " to the size of " + size + ".");
            village.addHistory(responder.getName(), "expanded to the size of " + size);
            HistoryManager.addHistory(responder.getName(), "expanded " + village.getName() + " to the size of " + size);
            QuestionParser.logger.info("The deed of " + villageName + ", id: " + village.deedid + ", has just been expanded by " + responder.getName() + " to the size of " + size + ".");
        }
        catch (NoSuchItemException nsi2) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village deed with id " + target, nsi2);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village with id " + target, nsv);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
        }
    }
    
    static void parseGateManageQuestion(final GateManagementQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        try {
            final Item deed = Items.getItem(target);
            final Village village = Villages.getVillage(deed.getData2());
            final Set<FenceGate> gates = village.getGates();
            String key = "";
            String val = "";
            if (gates != null) {
                for (final FenceGate gate : gates) {
                    final long id = gate.getWurmId();
                    key = "gate" + id;
                    val = question.getAnswer().getProperty(key);
                    if (val != null && val.length() > 0) {
                        val = val.replaceAll("\"", "");
                        gate.setName(val);
                    }
                    key = "open" + id;
                    val = question.getAnswer().getProperty(key);
                    if (val != null && val.length() > 0) {
                        try {
                            final int time = Integer.parseInt(val);
                            if (time > 23 || time < 0) {
                                responder.getCommunicator().sendNormalServerMessage("When setting open time for gate " + gate.getName() + " the time was " + val + " which is out of the range 0-23.");
                            }
                            else {
                                gate.setOpenTime(time);
                            }
                        }
                        catch (NumberFormatException nfe) {
                            responder.getCommunicator().sendNormalServerMessage("When setting open time for gate " + gate.getName() + " the time was " + val + " which did not work.");
                        }
                    }
                    key = "close" + id;
                    val = question.getAnswer().getProperty(key);
                    if (val != null && val.length() > 0) {
                        try {
                            final int time = Integer.parseInt(val);
                            if (time > 23 || time < 0) {
                                responder.getCommunicator().sendNormalServerMessage("When setting close time for gate " + gate.getName() + " the time was " + val + " which is out of the range 0-23.");
                            }
                            else {
                                gate.setCloseTime(time);
                            }
                        }
                        catch (NumberFormatException nfe) {
                            responder.getCommunicator().sendNormalServerMessage("When setting close time for gate " + gate.getName() + " the time was " + val + " which did not work.");
                        }
                    }
                }
                responder.getCommunicator().sendSafeServerMessage("Settings updated.");
            }
        }
        catch (NoSuchItemException nsi) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village deed with id " + target, nsi);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village for deed with id " + target, nsv);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
        }
    }
    
    static void parseVillageSettingsManageQuestion(final VillageSettingsManageQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        final Properties props = question.getAnswer();
        try {
            Village village;
            if (target == -10L) {
                village = responder.getCitizenVillage();
                if (village == null) {
                    throw new NoSuchVillageException("You are not a citizen of any village (on this server).");
                }
            }
            else {
                final Item deed = Items.getItem(target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            boolean highways = false;
            boolean kos = false;
            boolean routing = false;
            if (Features.Feature.HIGHWAYS.isEnabled()) {
                highways = Boolean.parseBoolean(props.getProperty("highways"));
                kos = Boolean.parseBoolean(props.getProperty("kos"));
                routing = Boolean.parseBoolean(props.getProperty("routing"));
                final boolean hasHighway = village.hasHighway();
                if (!highways && hasHighway) {
                    responder.getCommunicator().sendNormalServerMessage("Cannot disallow highways as you have one on (or next to) your settlement.");
                    return;
                }
                if (village.getReputations().length > 0 && highways) {
                    responder.getCommunicator().sendNormalServerMessage("Cannot allow highways if you have an active kos.");
                    return;
                }
                if (village.getReputations().length > 0 && !kos) {
                    responder.getCommunicator().sendNormalServerMessage("Cannot disallow kos if you have an active kos. Clear the kos list first.");
                    return;
                }
                if (highways && kos) {
                    responder.getCommunicator().sendNormalServerMessage("Cannot allow kos and allow highways at same time.");
                    return;
                }
                if (routing && !highways) {
                    responder.getCommunicator().sendNormalServerMessage("Cannot opt-in for you village to be found when routing, if you dont allow highways.");
                    return;
                }
            }
            String villageName = village.getName();
            final boolean changingName = false;
            if (village.mayChangeName()) {
                villageName = props.getProperty("vname");
                villageName = villageName.replaceAll("\"", "");
                villageName = villageName.trim();
                if (villageName.length() > 3) {
                    villageName = StringUtilities.raiseFirstLetter(villageName);
                    final StringTokenizer tokens = new StringTokenizer(villageName);
                    String newName = tokens.nextToken();
                    while (tokens.hasMoreTokens()) {
                        newName = newName + " " + StringUtilities.raiseFirstLetter(tokens.nextToken());
                    }
                    villageName = newName;
                }
                if (villageName.length() >= 41) {
                    villageName = villageName.substring(0, 39);
                    responder.getCommunicator().sendNormalServerMessage("The name of the settlement would be ''" + villageName + "''. Please select a shorter name.");
                    return;
                }
                if (villageName.length() < 3) {
                    responder.getCommunicator().sendNormalServerMessage("The name of the settlement would be ''" + villageName + "''. Please select a name with at least 3 letters.");
                    return;
                }
                if (containsIllegalVillageCharacters(villageName)) {
                    responder.getCommunicator().sendNormalServerMessage("The name ''" + villageName + "'' contains illegal characters. Please select another name.");
                    return;
                }
                if (villageName.equals("Wurm")) {
                    responder.getCommunicator().sendNormalServerMessage("The name ''" + villageName + "'' is illegal. Please select another name.");
                    return;
                }
                if (!Villages.isNameOk(villageName, village.id)) {
                    responder.getCommunicator().sendNormalServerMessage("The name ''" + villageName + "'' is already taken. Please select another name.");
                    return;
                }
                if (!villageName.equals(village.getName())) {
                    if (!village.mayChangeName()) {
                        responder.getCommunicator().sendNormalServerMessage("You try to change the settlement name as its just been changed. The action was aborted.");
                        return;
                    }
                    final long moneyNeeded = 50000L;
                    final long rest = VillageFoundationQuestion.getExpandMoneyNeededFromBank(moneyNeeded, village);
                    if (rest > 0L) {
                        if (Servers.localServer.testServer) {
                            responder.getCommunicator().sendNormalServerMessage("We need " + moneyNeeded + ". " + rest + " must be taken from the bank.");
                        }
                        if (!((Player)responder).chargeMoney(rest)) {
                            responder.getCommunicator().sendNormalServerMessage("You try to change the settlement size, but your bank account could not be charged. The action was aborted.");
                            return;
                        }
                        if (Servers.localServer.testServer) {
                            responder.getCommunicator().sendNormalServerMessage("We also take " + village.getAvailablePlanMoney() + " from upkeep.");
                        }
                        village.plan.updateGuardPlan(village.plan.moneyLeft - village.getAvailablePlanMoney());
                    }
                    else {
                        if (Servers.localServer.testServer) {
                            responder.getCommunicator().sendNormalServerMessage("We charge " + moneyNeeded + " from the plan which has " + village.plan.moneyLeft);
                        }
                        village.plan.updateGuardPlan(village.plan.moneyLeft - moneyNeeded);
                    }
                    Item deed2 = null;
                    try {
                        deed2 = Items.getItem(village.getDeedId());
                    }
                    catch (NoSuchItemException nsi) {
                        QuestionParser.logger.log(Level.WARNING, "Failed to locate settlement deed with id " + village.getDeedId(), nsi);
                        responder.getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
                    }
                    village.setName(villageName);
                    village.setFaithCreate(0.0f);
                    village.setFaithHeal(0.0f);
                    village.setFaithWar(0.0f);
                    village.setLastChangedName(System.currentTimeMillis());
                    deed2.setDescription(villageName);
                    responder.getCommunicator().sendNormalServerMessage("Changed settlement name to \"" + villageName + "\".");
                }
            }
            String key = "motto";
            String val = props.getProperty(key);
            if (val != null) {
                if (!containsIllegalCharacters(val)) {
                    try {
                        village.setMotto(val.replaceAll("\"", ""));
                    }
                    catch (IOException iox) {
                        responder.getCommunicator().sendNormalServerMessage("Failed to update the motto.");
                        QuestionParser.logger.log(Level.WARNING, iox.getMessage(), iox);
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("The motto contains some illegal characters.");
                }
            }
            key = "motd";
            val = props.getProperty(key);
            if (val != null) {
                try {
                    final String oldMotd = village.getMotd();
                    village.setMotd(val.replaceAll("\"", ""));
                    if (oldMotd != village.getMotd()) {
                        village.addHistory(responder.getName(), "changed Motd");
                    }
                }
                catch (IOException iox) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to update the motd.");
                    QuestionParser.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
            boolean setdemocracy = false;
            key = "democracy";
            val = props.getProperty(key);
            if (val != null) {
                setdemocracy = val.equals("true");
            }
            if (setdemocracy) {
                try {
                    village.setDemocracy(true);
                }
                catch (IOException iox2) {
                    QuestionParser.logger.log(Level.WARNING, "Failed to set " + village.getName() + " to democracy. " + iox2.getMessage(), iox2);
                }
            }
            key = "nondemocracy";
            val = props.getProperty(key);
            setdemocracy = (val != null && val.equals("true"));
            if (setdemocracy) {
                try {
                    village.setDemocracy(false);
                }
                catch (IOException iox2) {
                    QuestionParser.logger.log(Level.WARNING, "Failed to set " + village.getName() + " to dictatorship. " + iox2.getMessage(), iox2);
                }
            }
            boolean unlimitedCitizens = false;
            key = "unlimitC";
            val = props.getProperty(key);
            if (val != null) {
                unlimitedCitizens = val.equals("true");
            }
            else {
                unlimitedCitizens = false;
            }
            try {
                village.setUnlimitedCitizens(unlimitedCitizens);
            }
            catch (IOException iox3) {
                QuestionParser.logger.log(Level.WARNING, "Failed to set " + village.getName() + " to unlimitedC - " + unlimitedCitizens + ":" + iox3.getMessage(), iox3);
            }
            final int oldSettings = village.getSettings();
            if (routing != village.isHighwayFound()) {
                responder.getCommunicator().sendNormalServerMessage((routing ? "Enabled" : "Disabled") + " finding village through routing.");
            }
            if (highways != village.isHighwayAllowed()) {
                responder.getCommunicator().sendNormalServerMessage((highways ? "Enabled" : "Disabled") + " highways through village.");
            }
            if (kos != village.isKosAllowed()) {
                responder.getCommunicator().sendNormalServerMessage((kos ? "Enabled" : "Disabled") + " KOS.");
            }
            try {
                village.setIsHighwayFound(routing);
                village.setIsKosAllowed(kos);
                village.setIsHighwayAllowed(highways);
                if (village.getSettings() != oldSettings) {
                    village.saveSettings();
                }
            }
            catch (IOException iox4) {
                QuestionParser.logger.log(Level.WARNING, "Failed to save " + village.getName() + " settings:" + iox4.getMessage(), iox4);
            }
            boolean setKingdomSpawn = false;
            key = "spawns";
            val = props.getProperty(key);
            setKingdomSpawn = (val != null && val.equals("true"));
            if (setKingdomSpawn) {
                village.setSpawnSituation((byte)1);
            }
            else {
                village.setSpawnSituation((byte)0);
            }
            boolean setAllowAggros = false;
            key = "aggros";
            val = props.getProperty(key);
            if (val != null) {
                setAllowAggros = val.equals("true");
            }
            else {
                setAllowAggros = false;
            }
            try {
                village.setAllowsAggroCreatures(setAllowAggros);
            }
            catch (IOException iox5) {
                QuestionParser.logger.log(Level.WARNING, "Failed to set " + village.getName() + " to setAllowsAggros - " + setAllowAggros + ":" + iox5.getMessage(), iox5);
            }
            responder.getCommunicator().sendSafeServerMessage("Settings updated.");
        }
        catch (NoSuchItemException nsi2) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate settlement deed with id " + target, nsi2);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate settlement for deed with id " + target, nsv);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the settlement for that request. Please contact administration.");
        }
        catch (IOException ioe) {
            responder.getCommunicator().sendNormalServerMessage("Failed to change name of settlement. Please contact administration.");
        }
    }
    
    static void parseVillageWarQuestion(final DeclareWarQuestion question) {
        final Creature responder = question.getResponder();
        final boolean declare = question.getAnswer().getProperty("declare").equals("true");
        if (responder.getCitizenVillage() != null) {
            if (!declare) {
                responder.getCommunicator().sendNormalServerMessage("You decide not to declare war.");
            }
            else {
                Villages.declareWar(responder.getCitizenVillage(), question.getTargetVillage());
            }
        }
        else {
            responder.getCommunicator().sendNormalServerMessage("You are not citizen of a village.");
        }
    }
    
    static void parseVillagePeaceQuestion(final PeaceQuestion question) {
        final Creature asker = question.getResponder();
        final Creature responder = question.getInvited();
        final boolean peace = question.getAnswer().getProperty("peace").equals("true");
        final Village village = asker.getCitizenVillage();
        if (!peace) {
            responder.getCommunicator().sendNormalServerMessage("You decline the peace offer.");
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " declines your generous peace offer!");
        }
        else {
            Villages.declarePeace(asker, responder, village, responder.getCitizenVillage());
        }
    }
    
    static void parseVillageJoinQuestion(final VillageJoinQuestion question) {
        final Creature asker = question.getResponder();
        final Creature responder = question.getInvited();
        final boolean join = question.getAnswer().getProperty("join").equals("true");
        final Village village = asker.getCitizenVillage();
        if (!join) {
            responder.getCommunicator().sendNormalServerMessage("You decline to join the settlement.");
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " declines to join the settlement.");
        }
        else if (responder.isPlayer() && responder.mayChangeVillageInMillis() > 0L) {
            responder.getCommunicator().sendNormalServerMessage("You may not change settlement in " + Server.getTimeFor(responder.mayChangeVillageInMillis()) + ".");
            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " may join your settlement in " + Server.getTimeFor(responder.mayChangeVillageInMillis()) + ".");
        }
        else {
            try {
                village.addCitizen(responder, village.getRoleForStatus((byte)3));
                if (((Player)responder).canUseFreeVillageTeleport()) {
                    final VillageTeleportQuestion vtq = new VillageTeleportQuestion(responder);
                    vtq.sendQuestion();
                }
            }
            catch (IOException iox) {
                QuestionParser.logger.log(Level.INFO, "Failed to add " + responder.getName() + " to settlement " + village.getName() + "." + iox.getMessage(), iox);
                responder.getCommunicator().sendNormalServerMessage("Failed to add you to the settlement. Please contact administration.");
                asker.getCommunicator().sendNormalServerMessage("Failed to add " + responder.getName() + " to the settlement. Please contact administration.");
            }
            catch (NoSuchRoleException nsr) {
                QuestionParser.logger.log(Level.INFO, "Failed to add " + responder.getName() + " to settlement " + village.getName() + "." + nsr.getMessage(), nsr);
                responder.getCommunicator().sendNormalServerMessage("Failed to add you to the settlement. Please contact administration.");
                asker.getCommunicator().sendNormalServerMessage("Failed to add " + responder.getName() + " to the settlement. Please contact administration.");
            }
        }
    }
    
    static void parseVillageCitizenManageQuestion(final VillageCitizenManageQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        final Properties props = question.getAnswer();
        try {
            Village village;
            if (target == -10L) {
                village = responder.getCitizenVillage();
                if (village == null) {
                    throw new NoSuchVillageException("You are not a citizen of any village (on this server).");
                }
            }
            else {
                final Item deed = Items.getItem(target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            boolean unlimitedCitizens = false;
            final String key = "unlimitC";
            final String valu = question.getAnswer().getProperty("unlimitC");
            if (valu != null) {
                unlimitedCitizens = valu.equals("true");
            }
            else {
                unlimitedCitizens = false;
            }
            try {
                village.setUnlimitedCitizens(unlimitedCitizens);
            }
            catch (IOException iox) {
                QuestionParser.logger.log(Level.WARNING, "Failed to set " + village.getName() + " to unlimitedC - " + unlimitedCitizens + ":" + iox.getMessage(), iox);
            }
            if (question.isSelecting()) {
                final String startLetter = props.getProperty("selectRange");
                final VillageCitizenManageQuestion vc = new VillageCitizenManageQuestion(responder, "Citizen management", "Set statuses of citizens.", target);
                vc.setSelecting(false);
                if (startLetter != null) {
                    if (startLetter.equals("0")) {
                        vc.setAllowedLetters("abcdef");
                    }
                    else if (startLetter.equals("1")) {
                        vc.setAllowedLetters("ghijkl");
                    }
                    else if (startLetter.equals("2")) {
                        vc.setAllowedLetters("mnopqr");
                    }
                    else if (startLetter.equals("3")) {
                        vc.setAllowedLetters("stuvwxyz");
                    }
                }
                vc.sendQuestion();
                return;
            }
            final VillageRole[] roles = village.getRoles();
            for (final Integer i : question.getIdMap().keySet()) {
                final int x = i;
                final long citid = question.getIdMap().get(i);
                final Citizen citiz = village.getCitizen(citid);
                if (citiz != null) {
                    try {
                        final String revString = ((Hashtable<K, String>)props).get(x + "revoke");
                        boolean revoked = false;
                        if (revString != null) {
                            revoked = revString.equals("true");
                        }
                        if (!revoked) {
                            final String val = ((Hashtable<K, String>)props).get(String.valueOf(x));
                            if (val == null) {
                                continue;
                            }
                            final int roleid = Integer.parseInt(val);
                            VillageRole newRole = null;
                            int count = 0;
                            for (int r = 0; r < roles.length; ++r) {
                                if (roles[r].getStatus() != 4 && roles[r].getStatus() != 5 && roles[r].getStatus() != 1 && roles[r].getStatus() != 6) {
                                    if (roleid == count) {
                                        newRole = roles[r];
                                        break;
                                    }
                                    ++count;
                                }
                            }
                            if (newRole == null) {
                                responder.getCommunicator().sendNormalServerMessage("Failed to locate role for " + citiz.getName() + ".");
                            }
                            else {
                                final VillageRole role = citiz.getRole();
                                if (role.getStatus() == 2 && newRole.getStatus() != 2) {
                                    responder.getCommunicator().sendNormalServerMessage(citiz.getName() + " is the mayor as long as he/she possesses the village deed. You cannot change that manually.");
                                }
                                else if (newRole.getStatus() != 2) {
                                    if (role.equals(newRole)) {
                                        continue;
                                    }
                                    village.updateGatesForRole(newRole);
                                    citiz.setRole(newRole);
                                    village.updateGatesForRole(newRole);
                                    responder.getCommunicator().sendNormalServerMessage(citiz.getName() + " role changed to \"" + newRole.getName() + "\".");
                                }
                                else {
                                    if (role.equals(newRole)) {
                                        continue;
                                    }
                                    responder.getCommunicator().sendNormalServerMessage("Did not set " + citiz.getName() + " to Mayor. The possessor of the village deed is Mayor.");
                                }
                            }
                        }
                        else {
                            if (village.isDemocracy()) {
                                continue;
                            }
                            if (citiz.getRole().getStatus() == 2) {
                                responder.getCommunicator().sendNormalServerMessage("You cannot revoke the citizenship of the mayor this way. He/she has to give away the village deed.");
                            }
                            else if (citiz.getId() == responder.getWurmId()) {
                                responder.getCommunicator().sendNormalServerMessage("You must revoke your own citizenship by typing '/revoke " + village.getName() + "' on the command line.");
                            }
                            else {
                                village.removeCitizen(citiz);
                            }
                        }
                    }
                    catch (NumberFormatException nfe) {
                        QuestionParser.logger.log(Level.WARNING, "This is bad: " + nfe.getMessage(), nfe);
                    }
                    catch (IOException iox2) {
                        QuestionParser.logger.log(Level.WARNING, "This is bad: " + iox2.getMessage(), iox2);
                        responder.getCommunicator().sendNormalServerMessage("Failed to set role for one or more citizens. Please contact administration.");
                    }
                }
            }
        }
        catch (NoSuchItemException nsi) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village deed with id " + target, nsi);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            QuestionParser.logger.log(Level.WARNING, "Failed to locate village for deed with id " + target, nsv);
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
        }
    }
    
    static final boolean charge(final Creature responder, final long coinsNeeded, final String reason, float taxrate) throws FailedException {
        final Item[] items = responder.getInventory().getAllItems(false);
        final LinkedList<Item> coins = new LinkedList<Item>();
        long value = 0L;
        for (int x = 0; x < items.length; ++x) {
            if (items[x].isCoin()) {
                coins.add(items[x]);
                value += Economy.getValueFor(items[x].getTemplateId());
            }
        }
        if (value < coinsNeeded) {
            final Change change = new Change(coinsNeeded);
            throw new FailedException("You need " + change.getChangeString() + " coins.");
        }
        long curv = 0L;
        final ListIterator<Item> it = coins.listIterator();
        while (it.hasNext()) {
            final Item coin = it.next();
            curv += Economy.getValueFor(coin.getTemplateId());
            try {
                final Item parent = coin.getParent();
                parent.dropItem(coin.getWurmId(), false);
                Economy.getEconomy().returnCoin(coin, reason);
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + ":  Failed to locate the container for coin " + coin.getName() + ". Value returned is " + new Change(curv).getChangeString() + " coins.", nsi);
                final Item[] newCoins = Economy.getEconomy().getCoinsFor(Economy.getValueFor(coin.getTemplateId()));
                final Item inventory = responder.getInventory();
                for (int x2 = 0; x2 < newCoins.length; ++x2) {
                    inventory.insertItem(newCoins[x2]);
                }
                throw new FailedException("Failed to locate the container for coin " + coin.getName() + ". This is serious and should be reported. Returned " + new Change(curv).getChangeString() + " coins.", nsi);
            }
            if (curv >= coinsNeeded) {
                break;
            }
        }
        if (curv > coinsNeeded) {
            final Item[] newCoins2 = Economy.getEconomy().getCoinsFor(curv - coinsNeeded);
            final Item inventory2 = responder.getInventory();
            for (int x3 = 0; x3 < newCoins2.length; ++x3) {
                inventory2.insertItem(newCoins2[x3]);
            }
        }
        final Shop kingsMoney = Economy.getEconomy().getKingsShop();
        if (taxrate > 1.0f) {
            QuestionParser.logger.log(Level.WARNING, responder.getName() + ":  Taxrate should be max 1 but is " + taxrate, new Exception());
            taxrate = 1.0f;
        }
        kingsMoney.setMoney(kingsMoney.getMoney() + (long)(coinsNeeded * (1.0f - taxrate)));
        QuestionParser.logger.log(Level.INFO, "King now has " + kingsMoney.getMoney());
        return true;
    }
    
    static void parseGuardRentalQuestion(final GuardManagementQuestion question) {
        final Properties props = question.getAnswer();
        final Creature responder = question.getResponder();
        String key = "12345678910";
        String val = null;
        final Village village = responder.citizenVillage;
        final long money = responder.getMoney();
        if (money > 0L) {
            final long valueWithdrawn = getValueWithdrawn(question);
            if (valueWithdrawn > 0L) {
                try {
                    if (village.plan != null) {
                        if (responder.chargeMoney(valueWithdrawn)) {
                            village.plan.addMoney(valueWithdrawn);
                            village.plan.addPayment(responder.getName(), responder.getWurmId(), valueWithdrawn);
                            final Change newch = Economy.getEconomy().getChangeFor(valueWithdrawn);
                            responder.getCommunicator().sendNormalServerMessage("You pay " + newch.getChangeString() + " to the upkeep fund of " + village.getName() + ".");
                            QuestionParser.logger.log(Level.INFO, responder.getName() + " added " + valueWithdrawn + " irons to " + village.getName() + " upkeep.");
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("You don't have that much money.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("This village does not have an upkeep plan.");
                    }
                }
                catch (IOException iox) {
                    QuestionParser.logger.log(Level.WARNING, "Failed to withdraw money from " + responder.getName() + ":" + iox.getMessage(), iox);
                    responder.getCommunicator().sendNormalServerMessage("The transaction failed. Please contact the game masters using the <i>/dev</i> command.");
                }
            }
            else {
                responder.getCommunicator().sendNormalServerMessage("No money withdrawn.");
            }
        }
        if (responder.mayManageGuards()) {
            final GuardPlan plan = responder.getCitizenVillage().plan;
            if (plan != null) {
                boolean changed = false;
                key = "hired";
                val = ((Hashtable<K, String>)props).get(key);
                final int oldnums;
                int nums = oldnums = plan.getNumHiredGuards();
                if (val == null) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to parse the value " + val + ". Please enter a number if you wish to change the number of guards.");
                    return;
                }
                try {
                    nums = Integer.parseInt(val);
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to parse the value " + val + ". Please enter a number if you wish to change the number of guards.");
                    return;
                }
                if (nums != plan.getNumHiredGuards()) {
                    final boolean aboveMax = nums > GuardPlan.getMaxGuards(responder.getCitizenVillage());
                    nums = Math.min(nums, GuardPlan.getMaxGuards(responder.getCitizenVillage()));
                    final int diff = nums - plan.getNumHiredGuards();
                    if (diff > 0) {
                        final long moneyOver = plan.moneyLeft - plan.calculateMonthlyUpkeepTimeforType(0);
                        if (moneyOver > 10000 * diff) {
                            changed = true;
                            plan.changePlan(0, nums);
                            plan.updateGuardPlan(0, plan.moneyLeft - 10000 * diff, nums);
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("There was not enough upkeep to increase the number of guards. Please make sure that there is at least one month of upkeep left after you hire the guards.");
                        }
                    }
                    else if (diff < 0) {
                        changed = true;
                        plan.changePlan(0, nums);
                    }
                    if (aboveMax) {
                        responder.getCommunicator().sendNormalServerMessage("You tried to increase the amount of guards above the max of " + GuardPlan.getMaxGuards(responder.getCitizenVillage()) + " which was denied.");
                    }
                }
                if (changed && oldnums < nums) {
                    responder.getCommunicator().sendNormalServerMessage("You change the upkeep plan. New guards will arrive soon.");
                }
                else if (changed) {
                    responder.getCommunicator().sendNormalServerMessage("You change the upkeep plan.");
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("No change was made.");
                }
            }
        }
        else {
            QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to manage guards without the right.");
        }
    }
    
    private static final long getValueWithdrawn(final Question question) {
        final String golds = question.getAnswer().getProperty("gold");
        final String silvers = question.getAnswer().getProperty("silver");
        final String coppers = question.getAnswer().getProperty("copper");
        final String irons = question.getAnswer().getProperty("iron");
        try {
            long wantedGold = 0L;
            if (golds != null && golds.length() > 0) {
                wantedGold = Long.parseLong(golds);
            }
            long wantedSilver = 0L;
            if (silvers != null && silvers.length() > 0) {
                wantedSilver = Long.parseLong(silvers);
            }
            long wantedCopper = 0L;
            if (coppers != null && coppers.length() > 0) {
                wantedCopper = Long.parseLong(coppers);
            }
            long wantedIron = 0L;
            if (irons != null && irons.length() > 0) {
                wantedIron = Long.parseLong(irons);
            }
            if (wantedGold < 0L) {
                question.getResponder().getCommunicator().sendNormalServerMessage("You may not withdraw a negative amount of gold coins!");
                return 0L;
            }
            if (wantedSilver < 0L) {
                question.getResponder().getCommunicator().sendNormalServerMessage("You may not withdraw a negative amount of silver coins!");
                return 0L;
            }
            if (wantedCopper < 0L) {
                question.getResponder().getCommunicator().sendNormalServerMessage("You may not withdraw a negative amount of copper coins!");
                return 0L;
            }
            if (wantedIron < 0L) {
                question.getResponder().getCommunicator().sendNormalServerMessage("You may not withdraw a negative amount of iron coins!");
                return 0L;
            }
            long valueWithdrawn = 1000000L * wantedGold;
            valueWithdrawn += 10000L * wantedSilver;
            valueWithdrawn += 100L * wantedCopper;
            valueWithdrawn += 1L * wantedIron;
            return valueWithdrawn;
        }
        catch (NumberFormatException nfe) {
            question.getResponder().getCommunicator().sendNormalServerMessage("The values were incorrect.");
            return 0L;
        }
    }
    
    static final void parsePlayerPaymentQuestion(final PlayerPaymentQuestion question) {
        final Properties props = question.getAnswer();
        final String purch = props.getProperty("purchase");
        final Creature responder = question.getResponder();
        final boolean purchase = Boolean.parseBoolean(purch);
        final long money = responder.getMoney();
        if (money < 100000L) {
            responder.getCommunicator().sendAlertServerMessage("You do not have enough money to purchase game time for. You need at least 10 silver in your bank account.");
        }
        else if (purchase) {
            try {
                if (responder.chargeMoney(100000L)) {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    lsw.addPlayingTime(responder, responder.getName(), 1, 0, System.currentTimeMillis() + Servers.localServer.name);
                    responder.getCommunicator().sendSafeServerMessage("Your request for playing time is being processed. It may take up to half an hour until the system is fully updated.");
                    final long diff = 30000L;
                    Economy.getEconomy().getKingsShop().setMoney(Economy.getEconomy().getKingsShop().getMoney() + 30000L);
                    QuestionParser.logger.log(Level.INFO, responder.getName() + " purchased 1 month premium time for " + 10L + " silver coins. " + 30000L + " iron added to king.");
                }
                else {
                    responder.getCommunicator().sendAlertServerMessage("Failed to charge you 10 silvers. Please try later.");
                }
            }
            catch (IOException ex) {
                responder.getCommunicator().sendSafeServerMessage("Your request for playing time could not be processed.");
            }
        }
    }
    
    static String generateGuardMaleName() {
        final int rand = Server.rand.nextInt(50);
        final String[] firstPart = { "Carl", "John", "Bil", "Strong", "Dare", "Grave", "Hard", "Marde", "Verde", "Vold", "Tolk", "Roe", "Bee", "Har", "Rol", "Ma", "Lo", "Claw", "Drag", "Hug", "Te", "Two", "Fu", "Ji", "La", "Ze", "Jal", "Milk", "War", "Wild", "Hang", "Just", "Fan", "Cloclo", "Buy", "Bought", "Sard", "Smart", "Slo", "Shield", "Dark", "Hung", "Sed", "Sold", "Swing", "Gar", "Dig", "Bur", "Angel", "Sorrow" };
        final int rand2 = Server.rand.nextInt(50);
        final String[] secondPart = { "ho", "john", "fish", "tree", "ooy", "olli", "tack", "rank", "sy", "moy", "dangly", "tok", "rich", "do", "mark", "stuf", "sin", "nyt", "wer", "mor", "emort", "vaar", "salm", "holm", "wyr", "zah", "ty", "fast", "der", "mar", "star", "bark", "oo", "flifil", "innow", "shoo", "husk", "eric", "ic", "o", "moon", "little", "ien", "strong", "arm", "hope", "slem", "tro", "rot", "heart" };
        return firstPart[rand] + secondPart[rand2];
    }
    
    static String generateGuardFemaleName() {
        final int rand = Server.rand.nextInt(50);
        final String[] firstPart = { "Too", "Sand", "Tree", "Whisper", "Lore", "Yan", "Van", "Vard", "Nard", "Oli", "Ala", "Krady", "Whe", "Har", "Zizi", "Zaza", "Lyn", "Claw", "Mali", "High", "Bright", "Star", "Nord", "Jala", "Yna", "Ze", "Jal", "Milk", "War", "Wild", "Fine", "Sweet", "Witty", "Cloclo", "Lory", "Tran", "Vide", "Lax", "Quick", "Shield", "Dark", "Light", "Cry", "Sold", "Juna", "Tear", "Cheek", "Ani", "Angel", "Sorro" };
        final int rand2 = Server.rand.nextInt(50);
        final String[] secondPart = { "peno", "hag", "maiden", "woman", "loy", "oa", "dei", "sai", "nai", "nae", "ane", "aei", "peno", "doa", "ela", "hofaire", "sina", "nyta", "wera", "more", "emorta", "vaara", "salma", "holmi", "wyre", "zahe", "tya", "faste", "dere", "mara", "stare", "barkia", "ooa", "fila", "innowyn", "shoein", "huskyn", "erica", "ica", "oa", "moonie", "littly", "ieny", "strongie", "ermy", "hope", "steam", "high", "wind", "heart" };
        return firstPart[rand] + secondPart[rand2];
    }
    
    public static final void parseFriendQuestion(final FriendQuestion question) {
        final Properties props = question.getAnswer();
        final Player responder = (Player)question.getResponder();
        try {
            final Player sender = Players.getInstance().getPlayer(question.getTarget());
            final String accept = props.getProperty("join");
            if (accept != null) {
                if (accept.equals("accept")) {
                    sender.addFriend(responder.getWurmId(), Friend.Category.Other.getCatId(), "");
                    responder.addFriend(sender.getWurmId(), Friend.Category.Other.getCatId(), "");
                    sender.getCommunicator().sendNormalServerMessage("You are now friends with " + responder.getName() + ".");
                    responder.getCommunicator().sendNormalServerMessage("You are now friends with " + sender.getName() + ".");
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You decline the friendlist offer.");
                    sender.getCommunicator().sendNormalServerMessage(responder.getName() + " declines your friends list invitation.");
                }
            }
        }
        catch (NoSuchPlayerException nsp) {
            responder.getCommunicator().sendNormalServerMessage("The player who wanted to add you to the friends list has logged off. You will not be added.");
        }
    }
    
    static final void parsePvPAllianceQuestion(final AllianceQuestion question) {
        try {
            final Properties props = question.getAnswer();
            final Creature responder = question.getResponder();
            final Creature sender = Server.getInstance().getCreature(question.getTarget());
            final Village senderVillage = sender.getCitizenVillage();
            final Village responderVillage = responder.getCitizenVillage();
            final PvPAlliance respAlliance = PvPAlliance.getPvPAlliance(responderVillage.getAllianceNumber());
            if (respAlliance != null) {
                sender.getCommunicator().sendAlertServerMessage(responder.getName() + " is already in the " + respAlliance.getName() + " alliance.");
                responder.getCommunicator().sendAlertServerMessage("You are already in the " + respAlliance.getName() + " alliance.");
                return;
            }
            final String accept = props.getProperty("join");
            if (accept != null) {
                if (accept.equals("accept")) {
                    final PvPAlliance oldAlliance = PvPAlliance.getPvPAlliance(sender.getCitizenVillage().getAllianceNumber());
                    if (oldAlliance != null) {
                        responder.getCitizenVillage().setAllianceNumber(oldAlliance.getId());
                        senderVillage.broadCastNormal("Under the rule of " + senderVillage.getMayor().getName() + " of " + senderVillage.getName() + ", " + responderVillage.getName() + " has been convinced to join the " + oldAlliance.getName() + ". Citizens rejoice!");
                        responderVillage.broadCastNormal("Under the rule of " + responderVillage.getMayor().getName() + ", " + responderVillage.getName() + " has joined the " + oldAlliance.getName() + ". Citizens rejoice!");
                        oldAlliance.setWins(Math.max(oldAlliance.getNumberOfWins(), responder.getCitizenVillage().getHotaWins()));
                        responder.getCitizenVillage().sendMapAnnotationsToVillagers(oldAlliance.getAllianceMapAnnotationsArray());
                    }
                    else {
                        final PvPAlliance newAlliance = new PvPAlliance(sender.getCitizenVillage().getId(), question.getAllianceName());
                        responder.getCitizenVillage().setAllianceNumber(newAlliance.getId());
                        sender.getCitizenVillage().setAllianceNumber(newAlliance.getId());
                        senderVillage.broadCastNormal("Under the rule of " + senderVillage.getMayor().getName() + " of " + senderVillage.getName() + ", " + responderVillage.getName() + " has been convinced to form the " + newAlliance.getName() + " alliance. Citizens rejoice!");
                        responderVillage.broadCastNormal("Under the rule of " + responderVillage.getMayor().getName() + ", " + responderVillage.getName() + " has formed the " + newAlliance.getName() + " alliance. Citizens rejoice!");
                        newAlliance.setWins(Math.max(responder.getCitizenVillage().getHotaWins(), sender.getCitizenVillage().getHotaWins()));
                        responder.getCitizenVillage().sendMapAnnotationsToVillagers(newAlliance.getAllianceMapAnnotationsArray());
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You decline the alliance offer.");
                    sender.getCommunicator().sendNormalServerMessage(responder.getName() + " declines your generous offer to form an alliance.");
                }
            }
        }
        catch (NoSuchCreatureException nsc) {
            QuestionParser.logger.log(Level.WARNING, nsc.getMessage(), nsc);
        }
        catch (NoSuchPlayerException nsp) {
            QuestionParser.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
    }
    
    static final void parseManageAllianceQuestion(final ManageAllianceQuestion question) {
        final Properties props = question.getAnswer();
        final Creature responder = question.getResponder();
        final Village responderVillage = responder.getCitizenVillage();
        final PvPAlliance pvpAll = PvPAlliance.getPvPAlliance(responderVillage.getAllianceNumber());
        if (pvpAll != null && responderVillage.mayDoDiplomacy(responder)) {
            final String motd = question.getAnswer().getProperty("motd");
            if (motd != null) {
                pvpAll.setMotd(motd.replaceAll("\"", ""));
            }
            final Village[] alliances = question.getAllies();
            for (int x = 0; x < alliances.length; ++x) {
                final int id = alliances[x].getId();
                final String val = props.getProperty("break" + String.valueOf(id));
                if (val != null && Boolean.parseBoolean(val)) {
                    if (alliances[x].getId() == pvpAll.getId()) {
                        responder.getCitizenVillage().setAllianceNumber(0);
                        responder.getCitizenVillage().sendClearMapAnnotationsOfType((byte)2);
                        responder.getCitizenVillage().broadCastAlert(responder.getName() + " made " + responder.getCitizenVillage().getName() + " leave the " + pvpAll.getName() + ".");
                        alliances[x].broadCastAlert(responder.getCitizenVillage().getName() + " has left the " + pvpAll.getName());
                        alliances[x].addHistory(responder.getCitizenVillage().getName(), "left the " + pvpAll.getName() + ".");
                        if (!pvpAll.exists()) {
                            alliances[x].broadCastAlert(pvpAll.getName() + " alliance has been disbanded.");
                            pvpAll.delete();
                            pvpAll.sendClearAllianceAnnotations();
                            pvpAll.deleteAllianceMapAnnotations();
                            alliances[x].setAllianceNumber(0);
                        }
                    }
                    else if (responder.getCitizenVillage().getId() == pvpAll.getId()) {
                        alliances[x].setAllianceNumber(0);
                        alliances[x].sendClearMapAnnotationsOfType((byte)2);
                        responder.getCitizenVillage().broadCastAlert(responder.getName() + " ousted " + alliances[x].getName() + " from the " + pvpAll.getName() + ".");
                        alliances[x].broadCastAlert("You have been ousted from the " + pvpAll.getName() + ".");
                        alliances[x].addHistory(responder.getName(), "ousted you from the " + pvpAll.getName() + ".");
                        if (!pvpAll.exists()) {
                            responder.getCitizenVillage().broadCastAlert(pvpAll.getName() + " alliance has been disbanded.");
                            pvpAll.delete();
                            pvpAll.sendClearAllianceAnnotations();
                            pvpAll.deleteAllianceMapAnnotations();
                            responder.getCitizenVillage().setAllianceNumber(0);
                        }
                    }
                    else {
                        try {
                            Villages.getVillage(pvpAll.getId());
                        }
                        catch (NoSuchVillageException nsv) {
                            responder.getCitizenVillage().setAllianceNumber(0);
                            responder.getCitizenVillage().sendClearMapAnnotationsOfType((byte)2);
                            QuestionParser.logger.log(Level.INFO, responder.getName() + " made " + responder.getCitizenVillage().getName() + " leave the " + pvpAll.getName() + ".");
                            responder.getCitizenVillage().broadCastAlert(responder.getName() + " made " + responder.getCitizenVillage().getName() + " leave the " + pvpAll.getName() + ".");
                            alliances[x].broadCastAlert(responder.getCitizenVillage().getName() + " has left the " + pvpAll.getName());
                            alliances[x].addHistory(responder.getCitizenVillage().getName(), "left the " + pvpAll.getName() + ".");
                        }
                        if (!pvpAll.exists()) {
                            alliances[x].broadCastAlert(pvpAll.getName() + " alliance has been disbanded.");
                            pvpAll.delete();
                            pvpAll.sendClearAllianceAnnotations();
                            pvpAll.deleteAllianceMapAnnotations();
                            alliances[x].setAllianceNumber(0);
                        }
                    }
                }
            }
            String key = "declareWar";
            String val2 = props.getProperty(key);
            if (responderVillage.getMayor().getId() == responder.getWurmId()) {
                key = "masterVill";
                val2 = props.getProperty(key);
                if (val2 != null && val2.length() > 0) {
                    try {
                        final int index = Integer.parseInt(val2);
                        if (index < alliances.length) {
                            if (alliances[index].getAllianceNumber() == responderVillage.getAllianceNumber()) {
                                pvpAll.transferControl(responder, alliances[index].getId());
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("Unable to set " + alliances[index].getName() + " as alliance capital, as they are no longer in the alliance");
                            }
                        }
                    }
                    catch (NumberFormatException nfe) {
                        responder.getCommunicator().sendAlertServerMessage("Failed to parse value for new capital.");
                    }
                }
                key = "disbandAll";
                val2 = props.getProperty(key);
                if (val2 != null && val2.equals("true")) {
                    pvpAll.disband(responder);
                }
                key = "allName";
                val2 = props.getProperty(key);
                if (val2 != null && val2.length() > 0) {
                    pvpAll.setName(responder, val2);
                }
            }
        }
        if (responderVillage.warDeclarations != null) {
            final WarDeclaration[] declArr = responderVillage.warDeclarations.values().toArray(new WarDeclaration[responderVillage.warDeclarations.size()]);
            for (int y = 0; y < declArr.length; ++y) {
                if (declArr[y].declarer == responderVillage) {
                    final int id2 = declArr[y].receiver.getId();
                    final String val2 = props.getProperty("decl" + id2);
                    if (Boolean.parseBoolean(val2)) {
                        declArr[y].dissolve(false);
                    }
                }
                else {
                    final int id2 = declArr[y].declarer.getId();
                    final String val2 = props.getProperty("recv" + id2);
                    if (Boolean.parseBoolean(val2)) {
                        declArr[y].accept();
                    }
                }
            }
        }
    }
    
    public static final void parsePaymentQuestion(final PaymentQuestion question) {
        final Properties props = question.getAnswer();
        final String ds = props.getProperty("days");
        final String ms = props.getProperty("months");
        final String wid = props.getProperty("wurmid");
        final Creature responder = question.getResponder();
        int days = 0;
        int months = 0;
        int playerid = 0;
        long wurmid = -10L;
        if (ds != null && ds.length() > 0) {
            try {
                days = Integer.parseInt(ds);
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendAlertServerMessage("Failed to parse the string " + ds + " to a valid number.");
                return;
            }
        }
        if (ms != null && ms.length() > 0) {
            try {
                months = Integer.parseInt(ms);
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendAlertServerMessage("Failed to parse the string " + ms + " to a valid number.");
                return;
            }
        }
        if (wid != null) {
            try {
                playerid = Integer.parseInt(wid);
                final Long pid = question.getPlayerId(playerid);
                wurmid = pid;
            }
            catch (NumberFormatException nfe) {
                responder.getCommunicator().sendAlertServerMessage("Failed to parse the string " + wid + " to a valid number.");
                return;
            }
        }
        if (wurmid != -10L) {
            final LoginServerWebConnection wit = new LoginServerWebConnection();
            final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
            if (pinf == null) {
                responder.getCommunicator().sendAlertServerMessage("Failed to find a player with that wurmid. Try on the login server.");
                return;
            }
            if (wit != null && wit.addPlayingTime(responder, pinf.getName(), months, days, responder.getName() + Server.rand.nextInt(10000) + "add" + Servers.localServer.id)) {
                responder.getCommunicator().sendNormalServerMessage("Ok, added " + months + " months and " + days + " days to " + pinf.getName() + ".");
            }
            responder.getCommunicator().sendNormalServerMessage("The payment request is being processed.");
        }
    }
    
    public static final void parsePowerManagementQuestion(final PowerManagementQuestion question) {
        final Properties props = question.getAnswer();
        final String pow = props.getProperty("power");
        final String wid = props.getProperty("wurmid");
        final Creature responder = question.getResponder();
        if (responder.getPower() >= 3) {
            long wurmid = -10L;
            byte power = 0;
            if (pow != null) {
                try {
                    power = Byte.parseByte(pow);
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to parse the string " + pow + " to a valid number between -127 and 127.");
                    return;
                }
            }
            if (wid != null) {
                try {
                    final int pos = Integer.parseInt(wid);
                    final Long widdy = question.getPlayerId(pos);
                    wurmid = widdy;
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to parse the string " + wid + " to a valid number.");
                    return;
                }
            }
            if (wurmid != -10L) {
                try {
                    final Player player = Players.getInstance().getPlayer(wurmid);
                    if (responder.getPower() >= power) {
                        try {
                            player.setPower(power);
                            String powString = "normal adventurer";
                            if (power == 1) {
                                powString = "hero";
                            }
                            else if (power == 2) {
                                powString = "demigod";
                            }
                            else if (power == 3) {
                                powString = "high god";
                            }
                            else if (power == 4) {
                                powString = "arch angel";
                            }
                            else if (power == 5) {
                                powString = "implementor";
                            }
                            player.getCommunicator().sendSafeServerMessage("Your status has been set by " + responder.getName() + " to " + powString + "!");
                            responder.getCommunicator().sendSafeServerMessage("You set the power of " + player.getName() + " to the status of " + powString);
                            responder.getLogger().log(Level.INFO, responder.getName() + " set the power of " + player.getName() + " to " + powString + ".");
                        }
                        catch (IOException iox) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to change the power of " + player.getName() + " to " + power + ": " + iox.getMessage(), iox);
                            responder.getCommunicator().sendSafeServerMessage("Failed to set the power of " + player.getName() + " to the new status: " + iox.getMessage());
                        }
                    }
                    else {
                        responder.getCommunicator().sendSafeServerMessage("You can not set powers above your level.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to locate player with wurmid " + wurmid + ".");
                }
            }
        }
        else {
            QuestionParser.logger.warning(responder.getName() + " tried to set the power of a Player but did not have enough power.");
        }
    }
    
    static void parseGmVillageAdQuestion(final GmVillageAdInterface question) {
        final Properties props = question.getAnswer();
        final RecruitmentAd[] ads = RecruitmentAds.getAllRecruitmentAds();
        for (int i = 0; i < ads.length; ++i) {
            final String key = ads[i].getVillageId() + "remove";
            final String val = props.getProperty(key);
            if (val != null && Boolean.parseBoolean(val)) {
                RecruitmentAds.remove(ads[i]);
            }
        }
    }
    
    static void parseTraderManagementQuestion(final TraderManagementQuestion question) {
        final Creature responder = question.getResponder();
        Shop shop = null;
        Item contract = null;
        final Properties props = question.getAnswer();
        Creature trader = null;
        long traderId = -1L;
        try {
            contract = Items.getItem(question.getTarget());
            if (contract.getOwner() != responder.getWurmId()) {
                responder.getCommunicator().sendNormalServerMessage("You are no longer in possesion of the " + contract.getName() + "!");
                return;
            }
            traderId = contract.getData();
            if (traderId != -1L) {
                trader = Server.getInstance().getCreature(traderId);
                shop = Economy.getEconomy().getShop(trader);
            }
        }
        catch (NoSuchItemException nsi) {
            QuestionParser.logger.log(Level.WARNING, responder.getName() + " contract is missing! Contract ID: " + question.getTarget());
            responder.getCommunicator().sendNormalServerMessage("You are no longer in possesion of the contract!");
            return;
        }
        catch (NoSuchPlayerException nsp) {
            QuestionParser.logger.log(Level.WARNING, "Trader for " + responder.getName() + " is a player? Well it can't be found. Contract ID: " + question.getTarget());
            responder.getCommunicator().sendNormalServerMessage("The contract has been damaged by water. You can't read the letters!");
            if (contract != null) {
                contract.setData(-1, -1);
            }
            return;
        }
        catch (NoSuchCreatureException nsc) {
            QuestionParser.logger.log(Level.WARNING, "Trader for " + responder.getName() + " can't be found. Contract ID: " + question.getTarget());
            responder.getCommunicator().sendNormalServerMessage("The contract has been damaged by water. You can't read the letters!");
            if (contract != null) {
                contract.setData(-1, -1);
            }
            return;
        }
        catch (NotOwnedException no) {
            responder.getCommunicator().sendNormalServerMessage("You are no longer in possesion of the " + contract.getName() + "!");
            return;
        }
        if (shop != null) {
            String key = traderId + "dismiss";
            String val = props.getProperty(key);
            if (Boolean.parseBoolean(val)) {
                if (trader != null) {
                    if (!trader.isTrading()) {
                        Server.getInstance().broadCastAction(trader.getName() + " grunts, packs " + trader.getHisHerItsString() + " things and is off.", trader, 5);
                        responder.getCommunicator().sendNormalServerMessage("You dismiss " + trader.getName() + " from " + trader.getHisHerItsString() + " post.");
                        QuestionParser.logger.log(Level.INFO, responder.getName() + " dismisses trader " + trader.getName() + " with Contract ID: " + question.getTarget());
                        trader.destroy();
                        contract.setData(-1, -1);
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage(trader.getName() + " is trading. Try later.");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("An error occured on the server while dismissing the trader.");
                }
            }
            else {
                key = traderId + "local";
                val = props.getProperty(key);
                final boolean useLocal = Boolean.parseBoolean(val);
                key = traderId + "pricemod";
                val = props.getProperty(key);
                float priceMod = shop.getPriceModifier();
                if (val != null) {
                    try {
                        priceMod = Float.parseFloat(val);
                        shop.setPriceModifier(priceMod);
                    }
                    catch (NumberFormatException f) {
                        responder.getCommunicator().sendSafeServerMessage("Failed to set price modifier to " + val + ". Make sure it is a decimal figure using '.'-notation.");
                    }
                }
                key = traderId + "manage";
                val = props.getProperty(key);
                final boolean manageItems = Boolean.parseBoolean(val);
                shop.setUseLocalPrice(useLocal);
                if (manageItems) {
                    final MultiPriceManageQuestion mpm = new MultiPriceManageQuestion(responder, "Price management.", "Set prices for items", traderId);
                    mpm.sendQuestion();
                }
            }
        }
        else {
            String tname = props.getProperty("ptradername");
            final String gender = props.getProperty("gender");
            byte sex = 0;
            if (gender.equals("female")) {
                sex = 1;
            }
            if (tname != null && tname.length() > 0) {
                if (tname.length() < 3 || tname.length() > 20 || containsIllegalCharacters(tname)) {
                    if (sex == 0) {
                        tname = generateGuardMaleName();
                        responder.getCommunicator().sendSafeServerMessage("The name didn't fit the trader, so he chose another one.");
                    }
                    else {
                        responder.getCommunicator().sendSafeServerMessage("The name didn't fit the trader, so she chose another one.");
                        tname = generateGuardFemaleName();
                    }
                }
                tname = StringUtilities.raiseFirstLetter(tname);
                tname = "Merchant_" + tname;
                final VolaTile tile = responder.getCurrentTile();
                if (tile != null) {
                    boolean stall = false;
                    final Item[] items = tile.getItems();
                    for (int xx = 0; xx < items.length; ++xx) {
                        if (items[xx].isMarketStall()) {
                            stall = true;
                            break;
                        }
                    }
                    if (!Methods.isActionAllowed(responder, (short)85)) {
                        return;
                    }
                    final Structure struct = tile.getStructure();
                    if (stall || (struct != null && struct.isFinished()) || responder.getPower() > 1) {
                        final Creature[] crets = tile.getCreatures();
                        boolean notok = false;
                        for (int x = 0; x < crets.length; ++x) {
                            if (!crets[x].isPlayer()) {
                                notok = true;
                                break;
                            }
                        }
                        if (!notok) {
                            if (struct != null && !struct.isTypeBridge() && !struct.mayPlaceMerchants(responder)) {
                                responder.getCommunicator().sendNormalServerMessage("You do not have permission to place a trader in this building.");
                            }
                            else {
                                try {
                                    trader = Creature.doNew(9, (tile.getTileX() << 2) + 2.0f, (tile.getTileY() << 2) + 2.0f, 180.0f, responder.getLayer(), tname, sex, responder.getKingdomId());
                                    if (responder.getFloorLevel(true) != 0) {
                                        trader.pushToFloorLevel(responder.getFloorLevel());
                                    }
                                    shop = Economy.getEconomy().createShop(trader.getWurmId(), responder.getWurmId());
                                    contract.setData(trader.getWurmId());
                                    QuestionParser.logger.info(responder.getName() + " created a trader: " + trader);
                                }
                                catch (Exception ex) {
                                    responder.getCommunicator().sendAlertServerMessage("An error occured in the rifts of the void. The trader was not created.");
                                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to create trader.", ex);
                                }
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("The trader will only set up shop where no other creatures except you are standing.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("The trader will only set up shop inside a finished building or by a market stall.");
                    }
                }
            }
        }
    }
    
    static void parseMultiPriceQuestion(final MultiPriceManageQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        try {
            final Creature trader = Server.getInstance().getCreature(target);
            if (trader.isNpcTrader()) {
                final Shop shop = Economy.getEconomy().getShop(trader);
                final Properties props = question.getAnswer();
                if (shop == null) {
                    responder.getCommunicator().sendNormalServerMessage("No shop registered for that creature.");
                }
                else if (shop.getOwnerId() == responder.getWurmId()) {
                    final Item[] items = trader.getInventory().getAllItems(false);
                    Arrays.sort(items);
                    String key = "";
                    String val = "";
                    int price = 0;
                    final Map<Long, Integer> itemMap = question.getItemMap();
                    for (int x = 0; x < items.length; ++x) {
                        if (!items[x].isFullprice()) {
                            price = 0;
                            final long id = items[x].getWurmId();
                            final Integer bbid = itemMap.get(new Long(id));
                            final int bid = bbid;
                            key = bid + "g";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    price = Integer.parseInt(val) * 1000000;
                                }
                                catch (NumberFormatException nfe) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set the gold price for " + items[x].getName() + ". Note that a coin value is in whole numbers, no decimals.");
                                }
                            }
                            key = bid + "s";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    price += Integer.parseInt(val) * 10000;
                                }
                                catch (NumberFormatException nfe) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set a silver price for " + items[x].getName() + ". Note that a coin value is in whole numbers, no decimals.");
                                }
                            }
                            key = bid + "c";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    price += Integer.parseInt(val) * 100;
                                }
                                catch (NumberFormatException nfe) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set a copper price for " + items[x].getName() + ". Note that a coin value is in whole numbers, no decimals.");
                                }
                            }
                            key = bid + "i";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    price += Integer.parseInt(val);
                                }
                                catch (NumberFormatException nfe) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set an iron price for " + items[x].getName() + ". Note that a coin value is in whole numbers, no decimals.");
                                }
                            }
                            items[x].setPrice(price);
                        }
                    }
                    responder.getCommunicator().sendNormalServerMessage("The prices are updated.");
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You don't own that shop.");
                }
            }
        }
        catch (NoSuchCreatureException nsc) {
            responder.getCommunicator().sendNormalServerMessage("No such creature.");
            QuestionParser.logger.log(Level.WARNING, responder.getName(), nsc);
        }
        catch (NoSuchPlayerException nsp) {
            responder.getCommunicator().sendNormalServerMessage("No such creature.");
            QuestionParser.logger.log(Level.WARNING, responder.getName(), nsp);
        }
    }
    
    static void parseSinglePriceQuestion(final SinglePriceManageQuestion question) {
        final Creature responder = question.getResponder();
        final Properties props = question.getAnswer();
        final long target = question.getTarget();
        if (target == -10L) {
            final NameCountList itemNames = new NameCountList();
            int price = 0;
            String val = props.getProperty("gold");
            if (val != null && val.length() > 0) {
                try {
                    price = Integer.parseInt(val) * 1000000;
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to set the gold price. Note that a coin value is in whole numbers, no decimals.");
                }
            }
            val = props.getProperty("silver");
            if (val != null && val.length() > 0) {
                try {
                    price += Integer.parseInt(val) * 10000;
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to set a silver price. Note that a coin value is in whole numbers, no decimals.");
                }
            }
            val = props.getProperty("copper");
            if (val != null && val.length() > 0) {
                try {
                    price += Integer.parseInt(val) * 100;
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to set a copper price. Note that a coin value is in whole numbers, no decimals.");
                }
            }
            val = props.getProperty("iron");
            if (val != null && val.length() > 0) {
                try {
                    price += Integer.parseInt(val);
                }
                catch (NumberFormatException nfe) {
                    responder.getCommunicator().sendNormalServerMessage("Failed to set an iron price. Note that a coin value is in whole numbers, no decimals.");
                }
            }
            for (final Item item : question.getItems()) {
                if (item.isFullprice()) {
                    responder.getCommunicator().sendNormalServerMessage("You cannot set the price of " + item.getName() + ".");
                }
                else if (item.getOwnerId() != responder.getWurmId()) {
                    responder.getCommunicator().sendNormalServerMessage("You don't own " + item.getName() + ".");
                }
                else {
                    item.setPrice(price);
                    itemNames.add(item.getName());
                }
            }
            if (!itemNames.isEmpty()) {
                responder.getCommunicator().sendNormalServerMessage("You set the price to " + Economy.getEconomy().getChangeFor(price).getChangeString() + " for " + itemNames.toString() + ".");
            }
        }
        else {
            try {
                final Item item2 = Items.getItem(target);
                if (item2.getOwnerId() == responder.getWurmId()) {
                    int price = 0;
                    final long id = item2.getWurmId();
                    String key = id + "gold";
                    String val2 = props.getProperty(key);
                    if (val2 != null && val2.length() > 0) {
                        try {
                            price = Integer.parseInt(val2) * 1000000;
                        }
                        catch (NumberFormatException nfe2) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to set the gold price for " + item2.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                        }
                    }
                    key = id + "silver";
                    val2 = props.getProperty(key);
                    if (val2 != null && val2.length() > 0) {
                        try {
                            price += Integer.parseInt(val2) * 10000;
                        }
                        catch (NumberFormatException nfe2) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to set a silver price for " + item2.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                        }
                    }
                    key = id + "copper";
                    val2 = props.getProperty(key);
                    if (val2 != null && val2.length() > 0) {
                        try {
                            price += Integer.parseInt(val2) * 100;
                        }
                        catch (NumberFormatException nfe2) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to set a copper price for " + item2.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                        }
                    }
                    key = id + "iron";
                    val2 = props.getProperty(key);
                    if (val2 != null && val2.length() > 0) {
                        try {
                            price += Integer.parseInt(val2);
                        }
                        catch (NumberFormatException nfe2) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to set an iron price for " + item2.getName() + ". Note that a coin value is in whole numbers, no decimals.");
                        }
                    }
                    item2.setPrice(price);
                    responder.getCommunicator().sendNormalServerMessage("Set price to " + Economy.getEconomy().getChangeFor(price).getChangeString() + ".");
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You don't own that item.");
                }
            }
            catch (NoSuchItemException nsi) {
                responder.getCommunicator().sendNormalServerMessage("No such item.");
            }
        }
    }
    
    public static void parseTraderRentalQuestion(final TraderRentalQuestion question) {
        final Creature responder = question.getResponder();
        final Properties props = question.getAnswer();
        if (!responder.isOnSurface()) {
            responder.getCommunicator().sendNormalServerMessage("The trader refuses to work in this cave.");
            return;
        }
        try {
            final Item contract = Items.getItem(question.getTarget());
            if (contract.getOwner() != responder.getWurmId()) {
                responder.getCommunicator().sendNormalServerMessage("You are no longer in possesion of the " + contract.getName() + "!");
                return;
            }
        }
        catch (NoSuchItemException nsi) {
            responder.getCommunicator().sendNormalServerMessage("The contract no longer exists!");
            return;
        }
        catch (NotOwnedException no) {
            responder.getCommunicator().sendNormalServerMessage("You are no longer in possesion of the contract!");
            return;
        }
        String nname = props.getProperty("ntradername");
        try {
            Items.getItem(question.getTarget());
            final String gender = props.getProperty("gender");
            byte sex = 0;
            if (gender.equals("female")) {
                sex = 1;
            }
            if (nname != null && nname.length() > 0) {
                if (nname.length() < 3 || nname.length() > 20 || containsIllegalCharacters(nname)) {
                    if (sex == 0) {
                        nname = generateGuardMaleName();
                        responder.getCommunicator().sendSafeServerMessage("The name didn't fit the trader, so he chose another one.");
                    }
                    else {
                        responder.getCommunicator().sendSafeServerMessage("The name didn't fit the trader, so she chose another one.");
                        nname = generateGuardFemaleName();
                    }
                }
                nname = StringUtilities.raiseFirstLetter(nname);
                nname = "Trader_" + nname;
                final VolaTile tile = responder.getCurrentTile();
                if (tile != null) {
                    final Creature t = Economy.getEconomy().getTraderForZone(tile.getTileX(), tile.getTileY(), tile.isOnSurface());
                    if (t == null) {
                        final Structure struct = tile.getStructure();
                        if ((struct != null && struct.isFinished()) || responder.getPower() > 0) {
                            final Creature[] crets = tile.getCreatures();
                            if (crets != null && crets.length == 1) {
                                int tax = 0;
                                final Village v = tile.getVillage();
                                if (v != null) {
                                    final String taxs = props.getProperty("tax");
                                    if (taxs == null || taxs.length() == 0) {
                                        responder.getCommunicator().sendAlertServerMessage("The tax you filled in is not appropriate. Make sure it is a number between 0 and 40.");
                                        return;
                                    }
                                    try {
                                        tax = Integer.parseInt(taxs);
                                        if (tax < 0 || tax > 40) {
                                            responder.getCommunicator().sendAlertServerMessage("The tax you filled in is not appropriate. Make sure it is a number between 0 and 40.");
                                            return;
                                        }
                                    }
                                    catch (NumberFormatException nfw) {
                                        responder.getCommunicator().sendAlertServerMessage("The tax you filled in is not appropriate. Make sure it is a whole number between 0 and 40.");
                                        return;
                                    }
                                }
                                try {
                                    final Creature trader = Creature.doNew(9, (tile.getTileX() << 2) + 2.0f, (tile.getTileY() << 2) + 2.0f, 180.0f, responder.getLayer(), nname, sex, responder.getKingdomId());
                                    final Shop shop = Economy.getEconomy().createShop(trader.getWurmId());
                                    if (tax > 0) {
                                        shop.setTax(tax / 100.0f);
                                    }
                                    Items.destroyItem(question.getTarget());
                                    if (v != null) {
                                        v.addCitizen(trader, v.getRoleForStatus((byte)3));
                                    }
                                }
                                catch (Exception ex) {
                                    responder.getCommunicator().sendAlertServerMessage("An error occured in the rifts of the void. The trader was not created.");
                                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to create trader.", ex);
                                }
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("The trader will only set up shop where no other creatures except you are standing.");
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("The trader will only set up shop inside a finished building.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("The new trader would be too close to the shop of " + t.getName() + ". He refuses to set up shop here.");
                    }
                }
            }
        }
        catch (NoSuchItemException nsi2) {
            responder.getCommunicator().sendNormalServerMessage("Failed to locate the contract for that request.");
        }
    }
    
    private static final void setReputation(final Creature responder, final int value, final String name, final long wurmId, final boolean perma, final Village village) {
        final int oldvalue = village.getReputation(wurmId);
        boolean warning = false;
        if (!Servers.localServer.PVPSERVER && Servers.localServer.id != 3 && value > -30 && Players.getInstance().removeKosFor(wurmId)) {
            village.addHistory(responder.getName(), "pardons " + name + ".");
        }
        if (oldvalue != value) {
            if (oldvalue <= -30) {
                if (value > -30) {
                    village.addHistory(responder.getName(), "pardons " + name + ".");
                }
            }
            else if (value <= -30 && oldvalue > -30) {
                if (!Servers.localServer.PVPSERVER && Servers.localServer.id != 3 && value <= -30) {
                    warning = true;
                    if (Players.getInstance().addKosWarning(new KosWarning(wurmId, value, village, perma))) {
                        responder.getCommunicator().sendNormalServerMessage(name + " will receive a warning to leave the settlement. 3 minutes later the reputation will take the effect and he will be attacked by the guards.");
                        village.addHistory(responder.getName(), "adds " + name + " to the KOS warning list.");
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage(name + " is already put up on the kos list, pending activation.");
                    }
                }
                else {
                    village.addHistory(responder.getName(), "declares " + name + " to be a criminal.");
                }
            }
            if (!warning) {
                final Reputation r = village.setReputation(wurmId, value, false, true);
                if (r != null) {
                    r.setPermanent(perma);
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("The reputation for " + name + " was deleted.");
                }
            }
        }
    }
    
    public static void parseReputationQuestion(final ReputationQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        try {
            Village village;
            if (target == -10L) {
                village = responder.getCitizenVillage();
            }
            else {
                final Item deed = Items.getItem(target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            long touched = -10L;
            if (village == null) {
                responder.getCommunicator().sendNormalServerMessage("No village found.");
            }
            else {
                final Properties props = question.getAnswer();
                final Reputation[] reputations = village.getReputations();
                String key = "";
                String val = "";
                int value = 0;
                final Map<Long, Integer> itemMap = question.getItemMap();
                key = "nn";
                val = props.getProperty(key);
                if (val != null && val.length() > 0) {
                    try {
                        final Player player = Players.getInstance().getPlayer(StringUtilities.raiseFirstLetter(val));
                        key = "nr";
                        val = props.getProperty(key);
                        if (val != null && val.length() > 0) {
                            try {
                                value = Integer.parseInt(val);
                                if ((value < 0 && player.getPower() == 0) || value >= 0) {
                                    boolean perma = false;
                                    key = "np";
                                    val = props.getProperty(key);
                                    if (Boolean.parseBoolean(val)) {
                                        perma = true;
                                    }
                                    touched = player.getWurmId();
                                    setReputation(responder, value, player.getName(), player.getWurmId(), perma, village);
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You cannot modify the reputation for " + player.getName() + " below 0, since " + player.getHeSheItString() + " is a GM.");
                                }
                            }
                            catch (NumberFormatException nfe) {
                                responder.getCommunicator().sendNormalServerMessage("Failed to set the reputation for " + player.getName() + ". Bad value.");
                            }
                        }
                    }
                    catch (NoSuchPlayerException nsp4) {
                        final String name = val;
                        final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
                        if (pinf != null && pinf.wurmId > 0L) {
                            key = "nr";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    value = Integer.parseInt(val);
                                    if ((value < 0 && pinf.getPower() == 0) || value > 0) {
                                        boolean perma2 = false;
                                        key = "np";
                                        val = props.getProperty(key);
                                        if (Boolean.parseBoolean(val)) {
                                            perma2 = true;
                                        }
                                        touched = pinf.wurmId;
                                        setReputation(responder, value, pinf.getName(), pinf.wurmId, perma2, village);
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("Make sure " + name + " is a regular player.");
                                    }
                                }
                                catch (NumberFormatException nfe2) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set the reputation for " + name + ". Bad value.");
                                }
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("Failed to locate player with name " + name + ". Make sure he/she has an account.");
                        }
                    }
                }
                for (int x = 0; x < reputations.length; ++x) {
                    value = 0;
                    final long id = reputations[x].getWurmId();
                    final Integer bbid = itemMap.get(new Long(id));
                    if (bbid != null) {
                        try {
                            final Player player2 = Players.getInstance().getPlayer(id);
                            key = bbid + "r";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    value = Integer.parseInt(val);
                                    if (player2.getPower() == 0 || (value >= 0 && player2.getPower() > 0)) {
                                        boolean perma3 = false;
                                        key = bbid + "p";
                                        val = props.getProperty(key);
                                        if (Boolean.parseBoolean(val)) {
                                            perma3 = true;
                                        }
                                        if (player2.getWurmId() != touched) {
                                            setReputation(responder, value, player2.getName(), player2.getWurmId(), perma3, village);
                                        }
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("You cannot modify the reputation for " + player2.getName() + " below 0, since " + player2.getHeSheItString() + " is a GM.");
                                    }
                                }
                                catch (NumberFormatException nfe3) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set the reputation for " + player2.getName() + ". Bad value.");
                                }
                            }
                        }
                        catch (NoSuchPlayerException nsp5) {
                            key = bbid + "r";
                            val = props.getProperty(key);
                            if (val != null && val.length() > 0) {
                                try {
                                    final String bname = Players.getInstance().getNameFor(id);
                                    final PlayerInfo pinf2 = PlayerInfoFactory.createPlayerInfo(bname);
                                    pinf2.load();
                                    value = Integer.parseInt(val);
                                    if (pinf2.getPower() == 0 || (value > 0 && pinf2.getPower() > 0)) {
                                        boolean perma4 = false;
                                        key = bbid + "p";
                                        val = props.getProperty(key);
                                        if (Boolean.parseBoolean(val)) {
                                            perma4 = true;
                                        }
                                        if (pinf2.wurmId != touched) {
                                            setReputation(responder, value, pinf2.getName(), pinf2.wurmId, perma4, village);
                                        }
                                    }
                                }
                                catch (NumberFormatException nfe3) {
                                    responder.getCommunicator().sendNormalServerMessage("Failed to set the reputation for a player. Bad value.");
                                }
                                catch (IOException iox) {
                                    QuestionParser.logger.log(Level.WARNING, iox.getMessage(), iox);
                                }
                                catch (NoSuchPlayerException nsp2) {
                                    QuestionParser.logger.log(Level.WARNING, nsp2.getMessage(), nsp2);
                                }
                            }
                        }
                    }
                }
            }
            responder.getCommunicator().sendNormalServerMessage("The reputations are updated.");
        }
        catch (NoSuchItemException nsi) {
            responder.getCommunicator().sendNormalServerMessage("No such item.");
            QuestionParser.logger.log(Level.WARNING, responder.getName(), nsi);
        }
        catch (NoSuchVillageException nsp3) {
            responder.getCommunicator().sendNormalServerMessage("No such village.");
            QuestionParser.logger.log(Level.WARNING, responder.getName(), nsp3);
        }
    }
    
    static void parseSetDeityQuestion(final SetDeityQuestion question) {
        final Properties props = question.getAnswer();
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 26) {
            final String wid = props.getProperty("wurmid");
            final String did = props.getProperty("deityid");
            final String fai = props.getProperty("faith");
            final String dec = props.getProperty("faithdec");
            final String fav = props.getProperty("favor");
            try {
                final Item targ = Items.getItem(target);
                if (targ.getTemplateId() == 176 && WurmPermissions.maySetFaith(responder)) {
                    final int listid = Integer.parseInt(wid);
                    final int deitynum = Integer.parseInt(did);
                    final float faith = (float)Double.parseDouble(fai + "." + dec);
                    final int favor = Integer.parseInt(fav);
                    final Player player = question.getPlayer(listid);
                    if (player == null) {
                        responder.getCommunicator().sendNormalServerMessage("No such player!");
                        return;
                    }
                    final int deityid = question.getDeityNumberFromArrayPos(deitynum);
                    final Deity deity = Deities.getDeity(deityid);
                    if (deity == null) {
                        responder.getCommunicator().sendNormalServerMessage("No such deity!");
                        try {
                            player.setDeity(deity);
                            player.setPriest(false);
                        }
                        catch (IOException iox) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to clear deity! " + iox.getMessage());
                            QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to clear deity " + iox.getMessage(), iox);
                        }
                    }
                    else {
                        try {
                            player.setDeity(deity);
                            player.setPriest(faith > 30.0f);
                            if (faith > 30.0f) {
                                PlayerJournal.sendTierUnlock(player, PlayerJournal.getAllTiers().get((byte)10));
                            }
                            player.setFaith(faith);
                            player.setFavor(favor);
                            responder.getCommunicator().sendNormalServerMessage(player.getName() + " now has deity " + deity.name + ", faith " + faith + ", and favour: " + favor + ".");
                            player.getCommunicator().sendNormalServerMessage("You are now a follower of " + deity.name + ".", (byte)2);
                            responder.getLogger().info(player.getName() + " now has deity " + deity.name + ", faith " + faith + ", and favour: " + favor + ".");
                            if (deity.number == 4) {
                                if (Servers.isThisAPvpServer()) {
                                    player.setKingdomId((byte)3);
                                    responder.getCommunicator().sendNormalServerMessage(player.getName() + " now is with the " + "Horde of the Summoned" + ".");
                                    player.getCommunicator().sendNormalServerMessage("You are now with the Horde of the Summoned.");
                                }
                                player.setAlignment(Math.min(-50.0f, player.getAlignment()));
                            }
                            else if (player.getAlignment() < 0.0f) {
                                if (player.getKingdomId() == 3) {
                                    if (player.getCurrentTile().getKingdom() != 0) {
                                        player.setKingdomId(player.getCurrentTile().getKingdom());
                                    }
                                    else if (responder.getKingdomId() != 3) {
                                        player.setKingdomId(responder.getKingdomId());
                                    }
                                    else {
                                        player.setKingdomId((byte)4);
                                    }
                                }
                                player.setAlignment(50.0f);
                            }
                            if (player.isChampion()) {
                                Server.getInstance().broadCastAlert(player.getName() + " now is a Champion of " + deity.name + ".", true, (byte)2);
                                responder.getLogger().log(Level.WARNING, responder.getName() + " set the deity of real death player " + player.getName() + " to " + deity.name + ".");
                            }
                        }
                        catch (IOException iox) {
                            responder.getCommunicator().sendNormalServerMessage("Failed to set deity! " + iox.getMessage());
                            QuestionParser.logger.log(Level.WARNING, responder.getName() + " failed to set deity " + iox.getMessage(), iox);
                        }
                    }
                }
                else {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " item used to answer is not a wand! " + wid + ", " + did + "," + fai);
                }
            }
            catch (NumberFormatException nf) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + ":" + nf.getMessage() + ": " + wid + ", " + did + "," + fai + "," + fav);
                responder.getCommunicator().sendNormalServerMessage("The values " + wid + ", " + did + "," + fai + "," + fav + " are improper.");
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to use wand but it didn't exist! " + wid + ", " + did + "," + fai);
            }
        }
    }
    
    static void parseSetKingdomQuestion(final SetKingdomQuestion question) {
        final Properties props = question.getAnswer();
        final int type = question.getType();
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 37) {
            if (question.getResponder().getPower() <= 0) {
                final String key = "kingd";
                final String val = question.getAnswer().getProperty("kingd");
                if (Boolean.parseBoolean(val)) {
                    try {
                        final byte previousKingdom = responder.getKingdomId();
                        byte targetKingdom = responder.getKingdomTemplateId();
                        if (Servers.isThisAChaosServer() && targetKingdom != 3) {
                            targetKingdom = 4;
                        }
                        if (responder.setKingdomId(targetKingdom, false, false)) {
                            QuestionParser.logger.info(responder.getName() + " has just decided to leave " + Kingdoms.getNameFor(previousKingdom) + " and joins " + Kingdoms.getNameFor(targetKingdom));
                            responder.getCommunicator().sendNormalServerMessage("You decide to leave " + Kingdoms.getNameFor(previousKingdom) + " and join " + Kingdoms.getNameFor(targetKingdom) + ". Congratulations!");
                            Server.getInstance().broadCastAction(responder.getName() + " leaves " + Kingdoms.getNameFor(previousKingdom) + "!", responder, 5);
                        }
                    }
                    catch (IOException iox) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + ":" + iox.getMessage() + ": " + question.getResponder().getName(), iox);
                        responder.getCommunicator().sendNormalServerMessage("The moons are not properly aligned right now. You will have to wait (there is a server error).");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You decide not to leave " + Kingdoms.getNameFor(question.getResponder().getKingdomId()) + " for now.");
                }
            }
            else {
                final String wid = props.getProperty("wurmid");
                final String did = props.getProperty("kingdomid");
                try {
                    final int listid = Integer.parseInt(wid);
                    final int index = Integer.parseInt(did);
                    final Item targ = Items.getItem(target);
                    if ((targ.getTemplateId() == 176 || targ.getTemplateId() == 315) && responder.getPower() >= 2) {
                        final Kingdom k = question.getAvailKingdoms().get(index - 1);
                        final byte kingdomid = (byte)((k == null) ? 0 : k.getId());
                        final Player player = question.getPlayer(listid);
                        if (player == null) {
                            responder.getCommunicator().sendNormalServerMessage("No such player!");
                            return;
                        }
                        final String kname = Kingdoms.getNameFor(kingdomid);
                        if (kname.equals("no known kingdom")) {
                            responder.getCommunicator().sendNormalServerMessage("Not setting to no kingdom at the moment!");
                        }
                        else {
                            if (player.isChampion()) {
                                responder.getCommunicator().sendNormalServerMessage(player.getName() + " has real death and may not change kingdom.");
                                return;
                            }
                            try {
                                if (player.setKingdomId(kingdomid)) {
                                    responder.getCommunicator().sendNormalServerMessage(player.getName() + " now is part of " + kname + ".");
                                    player.getCommunicator().sendNormalServerMessage("You are now a part of " + kname + ".");
                                    responder.getLogger().log(Level.INFO, "Set kingdom of " + player.getName() + " to " + kname + ".");
                                    player.getCommunicator().sendUpdateKingdomId();
                                }
                                else {
                                    responder.getLogger().log(Level.INFO, "Tried to set kingdom of " + player.getName() + " to " + kname + " but it was not allowed.");
                                }
                            }
                            catch (IOException iox2) {
                                responder.getCommunicator().sendNormalServerMessage("Failed to set kingdom! " + iox2.getMessage());
                                QuestionParser.logger.log(Level.WARNING, responder.getName() + "failed to set kingdom " + iox2.getMessage(), iox2);
                            }
                        }
                    }
                    else {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " item used to answer is not a wand! " + wid + ", " + did);
                    }
                }
                catch (NumberFormatException nf) {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + ":" + nf.getMessage() + ": " + wid + ", " + did);
                    responder.getCommunicator().sendNormalServerMessage("The values " + wid + ", " + did + " are improper.");
                }
                catch (NoSuchItemException nsi) {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to use wand but it didn't exist! " + wid + ", " + did);
                }
            }
        }
    }
    
    static void parseAskKingdomQuestion(final AskKingdomQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.target;
        try {
            final Creature asker = Server.getInstance().getCreature(target);
            if (asker.getKingdomId() != responder.getKingdomId()) {
                if (responder instanceof Player) {
                    if (responder.isWithinTileDistanceTo(asker.getTileX(), asker.getTileY(), (int)(asker.getPositionZ() + asker.getAltOffZ()) >> 2, 4)) {
                        final String key = "conv";
                        final String val = question.getAnswer().getProperty("conv");
                        if (Boolean.parseBoolean(val)) {
                            boolean forceToCustom = false;
                            if (!Servers.localServer.HOMESERVER && Kingdoms.getKingdom(asker.getKingdomId()).isCustomKingdom() && responder.getCitizenVillage() != null && responder.getCitizenVillage().getMayor().wurmId == responder.getWurmId()) {
                                forceToCustom = true;
                            }
                            if (!responder.mayChangeKingdom(null)) {
                                if (!forceToCustom) {
                                    responder.getCommunicator().sendNormalServerMessage("You may not change kingdom too frequently. Also, mayors of a settlement may not change kingdom. You may not join a custom kingdom on a home server.");
                                    asker.getCommunicator().sendNormalServerMessage(responder.getName() + " may not change kingdom right now.");
                                    return;
                                }
                            }
                            try {
                                if (responder.setKingdomId(asker.getKingdomId(), forceToCustom)) {
                                    responder.getCommunicator().sendNormalServerMessage("You have now joined " + Kingdoms.getNameFor(responder.getKingdomId()) + "!");
                                    asker.getCommunicator().sendNormalServerMessage(responder.getName() + " has now joined " + Kingdoms.getNameFor(responder.getKingdomId()) + "!");
                                    if (Kingdoms.getKingdom(asker.getKingdomId()).isCustomKingdom()) {
                                        final String toSend = "<" + asker.getName() + "> convinced " + responder.getName() + " to join " + Kingdoms.getNameFor(responder.getKingdomId()) + ".";
                                        final Message mess = new Message(asker, (byte)10, "GL-" + Kingdoms.getChatNameFor(asker.getKingdomId()), toSend);
                                        Server.getInstance().addMessage(mess);
                                        final WcKingdomChat wc = new WcKingdomChat(WurmId.getNextWCCommandId(), asker.getWurmId(), asker.getName(), toSend, false, asker.getKingdomId(), -1, -1, -1);
                                        if (Servers.localServer.LOGINSERVER) {
                                            wc.sendFromLoginServer();
                                        }
                                        else {
                                            wc.sendToLoginServer();
                                        }
                                    }
                                }
                            }
                            catch (IOException iox) {
                                QuestionParser.logger.log(Level.WARNING, responder.getName() + ": " + iox.getMessage(), iox);
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("You decide not to join " + Kingdoms.getNameFor(asker.getKingdomId()) + ".");
                            asker.getCommunicator().sendNormalServerMessage(responder.getName() + " decides not to join " + Kingdoms.getNameFor(asker.getKingdomId()) + ".");
                        }
                    }
                    else {
                        asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too far away now.");
                    }
                }
                else {
                    asker.getCommunicator().sendNormalServerMessage("Only players may change kingdom.");
                }
            }
            else {
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is already in " + Kingdoms.getNameFor(responder.getKingdomId()) + ".");
            }
        }
        catch (NoSuchCreatureException nsc) {
            responder.getCommunicator().sendNormalServerMessage("The asker is not around any longer.");
        }
        catch (NoSuchPlayerException nsp) {
            responder.getCommunicator().sendNormalServerMessage("The asker is not around any longer.");
        }
    }
    
    static void parseAskConvertQuestion(final AskConvertQuestion question) {
        final Creature responder = question.getResponder();
        final Item holyItem = question.getHolyItem();
        final long target = question.getTarget();
        try {
            final Creature asker = Server.getInstance().getCreature(target);
            final Deity deity = asker.getDeity();
            if (deity == null) {
                asker.getCommunicator().sendNormalServerMessage("You have no deity.");
                return;
            }
            if (!responder.isPlayer()) {
                asker.getCommunicator().sendNormalServerMessage("You may only convert other players.");
                return;
            }
            if (!responder.isWithinTileDistanceTo(asker.getTileX(), asker.getTileY(), (int)(asker.getPositionZ() + asker.getAltOffZ()) >> 2, 4)) {
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too far away now.");
                return;
            }
            final String key = "conv";
            final String val = question.getAnswer().getProperty("conv");
            if (!Boolean.parseBoolean(val)) {
                responder.getCommunicator().sendNormalServerMessage("You decide not to listen.");
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " decides not to listen.");
                return;
            }
            try {
                asker.getCurrentAction();
                responder.getCommunicator().sendNormalServerMessage(asker.getName() + " is too busy to preach right now.");
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " wants to listen to your preachings but you are too busy.");
            }
            catch (NoSuchActionException nsa) {
                try {
                    BehaviourDispatcher.action(asker, asker.getCommunicator(), holyItem.getWurmId(), responder.getWurmId(), (short)216);
                }
                catch (FailedException ex3) {}
                catch (NoSuchBehaviourException | NoSuchItemException | NoSuchPlayerException | NoSuchWallException | NoSuchCreatureException ex4) {
                    final WurmServerException ex;
                    final WurmServerException nsb = ex;
                    QuestionParser.logger.log(Level.WARNING, nsb.getMessage(), nsb);
                }
            }
        }
        catch (NoSuchCreatureException | NoSuchPlayerException ex5) {
            final WurmServerException ex2;
            final WurmServerException nsc = ex2;
            responder.getCommunicator().sendNormalServerMessage("The preacher is not around any longer.");
        }
    }
    
    public static void parseConvertQuestion(final ConvertQuestion question) {
        final Creature responder = question.getResponder();
        final Item holyItem = question.getHolyItem();
        final long target = question.getTarget();
        try {
            final Creature asker = Server.getInstance().getCreature(target);
            final Deity deity = asker.getDeity();
            if (deity == null) {
                asker.getCommunicator().sendNormalServerMessage("You have no deity.");
                return;
            }
            if (!responder.isWithinTileDistanceTo(asker.getTileX(), asker.getTileY(), (int)(asker.getPositionZ() + asker.getAltOffZ()) >> 2, 4)) {
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " is too far away now.");
                return;
            }
            final String key = "conv";
            final String val = question.getAnswer().getProperty("conv");
            if (!Boolean.parseBoolean(val)) {
                responder.getCommunicator().sendNormalServerMessage("You decide not to convert.");
                asker.getCommunicator().sendNormalServerMessage(responder.getName() + " decides not to convert.");
                return;
            }
            if (canConvertToDeity(responder, deity)) {
                if (!doesKingdomTemplateAcceptDeity(responder.getKingdomTemplateId(), deity)) {
                    responder.getCommunicator().sendNormalServerMessage("Following that deity would expel you from " + responder.getKingdomName() + ".");
                    return;
                }
                try {
                    final Skill preaching = asker.getSkills().getSkillOrLearn(10065);
                    preaching.skillCheck(preaching.getKnowledge(0.0) - 10.0, holyItem, asker.zoneBonus, false, question.getSkillcounter());
                    responder.setChangedDeity();
                    responder.setDeity(deity);
                    responder.setFaith((float)preaching.getKnowledge(0.0) / 5.0f);
                    final Deity templateDeity = Deities.getDeity(deity.getTemplateDeity());
                    templateDeity.increaseFavor();
                    if (deity.isHateGod()) {
                        asker.maybeModifyAlignment(-1.0f);
                        responder.setAlignment(Math.min(-1.0f, responder.getAlignment()));
                    }
                    else {
                        asker.maybeModifyAlignment(1.0f);
                        responder.setAlignment(Math.max(1.0f, responder.getAlignment()));
                    }
                    asker.setFavor(Math.max(asker.getFavor(), asker.getFaith() / 2.0f));
                    responder.setFavor(1.0f);
                    responder.getCommunicator().sendNormalServerMessage("You have now converted to " + deity.name + "!");
                    asker.getCommunicator().sendNormalServerMessage(responder.getName() + " has now converted to " + deity.name + "!");
                    asker.achievement(621);
                }
                catch (IOException iox) {
                    responder.getCommunicator().sendNormalServerMessage("You failed to convert to " + deity.name + " due to a server error! Please report this to the GM's.");
                    asker.getCommunicator().sendNormalServerMessage(responder.getName() + " failed to convert to " + deity.name + " due to a server error! Please report this to the GM's.");
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + ":" + iox.getMessage(), iox);
                }
            }
        }
        catch (NoSuchCreatureException | NoSuchPlayerException ex2) {
            final WurmServerException ex;
            final WurmServerException nsc = ex;
            responder.getCommunicator().sendNormalServerMessage("The preacher is not around any longer.");
        }
    }
    
    public static final boolean canConvertToDeity(final Creature responder, final Deity deity) {
        if (responder == null || !responder.isPlayer()) {
            return false;
        }
        if (deity == null) {
            responder.getCommunicator().sendNormalServerMessage("That deity is not available to convert to.");
            return false;
        }
        if (!responder.mayChangeDeity(deity.getNumber())) {
            responder.getCommunicator().sendNormalServerMessage("Your faith cannot change so frequently. You will have to wait.");
            return false;
        }
        if (responder.getDeity() == deity) {
            responder.getCommunicator().sendNormalServerMessage("You already follow the teachings of " + deity.getName() + ".");
            return false;
        }
        return true;
    }
    
    public static final boolean doesKingdomTemplateAcceptDeity(final byte kingdomTemplate, final Deity deity) {
        if (kingdomTemplate == 3) {
            if (deity.isFo() || deity.isMagranon() || deity.isVynora()) {
                return false;
            }
        }
        else if (deity.isLibila()) {
            return kingdomTemplate == 4 && !Servers.localServer.PVPSERVER;
        }
        return true;
    }
    
    public static final void parseAltarConvertQuestion(final AltarConversionQuestion question) {
        final Creature responder = question.getResponder();
        final Deity deity = question.getDeity();
        final long target = question.getTarget();
        try {
            final Item altar = Items.getItem(target);
            if (deity == null) {
                responder.getCommunicator().sendNormalServerMessage("The altar has no deity anymore.");
                return;
            }
            if (!responder.isWithinTileDistanceTo(altar.getTileX(), altar.getTileY(), (int)altar.getPosZ() >> 2, 4)) {
                responder.getCommunicator().sendNormalServerMessage("The " + altar.getName() + " is too far away now.");
                return;
            }
            final String key = "conv";
            final String val = question.getAnswer().getProperty(key);
            if (!Boolean.parseBoolean(val)) {
                responder.getCommunicator().sendNormalServerMessage("You decide not to convert.");
                return;
            }
            if (canConvertToDeity(responder, deity)) {
                try {
                    if (!doesKingdomTemplateAcceptDeity(responder.getKingdomTemplateId(), deity)) {
                        responder.getCommunicator().sendNormalServerMessage("Following that deity would expel you from " + responder.getKingdomName() + ".");
                        return;
                    }
                    responder.setChangedDeity();
                    responder.setDeity(deity);
                    responder.setFaith(1.0f);
                    responder.setFavor(1.0f);
                    if (deity.isHateGod()) {
                        responder.setAlignment(Math.min(-1.0f, responder.getAlignment()));
                    }
                    else {
                        responder.setAlignment(Math.max(1.0f, responder.getAlignment()));
                    }
                    responder.getCommunicator().sendNormalServerMessage("You have now converted to " + deity.name + "!");
                }
                catch (IOException iox) {
                    responder.getCommunicator().sendNormalServerMessage("You failed to convert to " + deity.name + " due to a server error! Please report this to the GM's.");
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + ":" + iox.getMessage(), iox);
                }
            }
        }
        catch (NoSuchItemException nsc) {
            responder.getCommunicator().sendNormalServerMessage("The altar is not around any longer.");
        }
    }
    
    public static final void parseAscensionQuestion(final AscensionQuestion question) {
        final Creature responder = question.getResponder();
        if (responder.isPlayer()) {
            final String key = "demig";
            final String val = question.getAnswer().getProperty("demig");
            if (!Boolean.parseBoolean(val)) {
                responder.getCommunicator().sendNormalServerMessage("You decide to remain mortal for now. Maybe the chance returns, who knows?");
                QuestionParser.logger.log(Level.INFO, responder.getName() + " declined ascension to demigod!");
            }
        }
    }
    
    public static void parsePriestQuestion(final PriestQuestion question) {
        final Creature responder = question.getResponder();
        final long target = question.getTarget();
        final Deity deity = responder.getDeity();
        Label_0261: {
            if (WurmId.getType(target) == 2) {
                try {
                    final Item altar = Items.getItem(target);
                    if (!altar.isHugeAltar()) {
                        responder.getCommunicator().sendNormalServerMessage("You must be close to the huge altar in order to become a priest.");
                        return;
                    }
                    if (!responder.isWithinTileDistanceTo((int)altar.getPosX() >> 2, (int)altar.getPosY() >> 2, (int)altar.getPosZ() >> 2, 4)) {
                        responder.getCommunicator().sendNormalServerMessage("You must be close to the huge altar in order to become a priest.");
                        return;
                    }
                    break Label_0261;
                }
                catch (NoSuchItemException nsi) {
                    responder.getCommunicator().sendNormalServerMessage("You can not become a priest right now.");
                    return;
                }
            }
            if (deity == null) {
                responder.getCommunicator().sendNormalServerMessage("You are not even a follower of a faith!");
                return;
            }
            try {
                final Creature creature = Server.getInstance().getCreature(question.target);
                if (!responder.isWithinTileDistanceTo((int)creature.getPosX() >> 2, (int)creature.getPosY() >> 2, (int)(creature.getPositionZ() + creature.getAltOffZ()) >> 2, 4)) {
                    responder.getCommunicator().sendNormalServerMessage("You must be closer to the person who asked you.");
                    return;
                }
                if (deity != creature.getDeity()) {
                    responder.getCommunicator().sendNormalServerMessage("You must be of the same faith as " + creature.getName() + ".");
                    return;
                }
            }
            catch (NoSuchCreatureException nsc) {
                responder.getCommunicator().sendNormalServerMessage("You must be close to the person who asked you.");
                return;
            }
            catch (NoSuchPlayerException nsp) {
                responder.getCommunicator().sendNormalServerMessage("You must be close to the person who asked you.");
                return;
            }
        }
        if (deity != null) {
            if (!responder.isPriest()) {
                if (responder.isPlayer()) {
                    final String key = "priest";
                    final String val = question.getAnswer().getProperty("priest");
                    if (Boolean.parseBoolean(val)) {
                        QuestionParser.logger.info(responder.getName() + " has just become a priest of " + deity.name);
                        responder.getCommunicator().sendNormalServerMessage("You have become a priest of " + deity.name + ". Congratulations!");
                        Server.getInstance().broadCastAction(responder.getName() + " is now a priest of " + deity.name + "!", responder, 5);
                        responder.setPriest(true);
                        PlayerJournal.sendTierUnlock((Player)responder, PlayerJournal.getAllTiers().get((byte)10));
                        try {
                            responder.setFavor(responder.getFaith());
                        }
                        catch (IOException iox) {
                            QuestionParser.logger.log(Level.WARNING, iox.getMessage(), iox);
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("You decide not to become a priest for now.");
                    }
                }
            }
            else {
                responder.getCommunicator().sendNormalServerMessage("You are already a priest.");
            }
        }
        else {
            responder.getCommunicator().sendNormalServerMessage("You have no deity!");
        }
    }
    
    public static final void parseRealDeathQuestion(final RealDeathQuestion question) {
        final Creature responder = question.getResponder();
        final Deity deity = responder.getDeity();
        final long target = question.getTarget();
        if (!responder.isChampion()) {
            try {
                final Item altar = Items.getItem(target);
                if (altar.isHugeAltar()) {
                    if (deity != null) {
                        if (deity.accepts(responder.getAlignment())) {
                            if (responder instanceof Player) {
                                if (Players.getChampionsFromKingdom(responder.getKingdomId(), deity.getNumber()) < 1) {
                                    if (Players.getChampionsFromKingdom(responder.getKingdomId()) < 3) {
                                        if (responder.isWithinTileDistanceTo((int)altar.getPosX() >> 2, (int)altar.getPosY() >> 2, (int)altar.getPosZ() >> 2, 4)) {
                                            final String key = "rd";
                                            final String val = question.getAnswer().getProperty("rd");
                                            if (Boolean.parseBoolean(val)) {
                                                responder.becomeChamp();
                                            }
                                            else {
                                                responder.getCommunicator().sendNormalServerMessage("You decide not to become a champion of " + deity.name + ".");
                                            }
                                        }
                                        else {
                                            responder.getCommunicator().sendNormalServerMessage(altar.getName() + " is too far away now.");
                                        }
                                    }
                                    else {
                                        responder.getCommunicator().sendNormalServerMessage("Your kingdom does not support more champions right now.");
                                    }
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage(deity.name + " can not support another champion from your kingdom right now.");
                                }
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("Only players may become champions.");
                            }
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage(deity.name + " would not accept you as " + deity.getHisHerItsString() + " champion right now since you have strayn from the path.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("You no longer follow a deity.");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("The altar is not of the right type.");
                }
            }
            catch (NoSuchItemException nsc) {
                responder.getCommunicator().sendNormalServerMessage("The altar is not around any longer.");
            }
        }
        else {
            responder.getCommunicator().sendNormalServerMessage("You are already a champion of " + deity.name + ".");
        }
    }
    
    public static final void parseSpawnQuestion(final SpawnQuestion question) {
        boolean eserv = false;
        Label_0200: {
            if (Servers.localServer.KINGDOM != question.getResponder().getKingdomId()) {
                final String sps = question.getAnswer().getProperty("eserver");
                if (sps != null) {
                    eserv = true;
                    final Map<Integer, Integer> servers = question.getServerEntries();
                    final int i = Integer.parseInt(sps);
                    if (i > 0) {
                        final Integer serverNumber = servers.get(i);
                        final ServerEntry toGoTo = Servers.getServerWithId(serverNumber);
                        if (toGoTo.getKingdom() != question.getResponder().getKingdomId()) {
                            if (toGoTo.getKingdom() != 0) {
                                break Label_0200;
                            }
                        }
                        try {
                            final Player player = Players.getInstance().getPlayer(question.getResponder().getWurmId());
                            player.sendTransfer(Server.getInstance(), toGoTo.EXTERNALIP, Integer.parseInt(toGoTo.EXTERNALPORT), toGoTo.INTRASERVERPASSWORD, toGoTo.getId(), -1, -1, true, false, player.getKingdomId());
                            return;
                        }
                        catch (NoSuchPlayerException nsp2) {
                            QuestionParser.logger.log(Level.INFO, "Player " + question.getResponder().getWurmId() + " is no longer available.");
                        }
                    }
                }
            }
        }
        final String spq = question.getAnswer().getProperty("spawnpoint");
        if (spq != null) {
            final int j = Integer.parseInt(spq);
            final Spawnpoint sp = question.getSpawnpoint(j);
            if (sp == null) {
                return;
            }
            try {
                question.getResponder().spawnArmour = question.getAnswer().getProperty("armour");
                question.getResponder().spawnWeapon = question.getAnswer().getProperty("weapon");
                final Player p = Players.getInstance().getPlayer(question.getTarget());
                p.spawn(sp.number);
            }
            catch (NoSuchPlayerException nsp) {
                QuestionParser.logger.log(Level.WARNING, "Unknown player trying to spawn?", nsp);
            }
        }
        else if (!eserv) {
            question.getResponder().getCommunicator().sendNormalServerMessage("You can bring the spawn question back by typing /respawn in a chat window.");
        }
    }
    
    static final void parseWithdrawMoneyQuestion(final WithdrawMoneyQuestion question) {
        final Creature responder = question.getResponder();
        if (responder.isDead()) {
            responder.getCommunicator().sendNormalServerMessage("You are dead, and may not withdraw any money.");
            return;
        }
        final int type = question.getType();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 36) {
            try {
                final Item token = Items.getItem(question.getTarget());
                if (token.getTemplateId() != 236) {
                    responder.getCommunicator().sendNormalServerMessage("The " + token.getName() + " does not function as a bank.");
                    return;
                }
                if (!responder.isWithinDistanceTo(token.getPosX(), token.getPosY(), token.getPosZ(), 30.0f)) {
                    responder.getCommunicator().sendNormalServerMessage("You are too far away from the bank.");
                    return;
                }
            }
            catch (NoSuchItemException nsi) {
                responder.getCommunicator().sendNormalServerMessage("The bank no longer is available as the token is gone.");
                return;
            }
            final long money = responder.getMoney();
            if (money > 0L) {
                final long valueWithdrawn = getValueWithdrawn(question);
                if (valueWithdrawn > 0L) {
                    try {
                        if (responder.chargeMoney(valueWithdrawn)) {
                            final Item[] coins = Economy.getEconomy().getCoinsFor(valueWithdrawn);
                            final Item inventory = responder.getInventory();
                            for (int x = 0; x < coins.length; ++x) {
                                inventory.insertItem(coins[x]);
                            }
                            final Change withd = Economy.getEconomy().getChangeFor(valueWithdrawn);
                            responder.getCommunicator().sendNormalServerMessage("You withdraw " + withd.getChangeString() + " from the bank.");
                            final Change c = new Change(money - valueWithdrawn);
                            responder.getCommunicator().sendNormalServerMessage("New balance: " + c.getChangeString() + ".");
                            QuestionParser.logger.info(responder.getName() + " withdraw " + withd.getChangeString() + " from the bank and should have " + c.getChangeString() + " now.");
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("You can not withdraw that amount of money at the moment.");
                        }
                    }
                    catch (IOException iox) {
                        QuestionParser.logger.log(Level.WARNING, "Failed to withdraw money from " + responder.getName() + ":" + iox.getMessage(), iox);
                        responder.getCommunicator().sendNormalServerMessage("The transaction failed. Please contact the game masters using the <i>/dev</i> command.");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("No money withdrawn.");
                }
            }
            else {
                responder.getCommunicator().sendNormalServerMessage("You have no money in the bank.");
            }
        }
    }
    
    static final void parseVillageInfoQuestion(final VillageInfo question) {
        final Creature responder = question.getResponder();
        final int type = question.getType();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 14) {
            try {
                final Item token = Items.getItem(question.target);
                final int vill = token.getData2();
                final Village village = Villages.getVillage(vill);
                final long money = responder.getMoney();
                if (money > 0L) {
                    final long valueWithdrawn = getValueWithdrawn(question);
                    if (valueWithdrawn > 0L) {
                        try {
                            if (village.plan != null) {
                                if (responder.chargeMoney(valueWithdrawn)) {
                                    village.plan.addMoney(valueWithdrawn);
                                    village.plan.addPayment(responder.getName(), responder.getWurmId(), valueWithdrawn);
                                    final Change newch = Economy.getEconomy().getChangeFor(valueWithdrawn);
                                    responder.getCommunicator().sendNormalServerMessage("You pay " + newch.getChangeString() + " to the upkeep fund of " + village.getName() + ".");
                                    QuestionParser.logger.log(Level.INFO, responder.getName() + " added " + valueWithdrawn + " irons to " + village.getName() + " upkeep.");
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You don't have that much money.");
                                }
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("This village does not have an upkeep plan.");
                            }
                        }
                        catch (IOException iox) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to withdraw money from " + responder.getName() + ":" + iox.getMessage(), iox);
                            responder.getCommunicator().sendNormalServerMessage("The transaction failed. Please contact the game masters using the <i>/dev</i> command.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("No money withdrawn.");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You have no money in the bank.");
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to get info for null token with id " + question.target, nsi);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
            }
            catch (NoSuchVillageException nsv) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to get info for null village for token with id " + question.target);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
            }
        }
    }
    
    static final void parseVillageUpkeepQuestion(final VillageUpkeep question) {
        final Creature responder = question.getResponder();
        final int type = question.getType();
        if (type == 0) {
            QuestionParser.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (type == 120) {
            try {
                Village village;
                if (question.target == -10L) {
                    village = question.getResponder().getCitizenVillage();
                }
                else {
                    final Item token = Items.getItem(question.target);
                    final int vill = token.getData2();
                    village = Villages.getVillage(vill);
                }
                final long money = responder.getMoney();
                if (money > 0L) {
                    final long valueWithdrawn = getValueWithdrawn(question);
                    if (valueWithdrawn > 0L) {
                        try {
                            if (village.plan != null) {
                                if (responder.chargeMoney(valueWithdrawn)) {
                                    village.plan.addMoney(valueWithdrawn);
                                    village.plan.addPayment(responder.getName(), responder.getWurmId(), valueWithdrawn);
                                    final Change newch = Economy.getEconomy().getChangeFor(valueWithdrawn);
                                    responder.getCommunicator().sendNormalServerMessage("You pay " + newch.getChangeString() + " to the upkeep fund of " + village.getName() + ".");
                                    QuestionParser.logger.log(Level.INFO, responder.getName() + " added " + valueWithdrawn + " irons to " + village.getName() + " upkeep.");
                                }
                                else {
                                    responder.getCommunicator().sendNormalServerMessage("You don't have that much money.");
                                }
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("This village does not have an upkeep plan.");
                            }
                        }
                        catch (IOException iox) {
                            QuestionParser.logger.log(Level.WARNING, "Failed to withdraw money from " + responder.getName() + ":" + iox.getMessage(), iox);
                            responder.getCommunicator().sendNormalServerMessage("The transaction failed. Please contact the game masters using the <i>/dev</i> command.");
                        }
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage("No money withdrawn.");
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage("You have no money in the bank.");
                }
            }
            catch (NoSuchItemException nsi) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to get info for null token with id " + question.target, nsi);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
            }
            catch (NoSuchVillageException nsv) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to get info for null village for token with id " + question.target);
                responder.getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
            }
        }
    }
    
    static final void parseTitleCompoundQuestion(final TitleCompoundQuestion question) {
        final Player responder = (Player)question.getResponder();
        final Titles.Title[] titles = responder.getTitles();
        if (titles.length == 0 && responder.getAppointments() == 0L && !responder.isAppointed()) {
            QuestionParser.logger.info(String.format("No titles found for %s.", responder.getName()));
            return;
        }
        if (Servers.isThisAPvpServer()) {
            final King king = King.getKing(question.getResponder().getKingdomId());
            if (king != null && (question.getResponder().getAppointments() != 0L || question.getResponder().isAppointed())) {
                final Appointments a = Appointments.getAppointments(king.era);
                for (int x = 0; x < a.officials.length; ++x) {
                    final int oId = x + 1500;
                    final String office = question.getAnswer().getProperty("office" + oId);
                    if (office != null && Boolean.parseBoolean(office) && a.officials[x] == question.getResponder().getWurmId()) {
                        final Appointment o = a.getAppointment(oId);
                        question.getResponder().getCommunicator().sendNormalServerMessage("You vacate the office of " + o.getNameForGender((byte)0) + ".", (byte)2);
                        a.setOfficial(x + 1500, 0L);
                    }
                }
            }
        }
        final String occultistAns = question.getAnswer().getProperty("hideoccultist");
        if (occultistAns != null) {
            final boolean bool = Boolean.parseBoolean(occultistAns);
            responder.setFlag(24, bool);
        }
        final String meditationAns = question.getAnswer().getProperty("hidemeditation");
        if (meditationAns != null) {
            final boolean bool2 = Boolean.parseBoolean(meditationAns);
            responder.setFlag(25, bool2);
        }
        final String t1 = question.getAnswer().getProperty("First");
        final String t2 = question.getAnswer().getProperty("Second");
        if (t1 != null) {
            try {
                final int id = Integer.parseInt(t1);
                if (id == 0) {
                    responder.setTitle(null);
                }
                else {
                    final Titles.Title title = question.getFirstTitle(id - 1);
                    if (title == null) {
                        return;
                    }
                    responder.setTitle(title);
                }
            }
            catch (NumberFormatException nfe) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + t1 + " as int.");
            }
        }
        if (t2 != null) {
            try {
                final int id = Integer.parseInt(t2);
                if (id == 0) {
                    responder.setSecondTitle(null);
                }
                else {
                    final Titles.Title title = question.getFirstTitle(id - 1);
                    if (title == null) {
                        return;
                    }
                    if (title == responder.getTitle()) {
                        responder.getCommunicator().sendSafeServerMessage("You cannot use two of the same title.");
                        responder.setSecondTitle(null);
                        return;
                    }
                    responder.setSecondTitle(title);
                }
            }
            catch (NumberFormatException nfe) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + t2 + " as int.");
            }
        }
    }
    
    static final void parseTitleQuestion(final TitleQuestion question) {
        final Player responder = (Player)question.getResponder();
        final Titles.Title[] titles = responder.getTitles();
        if (titles.length == 0 && responder.getAppointments() == 0L && !responder.isAppointed()) {
            return;
        }
        if (Servers.isThisAPvpServer()) {
            final King king = King.getKing(question.getResponder().getKingdomId());
            if (king != null && (question.getResponder().getAppointments() != 0L || question.getResponder().isAppointed())) {
                final Appointments a = Appointments.getAppointments(king.era);
                for (int x = 0; x < a.officials.length; ++x) {
                    final int oId = x + 1500;
                    final String office = question.getAnswer().getProperty("office" + oId);
                    if (office != null && Boolean.parseBoolean(office) && a.officials[x] == question.getResponder().getWurmId()) {
                        final Appointment o = a.getAppointment(oId);
                        question.getResponder().getCommunicator().sendNormalServerMessage("You vacate the office of " + o.getNameForGender((byte)0) + ".", (byte)2);
                        a.setOfficial(x + 1500, 0L);
                    }
                }
            }
        }
        final String occultistAns = question.getAnswer().getProperty("hideoccultist");
        if (occultistAns != null) {
            final boolean bool = Boolean.parseBoolean(occultistAns);
            responder.setFlag(24, bool);
        }
        final String meditationAns = question.getAnswer().getProperty("hidemeditation");
        if (meditationAns != null) {
            final boolean bool2 = Boolean.parseBoolean(meditationAns);
            responder.setFlag(25, bool2);
        }
        final String accept = question.getAnswer().getProperty("TITLE");
        if (accept != null) {
            try {
                final int id = Integer.parseInt(accept);
                if (id == 0) {
                    responder.setTitle(null);
                }
                else {
                    final Titles.Title title = question.getTitle(id - 1);
                    if (title == null) {
                        return;
                    }
                    responder.setTitle(title);
                }
            }
            catch (NumberFormatException nfe) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + accept + " as int.");
            }
        }
    }
    
    static final void parseServerQuestion(final ServerQuestion question) {
        final Creature responder = question.getResponder();
        String key = "transferTo";
        String val = question.getAnswer().getProperty(key);
        int transid = 0;
        if (val != null) {
            try {
                transid = Integer.parseInt(val);
            }
            catch (NumberFormatException nfe) {
                QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                return;
            }
            if (transid > 0) {
                final ServerEntry targetserver = question.getTransferEntry(transid - 1);
                if (targetserver != null) {
                    if (targetserver.isAvailable(responder.getPower(), responder.isReallyPaying())) {
                        Player playerToTransfer = (Player)responder;
                        if (WurmId.getType(question.getTarget()) == 0) {
                            try {
                                playerToTransfer = Players.getInstance().getPlayer(question.getTarget());
                            }
                            catch (NoSuchPlayerException ex) {}
                        }
                        if (!responder.equals(playerToTransfer)) {
                            if (playerToTransfer.getPower() > responder.getPower()) {
                                responder.getCommunicator().sendNormalServerMessage("You are too weak to transfer " + playerToTransfer.getName() + " to " + targetserver.name + ".");
                                return;
                            }
                            responder.getCommunicator().sendNormalServerMessage("Transferring " + playerToTransfer.getName() + " to " + targetserver.name + ".");
                            playerToTransfer.getCommunicator().sendNormalServerMessage(responder.getName() + " transfers you to " + targetserver.name + ".");
                            QuestionParser.logger.info(responder.getName() + " transfers " + playerToTransfer.getName() + " to " + targetserver.name + ".");
                        }
                        else {
                            playerToTransfer.getCommunicator().sendNormalServerMessage("Transferring to " + targetserver.name + ".");
                            QuestionParser.logger.info(playerToTransfer.getName() + " transferring to " + targetserver.name + ".");
                        }
                        Server.getInstance().broadCastAction(playerToTransfer.getName() + " transfers to " + targetserver.name + ".", playerToTransfer, 5);
                        int tilex = targetserver.SPAWNPOINTJENNX;
                        int tiley = targetserver.SPAWNPOINTJENNY;
                        if (playerToTransfer.getPower() <= 0) {
                            byte targetKingdom = playerToTransfer.getKingdomId();
                            if (targetserver.getKingdom() == 4) {
                                targetKingdom = 4;
                            }
                            playerToTransfer.lastKingdom = playerToTransfer.getKingdomId();
                            if (targetKingdom != playerToTransfer.getKingdomId()) {
                                try {
                                    playerToTransfer.setKingdomId(targetKingdom);
                                }
                                catch (IOException ex2) {}
                            }
                        }
                        if (playerToTransfer.getKingdomId() == 1) {
                            tilex = targetserver.SPAWNPOINTJENNX;
                            tiley = targetserver.SPAWNPOINTJENNY;
                        }
                        else if (playerToTransfer.getKingdomId() == 3) {
                            tilex = targetserver.SPAWNPOINTLIBX;
                            tiley = targetserver.SPAWNPOINTLIBY;
                        }
                        else if (playerToTransfer.getKingdomId() == 2) {
                            tilex = targetserver.SPAWNPOINTMOLX;
                            tiley = targetserver.SPAWNPOINTMOLY;
                        }
                        playerToTransfer.sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, tilex, tiley, true, false, playerToTransfer.getKingdomId());
                        playerToTransfer.transferCounter = 30;
                        return;
                    }
                    responder.getCommunicator().sendNormalServerMessage(targetserver.name + " is not available now.");
                }
            }
        }
        if (responder.getPower() > 2) {
            int addid = -1;
            key = "neighbourServer";
            val = question.getAnswer().getProperty(key);
            if (val != null) {
                try {
                    addid = Integer.parseInt(val);
                    if (addid > 0) {
                        key = "direction";
                        val = question.getAnswer().getProperty(key);
                        if (val != null) {
                            final ServerEntry entry = question.getServerEntry(addid - 1);
                            if (entry != null) {
                                if (val.equals("0")) {
                                    val = "NORTH";
                                }
                                if (val.equals("1")) {
                                    val = "EAST";
                                }
                                if (val.equals("2")) {
                                    val = "SOUTH";
                                }
                                if (val.equals("3")) {
                                    val = "WEST";
                                }
                                Servers.addServerNeighbour(entry.id, val);
                                responder.getCommunicator().sendNormalServerMessage("Added server with id " + entry.id + " " + val + " of this server.");
                                QuestionParser.logger.info(responder.getName() + " added server with name " + entry.name + " and id " + entry.id + " " + val + " of this server.");
                            }
                            else {
                                responder.getCommunicator().sendNormalServerMessage("Failed to locate the server to add.");
                            }
                        }
                    }
                }
                catch (NumberFormatException nfe2) {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                    responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                    return;
                }
            }
            int delid = -1;
            key = "deleteServer";
            val = question.getAnswer().getProperty(key);
            if (val != null) {
                try {
                    delid = Integer.parseInt(val);
                    if (delid > 0) {
                        final ServerEntry entry2 = question.getServerEntry(delid - 1);
                        if (entry2 != null) {
                            Servers.deleteServerEntry(entry2.id);
                            responder.getCommunicator().sendNormalServerMessage("Deleted server with id " + entry2.id + ".");
                            QuestionParser.logger.info(responder.getName() + " Deleted server with name " + entry2.name + " and id " + entry2.id + '.');
                        }
                        else {
                            responder.getCommunicator().sendNormalServerMessage("Failed to locate the server to delete.");
                        }
                    }
                }
                catch (NumberFormatException nfe3) {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                    responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                    return;
                }
            }
            int id = -1;
            key = "addid";
            val = question.getAnswer().getProperty(key);
            if (val != null && val.length() > 0) {
                try {
                    id = Integer.parseInt(val);
                    if (id < 0) {
                        responder.getCommunicator().sendAlertServerMessage("The id of the server can not be " + id + ".");
                        return;
                    }
                    final ServerEntry entry3 = Servers.getServerWithId(id);
                    if (entry3 != null) {
                        responder.getCommunicator().sendAlertServerMessage("The id of the server already exists: " + id + ".");
                        return;
                    }
                }
                catch (NumberFormatException nfe4) {
                    QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                    responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                    return;
                }
            }
            if (id >= 0) {
                key = "addname";
                final String name;
                val = (name = question.getAnswer().getProperty(key));
                if (val != null && val.length() < 4) {
                    responder.getCommunicator().sendAlertServerMessage("The name of the server can not be " + val + ". It is too short.");
                    return;
                }
                key = "addhome";
                val = question.getAnswer().getProperty(key);
                boolean homeServer = false;
                if (val != null && val.equals("true")) {
                    homeServer = true;
                }
                key = "addpayment";
                val = question.getAnswer().getProperty(key);
                boolean isPayment = false;
                if (val != null && val.equals("true")) {
                    isPayment = true;
                }
                key = "addlogin";
                val = question.getAnswer().getProperty(key);
                boolean isLogin = false;
                if (val != null && val.equals("true")) {
                    isLogin = true;
                }
                int jennx = -1;
                key = "addsjx";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        jennx = Integer.parseInt(val);
                        if (jennx < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start jenn x " + jennx + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe5) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int jenny = -1;
                key = "addsjy";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        jenny = Integer.parseInt(val);
                        if (jenny < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start jenn y " + jenny + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe6) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int liby = -1;
                key = "addsly";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        liby = Integer.parseInt(val);
                        if (liby < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start Libila y " + liby + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe7) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int libx = -1;
                key = "addslx";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        libx = Integer.parseInt(val);
                        if (libx < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start Libila x " + libx + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe8) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int molx = -1;
                key = "addsmx";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        molx = Integer.parseInt(val);
                        if (molx < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start Mol Rehan x " + molx + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe9) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int moly = -1;
                key = "addsmy";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        moly = Integer.parseInt(val);
                        if (moly < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for start Mol Rehan y " + moly + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe10) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int intraport = -1;
                key = "addintport";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        intraport = Integer.parseInt(val);
                        if (intraport < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for intra server port " + intraport + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe11) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                int externalport = -1;
                key = "addextport";
                val = question.getAnswer().getProperty(key);
                if (val != null) {
                    try {
                        externalport = Integer.parseInt(val);
                        if (externalport < 0) {
                            responder.getCommunicator().sendAlertServerMessage("Illegal value for external server port " + externalport + ".");
                            return;
                        }
                    }
                    catch (NumberFormatException nfe12) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                key = "addintip";
                final String intraip;
                val = (intraip = question.getAnswer().getProperty(key));
                if (val != null && val.length() < 8) {
                    responder.getCommunicator().sendAlertServerMessage("The internal ip address of the server can not be " + val + ". It is too short.");
                    return;
                }
                key = "addextip";
                val = question.getAnswer().getProperty(key);
                final String externalip;
                if ((externalip = val) != null && val.length() < 8) {
                    responder.getCommunicator().sendAlertServerMessage("The external ip address of the server can not be " + val + ". It is too short.");
                    return;
                }
                key = "addintpass";
                val = question.getAnswer().getProperty(key);
                final String password;
                if ((password = val) != null && val.length() < 4) {
                    responder.getCommunicator().sendAlertServerMessage("The password of the server can not be " + val + ". It is too short.");
                    return;
                }
                key = "addkingdom";
                val = question.getAnswer().getProperty(key);
                byte kingdom = 0;
                if (homeServer && val != null) {
                    try {
                        kingdom = Byte.parseByte(val);
                    }
                    catch (NumberFormatException nfe13) {
                        QuestionParser.logger.log(Level.WARNING, responder.getName() + " tried to parse " + val + " as int.");
                        responder.getCommunicator().sendNormalServerMessage("Illegal value for key " + key);
                        return;
                    }
                }
                String _consumerKeyToUse = "";
                String _consumerSecretToUse = "";
                String _applicationToken = "";
                String _applicationSecret = "";
                key = "consumerKeyToUse";
                val = question.getAnswer().getProperty(key);
                if (val == null) {
                    _consumerKeyToUse = val;
                }
                key = "consumerSecretToUse";
                val = question.getAnswer().getProperty(key);
                if (val == null) {
                    _consumerSecretToUse = val;
                }
                key = "applicationToken";
                val = question.getAnswer().getProperty(key);
                if (val == null) {
                    _applicationToken = val;
                }
                key = "applicationSecret";
                val = question.getAnswer().getProperty(key);
                if (val == null) {
                    _applicationSecret = val;
                }
                Servers.registerServer(id, name, homeServer, jennx, jenny, libx, liby, molx, moly, intraip, String.valueOf(intraport), password, externalip, String.valueOf(externalport), isLogin, kingdom, isPayment, _consumerKeyToUse, _consumerSecretToUse, _applicationToken, _applicationSecret, false, false, false);
                responder.getCommunicator().sendAlertServerMessage("You have successfully registered the server " + name + " and may now add it as a neighbour.");
                QuestionParser.logger.info(responder.getName() + " successfully registered the server " + name + " with ID " + id + " and may now add it as a neighbour.");
            }
        }
    }
    
    static final void parseLCMManagementQuestion(final LCMManagementQuestion question) {
        final String playerName = question.getAnswer().getProperty("name");
        final Creature performer = question.getResponder();
        if (playerName.isEmpty()) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("You didn't fill in a name.");
            }
            return;
        }
        if (question.getActionType() == 698) {
            Players.appointCA(performer, playerName);
        }
        else if (question.getActionType() == 699) {
            Players.appointCM(performer, playerName);
        }
        else if (question.getActionType() == 700) {
            Players.displayLCMInfo(performer, playerName);
        }
    }
    
    static {
        QuestionParser.logger = Logger.getLogger(QuestionParser.class.getName());
    }
}
