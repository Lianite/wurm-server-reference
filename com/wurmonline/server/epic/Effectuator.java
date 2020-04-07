// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.bodys.BodyHuman;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.behaviours.TerraformingTask;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.meshgen.IslandAdder;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.Servers;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public final class Effectuator implements CreatureTemplateIds, MiscConstants
{
    private static final Random rand;
    public static final int EFFECT_NONE = 0;
    public static final int EFFECT_SPEED = 1;
    public static final int EFFECT_COMBATRATING = 2;
    public static final int EFFECT_STAMINA_REGAIN = 3;
    public static final int EFFECT_FAVORGAIN = 4;
    public static final int EFFECT_SPAWN = 5;
    private static final String LOAD_KINGDOM_EFFECTS = "SELECT * FROM KINGDOMEFFECTS";
    private static final String INSERT_KINGDOM_EFFECTS = "INSERT INTO KINGDOMEFFECTS (EFFECT,KINGDOM) VALUES(?,?)";
    private static final String UPDATE_KINGDOM_EFFECTS = "UPDATE KINGDOMEFFECTS SET KINGDOM=? WHERE EFFECT=?";
    private static final String LOAD_DEITY_EFFECTS = "SELECT * FROM DEITYEFFECTS";
    private static final String INSERT_DEITY_EFFECTS = "INSERT INTO DEITYEFFECTS (EFFECT,DEITY) VALUES(?,?)";
    private static final String UPDATE_DEITY_EFFECTS = "UPDATE DEITYEFFECTS SET DEITY=? WHERE EFFECT=?";
    private static int kingdomTemplateWithSpeedBonus;
    private static int kingdomTemplateWithCombatRating;
    private static int kingdomTemplateWithStaminaRegain;
    private static int kingdomTemplateWithFavorGain;
    private static int deityWithSpeedBonus;
    private static int deityWithCombatRating;
    private static int deityWithStaminaRegain;
    private static int deityWithFavorGain;
    private static final LinkedBlockingQueue<SynchedEpicEffect> comingEvents;
    private static final Logger logger;
    
    public static String getSpiritType(final int effect) {
        String toReturn = null;
        switch (effect) {
            case 1: {
                toReturn = "fire";
                break;
            }
            case 2: {
                toReturn = "forest";
                break;
            }
            case 3: {
                toReturn = "mountain";
                break;
            }
            case 4: {
                toReturn = "water";
                break;
            }
            default: {
                toReturn = "hidden";
                break;
            }
        }
        return toReturn;
    }
    
    public static final void addEpicEffect(final SynchedEpicEffect effect) {
        Effectuator.comingEvents.add(effect);
    }
    
    public static final void pollEpicEffects() {
        for (final SynchedEpicEffect effect : Effectuator.comingEvents) {
            effect.run();
        }
        Effectuator.comingEvents.clear();
    }
    
    public static void doEvent(final int eventNum, final long deityNumber, final int creatureTemplateId, final int bonusEffectNum, final String eventDesc) {
        if (Servers.localServer.EPIC && !Servers.localServer.LOGINSERVER) {
            setEffectController(4, 0L);
            setEffectController(2, 0L);
            setEffectController(1, 0L);
            setEffectController(3, 0L);
            final byte favoredKingdom = Deities.getFavoredKingdom((int)deityNumber);
            final boolean doNegative = false;
            switch (Effectuator.rand.nextInt(7)) {
                case 0: {
                    spawnDefenders(deityNumber, creatureTemplateId);
                }
                case 1: {}
                case 2: {}
                case 3: {}
                case 4: {}
                case 5: {}
            }
        }
    }
    
    static void doEvent1(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            if (deityNumber == 5L) {
                wurmPunish(4000, 0.0f, 20.0f, (byte)6);
            }
            spawnOwnCreatures(deityNumber, 38, true);
        }
    }
    
    static void doEvent5(final long deityNumber) {
        if (Servers.localServer.EPIC && deityNumber == 5L) {
            wurmPunish(4000, 20.0f, 0.0f, (byte)5);
        }
    }
    
    static void doEvent7(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            if (deityNumber == 5L) {
                crushStructures();
            }
            else {
                final IslandAdder isl = new IslandAdder(Server.surfaceMesh, Server.rockMesh);
                isl.addOneIsland(Zones.worldTileSizeX, Zones.worldTileSizeY);
            }
        }
    }
    
    static void doEvent8(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            if (deityNumber == 5L) {
                wurmPunish(8000, 0.0f, 0.0f, (byte)9);
            }
            else {
                doEvent15(deityNumber);
            }
        }
    }
    
    static void terraform(final int task, final long deityNumber, final int nums) {
        final byte favoredKingdom = Deities.getFavoredKingdom((int)deityNumber);
        final Deity d = Deities.getDeity((int)deityNumber);
        if (d != null) {
            new TerraformingTask(task, favoredKingdom, d.getName(), (int)deityNumber, nums, true);
        }
    }
    
    static void doEvent12(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            disease(deityNumber);
        }
    }
    
    static void doEvent14(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            slay(deityNumber);
        }
    }
    
    static void doEvent15(final long deityNumber) {
        awardSkill(deityNumber, 103, 0.005f, 20.0f);
    }
    
    static void doEvent17(final long deityNumber) {
        if (Servers.localServer.EPIC) {
            if (deityNumber == 5L) {
                wurmPunish(4000, 0.0f, 0.0f, (byte)9);
            }
            else {
                awardSkill(deityNumber, 105, 0.005f, 20.0f);
            }
        }
    }
    
    static void appointAlly(final long deityNumber) {
        if (Servers.localServer.EPIC && deityNumber == 5L) {
            wurmPunish(14000, 20.0f, 20.0f, (byte)9);
        }
    }
    
    static final void promoteImmortal(final long deityNumber) {
        if (!Servers.localServer.LOGINSERVER || !HexMap.VALREI.elevateDemigod(deityNumber)) {}
    }
    
    static void doEvent20(final long deityNumber) {
        punishSkill(deityNumber, 100, 0.5f);
        punishSkill(deityNumber, 102, 0.5f);
        punishSkill(deityNumber, 106, 0.5f);
        punishSkill(deityNumber, 104, 0.5f);
        punishSkill(deityNumber, 101, 0.5f);
        punishSkill(deityNumber, 105, 0.5f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(20, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    static void doEvent21(final long deityNumber) {
        punishSkill(deityNumber, 100, 0.04f);
        punishSkill(deityNumber, 102, 0.04f);
        punishSkill(deityNumber, 106, 0.04f);
        punishSkill(deityNumber, 104, 0.04f);
        punishSkill(deityNumber, 101, 0.04f);
        punishSkill(deityNumber, 105, 0.04f);
        awardSkill(deityNumber, 100, 0.005f, 20.0f);
        awardSkill(deityNumber, 102, 0.005f, 20.0f);
        awardSkill(deityNumber, 106, 0.005f, 20.0f);
        awardSkill(deityNumber, 104, 0.005f, 20.0f);
        awardSkill(deityNumber, 101, 0.005f, 20.0f);
        awardSkill(deityNumber, 105, 0.005f, 20.0f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(10, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    static void doEvent22(final long deityNumber) {
        punishSkill(deityNumber, 100, 0.05f);
        punishSkill(deityNumber, 102, 0.05f);
        punishSkill(deityNumber, 106, 0.05f);
        punishSkill(deityNumber, 105, 0.05f);
        awardSkill(deityNumber, 100, 0.005f, 20.0f);
        awardSkill(deityNumber, 102, 0.005f, 20.0f);
        awardSkill(deityNumber, 106, 0.005f, 20.0f);
        awardSkill(deityNumber, 104, 0.005f, 20.0f);
        awardSkill(deityNumber, 101, 0.005f, 20.0f);
        awardSkill(deityNumber, 105, 0.005f, 20.0f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(10, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    static void doEvent23(final long deityNumber) {
        punishSkill(deityNumber, 100, 0.05f);
        punishSkill(deityNumber, 102, 0.05f);
        punishSkill(deityNumber, 101, 0.05f);
        punishSkill(deityNumber, 105, 0.05f);
        awardSkill(deityNumber, 100, 0.005f, 20.0f);
        awardSkill(deityNumber, 102, 0.005f, 20.0f);
        awardSkill(deityNumber, 106, 0.005f, 20.0f);
        awardSkill(deityNumber, 104, 0.005f, 20.0f);
        awardSkill(deityNumber, 101, 0.005f, 20.0f);
        awardSkill(deityNumber, 105, 0.005f, 20.0f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(10, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    static void doEvent24(final long deityNumber) {
        punishSkill(deityNumber, 103, 0.05f);
        punishSkill(deityNumber, 102, 0.05f);
        punishSkill(deityNumber, 101, 0.05f);
        punishSkill(deityNumber, 105, 0.05f);
        awardSkill(deityNumber, 100, 0.005f, 20.0f);
        awardSkill(deityNumber, 102, 0.005f, 20.0f);
        awardSkill(deityNumber, 106, 0.005f, 20.0f);
        awardSkill(deityNumber, 104, 0.005f, 20.0f);
        awardSkill(deityNumber, 101, 0.005f, 20.0f);
        awardSkill(deityNumber, 105, 0.005f, 20.0f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(10, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    static void doEvent25(final long deityNumber) {
        punishSkill(deityNumber, 103, 0.05f);
        punishSkill(deityNumber, 102, 0.05f);
        punishSkill(deityNumber, 101, 0.05f);
        punishSkill(deityNumber, 105, 0.05f);
        awardSkill(deityNumber, 100, 0.005f, 20.0f);
        awardSkill(deityNumber, 102, 0.005f, 20.0f);
        awardSkill(deityNumber, 106, 0.005f, 20.0f);
        awardSkill(deityNumber, 104, 0.005f, 20.0f);
        awardSkill(deityNumber, 101, 0.005f, 20.0f);
        awardSkill(deityNumber, 105, 0.005f, 20.0f);
        lowerFaith(deityNumber);
        for (int x = 0; x < Math.min(20, Players.getInstance().getNumberOfPlayers()); ++x) {
            slay(deityNumber);
        }
    }
    
    private static void punishSkill(final long deityNum, final int skillNum, final float toDecrease) {
    }
    
    private static void disease(final long deityNumberSaved) {
        final byte friendlyKingdom = Deities.getFavoredKingdom((int)deityNumberSaved);
        final Player[] players = Players.getInstance().getPlayers();
        for (int x = 0; x < players.length; ++x) {
            if ((friendlyKingdom == 0 || players[x].getKingdomTemplateId() != friendlyKingdom) && (players[x].getDeity() == null || players[x].getDeity().getNumber() != deityNumberSaved)) {
                players[x].getCommunicator().sendAlertServerMessage("An evil aura emanates from valrei. You suddenly feel like vomiting.");
                players[x].setDisease((byte)50);
            }
        }
    }
    
    private static void awardSkill(final long deityNum, final int skillNum, final float toIncrease, final float minNumber) {
        final byte friendlyKingdom = Deities.getFavoredKingdom((int)deityNum);
        final Player[] players = Players.getInstance().getPlayers();
        for (int x = 0; x < players.length; ++x) {
            if (players[x].getKingdomTemplateId() == friendlyKingdom) {
                try {
                    final Skill old = players[x].getSkills().getSkill(skillNum);
                    old.setKnowledge(old.getKnowledge() + (100.0 - old.getKnowledge()) * toIncrease, false);
                }
                catch (NoSuchSkillException nss) {
                    players[x].getSkills().learn(skillNum, minNumber);
                }
            }
        }
    }
    
    private static void slay(final long deityNum) {
        final Player[] players = Players.getInstance().getPlayers();
        if (deityNum == 5L) {
            boolean found = false;
            while (!found) {
                final int p = Effectuator.rand.nextInt(players.length);
                if (!players[p].isDead() && players[p].isFullyLoaded() && players[p].getVisionArea() != null) {
                    players[p].getCommunicator().sendAlertServerMessage("You feel an abnormal wave of heat coming from Valrei! Wurm has punished you!");
                    players[p].die(false, "Valrei Lazer Beams");
                    found = true;
                }
                if (!found && players.length < 5 && Effectuator.rand.nextBoolean()) {
                    return;
                }
            }
        }
        else {
            boolean found = false;
            int seeks = 0;
            final byte friendlyKingdom = Deities.getFavoredKingdom((int)deityNum);
            while (!found) {
                ++seeks;
                final int p2 = Effectuator.rand.nextInt(players.length);
                if (!players[p2].isDead() && players[p2].isFullyLoaded() && players[p2].getVisionArea() != null && players[p2].getKingdomTemplateId() != friendlyKingdom) {
                    if (players[p2].getDeity() != null && players[p2].getDeity().getNumber() != deityNum) {
                        if (deityNum != 1L || players[p2].getDeity().getNumber() != 3) {
                            if (deityNum != 3L || players[p2].getDeity().getNumber() != 1) {
                                players[p2].getCommunicator().sendAlertServerMessage("You suddenly feel yourself immolated in an abnormal wave of heat coming from Valrei!");
                                players[p2].die(false, "Valrei Bombardment");
                                found = true;
                            }
                        }
                    }
                    else {
                        players[p2].getCommunicator().sendAlertServerMessage("You suddenly feel yourself immolated in an abnormal wave of heat coming from Valrei!");
                        players[p2].die(false, "Valrei Nuclear Blast");
                        found = true;
                    }
                }
                if (!found && seeks > players.length && Effectuator.rand.nextBoolean()) {
                    return;
                }
            }
        }
    }
    
    private static void lowerFaith(final long deityNum) {
        final PlayerInfo[] infos = PlayerInfoFactory.getPlayerInfos();
        for (int x = 0; x < infos.length; ++x) {
            final byte kingdom = Players.getInstance().getKingdomForPlayer(infos[x].wurmId);
            final Kingdom k = Kingdoms.getKingdom(kingdom);
            final byte kingdomTemplateId = k.getTemplate();
            final byte favoredKingdom = Deities.getFavoredKingdom((int)deityNum);
            if (kingdomTemplateId != favoredKingdom) {
                try {
                    if (infos[x].getFaith() > 80.0f) {
                        infos[x].setFaith(infos[x].getFaith() - 1.0f);
                    }
                    else if (infos[x].getFaith() > 50.0f) {
                        infos[x].setFaith(infos[x].getFaith() - 3.0f);
                    }
                    else if (infos[x].getFaith() > 20.0f) {
                        infos[x].setFaith(infos[x].getFaith() * 0.8f);
                    }
                    infos[x].setFavor(0.0f);
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private static void wurmPunish(final int damage, final float poisondam, final float disease, final byte woundType) {
        final Player[] players = Players.getInstance().getPlayers();
        final BodyHuman body = new BodyHuman();
        for (int x = 0; x < players.length; ++x) {
            try {
                CombatEngine.addWound(null, players[x], woundType, body.getRandomWoundPos(), damage, 1.0f, "hurts", null, disease, poisondam, false, false, false, false);
            }
            catch (Exception ex) {}
        }
    }
    
    private static void crushStructures() {
        final Structure[] structures = Structures.getAllStructures();
        if (structures.length > 0) {
            structures[Effectuator.rand.nextInt(structures.length)].totallyDestroy();
        }
    }
    
    public static void spawnDefenders(final long deityId, final int creatureTemplateId) {
        if (!Servers.isThisATestServer() && (Servers.localServer.isChallengeOrEpicServer() || Servers.isThisAChaosServer())) {
            final byte friendlyKingdom = Deities.getFavoredKingdom((int)deityId);
            final Deity deity = Deities.getDeity((int)deityId);
            try {
                final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(creatureTemplateId);
                if (friendlyKingdom != 0) {
                    final Kingdom k = Kingdoms.getKingdom(friendlyKingdom);
                    if (k != null && k.lastConfrontationTileX > 1 && k.lastConfrontationTileY > 1) {
                        for (int a = 0; a < Effectuator.rand.nextInt(7) + 1; ++a) {
                            final int tx = Zones.safeTileX(k.lastConfrontationTileX - 5 + Effectuator.rand.nextInt(10));
                            final int ty = Zones.safeTileY(k.lastConfrontationTileY - 5 + Effectuator.rand.nextInt(10));
                            spawnCreatureAt(tx, ty, ctemplate, friendlyKingdom);
                        }
                    }
                }
                if (deity != null && deity.lastConfrontationTileX > 1 && deity.lastConfrontationTileY > 1) {
                    for (int a2 = 0; a2 < Effectuator.rand.nextInt(7) + 1; ++a2) {
                        final int tx2 = Zones.safeTileX(deity.lastConfrontationTileX - 5 + Effectuator.rand.nextInt(10));
                        final int ty2 = Zones.safeTileY(deity.lastConfrontationTileY - 5 + Effectuator.rand.nextInt(10));
                        spawnCreatureAt(tx2, ty2, ctemplate, friendlyKingdom);
                    }
                }
            }
            catch (NoSuchCreatureTemplateException ex) {
                Effectuator.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        else {
            Effectuator.logger.log(Level.INFO, "Spawning defenders");
        }
    }
    
    public static void spawnOwnCreatures(final long deityId, final int creatureTemplateId, final boolean onlyAtHome) {
        final byte friendlyKingdom = Deities.getFavoredKingdom((int)deityId);
        try {
            final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(creatureTemplateId);
            int summoned = 0;
            final Player[] players = Players.getInstance().getPlayers();
            int maxplayers = players.length / 10;
            int maxSummoned = (int)(200.0f / ctemplate.baseCombatRating);
            if (creatureTemplateId == 75) {
                maxplayers = 2;
                maxSummoned = 5;
            }
            if (!Servers.localServer.isChallengeOrEpicServer() && !Servers.isThisAChaosServer()) {
                return;
            }
            if (players.length > 10) {
                for (int x = 0; x < maxplayers; ++x) {
                    final int playint = Effectuator.rand.nextInt(players.length);
                    if ((players[playint].getPositionZ() > -1.0f || (ctemplate.isSwimming() && players[playint].getPositionZ() < -4.0f)) && players[playint].getKingdomTemplateId() != friendlyKingdom && !players[playint].isFriendlyKingdom(friendlyKingdom)) {
                        final int centerx = players[playint].getTileX();
                        final int centery = players[playint].getTileY();
                        for (int a = 0; a < Math.max(1.0f, 30.0f / ctemplate.baseCombatRating); ++a) {
                            final int tx = Zones.safeTileX(centerx - 5 + Effectuator.rand.nextInt(10));
                            final int ty = Zones.safeTileY(centery - 5 + Effectuator.rand.nextInt(10));
                            final VolaTile t = Zones.getOrCreateTile(tx, ty, true);
                            if (t.getStructure() == null && t.getVillage() == null) {
                                spawnCreatureAt(tx, ty, ctemplate, friendlyKingdom);
                                if (++summoned >= maxSummoned) {
                                    break;
                                }
                            }
                        }
                        if (summoned >= maxSummoned) {
                            break;
                        }
                    }
                }
            }
            if (!Servers.isThisATestServer()) {
                for (int tries = 0; summoned < maxSummoned && tries < 5000; ++summoned) {
                    ++tries;
                    final int centerx2 = Effectuator.rand.nextInt(Zones.worldTileSizeX);
                    final int centery2 = Effectuator.rand.nextInt(Zones.worldTileSizeY);
                    if ((onlyAtHome && Zones.getKingdom(centerx2, centery2) == friendlyKingdom) || Zones.getKingdom(centerx2, centery2) != friendlyKingdom) {
                        for (int x2 = 0; x2 < 10; ++x2) {
                            final int tx2 = Zones.safeTileX(centerx2 - 5 + Effectuator.rand.nextInt(10));
                            final int ty2 = Zones.safeTileY(centery2 - 5 + Effectuator.rand.nextInt(10));
                            try {
                                final float height = Zones.calculateHeight(tx2 * 4 + 2, ty2 * 4 + 2, true);
                                if (height >= 0.0f || (ctemplate.isSwimming() && height < -2.0f)) {
                                    final VolaTile t = Zones.getOrCreateTile(tx2, ty2, true);
                                    if (t.getStructure() == null && t.getVillage() == null) {
                                        spawnCreatureAt(tx2, ty2, ctemplate, friendlyKingdom);
                                        break;
                                    }
                                }
                            }
                            catch (NoSuchZoneException nsz) {
                                Effectuator.logger.log(Level.WARNING, nsz.getMessage());
                            }
                        }
                    }
                }
            }
            else {
                Effectuator.logger.log(Level.INFO, "Spawning Own creatures");
            }
        }
        catch (NoSuchCreatureTemplateException ex) {
            Effectuator.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    private static void spawnCreatureAt(final int tilex, final int tiley, final CreatureTemplate ctemplate, final byte friendlyKingdom) {
        if (ctemplate != null) {
            try {
                byte sex = ctemplate.getSex();
                if (sex == 0 && !ctemplate.keepSex && Server.rand.nextInt(2) == 0) {
                    sex = 1;
                }
                byte ctype = 0;
                final int switchi = Server.rand.nextInt(40);
                if (switchi == 0) {
                    ctype = 99;
                }
                else if (switchi == 1) {
                    ctype = 1;
                }
                else if (switchi == 2) {
                    ctype = 4;
                }
                else if (switchi == 4) {
                    ctype = 11;
                }
                Zones.flash(tilex, tiley, false);
                Creature.doNew(ctemplate.getTemplateId(), false, tilex * 4 + 2, tiley * 4 + 2, Effectuator.rand.nextFloat() * 360.0f, 0, ctemplate.getName(), sex, friendlyKingdom, ctype, false);
            }
            catch (Exception ex) {
                Effectuator.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    public static void loadEffects() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (Servers.localServer.PVPSERVER && !Servers.localServer.HOMESERVER) {
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM KINGDOMEFFECTS");
                rs = ps.executeQuery();
                int found = 0;
                while (rs.next()) {
                    final int effect = rs.getInt("EFFECT");
                    final byte kingdomId = rs.getByte("KINGDOM");
                    implementEffectControl(effect, kingdomId);
                    ++found;
                }
                if (found == 0) {
                    createEffects();
                }
            }
            catch (SQLException sqx) {
                Effectuator.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        else {
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM DEITYEFFECTS");
                rs = ps.executeQuery();
                int found = 0;
                while (rs.next()) {
                    final int effect = rs.getInt("EFFECT");
                    final int deityId = rs.getByte("DEITY");
                    implementDeityEffectControl(effect, deityId);
                    ++found;
                }
                if (found == 0) {
                    createEffects();
                }
            }
            catch (SQLException sqx) {
                Effectuator.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static void createEffects() {
        initializeEffect(3);
        initializeEffect(4);
        initializeEffect(1);
        initializeEffect(2);
    }
    
    public static int getKingdomTemplateWithSpeedBonus() {
        return Effectuator.kingdomTemplateWithSpeedBonus;
    }
    
    public static int getKingdomTemplateWithCombatRating() {
        return Effectuator.kingdomTemplateWithCombatRating;
    }
    
    public static int getKingdomTemplateWithStaminaRegain() {
        return Effectuator.kingdomTemplateWithStaminaRegain;
    }
    
    public static int getKingdomTemplateWithFavorGain() {
        return Effectuator.kingdomTemplateWithFavorGain;
    }
    
    private static void removeEffectFromPlayersWithKingdom(final int effectId, final int deityId) {
        final byte kingdomTemplate = Deities.getFavoredKingdom(deityId);
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player p : players2) {
            if (p.getKingdomTemplateId() == kingdomTemplate) {
                p.sendRemoveDeityEffectBonus(effectId);
            }
        }
    }
    
    private static void addEffectToPlayersWithKingdom(final int effectId, final int deityId) {
        final byte kingdomTemplate = Deities.getFavoredKingdom(deityId);
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player p : players2) {
            if (p.getKingdomTemplateId() == kingdomTemplate) {
                p.sendAddDeityEffectBonus(effectId);
            }
        }
    }
    
    static void implementEffectControl(final int effectId, final int kingdomTemplateId) {
        switch (effectId) {
            case 3: {
                if (Effectuator.kingdomTemplateWithStaminaRegain != 0 && Effectuator.kingdomTemplateWithStaminaRegain != kingdomTemplateId) {
                    removeEffectFromPlayersWithKingdom(effectId, kingdomTemplateId);
                }
                Effectuator.kingdomTemplateWithStaminaRegain = kingdomTemplateId;
                if (Effectuator.kingdomTemplateWithStaminaRegain != 0) {
                    addEffectToPlayersWithKingdom(3, Effectuator.kingdomTemplateWithStaminaRegain);
                    break;
                }
                break;
            }
            case 4: {
                if (Effectuator.kingdomTemplateWithFavorGain != 0 && Effectuator.kingdomTemplateWithFavorGain != kingdomTemplateId) {
                    removeEffectFromPlayersWithKingdom(effectId, kingdomTemplateId);
                }
                Effectuator.kingdomTemplateWithFavorGain = kingdomTemplateId;
                if (Effectuator.kingdomTemplateWithFavorGain != 0) {
                    addEffectToPlayersWithKingdom(4, Effectuator.kingdomTemplateWithFavorGain);
                    break;
                }
                break;
            }
            case 1: {
                if (Effectuator.kingdomTemplateWithSpeedBonus != 0 && Effectuator.kingdomTemplateWithSpeedBonus != kingdomTemplateId) {
                    removeEffectFromPlayersWithKingdom(effectId, kingdomTemplateId);
                }
                Effectuator.kingdomTemplateWithSpeedBonus = kingdomTemplateId;
                if (Effectuator.kingdomTemplateWithSpeedBonus != 0) {
                    addEffectToPlayersWithKingdom(1, Effectuator.kingdomTemplateWithSpeedBonus);
                    break;
                }
                break;
            }
            case 2: {
                if (Effectuator.kingdomTemplateWithCombatRating != 0 && Effectuator.kingdomTemplateWithCombatRating != kingdomTemplateId) {
                    removeEffectFromPlayersWithKingdom(effectId, kingdomTemplateId);
                }
                Effectuator.kingdomTemplateWithCombatRating = kingdomTemplateId;
                if (Effectuator.kingdomTemplateWithCombatRating != 0) {
                    addEffectToPlayersWithKingdom(2, Effectuator.kingdomTemplateWithCombatRating);
                    break;
                }
                break;
            }
        }
    }
    
    static void initializeEffect(final int effectId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        if (Servers.localServer.PVPSERVER && !Servers.localServer.HOMESERVER) {
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("INSERT INTO KINGDOMEFFECTS (EFFECT,KINGDOM) VALUES(?,?)");
                ps.setInt(1, effectId);
                ps.setByte(2, (byte)0);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Effectuator.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else {
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("INSERT INTO DEITYEFFECTS (EFFECT,DEITY) VALUES(?,?)");
                ps.setInt(1, effectId);
                ps.setInt(2, 0);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Effectuator.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static int getDeityWithSpeedBonus() {
        return Effectuator.deityWithSpeedBonus;
    }
    
    public static int getDeityWithCombatRating() {
        return Effectuator.deityWithCombatRating;
    }
    
    public static int getDeityWithStaminaRegain() {
        return Effectuator.deityWithStaminaRegain;
    }
    
    public static int getDeityWithFavorGain() {
        return Effectuator.deityWithFavorGain;
    }
    
    private static void removeEffectFromPlayersWithDeity(final int effectId, final int deityId) {
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player p : players2) {
            if (p.getDeity() != null && p.getDeity().number == deityId) {
                p.sendRemoveDeityEffectBonus(effectId);
            }
        }
    }
    
    private static void addEffectToPlayersWithDeity(final int effectId, final int deityId) {
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player p : players2) {
            if (p.getDeity() != null && p.getDeity().number == deityId) {
                p.sendAddDeityEffectBonus(effectId);
            }
        }
    }
    
    static void implementDeityEffectControl(final int effectId, final int deityId) {
        if (!Servers.localServer.PVPSERVER || Servers.localServer.HOMESERVER) {
            switch (effectId) {
                case 3: {
                    if (Effectuator.deityWithStaminaRegain != 0 && Effectuator.deityWithStaminaRegain != deityId) {
                        removeEffectFromPlayersWithDeity(effectId, deityId);
                    }
                    Effectuator.deityWithStaminaRegain = deityId;
                    if (Effectuator.deityWithStaminaRegain != 0) {
                        addEffectToPlayersWithDeity(3, Effectuator.deityWithStaminaRegain);
                        break;
                    }
                    break;
                }
                case 4: {
                    if (Effectuator.deityWithFavorGain != 0 && Effectuator.deityWithFavorGain != deityId) {
                        removeEffectFromPlayersWithDeity(effectId, deityId);
                    }
                    Effectuator.deityWithFavorGain = deityId;
                    if (Effectuator.deityWithFavorGain != 0) {
                        addEffectToPlayersWithDeity(4, Effectuator.deityWithFavorGain);
                        break;
                    }
                    break;
                }
                case 1: {
                    if (Effectuator.deityWithSpeedBonus != 0 && Effectuator.deityWithSpeedBonus != deityId) {
                        removeEffectFromPlayersWithDeity(effectId, deityId);
                    }
                    Effectuator.deityWithSpeedBonus = deityId;
                    if (Effectuator.deityWithSpeedBonus != 0) {
                        addEffectToPlayersWithDeity(1, Effectuator.deityWithSpeedBonus);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (Effectuator.deityWithCombatRating != 0 && Effectuator.deityWithCombatRating != deityId) {
                        removeEffectFromPlayersWithDeity(effectId, deityId);
                    }
                    Effectuator.deityWithCombatRating = deityId;
                    if (Effectuator.deityWithCombatRating != 0) {
                        addEffectToPlayersWithDeity(2, Effectuator.deityWithCombatRating);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public static void setEffectController(final int effectId, final long deityId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        if (Servers.localServer.PVPSERVER && !Servers.localServer.HOMESERVER) {
            final byte kingdomId = Deities.getFavoredKingdom((int)deityId);
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("UPDATE KINGDOMEFFECTS SET KINGDOM=? WHERE EFFECT=?");
                ps.setByte(1, kingdomId);
                ps.setInt(2, effectId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Effectuator.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            implementEffectControl(effectId, kingdomId);
        }
        else {
            try {
                dbcon = DbConnector.getDeityDbCon();
                ps = dbcon.prepareStatement("UPDATE DEITYEFFECTS SET DEITY=? WHERE EFFECT=?");
                ps.setLong(1, deityId);
                ps.setInt(2, effectId);
                ps.executeUpdate();
            }
            catch (SQLException sqx2) {
                Effectuator.logger.log(Level.WARNING, sqx2.getMessage(), sqx2);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            implementDeityEffectControl(effectId, (int)deityId);
        }
    }
    
    static {
        rand = new Random();
        Effectuator.kingdomTemplateWithSpeedBonus = 0;
        Effectuator.kingdomTemplateWithCombatRating = 0;
        Effectuator.kingdomTemplateWithStaminaRegain = 0;
        Effectuator.kingdomTemplateWithFavorGain = 0;
        Effectuator.deityWithSpeedBonus = 0;
        Effectuator.deityWithCombatRating = 0;
        Effectuator.deityWithStaminaRegain = 0;
        Effectuator.deityWithFavorGain = 0;
        comingEvents = new LinkedBlockingQueue<SynchedEpicEffect>();
        logger = Logger.getLogger(Effectuator.class.getName());
    }
}
