// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import javax.annotation.Nullable;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.effects.Effect;
import java.io.IOException;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.shared.util.TerrainUtilities;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.CreationEntry;
import com.wurmonline.server.items.AdvancedCreationEntry;
import com.wurmonline.server.items.CreationMatrix;
import com.wurmonline.server.kingdom.GuardTower;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.Item;
import java.util.Map;
import com.wurmonline.server.webinterface.WcEpicStatusReport;
import com.wurmonline.server.WurmId;
import java.util.HashSet;
import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.Players;
import com.wurmonline.server.tutorial.TriggerEffects;
import com.wurmonline.server.tutorial.Triggers2Effects;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.tutorial.Missions;
import com.wurmonline.server.tutorial.TriggerEffect;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.tutorial.Mission;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Server;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.deities.Deities;
import java.util.Iterator;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.Servers;
import java.util.ArrayList;
import com.wurmonline.server.items.ItemTemplate;
import java.util.List;
import com.wurmonline.server.creatures.ai.PathTile;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public class EpicServerStatus implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    private static final Set<EpicMission> epicMissions;
    private static final EpicMission[] emptyEpicMArr;
    private static HashMap<Integer, Integer> backupDifficultyMap;
    private final int serverId;
    private final PathTile notFoundTile;
    static final String LOAD_LOCAL_EPIC_ENTITY_MISSIONS = "SELECT * FROM EPICMISSIONS";
    private static final int BUILD_TARGET = 0;
    private static final int USE_TARGET = 1;
    private static final int USE_TILE = 2;
    private static final int USE_MANY_TILES = 3;
    private static final int USE_GUARDTOWER = 4;
    private static final int DRAIN_SETTLEMENT = 5;
    private static final int KILL_CREATURES = 6;
    private static final int SACRIFICE_ITEMS = 7;
    private static final int BUILD_COMPLEX_ITEM = 8;
    private static final int BRING_ITEM_TO_CREATURE = 9;
    private static final int SACRIFICE_CREATURES = 10;
    public static final byte TYPE_BUILDSTRUCTURE_SP = 101;
    public static final byte TYPE_BUILDSTRUCTURE_TO = 102;
    public static final byte TYPE_BUILDSTRUCTURE_SG = 103;
    public static final byte TYPE_RITUALMS_F = 104;
    public static final byte TYPE_RITUALMS_E = 105;
    public static final byte TYPE_CUTTREE_F = 106;
    public static final byte TYPE_CUTTREE_E = 107;
    public static final byte TYPE_RITUALGT = 108;
    public static final byte TYPE_SACMISSION = 109;
    public static final byte TYPE_SACITEM = 110;
    public static final byte TYPE_CREATEITEM = 111;
    public static final byte TYPE_GIVEITEM_F = 112;
    public static final byte TYPE_GIVEITEM_E = 113;
    public static final byte TYPE_SLAYCREATURE_P = 114;
    public static final byte TYPE_SLAYCREATURE_L = 115;
    public static final byte TYPE_SLAYCREATURE_H = 116;
    public static final byte TYPE_SLAYTRAITOR_P = 117;
    public static final byte TYPE_SLAYTRAITOR_L = 118;
    public static final byte TYPE_SLAYTRAITOR_H = 119;
    public static final byte TYPE_DESTROYGT = 120;
    public static final byte TYPE_SACCREATURE_P = 121;
    public static final byte TYPE_SACCREATURE_L = 122;
    public static final byte TYPE_SACCREATURE_H = 123;
    public static final byte TYPE_SLAYTOWERGUARD = 124;
    private int maxTimeSecs;
    private static EpicScenario currentScenario;
    private static final List<ItemTemplate> itemplates;
    private static HexMap valrei;
    private static final String[] missionAdjectives;
    private static final String[] missionNames;
    private static final String[] missionFor;
    private static ArrayList<EpicMission> matchingMissions;
    
    public EpicServerStatus() {
        this.notFoundTile = new PathTile(-1, -1, -1, true, 0);
        this.maxTimeSecs = 1000;
        this.serverId = Servers.localServer.id;
    }
    
    EpicServerStatus(final int server) {
        this.notFoundTile = new PathTile(-1, -1, -1, true, 0);
        this.maxTimeSecs = 1000;
        this.serverId = server;
    }
    
    public static final HexMap getValrei() {
        if (EpicServerStatus.valrei == null) {
            EpicServerStatus.valrei = new Valrei();
        }
        return EpicServerStatus.valrei;
    }
    
    static void setupMissionItemTemplates() {
        final ItemTemplate[] templates2;
        final ItemTemplate[] templates = templates2 = ItemTemplateFactory.getInstance().getTemplates();
        for (final ItemTemplate lTemplate : templates2) {
            if (lTemplate.isMissionItem() && !lTemplate.isUseOnGroundOnly() && !lTemplate.isNoTake() && !lTemplate.unique && !lTemplate.artifact && !lTemplate.isRiftLoot() && lTemplate.getTemplateId() != 737 && lTemplate.getTemplateId() != 683 && lTemplate.getTemplateId() != 1414) {
                EpicServerStatus.itemplates.add(lTemplate);
            }
        }
    }
    
    public static final void addMission(final EpicMission mission) {
        EpicServerStatus.epicMissions.add(mission);
    }
    
    public static final EpicMission getEpicMissionForMission(final int missionId) {
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.getMissionId() == missionId) {
                return em;
            }
        }
        return null;
    }
    
    public static final EpicMission getEpicMissionForEntity(final int entityId) {
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.getEpicEntityId() == entityId && em.isCurrent()) {
                return em;
            }
        }
        return null;
    }
    
    public static final EpicMission[] getEpicMissionsForKingdomTemplate(final byte kingdomTemplateId) {
        final List<EpicMission> toRet = new ArrayList<EpicMission>();
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.isCurrent()) {
                final Deity d = Deities.translateDeityForEntity(em.getEpicEntityId());
                int deityNum = -1;
                if (d != null) {
                    deityNum = d.getNumber();
                }
                if (Deities.getFavoredKingdom(deityNum) != kingdomTemplateId) {
                    continue;
                }
                toRet.add(em);
            }
        }
        if (toRet.size() > 0) {
            return toRet.toArray(new EpicMission[toRet.size()]);
        }
        return EpicServerStatus.emptyEpicMArr;
    }
    
    public static final EpicMission[] getCurrentEpicMissions() {
        final List<EpicMission> toRet = new ArrayList<EpicMission>();
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.isCurrent()) {
                toRet.add(em);
            }
        }
        if (toRet.size() > 0) {
            return toRet.toArray(new EpicMission[toRet.size()]);
        }
        return EpicServerStatus.emptyEpicMArr;
    }
    
    public static final EpicMission[] getEpicMissionsForDeity(final int deityNum) {
        final List<EpicMission> toRet = new ArrayList<EpicMission>();
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.isCurrent() && em.getEpicEntityId() == deityNum) {
                toRet.add(em);
            }
        }
        if (toRet.size() > 0) {
            return toRet.toArray(new EpicMission[toRet.size()]);
        }
        return EpicServerStatus.emptyEpicMArr;
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    public static final EpicScenario getCurrentScenario() {
        return EpicServerStatus.currentScenario;
    }
    
    public static void loadLocalEntries() {
        EpicServerStatus.logger.log(Level.INFO, "LOADING LOCAL EPIC MISSIONS");
        (EpicServerStatus.currentScenario = new EpicScenario()).loadCurrentScenario();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM EPICMISSIONS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final EpicMission mp = new EpicMission(rs.getInt("ENTITY"), rs.getInt("SCENARIO"), rs.getString("NAME"), rs.getString("SCENARIONAME"), rs.getInt("MISSION"), rs.getByte("MISSIONTYPE"), rs.getInt("DIFFICULTY"), rs.getFloat("PROGRESS"), rs.getInt("SERVERID"), rs.getLong("TSTAMP"), true, rs.getBoolean("CURRENT"));
                addMission(mp);
            }
        }
        catch (SQLException sqx) {
            EpicServerStatus.logger.log(Level.WARNING, "Failed to load epic mission.", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final int getRitualAction(final byte targetKingdom) {
        if (targetKingdom != 3) {
            return 496 + Server.rand.nextInt(5);
        }
        return 496 + Server.rand.nextInt(7);
    }
    
    public PathTile getTargetTileInProximityTo(final PathTile p, final int direction, final int sizeX, final int sizeY, final boolean isTree) {
        if (direction == 0) {
            for (int x = p.getTileX(); x < p.getTileX() + sizeX; ++x) {
                for (int y = p.getTileY() - sizeY; y < p.getTileY(); ++y) {
                    if (Server.rand.nextInt(Math.max(1, sizeX / 2)) == 0) {
                        final int tileToCheck = Zones.getTileIntForTile(x, y, 0);
                        final byte type = Tiles.decodeType(tileToCheck);
                        final byte data = Tiles.decodeData(tileToCheck);
                        final Tiles.Tile theTile = Tiles.getTile(type);
                        if (!isTree) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                        if ((theTile.isMyceliumTree() || theTile.isNormalTree()) && FoliageAge.getAgeAsByte(data) > FoliageAge.MATURE_ONE.getAgeId()) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                    }
                }
            }
        }
        if (direction == 6) {
            for (int x = p.getTileX() - sizeX; x < p.getTileX(); ++x) {
                for (int y = p.getTileY(); y < p.getTileY() + sizeY; ++y) {
                    if (Server.rand.nextInt(Math.max(1, sizeX / 2)) == 0) {
                        final int tileToCheck = Zones.getTileIntForTile(x, y, 0);
                        final byte type = Tiles.decodeType(tileToCheck);
                        final byte data = Tiles.decodeData(tileToCheck);
                        final Tiles.Tile theTile = Tiles.getTile(type);
                        if (!isTree) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                        if ((theTile.isMyceliumTree() || theTile.isNormalTree()) && FoliageAge.getAgeAsByte(data) > FoliageAge.MATURE_ONE.getAgeId()) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                    }
                }
            }
        }
        if (direction == 2) {
            for (int x = p.getTileX(); x < p.getTileX() + sizeX; ++x) {
                for (int y = p.getTileY(); y < p.getTileY() + sizeY; ++y) {
                    if (Server.rand.nextInt(Math.max(1, sizeX / 2)) == 0) {
                        final int tileToCheck = Zones.getTileIntForTile(x, y, 0);
                        final byte type = Tiles.decodeType(tileToCheck);
                        final byte data = Tiles.decodeData(tileToCheck);
                        final Tiles.Tile theTile = Tiles.getTile(type);
                        if (!isTree) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                        if ((theTile.isMyceliumTree() || theTile.isNormalTree()) && FoliageAge.getAgeAsByte(data) > FoliageAge.MATURE_ONE.getAgeId()) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                    }
                }
            }
        }
        if (direction == 4) {
            for (int x = p.getTileX(); x < p.getTileX() + sizeX; ++x) {
                for (int y = p.getTileY(); y < p.getTileY() + sizeY; ++y) {
                    if (Server.rand.nextInt(Math.max(1, sizeX / 2)) == 0) {
                        final int tileToCheck = Zones.getTileIntForTile(x, y, 0);
                        final byte type = Tiles.decodeType(tileToCheck);
                        final byte data = Tiles.decodeData(tileToCheck);
                        final Tiles.Tile theTile = Tiles.getTile(type);
                        if (!isTree) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                        if ((theTile.isMyceliumTree() || theTile.isNormalTree()) && FoliageAge.getAgeAsByte(data) > FoliageAge.MATURE_ONE.getAgeId()) {
                            return new PathTile(x, y, tileToCheck, true, 0);
                        }
                    }
                }
            }
        }
        return this.notFoundTile;
    }
    
    public static long getTileId(final int x, final int y) {
        return Tiles.getTileId(x, y, 0);
    }
    
    private final String generateNeedString(final int difficulty) {
        String needString = null;
        switch (difficulty) {
            case 1: {
                needString = " asks ";
                break;
            }
            case 2: {
                needString = " wants ";
                break;
            }
            case 3: {
                needString = " requires ";
                break;
            }
            case 4: {
                needString = " needs ";
                break;
            }
            case 5: {
                needString = " urges ";
                break;
            }
            case 6: {
                needString = " demands ";
                break;
            }
            case 7: {
                needString = " commands ";
                break;
            }
            default: {
                needString = " needs ";
                break;
            }
        }
        return needString;
    }
    
    private final void linkMission(final Mission m, final MissionTrigger trig, final TriggerEffect effect, final byte missionType, final int difficulty) {
        m.update();
        Missions.addMission(m);
        trig.setMissionRequirement(m.getId());
        trig.create();
        MissionTriggers.addMissionTrigger(trig);
        effect.setTrigger(trig.getId());
        effect.setMission(m.getId());
        effect.setStopSkillgain(false);
        effect.setStartSkillgain(false);
        effect.create();
        Triggers2Effects.addLink(trig.getId(), effect.getId(), false);
        TriggerEffects.addTriggerEffect(effect);
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.isCurrent() && em.getEpicEntityId() == (int)m.getOwnerId()) {
                em.setCurrent(false);
                em.update();
            }
        }
        final EpicMission mp = new EpicMission((int)m.getOwnerId(), EpicServerStatus.currentScenario.getScenarioNumber(), m.getName(), EpicServerStatus.currentScenario.getScenarioName(), m.getId(), missionType, difficulty, 1.0f, Servers.localServer.id, System.currentTimeMillis() + this.maxTimeSecs * 1000L, false, true);
        addMission(mp);
        Players.getInstance().sendUpdateEpicMission(mp);
    }
    
    public static final String getAreaString(final int tilex, final int tiley) {
        final StringBuilder sbuild = new StringBuilder();
        if (tiley < Zones.worldTileSizeY / 3) {
            sbuild.append("north");
        }
        else if (tiley > Zones.worldTileSizeY - Zones.worldTileSizeY / 3) {
            sbuild.append("south");
        }
        else {
            sbuild.append("center");
        }
        if (tilex < Zones.worldTileSizeX / 3) {
            sbuild.append("west");
        }
        else if (tilex > Zones.worldTileSizeX - Zones.worldTileSizeX / 3) {
            sbuild.append("east");
        }
        sbuild.append(" regions");
        return sbuild.toString();
    }
    
    private final ItemTemplate getRandomItemTemplateUsed() {
        return EpicServerStatus.itemplates.get(Server.rand.nextInt(EpicServerStatus.itemplates.size()));
    }
    
    private final MissionTrigger initializeMissionTrigger(final int epicEntityId, final String epicEntityName) {
        final MissionTrigger trig = new MissionTrigger();
        trig.setStateRequirement(0.0f);
        trig.setName(epicEntityName + "Auto" + Server.rand.nextInt());
        trig.setCreatorType((byte)2);
        trig.setCreatorName("System");
        trig.setLastModifierName(epicEntityName);
        trig.setOwnerId(epicEntityId);
        return trig;
    }
    
    private final TriggerEffect initializeTriggerEffect(final int epicEntityId, final String epicEntityName) {
        final TriggerEffect effect = new TriggerEffect();
        effect.setName(epicEntityName + "Auto" + Server.rand.nextInt());
        effect.setCreatorType((byte)2);
        effect.setCreatorName("System");
        effect.setLastModifierName(epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased. You do your part well.");
        effect.setSoundName("sound.music.song.spawn1");
        effect.setOwnerId(epicEntityId);
        return effect;
    }
    
    public static final void deleteMission(final EpicMission mission) {
        EpicServerStatus.epicMissions.remove(mission);
        mission.delete();
    }
    
    private static final void destroyLastMissionForEntity(final int epicEntityId) {
        for (final EpicMission em : EpicServerStatus.epicMissions) {
            if (em.isCurrent() && em.getEpicEntityId() == epicEntityId) {
                destroySpecificMission(em);
            }
        }
    }
    
    private static final void destroySpecificMission(final EpicMission em) {
        boolean failed = false;
        if (!em.isCompleted()) {
            failed = true;
            em.updateProgress(-1.0f);
        }
        em.setCurrent(false);
        em.update();
        if (Servers.localServer.EPIC) {
            Players.getInstance().sendUpdateEpicMission(em);
        }
        final Mission[] missions = Missions.getAllMissions();
        final MissionTrigger[] triggers = MissionTriggers.getAllTriggers();
        final TriggerEffect[] effects = TriggerEffects.getAllEffects();
        for (final Mission m : missions) {
            if (m.getCreatorType() == 2 && m.getOwnerId() == em.getEpicEntityId() && !m.isInactive()) {
                if (failed) {
                    final MissionPerformer[] allPerformers;
                    final MissionPerformer[] allperfs = allPerformers = MissionPerformed.getAllPerformers();
                    for (final MissionPerformer mp : allPerformers) {
                        final MissionPerformed thisMission = mp.getMission(m.getId());
                        if (thisMission != null) {
                            thisMission.setState(-1.0f, mp.getWurmId());
                        }
                    }
                }
                m.setInactive(true);
                for (final MissionTrigger t : triggers) {
                    if (t.getMissionRequired() == m.getId()) {
                        TriggerEffects.destroyEffectsForTrigger(t.getId());
                        t.destroy();
                    }
                }
                for (final TriggerEffect e : effects) {
                    if (e.getMissionId() == m.getId()) {
                        e.destroy();
                    }
                }
            }
        }
    }
    
    public static final void pollExpiredMissions() {
        final HashSet<Integer> deities = new HashSet<Integer>();
        for (final EpicMission m : getCurrentEpicMissions()) {
            final Mission mis = Missions.getMissionWithId(m.getMissionId());
            if (m.isCurrent()) {
                deities.add(m.getEpicEntityId());
            }
            if (m.isCurrent() && System.currentTimeMillis() > m.getExpireTime()) {
                final int dietyNum = m.getEpicEntityId();
                destroyLastMissionForEntity(dietyNum);
                final WcEpicStatusReport wce = new WcEpicStatusReport(WurmId.getNextWCCommandId(), false, dietyNum, m.getMissionType(), m.getDifficulty());
                wce.sendToLoginServer();
                storeLastMissionForEntity(m.getEpicEntityId(), m);
                if (!Servers.localServer.EPIC && dietyNum > 0 && dietyNum <= 4) {
                    final EpicServerStatus es = new EpicServerStatus();
                    if (getCurrentScenario() == null) {
                        loadLocalEntries();
                    }
                    if (getCurrentScenario() != null) {
                        es.generateNewMissionForEpicEntity(dietyNum, Deities.getDeityName(dietyNum), Math.max(1, m.getDifficulty() - 2), 604800, getCurrentScenario().getScenarioName(), getCurrentScenario().getScenarioNumber(), getCurrentScenario().getScenarioQuest(), true);
                    }
                }
            }
            else if (mis != null && !Servers.localServer.EPIC && m.isCurrent() && System.currentTimeMillis() > mis.getLastModifiedAsLong() + 302400000L && m.getMissionProgress() < 33.0f) {
                final EpicMissionEnum missionEnum = EpicMissionEnum.getMissionForType(m.getMissionType());
                if ((missionEnum.isKarmaMultProgress() || EpicMissionEnum.isRitualMission(missionEnum)) && m.getExpireTime() > System.currentTimeMillis() + 43200000L) {
                    m.setExpireTime(System.currentTimeMillis() + 43200000L);
                }
            }
        }
        final Map<Integer, String> entityMap = Deities.getEntities();
        for (final Map.Entry<Integer, String> entry : entityMap.entrySet()) {
            boolean found = false;
            for (final Integer i : deities) {
                if (entry.getKey() == (int)i) {
                    found = true;
                }
            }
            if (!found) {
                final EpicMission j = getEpicMissionForEntity(entry.getKey());
                if (j == null) {
                    continue;
                }
                destroyLastMissionForEntity(entry.getKey());
                final WcEpicStatusReport wce2 = new WcEpicStatusReport(WurmId.getNextWCCommandId(), false, entry.getKey(), j.getMissionType(), j.getDifficulty());
                wce2.sendToLoginServer();
                storeLastMissionForEntity(j.getEpicEntityId(), j);
            }
        }
    }
    
    public void generateNewMissionForEpicEntity(final int epicEntityId, final String epicEntityName, int baseDifficulty, final int maxTimeSeconds, final String scenarioNameId, final int scenarioIdentity, final String questReasonString, final boolean destroyPreviousMission) {
        int attempt = 0;
        boolean error = true;
        String creationResponse = "";
        while (error && attempt++ < 10) {
            if (baseDifficulty == -1) {
                final EpicMission em = getEpicMissionForEntity(epicEntityId);
                if (em != null) {
                    baseDifficulty = em.getDifficulty();
                }
                else {
                    baseDifficulty = 1;
                }
            }
            else if (baseDifficulty == -2 || baseDifficulty == -3) {
                final EpicEntity e = getValrei().getEntity(epicEntityId);
                if (e != null && e.getLatestMissionDifficulty() != -10L) {
                    if (baseDifficulty == -2) {
                        baseDifficulty = e.getLatestMissionDifficulty();
                        if (Server.rand.nextInt(Math.max(1, baseDifficulty)) == 0) {
                            ++baseDifficulty;
                        }
                    }
                    else {
                        baseDifficulty = e.getLatestMissionDifficulty() - 2;
                    }
                }
                else if (EpicServerStatus.backupDifficultyMap == null || !EpicServerStatus.backupDifficultyMap.containsKey(epicEntityId)) {
                    baseDifficulty = 1;
                    EpicServerStatus.logger.log(Level.WARNING, "Error getting proper difficulty of new mission for " + epicEntityName + " sent from login server. Empty backup map.");
                }
                else {
                    if (baseDifficulty == -2) {
                        baseDifficulty = EpicServerStatus.backupDifficultyMap.get(epicEntityId);
                        if (Server.rand.nextInt(Math.max(1, baseDifficulty)) == 0) {
                            ++baseDifficulty;
                        }
                    }
                    else {
                        baseDifficulty = EpicServerStatus.backupDifficultyMap.get(epicEntityId) - 1;
                    }
                    EpicServerStatus.logger.log(Level.WARNING, "Error getting proper difficulty of new mission for " + epicEntityName + " sent from login server. Used backup value.");
                }
            }
            baseDifficulty = Math.max(1, Math.min(7, baseDifficulty));
            if (!destroyPreviousMission) {
                for (final EpicMission m : getCurrentEpicMissions()) {
                    if (m.getEpicEntityId() == epicEntityId && System.currentTimeMillis() < m.getExpireTime() && m.getMissionProgress() < 100.0f) {
                        return;
                    }
                }
            }
            destroyLastMissionForEntity(epicEntityId);
            final Deity d = Deities.translateDeityForEntity(epicEntityId);
            final int deityNumber = (d == null) ? -1 : d.getNumber();
            byte favoredKingdomId = Deities.getFavoredKingdom(deityNumber);
            final boolean battlegroundServer = Servers.localServer.PVPSERVER && !Servers.localServer.HOMESERVER;
            final boolean enemyHomeServer = Servers.localServer.EPIC && Servers.localServer.HOMESERVER && Servers.localServer.KINGDOM != favoredKingdomId;
            final boolean friendlyHomeServer = Servers.localServer.HOMESERVER && !enemyHomeServer;
            EpicMissionEnum newMission = EpicMissionEnum.getRandomMission(baseDifficulty, battlegroundServer, friendlyHomeServer, enemyHomeServer);
            if (newMission == null) {
                return;
            }
            if ((newMission.getMissionType() == 121 || newMission.getMissionType() == 122 || newMission.getMissionType() == 123) && deityNumber == 4 && !Servers.localServer.EPIC && !Servers.localServer.isChaosServer()) {
                newMission = null;
            }
            if (newMission == null) {
                for (int tries = 0; newMission == null && tries < 30; ++tries) {
                    newMission = EpicMissionEnum.getRandomMission(baseDifficulty, battlegroundServer, friendlyHomeServer, enemyHomeServer);
                    if ((newMission.getMissionType() == 121 || newMission.getMissionType() == 122 || newMission.getMissionType() == 123) && deityNumber == 4 && !Servers.localServer.EPIC && !Servers.localServer.isChaosServer()) {
                        newMission = null;
                    }
                }
                if (newMission == null) {
                    EpicServerStatus.logger.warning("Failed to create new mission for " + epicEntityName + " (difficulty=" + baseDifficulty + ")");
                    return;
                }
            }
            byte targetKingdom = favoredKingdomId;
            if (friendlyHomeServer || enemyHomeServer) {
                targetKingdom = Servers.localServer.KINGDOM;
            }
            if (friendlyHomeServer) {
                favoredKingdomId = Servers.localServer.KINGDOM;
            }
            if (battlegroundServer && newMission.isEnemyTerritory()) {
                final byte[] enemyKingdoms = getEnemyKingdoms(epicEntityId, favoredKingdomId);
                if (enemyKingdoms.length <= 0) {
                    continue;
                }
                targetKingdom = enemyKingdoms[Server.rand.nextInt(enemyKingdoms.length)];
            }
            final Mission i = new Mission("System", epicEntityName);
            i.setCreatorType((byte)2);
            i.setMaxTimeSeconds(maxTimeSeconds);
            i.setOwnerId(epicEntityId);
            i.setLastModifierName(epicEntityName);
            this.maxTimeSecs = maxTimeSeconds;
            switch (newMission.getMissionType()) {
                case 101:
                case 102:
                case 103: {
                    creationResponse = this.createBuildStructureMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 104:
                case 105: {
                    creationResponse = this.createMSRitualMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 106:
                case 107: {
                    creationResponse = this.createCutTreeMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 108: {
                    creationResponse = this.createGTRitualMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 109: {
                    creationResponse = this.createMISacrificeMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 110: {
                    creationResponse = this.createGenericSacrificeMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 111: {
                    creationResponse = this.createCreateItemMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 112:
                case 113: {
                    creationResponse = this.createGiveItemMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 114:
                case 115:
                case 116: {
                    creationResponse = this.createSlayCreatureMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 117:
                case 118:
                case 119: {
                    creationResponse = this.createSlayTraitorMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 120: {
                    creationResponse = this.createDestroyGTMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 121:
                case 122:
                case 123: {
                    creationResponse = this.createSacrificeCreatureMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
                case 124: {
                    creationResponse = this.createSlayTowerGuardsMission(i, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, targetKingdom);
                    break;
                }
            }
            error = (creationResponse.contains("Error") || creationResponse.contains("error"));
        }
        EpicServerStatus.logger.log(error ? Level.WARNING : Level.INFO, creationResponse);
    }
    
    private String createBuildStructureMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        int targetItem = 712;
        switch (newMission.getMissionType()) {
            case 101: {
                if (Server.rand.nextBoolean()) {
                    targetItem = 717;
                    break;
                }
                break;
            }
            case 102: {
                if (Server.rand.nextBoolean()) {
                    targetItem = 715;
                    break;
                }
                targetItem = 714;
                break;
            }
            case 103: {
                if (Server.rand.nextBoolean()) {
                    targetItem = 713;
                    break;
                }
                targetItem = 716;
                break;
            }
        }
        try {
            m.setName(getMissionName(epicEntityName, newMission));
            m.create();
            final ItemTemplate targetTemplate = ItemTemplateFactory.getInstance().getTemplate(targetItem);
            final String actionString = Item.getMaterialString(targetTemplate.getMaterial()) + " " + targetTemplate.getName();
            final int placementLocation = EpicTargetItems.getTargetItemPlacement(m.getId());
            final String location = EpicTargetItems.getTargetItemPlacementString(placementLocation);
            final String requirement = EpicTargetItems.getInstructionStringForKingdom(targetItem, favoredKingdomId);
            final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
            effect.setDescription("Create " + actionString);
            effect.setMissionStateChange(100.0f);
            effect.setTopText("Mission complete!");
            effect.setTextDisplayed("The " + targetTemplate.getName() + " is complete! " + epicEntityName + " is pleased.");
            final String missionInstruction = epicEntityName + this.generateNeedString(baseDifficulty) + "you to construct " + targetTemplate.getNameWithGenus() + '.' + ' ' + requirement + ' ' + location;
            final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
            trig.setDescription("Create " + actionString);
            trig.setOnActionPerformed(148);
            trig.setOnItemUsedId(targetItem);
            m.setInstruction(missionInstruction.toString());
            this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
            return "Build structure mission successfully created for " + epicEntityName + ".";
        }
        catch (NoSuchTemplateException nst) {
            EpicServerStatus.logger.log(Level.WARNING, nst.getMessage());
            return "Error when creating build structure mission for " + epicEntityName + ".";
        }
    }
    
    private String createGenericRitualMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final Item ritualTargetItem, final String targetName) {
        if (ritualTargetItem == null) {
            return "Error creating generic ritual mission for " + epicEntityName + ". Null target item.";
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final String location = getAreaString(ritualTargetItem.getTileX(), ritualTargetItem.getTileY());
        final int action = getRitualAction(favoredKingdomId);
        final ActionEntry e = Actions.actionEntrys[action];
        final String actionString = "perform the " + e.getActionString();
        final int numbers = getNumberRequired(baseDifficulty, newMission);
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setDescription(actionString);
        effect.setMissionStateChange(100.0f / numbers);
        effect.setTopText("Mission complete!");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        final StringBuilder sbuild = new StringBuilder(epicEntityName + this.generateNeedString(baseDifficulty) + numbers + " of you to " + actionString + " at the " + targetName);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        if (baseDifficulty > 1) {
            final ItemTemplate it = this.getRandomItemTemplateUsed();
            trig.setOnItemUsedId(it.getTemplateId());
            sbuild.append(" using " + it.getNameWithGenus());
        }
        sbuild.append(". It is located in the " + location + ".");
        trig.setDescription(actionString);
        trig.setOnActionPerformed(action);
        trig.setOnTargetId(ritualTargetItem.getWurmId());
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Ritual mission successfully created for " + epicEntityName + ".";
    }
    
    private String createMSRitualMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        Item ritualTargetItem = null;
        int itemFindAttempts = 0;
        while (ritualTargetItem == null) {
            ritualTargetItem = EpicTargetItems.getRandomRitualTarget();
            if (ritualTargetItem != null) {
                final VolaTile t = Zones.getTileOrNull(ritualTargetItem.getTilePos(), ritualTargetItem.isOnSurface());
                if (t != null) {
                    if (t.getVillage() != null || t.getStructure() != null) {
                        ritualTargetItem = null;
                    }
                    if (t.getKingdom() != targetKingdom) {
                        ritualTargetItem = null;
                    }
                }
            }
            if (ritualTargetItem == null && ++itemFindAttempts > 50) {
                return "Error finding correct target item for ritual mission for " + epicEntityName + ".";
            }
        }
        return this.createGenericRitualMission(m, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, ritualTargetItem, ritualTargetItem.getName());
    }
    
    private String createGTRitualMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        Item ritualTargetItem = null;
        int itemFindAttempts = 0;
        String targetName = "";
        while (ritualTargetItem == null && itemFindAttempts++ < 50) {
            final GuardTower tower = Kingdoms.getRandomTowerForKingdom(targetKingdom);
            if (tower == null) {
                continue;
            }
            ritualTargetItem = tower.getTower();
            if (ritualTargetItem == null) {
                continue;
            }
            if (ritualTargetItem.getKingdom() != targetKingdom) {
                ritualTargetItem = null;
            }
            else {
                final VolaTile t = Zones.getTileOrNull(ritualTargetItem.getTilePos(), ritualTargetItem.isOnSurface());
                if (t != null && (t.getVillage() != null || t.getStructure() != null)) {
                    ritualTargetItem = null;
                }
                targetName = Kingdoms.getNameFor(targetKingdom) + " guard tower (" + tower.getName() + ")";
            }
        }
        if (ritualTargetItem == null) {
            return "Error finding correct target item for guard tower ritual mission for " + epicEntityName + ".";
        }
        return this.createGenericRitualMission(m, newMission, epicEntityId, epicEntityName, baseDifficulty, favoredKingdomId, ritualTargetItem, targetName);
    }
    
    private String createMISacrificeMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        try {
            final int required = getNumberRequired(baseDifficulty, newMission);
            final ItemTemplate usedTemplate = ItemTemplateFactory.getInstance().getTemplate(737);
            m.setName(getMissionName(epicEntityName, newMission));
            m.create();
            String itemName = HexMap.generateFirstName(m.getId()) + ' ' + HexMap.generateSecondName(m.getId());
            itemName += ((required > 1 && !itemName.endsWith("s")) ? "s" : "");
            final int action = 142;
            final String actionString = "Sacrifice";
            final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
            effect.setTopText("Mission progress");
            effect.setTextDisplayed(epicEntityName + " is pleased.");
            effect.setDescription("Sacrifice");
            effect.setMissionStateChange(100.0f / required);
            final StringBuilder sbuild = new StringBuilder(epicEntityName + this.generateNeedString(baseDifficulty) + "you to sacrifice " + required + " of the hidden " + itemName + " which can be found by investigating and digging in the wilderness.");
            final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
            trig.setDescription("Sacrifice");
            trig.setOnActionPerformed(142);
            trig.setOnTargetId(-10L);
            trig.setOnItemUsedId(usedTemplate.getTemplateId());
            m.setInstruction(sbuild.toString());
            this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
            return "Created sacrifice mission item mission for " + epicEntityName + ".";
        }
        catch (NoSuchTemplateException nst) {
            return "Error creating sacrifice mission item mission for " + epicEntityName + ". Unable to find template.";
        }
    }
    
    private String createGenericSacrificeMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        int required = getNumberRequired(baseDifficulty, newMission);
        ItemTemplate usedTemplate = null;
        final ItemTemplate[] templates = ItemTemplateFactory.getInstance().getEpicMissionTemplates();
        if (templates.length == 0) {
            return "Error creating generic sacrifice mission for " + epicEntityName + ". Failed to load templates.";
        }
        boolean found = false;
        try {
            final ItemTemplate altar = ItemTemplateFactory.getInstance().getTemplate(322);
            while (!found) {
                usedTemplate = templates[Server.rand.nextInt(templates.length)];
                if (usedTemplate.getSizeZ() <= altar.getSizeZ() && usedTemplate.getSizeY() <= altar.getSizeY() && usedTemplate.getSizeX() <= altar.getSizeX()) {
                    final CreationEntry ce = CreationMatrix.getInstance().getCreationEntry(usedTemplate.getTemplateId());
                    if (ce != null) {
                        required /= Math.min(100, ce.getTotalNumberOfItems());
                        if (ce.depleteSource || ce.depleteEqually) {
                            final CreationEntry ceSource = CreationMatrix.getInstance().getCreationEntry(ce.getObjectSource());
                            if (ceSource != null && ceSource instanceof AdvancedCreationEntry) {
                                required /= Math.min(100, ceSource.getTotalNumberOfItems());
                            }
                        }
                        if (ce.depleteTarget || ce.depleteEqually) {
                            final CreationEntry ceTarg = CreationMatrix.getInstance().getCreationEntry(ce.getObjectTarget());
                            if (ceTarg != null && ceTarg instanceof AdvancedCreationEntry) {
                                required /= Math.min(100, ceTarg.getTotalNumberOfItems());
                            }
                        }
                    }
                    found = true;
                }
            }
        }
        catch (NoSuchTemplateException nst) {
            EpicServerStatus.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        required = Math.max(required, 1);
        if (usedTemplate == null) {
            return "Error creating generic sacrifice mission for " + epicEntityName + ". Null template.";
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        String itemName = " decent " + usedTemplate.sizeString;
        itemName += ((required > 1) ? usedTemplate.getPlural() : usedTemplate.getName());
        final int action = 142;
        final String actionString = "Sacrifice";
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Sacrifice");
        effect.setMissionStateChange(100.0f / required);
        final StringBuilder sbuild = new StringBuilder(epicEntityName + this.generateNeedString(baseDifficulty) + "you to sacrifice " + required + itemName + ".");
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setDescription("Sacrifice");
        trig.setOnActionPerformed(142);
        trig.setOnTargetId(-10L);
        trig.setOnItemUsedId(usedTemplate.getTemplateId());
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Created sacrifice mission item mission for " + epicEntityName + ".";
    }
    
    private String createCutTreeMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final int action = 492;
        if (Villages.getVillages().length == 0) {
            return "Error creating cut down tree mission for " + epicEntityName + ". No villages.";
        }
        Village randVillage = null;
        int villageAttempts = 0;
        while (randVillage == null) {
            randVillage = Villages.getVillages()[Server.rand.nextInt(Villages.getVillages().length)];
            if (randVillage.kingdom != targetKingdom) {
                randVillage = null;
            }
            else {
                final byte direction = (byte)Server.rand.nextInt(8);
                final int[] position = getTileOutsideVillage(direction, randVillage.getPerimeterSize() + baseDifficulty * baseDifficulty, randVillage.getPerimeterSize() + baseDifficulty * baseDifficulty, randVillage);
                if (position[0] >= 0 && position[1] >= 0) {
                    m.setName(getMissionName(epicEntityName, newMission));
                    m.create();
                    final String dirProximityString = MiscConstants.getDirectionString(direction) + " of " + randVillage.getName();
                    final long nextTarget = getTileId(position[0], position[1]);
                    final String location = getAreaString(position[0], position[1]);
                    final int tileToCheck = Zones.getTileIntForTile(position[0], position[1], 0);
                    final Tiles.Tile theTile = Tiles.getTile(Tiles.decodeType(tileToCheck));
                    final String treeName = theTile.getTileName(Tiles.decodeData(tileToCheck));
                    final String actionString = "cut down ";
                    final byte kingdom = Zones.getKingdom(position[0], position[1]);
                    final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
                    effect.setMissionStateChange(100.0f);
                    effect.setDescription("cut down  " + treeName + " near " + randVillage.getName());
                    final StringBuilder sbuild = new StringBuilder(epicEntityName + this.generateNeedString(baseDifficulty) + "you to " + "cut down " + "the " + treeName + ' ' + dirProximityString);
                    sbuild.append(". It is located in the " + location);
                    final Kingdom k2 = Kingdoms.getKingdom(kingdom);
                    if (k2 != null) {
                        sbuild.append(" in " + k2.getName() + '.');
                    }
                    else {
                        sbuild.append('.');
                    }
                    final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
                    trig.setDescription("cut down ");
                    trig.setOnActionPerformed(492);
                    trig.setOnTargetId(nextTarget);
                    m.setInstruction(sbuild.toString());
                    try {
                        final float xTree = (position[0] << 2) + 4.0f * TerrainUtilities.getTreePosX(position[0], position[1]);
                        final float yTree = (position[1] << 2) + 4.0f * TerrainUtilities.getTreePosY(position[0], position[1]);
                        final float zTree = Zones.calculateHeight(xTree, yTree, true) + 4.0f;
                        EffectFactory.getInstance().createGenericEffect(nextTarget, "tree", xTree, yTree, zTree, true, -1.0f, Server.rand.nextInt(360));
                    }
                    catch (NoSuchZoneException e) {
                        EpicServerStatus.logger.log(Level.WARNING, "Unable to add tree effect when creating mission", e);
                    }
                    this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
                    return "Cut tree mission successfully created for " + epicEntityName + ".";
                }
                randVillage = null;
            }
            if (++villageAttempts > 30) {
                break;
            }
        }
        return "Error creating cut down tree mission for " + epicEntityName + ".";
    }
    
    private String createCreateItemMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final CreationEntry[] entries = CreationMatrix.getInstance().getAdvancedEntriesNotEpicMission();
        if (entries.length == 0) {
            return "Error creating create item mission for " + epicEntityName + ". Found no creation entries.";
        }
        int required = getNumberRequired(baseDifficulty, newMission);
        ItemTemplate usedTemplate = null;
        try {
            usedTemplate = ItemTemplateFactory.getInstance().getTemplate(entries[Server.rand.nextInt(entries.length)].getObjectCreated());
            final CreationEntry ce = CreationMatrix.getInstance().getCreationEntry(usedTemplate.getTemplateId());
            if (ce != null) {
                required /= Math.min(100, ce.getTotalNumberOfItems());
                if (ce.depleteSource || ce.depleteEqually) {
                    final CreationEntry ceSource = CreationMatrix.getInstance().getCreationEntry(ce.getObjectSource());
                    if (ceSource != null && ceSource instanceof AdvancedCreationEntry) {
                        required /= Math.min(100, ceSource.getTotalNumberOfItems());
                    }
                }
                if (ce.depleteTarget || ce.depleteEqually) {
                    final CreationEntry ceTarg = CreationMatrix.getInstance().getCreationEntry(ce.getObjectTarget());
                    if (ceTarg != null && ceTarg instanceof AdvancedCreationEntry) {
                        required /= Math.min(100, ceTarg.getTotalNumberOfItems());
                    }
                }
            }
        }
        catch (NoSuchTemplateException e) {
            EpicServerStatus.logger.log(Level.WARNING, e.getMessage(), e);
        }
        required = Math.max(required, 1);
        if (usedTemplate == null) {
            return "Error creating create item mission for " + epicEntityName + ". Null item template.";
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final int action = 148;
        final String actionString = "Create";
        String itemName = usedTemplate.sizeString;
        itemName += ((required > 1) ? usedTemplate.getPlural() : usedTemplate.getName());
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setDescription("Create");
        effect.setMissionStateChange(100.0f / required);
        final StringBuilder sbuild = new StringBuilder(epicEntityName + this.generateNeedString(baseDifficulty) + "you to create " + required + ' ' + itemName + '.');
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setDescription("Create");
        trig.setOnActionPerformed(148);
        trig.setOnItemUsedId(usedTemplate.getTemplateId());
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Create item mission created successfully for " + epicEntityName + ".";
    }
    
    private String createGiveItemMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final ItemTemplate[] templates = ItemTemplateFactory.getInstance().getEpicMissionTemplates();
        if (templates.length == 0) {
            return "Error creating give item mission for " + epicEntityName + ". Unable to load templates.";
        }
        final ItemTemplate usedTemplate = templates[Server.rand.nextInt(templates.length)];
        final int required = getNumberRequired(baseDifficulty, newMission);
        final String prefix = (required == 1 && !usedTemplate.getName().endsWith("s")) ? (usedTemplate.sizeString.equals("") ? (StringUtilities.isVowel(usedTemplate.getName().charAt(0)) ? "an " : "a ") : (StringUtilities.isVowel(usedTemplate.sizeString.charAt(0)) ? "an " : "a ")) : "";
        if (Villages.getVillages().length == 0) {
            return "Error creating give item mission for " + epicEntityName + ". No villages.";
        }
        Village randVillage = null;
        int villageAttempts = 0;
        while (randVillage == null) {
            randVillage = Villages.getVillages()[Server.rand.nextInt(Villages.getVillages().length)];
            if (randVillage.kingdom != targetKingdom) {
                randVillage = null;
            }
            else {
                final byte direction = (byte)Server.rand.nextInt(8);
                final int[] position = getTileOutsideVillage(direction, randVillage.getDiameterX() * baseDifficulty, randVillage.getDiameterY() * baseDifficulty, randVillage);
                if (position[0] < 0 || position[1] < 0) {
                    randVillage = null;
                }
                else {
                    int cid = 68;
                    if (epicEntityId == 4) {
                        cid = 81;
                    }
                    else if (epicEntityId == 3) {
                        cid = 78;
                    }
                    else if (epicEntityId == 1) {
                        cid = 80;
                    }
                    else if (epicEntityId == 2) {
                        cid = 79;
                    }
                    try {
                        final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(cid);
                        final byte sex = ct.getSex();
                        final Creature target = Creature.doNew(cid, false, (position[0] << 2) + 2, (position[1] << 2) + 2, Server.rand.nextFloat() * 360.0f, 0, "Avatar of " + epicEntityName, sex, favoredKingdomId, (byte)0, false);
                        final String proximityString = "near " + randVillage.getName();
                        final int action = 47;
                        final String actionString = "Give";
                        m.setName(getMissionName(epicEntityName, newMission));
                        m.create();
                        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
                        effect.setTopText("Mission progress");
                        effect.setTextDisplayed(epicEntityName + " is pleased.");
                        effect.setDescription("Give");
                        effect.setMissionStateChange(100.0f / required);
                        effect.setDestroysTarget(true);
                        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
                        trig.setDescription("Give");
                        trig.setOnActionPerformed(47);
                        trig.setOnTargetId(target.getWurmId());
                        trig.setOnItemUsedId(usedTemplate.getTemplateId());
                        String itemName = usedTemplate.sizeString;
                        itemName += ((required > 1) ? usedTemplate.getPlural() : usedTemplate.getName());
                        final StringBuilder sbuild = new StringBuilder();
                        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + ' ' + required + " of you to bring and give " + prefix + itemName + " to " + target.getName() + ". ");
                        sbuild.append(target.getName().substring(0, 1).toUpperCase() + target.getName().substring(1));
                        sbuild.append(" was last seen " + proximityString + " in the " + getAreaString(position[0], position[1]) + '.');
                        m.setInstruction(sbuild.toString());
                        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
                        return "Give item mission created successfully for " + epicEntityName + ".";
                    }
                    catch (Exception ex) {
                        EpicServerStatus.logger.log(Level.WARNING, ex.getMessage());
                    }
                }
            }
            if (++villageAttempts > 30) {
                break;
            }
        }
        return "Error creating give item mission for " + epicEntityName + ". Unable to find suitable village.";
    }
    
    private String createSlayCreatureMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final CreatureTemplate[] templates = CreatureTemplateFactory.getInstance().getTemplates();
        if (templates.length == 0) {
            return "Error creating slay creature mission for " + epicEntityName + ". Unable to load templates.";
        }
        final ArrayList<CreatureTemplate> possibleTemplates = new ArrayList<CreatureTemplate>();
        CreatureTemplate usedTemplate = null;
        boolean forceChamps = false;
        switch (newMission.getMissionType()) {
            case 114: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (t.isHerbivore() && !t.isBabyCreature()) {
                            possibleTemplates.add(t);
                        }
                    }
                }
                break;
            }
            case 115: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (t.isEpicMissionSlayable() && !t.isBabyCreature()) {
                            possibleTemplates.add(t);
                        }
                    }
                }
                break;
            }
            case 116: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if ((Servers.localServer.PVPSERVER && t.isFromValrei && t.getTemplateId() != 68) || (t.isEpicMissionSlayable() && t.getBaseCombatRating() > 7.0f)) {
                            possibleTemplates.add(t);
                            if (!Servers.localServer.PVPSERVER) {
                                forceChamps = true;
                            }
                        }
                    }
                }
                break;
            }
        }
        if (!possibleTemplates.isEmpty()) {
            usedTemplate = possibleTemplates.get(Server.rand.nextInt(possibleTemplates.size()));
        }
        if (usedTemplate == null) {
            return "Error creating slay creature mission for " + epicEntityName + ". Null creature template.";
        }
        final int required = getNumberRequired(baseDifficulty, newMission);
        final int action = 491;
        final String actionString = "Slay";
        int requiredSpawn = Zones.worldTileSizeX / 2048 * required;
        if (newMission.getMissionType() == 116) {
            requiredSpawn /= 2;
        }
        requiredSpawn = (int)Math.max(required * 1.5f, requiredSpawn);
        for (int i = 0; i < requiredSpawn; ++i) {
            spawnSingleCreature(usedTemplate, forceChamps);
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Slay");
        effect.setMissionStateChange(100.0f / required);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setOnItemUsedId(usedTemplate.getTemplateId());
        trig.setDescription("Slay");
        trig.setOnActionPerformed(491);
        final String creatureName = (required > 1) ? usedTemplate.getPlural() : usedTemplate.getName();
        final StringBuilder sbuild = new StringBuilder();
        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + "you to slay " + required + (forceChamps ? " champion " : " ") + creatureName + " that have appeared across the land.");
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Slay creature mission successfully created for " + epicEntityName + ".";
    }
    
    private String createSlayTraitorMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final CreatureTemplate[] templates = CreatureTemplateFactory.getInstance().getTemplates();
        if (templates.length == 0) {
            return "Error creating slay creature mission for " + epicEntityName + ". Unable to load templates.";
        }
        final ArrayList<CreatureTemplate> possibleTemplates = new ArrayList<CreatureTemplate>();
        CreatureTemplate usedTemplate = null;
        boolean forceChamps = false;
        switch (newMission.getMissionType()) {
            case 117: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (t.isHerbivore() && !t.isBabyCreature()) {
                            possibleTemplates.add(t);
                        }
                    }
                }
                break;
            }
            case 118: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (t.isEpicMissionTraitor() && !t.isBabyCreature()) {
                            possibleTemplates.add(t);
                        }
                    }
                }
                break;
            }
            case 119: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if ((Servers.localServer.PVPSERVER && t.isFromValrei) || (t.isEpicMissionTraitor() && t.getBaseCombatRating() > 7.0f)) {
                            possibleTemplates.add(t);
                            if (!Servers.localServer.PVPSERVER) {
                                forceChamps = true;
                            }
                        }
                    }
                }
                break;
            }
        }
        if (!possibleTemplates.isEmpty()) {
            usedTemplate = possibleTemplates.get(Server.rand.nextInt(possibleTemplates.size()));
        }
        if (usedTemplate == null) {
            return "Error creating slay traitor mission for " + epicEntityName + ". Null creature template.";
        }
        final int action = 491;
        final String actionString = "Slay";
        final Creature spawnedTraitor = spawnSingleCreature(usedTemplate, forceChamps);
        if (spawnedTraitor == null) {
            return "Error creating slay traitor mission for " + epicEntityName + ". Failed to create creature.";
        }
        spawnedTraitor.setName(usedTemplate.getName() + " (traitor)");
        spawnedTraitor.getStatus().setTraitBit(28, true);
        final SpellEffect eff = new SpellEffect(spawnedTraitor.getWurmId(), (byte)22, 80.0f, 20000000, (byte)9, (byte)0, true);
        if (spawnedTraitor.getSpellEffects() == null) {
            spawnedTraitor.createSpellEffects();
        }
        spawnedTraitor.getSpellEffects().addSpellEffect(eff);
        spawnedTraitor.setVisible(false);
        Zones.flash(spawnedTraitor.getTileX(), spawnedTraitor.getTileY(), false);
        spawnedTraitor.setVisible(true);
        final Effect traitorEffect = EffectFactory.getInstance().createGenericEffect(spawnedTraitor.getWurmId(), "traitor", spawnedTraitor.getPosX(), spawnedTraitor.getPosY(), spawnedTraitor.getPositionZ() + spawnedTraitor.getHalfHeightDecimeters() / 10.0f, spawnedTraitor.isOnSurface(), -1.0f, spawnedTraitor.getStatus().getRotation());
        spawnedTraitor.addEffect(traitorEffect);
        try {
            spawnedTraitor.getStatus().setChanged(true);
            spawnedTraitor.save();
        }
        catch (IOException e) {
            EpicServerStatus.logger.log(Level.WARNING, "Unable to save new traitor creature.", e);
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Slay");
        effect.setMissionStateChange(100.0f);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setOnTargetId(spawnedTraitor.getWurmId());
        trig.setDescription("Slay");
        trig.setOnActionPerformed(491);
        final StringBuilder sbuild = new StringBuilder();
        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + "you to slay the traitor" + (forceChamps ? " champion " : " ") + usedTemplate.getName() + " seen fleeing from Valrei to these lands. It was last spotted in the " + getAreaString(spawnedTraitor.getTileX(), spawnedTraitor.getTileY()) + ".");
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Slay traitor mission successfully created for " + epicEntityName + ".";
    }
    
    private String createSacrificeCreatureMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        final CreatureTemplate[] templates = CreatureTemplateFactory.getInstance().getTemplates();
        if (templates.length == 0) {
            return "Error creating sacrifice creature mission for " + epicEntityName + ". Unable to load templates.";
        }
        final ArrayList<CreatureTemplate> possibleTemplates = new ArrayList<CreatureTemplate>();
        CreatureTemplate usedTemplate = null;
        switch (newMission.getMissionType()) {
            case 121: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (!t.isSubmerged()) {
                            if (t.isHerbivore() && !t.isBabyCreature()) {
                                possibleTemplates.add(t);
                            }
                        }
                    }
                }
                break;
            }
            case 122: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (!t.isSubmerged()) {
                            if (t.isEpicMissionSlayable() && t.getBaseCombatRating() <= 7.0f && !t.isBabyCreature()) {
                                possibleTemplates.add(t);
                            }
                        }
                    }
                }
                break;
            }
            case 123: {
                for (final CreatureTemplate t : templates) {
                    if (!t.isMissionDisabled()) {
                        if (!t.isSubmerged()) {
                            if ((Servers.localServer.PVPSERVER && t.isFromValrei) || (t.isEpicMissionSlayable() && t.getBaseCombatRating() > 7.0f)) {
                                possibleTemplates.add(t);
                            }
                        }
                    }
                }
                break;
            }
        }
        if (!possibleTemplates.isEmpty()) {
            usedTemplate = possibleTemplates.get(Server.rand.nextInt(possibleTemplates.size()));
        }
        if (usedTemplate == null) {
            return "Error creating sacrifice creature mission for " + epicEntityName + ". Null creature template.";
        }
        final int required = getNumberRequired(baseDifficulty, newMission);
        final int action = 142;
        final String actionString = "Sacrifice";
        for (int requiredSpawn = Math.max(required, Zones.worldTileSizeX / 2048 * required), i = 0; i < requiredSpawn; ++i) {
            spawnSingleCreature(usedTemplate, false);
        }
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Sacrifice");
        effect.setMissionStateChange(100.0f / required);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setOnItemUsedId(-usedTemplate.getTemplateId());
        trig.setOnTargetId(-10L);
        trig.setDescription("Sacrifice");
        trig.setOnActionPerformed(142);
        final String creatureName = (required > 1) ? usedTemplate.getPlural() : usedTemplate.getName();
        final StringBuilder sbuild = new StringBuilder();
        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + "you to sacrifice " + required + " " + creatureName + " that " + ((required > 1) ? "have appeared across the land" : "has appeared in the world") + ". Use a sacrificial knife on them inside the domain of " + epicEntityName + " after weakening them to at least half of their health.");
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Sacrifice creature mission successfully created for " + epicEntityName + ".";
    }
    
    private String createSlayTowerGuardsMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        int templateId = 67;
        final Kingdom k = Kingdoms.getKingdom(targetKingdom);
        if (k == null) {
            return "Error creating slay tower guards mission for " + epicEntityName + ". Invalid kingdom.";
        }
        switch (k.getTemplate()) {
            case 3: {
                templateId = 35;
                break;
            }
            case 1: {
                templateId = 34;
                break;
            }
            case 2: {
                templateId = 36;
                break;
            }
            default: {
                return "Error creating slay tower guards mission for " + epicEntityName + ". Invalid kingdom template.";
            }
        }
        CreatureTemplate usedTemplate;
        try {
            usedTemplate = CreatureTemplateFactory.getInstance().getTemplate(templateId);
        }
        catch (NoSuchCreatureTemplateException e) {
            return "Error creating slay tower guards mission for " + epicEntityName + ". Invalid creature template.";
        }
        final int required = getNumberRequired(baseDifficulty, newMission);
        final int action = 491;
        final String actionString = "Slay";
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Slay");
        effect.setMissionStateChange(100.0f / required);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setOnItemUsedId(usedTemplate.getTemplateId());
        trig.setDescription("Slay");
        trig.setOnActionPerformed(491);
        final String creatureName = (required > 1) ? usedTemplate.getPlural() : usedTemplate.getName();
        final StringBuilder sbuild = new StringBuilder();
        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + "you to slay " + required + " " + creatureName + " to help cleanse the lands.");
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Slay tower guard mission successfully created for " + epicEntityName + ".";
    }
    
    private String createDestroyGTMission(final Mission m, final EpicMissionEnum newMission, final int epicEntityId, final String epicEntityName, final int baseDifficulty, final byte favoredKingdomId, final byte targetKingdom) {
        Item targetTower = null;
        int itemFindAttempts = 0;
        while (targetTower == null && itemFindAttempts++ < 50 && Zones.getGuardTowers().size() > 0) {
            targetTower = Zones.getGuardTowers().get(Server.rand.nextInt(Zones.getGuardTowers().size()));
            if (targetTower != null) {
                if (targetTower.getKingdom() != targetKingdom) {
                    targetTower = null;
                }
                else {
                    final VolaTile t = Zones.getTileOrNull(targetTower.getTilePos(), targetTower.isOnSurface());
                    if (t == null || (t.getVillage() == null && t.getStructure() == null)) {
                        continue;
                    }
                    targetTower = null;
                }
            }
        }
        if (targetTower == null) {
            return "Error finding correct target item for destroy guard tower mission for " + epicEntityName + ".";
        }
        final int action = 913;
        final String actionString = "Destroy";
        m.setName(getMissionName(epicEntityName, newMission));
        m.create();
        final TriggerEffect effect = this.initializeTriggerEffect(epicEntityId, epicEntityName);
        effect.setTopText("Mission progress");
        effect.setTextDisplayed(epicEntityName + " is pleased.");
        effect.setDescription("Destroy");
        effect.setMissionStateChange(100.0f);
        final MissionTrigger trig = this.initializeMissionTrigger(epicEntityId, epicEntityName);
        trig.setOnTargetId(targetTower.getWurmId());
        trig.setDescription("Destroy");
        trig.setOnActionPerformed(913);
        final StringBuilder sbuild = new StringBuilder();
        sbuild.append(epicEntityName + this.generateNeedString(baseDifficulty) + "you to destroy " + targetTower.getName() + " which can be found in the " + getAreaString(targetTower.getTileX(), targetTower.getTileY()) + ".");
        m.setInstruction(sbuild.toString());
        this.linkMission(m, trig, effect, newMission.getMissionType(), baseDifficulty);
        return "Destroy guard tower mission successfully created for " + epicEntityName + ".";
    }
    
    private static int[] getTileOutsideVillage(final byte direction, int distanceX, int distanceY, final Village v) {
        int startX = v.getTokenX();
        int startY = v.getTokenY();
        if (direction == 7 || direction == 0 || direction == 1) {
            startY = v.getStartY() - distanceY;
        }
        else if (direction == 6 || direction == 2) {
            startY = v.getStartY();
            distanceY = v.getDiameterY();
        }
        else {
            startY = v.getEndY();
        }
        if (direction == 7 || direction == 6 || direction == 5) {
            startX = v.getStartX() - distanceX;
        }
        else if (direction == 0 || direction == 4) {
            startX = v.getStartX();
            distanceX = v.getDiameterX();
        }
        else {
            startX = v.getEndX();
        }
        int tileAttempts = 0;
        int tileX = -1;
        int tileY = -1;
        while ((tileX < 0 || tileY < 0) && tileAttempts++ < 50) {
            final int tempX = startX + Server.rand.nextInt(Math.max(1, distanceX));
            final int tempY = startY + Server.rand.nextInt(Math.max(1, distanceY));
            final Village vt = Villages.getVillageWithPerimeterAt(tempX, tempY, true);
            if (vt != null) {
                continue;
            }
            final int tileToCheck = Zones.getTileIntForTile(tempX, tempY, 0);
            final byte type = Tiles.decodeType(tileToCheck);
            final byte data = Tiles.decodeData(tileToCheck);
            final Tiles.Tile theTile = Tiles.getTile(type);
            if (!theTile.isTree() || theTile.isEnchanted() || FoliageAge.getAgeAsByte(data) <= FoliageAge.MATURE_ONE.getAgeId() || FoliageAge.getAgeAsByte(data) >= FoliageAge.OVERAGED.getAgeId()) {
                continue;
            }
            tileX = tempX;
            tileY = tempY;
        }
        return new int[] { tileX, tileY };
    }
    
    public static final int getNumberRequired(final int difficulty, final EpicMissionEnum newMission) {
        final int playerMax = (int)(Servers.localServer.PVPSERVER ? (difficulty * 1.5) : (difficulty * 2.5));
        switch (newMission.getMissionType()) {
            case 101:
            case 102:
            case 103: {
                return 1;
            }
            case 104:
            case 105: {
                return Math.max(1, Math.min(difficulty * difficulty, playerMax));
            }
            case 106:
            case 107: {
                return 1;
            }
            case 108: {
                return Math.max(1, playerMax);
            }
            case 109: {
                return Math.max(1, difficulty * difficulty);
            }
            case 110:
            case 111: {
                return Math.max(1, difficulty * 75);
            }
            case 112:
            case 113: {
                return Math.max(1, Math.min(difficulty * difficulty, playerMax));
            }
            case 114:
            case 115: {
                return Math.max(1, difficulty * difficulty * (difficulty / 2) + 5);
            }
            case 116: {
                return Math.max(1, (difficulty * difficulty * difficulty + 5) / 15);
            }
            case 117:
            case 118:
            case 119: {
                return 1;
            }
            case 120: {
                return 1;
            }
            case 121:
            case 122:
            case 123: {
                return Math.max(1, (int)Math.min(difficulty * difficulty, difficulty * 2.5));
            }
            case 124: {
                return Math.max(1, difficulty * difficulty * (difficulty / 2));
            }
            default: {
                return 1;
            }
        }
    }
    
    private final PathTile getTargetVillage(final byte requiredKingdom) {
        PathTile tile = null;
        final Village[] varr = Villages.getVillages();
        final List<Village> prospects = new ArrayList<Village>();
        for (final Village lElement : varr) {
            if (lElement.kingdom == requiredKingdom) {
                prospects.add(lElement);
            }
        }
        if (prospects.size() > 0) {
            final Village prosp = prospects.get(Server.rand.nextInt(prospects.size()));
            tile = new PathTile(prosp.getTokenX(), prosp.getTokenY(), 0, true, 0);
        }
        return tile;
    }
    
    private static final byte[] getEnemyKingdoms(final int epicEntityId, final byte favoredKingdom) {
        final Set<Byte> toReturn = new HashSet<Byte>();
        if (favoredKingdom != 0) {
            final Kingdom[] allKingdoms;
            final Kingdom[] karr = allKingdoms = Kingdoms.getAllKingdoms();
            for (final Kingdom lElement : allKingdoms) {
                if (lElement.getTemplate() != favoredKingdom && !lElement.isAllied(favoredKingdom)) {
                    toReturn.add(lElement.getId());
                }
            }
        }
        final byte[] toRet = new byte[toReturn.size()];
        int x = 0;
        for (final Byte b : toReturn) {
            toRet[x] = b;
            ++x;
        }
        return toRet;
    }
    
    private static final String getMissionName(final String epicEntityName, final EpicMissionEnum newMission) {
        final StringBuilder builder = new StringBuilder();
        final boolean startWithName = Server.rand.nextBoolean();
        if (startWithName) {
            builder.append(epicEntityName);
            builder.append("'s ");
        }
        if (Server.rand.nextBoolean() && EpicServerStatus.missionAdjectives.length > 0) {
            builder.append(EpicServerStatus.missionAdjectives[Server.rand.nextInt(EpicServerStatus.missionAdjectives.length)]);
        }
        builder.append(EpicServerStatus.missionNames[Server.rand.nextInt(EpicServerStatus.missionNames.length)]);
        if (!startWithName) {
            builder.append(EpicServerStatus.missionFor[Server.rand.nextInt(EpicServerStatus.missionFor.length)]);
            builder.append(epicEntityName);
        }
        else if (Server.rand.nextInt(3) == 0) {
            if (Server.rand.nextBoolean()) {
                builder.append(" of ");
            }
            else {
                builder.append(" for ");
            }
            builder.append(newMission.getRandomMissionName());
        }
        String toret = LoginHandler.raiseFirstLetter(builder.toString());
        if (!startWithName) {
            toret = toret.replace(epicEntityName.toLowerCase(), epicEntityName);
        }
        return toret;
    }
    
    public static EpicMission getMISacrificeMission() {
        EpicServerStatus.matchingMissions.clear();
        for (final EpicMission m : getCurrentEpicMissions()) {
            if (m.getMissionType() == 109) {
                EpicServerStatus.matchingMissions.add(m);
            }
        }
        if (EpicServerStatus.matchingMissions.isEmpty()) {
            return null;
        }
        return EpicServerStatus.matchingMissions.get(Server.rand.nextInt(EpicServerStatus.matchingMissions.size()));
    }
    
    public static EpicMission[] getTraitorMissions() {
        EpicServerStatus.matchingMissions.clear();
        for (final EpicMission m : getCurrentEpicMissions()) {
            if (m.getMissionType() == 117 || m.getMissionType() == 118 || m.getMissionType() == 119) {
                EpicServerStatus.matchingMissions.add(m);
            }
        }
        if (EpicServerStatus.matchingMissions.isEmpty()) {
            return null;
        }
        return EpicServerStatus.matchingMissions.toArray(new EpicMission[EpicServerStatus.matchingMissions.size()]);
    }
    
    public static Creature[] getCurrentTraitors() {
        final EpicMission[] missions = getTraitorMissions();
        if (missions == null) {
            return null;
        }
        final ArrayList<Creature> creatureList = new ArrayList<Creature>();
        for (final EpicMission m : missions) {
            if (m.getMissionProgress() >= 1.0f) {
                if (m.getMissionProgress() < 100.0f) {
                    for (final MissionTrigger mt : MissionTriggers.getAllTriggers()) {
                        if (mt.getMissionRequired() == m.getMissionId()) {
                            final long traitorId = mt.getTarget();
                            final Creature c = Creatures.getInstance().getCreatureOrNull(traitorId);
                            if (c != null) {
                                creatureList.add(c);
                            }
                        }
                    }
                }
            }
        }
        if (creatureList.isEmpty()) {
            return null;
        }
        return creatureList.toArray(new Creature[creatureList.size()]);
    }
    
    public static boolean doesGiveItemMissionExist(final long creatureId) {
        for (final MissionTrigger mt : MissionTriggers.getAllTriggers()) {
            if (mt.getOnActionPerformed() == 47 && mt.getTarget() == creatureId) {
                final EpicMission mis = getEpicMissionForMission(mt.getMissionRequired());
                if (mis != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean doesTraitorMissionExist(final long wurmId) {
        final Creature[] cList = getCurrentTraitors();
        if (cList == null) {
            return false;
        }
        for (final Creature c : cList) {
            if (c.getWurmId() == wurmId) {
                return true;
            }
        }
        return false;
    }
    
    public static void avatarCreatureKilled(final long creatureId) {
        for (final MissionTrigger mt : MissionTriggers.getAllTriggers()) {
            if (mt.getOnActionPerformed() == 47 && mt.getTarget() == creatureId) {
                final EpicMission mis = getEpicMissionForMission(mt.getMissionRequired());
                if (mis != null) {
                    destroySpecificMission(mis);
                    final WcEpicStatusReport wce = new WcEpicStatusReport(WurmId.getNextWCCommandId(), false, mis.getEpicEntityId(), mis.getMissionType(), mis.getDifficulty());
                    wce.sendToLoginServer();
                    storeLastMissionForEntity(mis.getEpicEntityId(), mis);
                }
            }
        }
    }
    
    public static void traitorCreatureKilled(final long creatureId) {
        for (final MissionTrigger mt : MissionTriggers.getAllTriggers()) {
            if (mt.getOnActionPerformed() == 491 && mt.getTarget() == creatureId) {
                final EpicMission mis = getEpicMissionForMission(mt.getMissionRequired());
                if (mis != null) {
                    destroySpecificMission(mis);
                    final WcEpicStatusReport wce = new WcEpicStatusReport(WurmId.getNextWCCommandId(), false, mis.getEpicEntityId(), mis.getMissionType(), mis.getDifficulty());
                    wce.sendToLoginServer();
                    storeLastMissionForEntity(mis.getEpicEntityId(), mis);
                }
            }
        }
    }
    
    @Nullable
    private static Creature spawnSingleCreature(final CreatureTemplate template, final boolean champion) {
        int attempts = 0;
        while (attempts < 5000) {
            ++attempts;
            final int centerx = Server.rand.nextInt(Zones.worldTileSizeX);
            final int centery = Server.rand.nextInt(Zones.worldTileSizeY);
            for (int x = 0; x < 10; ++x) {
                final int tx = Zones.safeTileX(centerx - 5 + Server.rand.nextInt(10));
                final int ty = Zones.safeTileY(centery - 5 + Server.rand.nextInt(10));
                try {
                    final float height = Zones.calculateHeight(tx * 4 + 2, ty * 4 + 2, true);
                    if ((height >= 0.0f && !template.isSubmerged()) || (template.isSubmerged() && height < -30.0f) || (template.isSwimming() && height < -2.0f && height > -30.0f)) {
                        final VolaTile t = Zones.getOrCreateTile(tx, ty, true);
                        if (t.getStructure() == null && t.getVillage() == null) {
                            byte sex = template.getSex();
                            if (sex == 0 && !template.keepSex && Server.rand.nextBoolean()) {
                                sex = 1;
                            }
                            final byte ctype = (byte)(champion ? 99 : 0);
                            final Creature toReturn = Creature.doNew(template.getTemplateId(), false, tx * 4 + 2, ty * 4 + 2, Server.rand.nextFloat() * 360.0f, 0, template.getName(), sex, (byte)0, ctype, false, (byte)(Server.rand.nextInt(8) + 4));
                            toReturn.getStatus().setTraitBit(29, true);
                            return toReturn;
                        }
                    }
                }
                catch (Exception ex) {}
            }
        }
        return null;
    }
    
    public static void storeLastMissionForEntity(final int epicEntityId, final EpicMission em) {
        final EpicEntity entity = getValrei().getEntity(epicEntityId);
        if (entity != null) {
            entity.setLatestMissionDifficulty(em.getDifficulty());
        }
        else {
            if (EpicServerStatus.backupDifficultyMap == null) {
                EpicServerStatus.backupDifficultyMap = new HashMap<Integer, Integer>();
            }
            EpicServerStatus.backupDifficultyMap.put(epicEntityId, em.getDifficulty());
        }
    }
    
    public static EpicMission getRitualMissionForTarget(final long targetId) {
        final ArrayList<MissionTrigger> allTriggers = new ArrayList<MissionTrigger>();
        for (int actionId = 496; actionId <= 502; ++actionId) {
            final MissionTrigger[] missionTriggersWith;
            final MissionTrigger[] triggers = missionTriggersWith = MissionTriggers.getMissionTriggersWith(-1, actionId, targetId);
            for (final MissionTrigger t : missionTriggersWith) {
                allTriggers.add(t);
            }
        }
        if (allTriggers.isEmpty()) {
            return null;
        }
        return getEpicMissionForMission(allTriggers.get(0).getMissionRequired());
    }
    
    public static EpicMission getBuildMissionForTemplate(final int templateId) {
        final ArrayList<MissionTrigger> allTriggers = new ArrayList<MissionTrigger>();
        final MissionTrigger[] missionTriggersWith;
        final MissionTrigger[] triggers = missionTriggersWith = MissionTriggers.getMissionTriggersWith(templateId, 148, -1L);
        for (final MissionTrigger t : missionTriggersWith) {
            allTriggers.add(t);
        }
        if (allTriggers.isEmpty()) {
            return null;
        }
        return getEpicMissionForMission(allTriggers.get(0).getMissionRequired());
    }
    
    static {
        logger = Logger.getLogger(EpicServerStatus.class.getName());
        epicMissions = new HashSet<EpicMission>();
        emptyEpicMArr = new EpicMission[0];
        itemplates = new ArrayList<ItemTemplate>();
        setupMissionItemTemplates();
        EpicServerStatus.valrei = null;
        missionAdjectives = new String[] { "last ", "horrendous ", "shining ", "first ", "scary ", "mysterious ", "enigmatic ", "important ", "strong ", "massive ", "gigantic ", "heavy ", "light ", "bright ", "deadly ", "dangerous ", "marked ", "fantastic ", "imposing ", "paradoxical ", "final " };
        missionNames = new String[] { "whisper", "gesture", "tears", "laughter", "horror", "mystery", "enigma", "celebration", "word", "run", "challenge", "test", "jest", "joke", "need", "quest", "trip", "folly", "lesson", "journey", "adventure" };
        missionFor = new String[] { " to ", " for ", " from ", " in honour of ", " of ", " to help ", " in aid of ", " in service of " };
        EpicServerStatus.matchingMissions = new ArrayList<EpicMission>();
    }
}
