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
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Servers;
import com.wurmonline.server.deities.Deities;
import javax.annotation.Nullable;
import com.wurmonline.server.Features;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.webinterface.WcEpicEvent;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.webinterface.WcEpicKarmaCommand;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.MiscConstants;

public class HexMap implements MiscConstants, CreatureTemplateIds, TimeConstants
{
    private static final Logger logger;
    public static Valrei VALREI;
    public static final String VALREINAME = "Valrei";
    private static final String UPDATE_ENTITY_CONTROLLER = "UPDATE CONTROLLERS SET CONTROLLER=? WHERE CREATURE=?";
    private static final String CREATE_ENTITY_CONTROLLER = "INSERT INTO CONTROLLERS (CREATURE) VALUES (?)";
    private static final String LOAD_ENTITY_CONTROLLERS = "SELECT * FROM CONTROLLERS";
    private static final String LOAD_ALL_VISITED_HEX = "SELECT * FROM VISITED";
    static final Random rand;
    private final Map<Integer, EpicMapListener> eventListeners;
    private final Map<Integer, MapHex> hexmap;
    private final Map<Long, LinkedList<Integer>> controllers;
    private final Map<Long, EpicEntity> entities;
    private final EpicScenario currentScenario;
    private final String name;
    private static Random nameRand;
    
    HexMap(final String _name) {
        this.eventListeners = new ConcurrentHashMap<Integer, EpicMapListener>();
        this.hexmap = new ConcurrentHashMap<Integer, MapHex>();
        this.controllers = new ConcurrentHashMap<Long, LinkedList<Integer>>();
        this.entities = new ConcurrentHashMap<Long, EpicEntity>();
        this.name = _name;
        this.currentScenario = new EpicScenario();
        if (this.name.equals("Valrei")) {
            HexMap.VALREI = (Valrei)this;
        }
        else {
            HexMap.VALREI = new Valrei();
        }
    }
    
    final String getName() {
        return this.name;
    }
    
    public final MapHex getMapHex(final int id) {
        return this.hexmap.get(id);
    }
    
    final MapHex getMapHex(final Integer id) {
        return this.hexmap.get(id);
    }
    
    MapHex getSpawnHex(final EpicEntity entity) {
        for (final MapHex hm : this.hexmap.values()) {
            if (hm.isSpawnFor(entity.getId())) {
                return hm;
            }
        }
        return null;
    }
    
    final void addEntity(final EpicEntity entity) {
        if (entity.isPlayerGod()) {
            return;
        }
        this.entities.put(entity.getId(), entity);
    }
    
    void destroyEntity(final EpicEntity entity) {
        entity.dropAll(false);
        entity.setHexMap(null);
        this.removeEntity(entity);
        if (entity.getCarrier() != null) {
            entity.setCarrier(null, true, false, false);
        }
        entity.deleteEntity();
    }
    
    final void removeEntity(final EpicEntity entity) {
        this.entities.remove(entity.getId());
    }
    
    public final void loadAllEntities() {
        if (this.entities.isEmpty()) {
            this.generateEntities();
        }
    }
    
    void generateEntities() {
    }
    
    final MapHex getRandomHex() {
        final int toget = HexMap.rand.nextInt(this.hexmap.size());
        int x = 0;
        for (final MapHex hm : this.hexmap.values()) {
            if (x == toget) {
                return hm;
            }
            ++x;
        }
        return null;
    }
    
    final void addMapHex(final MapHex mh) {
        this.hexmap.put(mh.getId(), mh);
    }
    
    public final EpicEntity[] getAllEntities() {
        return this.entities.values().toArray(new EpicEntity[this.entities.size()]);
    }
    
    public final void pollAllEntities(final boolean testing) {
        final EpicEntity[] allEntities;
        final EpicEntity[] entityArr = allEntities = this.getAllEntities();
        for (final EpicEntity entity : allEntities) {
            entity.poll();
            if (testing) {
                entity.setHelped(true, false);
            }
            if (entity.checkWinCondition()) {
                break;
            }
        }
    }
    
    final int getReasonAndEffectInt() {
        return this.currentScenario.getReasonPlusEffect();
    }
    
    final void win(final EpicEntity entity, final String collName, final int nums) {
        this.setWinEffects(entity, collName, nums);
        this.checkSpecialMapWinCases(entity);
        this.nextScenario();
        WcEpicKarmaCommand.clearKarma();
        PlayerInfoFactory.resetScenarioKarma();
    }
    
    void setWinEffects(final EpicEntity entity, final String collName, final int nums) {
    }
    
    public final void setEntityHelped(final long entityId, final byte missionType, final int missionDifficulty) {
        final EpicEntity entity = this.getEntity(entityId);
        if (entity != null) {
            float current = 0.0f;
            switch (HexMap.rand.nextInt(7)) {
                case 0: {
                    current = entity.getCurrentSkill(102);
                    entity.setSkill(102, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 1: {
                    current = entity.getCurrentSkill(103);
                    entity.setSkill(103, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 2: {
                    current = entity.getCurrentSkill(104);
                    entity.setSkill(104, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 3: {
                    current = entity.getCurrentSkill(100);
                    entity.setSkill(100, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 4: {
                    current = entity.getCurrentSkill(101);
                    entity.setSkill(101, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 5: {
                    current = entity.getCurrentSkill(105);
                    entity.setSkill(105, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
                case 6: {
                    current = entity.getCurrentSkill(106);
                    entity.setSkill(106, current + (100.0f - current) / (500 + (7 - missionDifficulty) * 50 + HexMap.rand.nextFloat() * ((7 - missionDifficulty) * 50)));
                    break;
                }
            }
            entity.setHelped(true, false);
            final long timeToLeave = entity.modifyTimeToLeave(-EpicMissionEnum.getTimeReductionForMission(missionType, missionDifficulty));
            if (timeToLeave < System.currentTimeMillis()) {
                final int effect = Server.rand.nextInt(4) + 1;
                final WcEpicEvent wce = new WcEpicEvent(WurmId.getNextWCCommandId(), effect, entity.getId(), 0, 3, entity.getName() + "s followers now have the attention of the " + Effectuator.getSpiritType(effect) + " spirits.", false);
                wce.sendFromLoginServer();
                if (HexMap.rand.nextInt(20) == 0) {
                    final int template = 72 + Server.rand.nextInt(6);
                    this.setCreatureController(template, entity.getId());
                    try {
                        final CreatureTemplate c = CreatureTemplateFactory.getInstance().getTemplate(template);
                        this.broadCast(entity.getName() + " now controls the " + c.getName() + "s.");
                    }
                    catch (NoSuchCreatureTemplateException nst) {
                        HexMap.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
            entity.setShouldCreateMission(true, true);
        }
    }
    
    void checkSpecialMapWinCases(final EpicEntity winner) {
    }
    
    void nextScenario() {
        this.currentScenario.saveScenario(false);
    }
    
    final boolean winCondition(final boolean isWurm, final int currentCollectibles, final boolean isAtSpawn, final int currentHex) {
        if ((!this.currentScenario.isSpawnPointRequiredToWin() && currentHex == this.currentScenario.getHexNumRequiredToWin()) || (isAtSpawn && this.currentScenario.isSpawnPointRequiredToWin())) {
            if (isWurm) {
                if (currentCollectibles >= this.currentScenario.getCollectiblesForWurmToWin()) {
                    return true;
                }
            }
            else if (currentCollectibles >= this.currentScenario.getCollectiblesToWin()) {
                return true;
            }
        }
        return false;
    }
    
    public void addDemigod(final String _name, final long id, final long companion, final float initialBStr, final float initialBSta, final float initialBCon, final float initialML, final float initialMS, final float initialSS, final float initialSD) {
        final EpicEntity newDemi = new EpicEntity(this, id, _name, 7, -1.0f, -1.0f);
        boolean foundHex = false;
        while (!foundHex) {
            final MapHex hex = this.getRandomHex();
            if (hex != null && !hex.isSpawn()) {
                hex.setHomeEntityId(id);
                hex.setSpawnEntityId(id);
                newDemi.createEntity(hex.getId());
                foundHex = true;
                HexMap.logger.log(Level.INFO, _name + " will spawn " + hex.getPrepositionString() + " " + hex.getName());
            }
        }
        newDemi.addSkill(102, initialBStr);
        newDemi.addSkill(103, initialBSta);
        newDemi.addSkill(104, initialBCon);
        newDemi.addSkill(100, initialML);
        newDemi.addSkill(101, initialMS);
        newDemi.addSkill(105, initialSS);
        newDemi.addSkill(106, initialSD);
        newDemi.createAndSaveSkills();
        if (companion != 0L) {
            final EpicEntity compa = this.getEntity(companion);
            if (compa != null) {
                newDemi.setCompanion(compa);
                compa.setDemigodPlusForEntity((byte)(compa.getDemigodsToAppoint() - 1));
            }
        }
        newDemi.spawn();
        if (Features.Feature.VALREI_MAP.isEnabled()) {
            ValreiMapData.updateFromEpicEntity(newDemi);
            ValreiMapData.lastPolled = System.currentTimeMillis() - 1860000L;
            ValreiMapData.lastUpdatedTime = System.currentTimeMillis() - 2460000L;
        }
    }
    
    EpicEntity getDemiGodFor(final EpicEntity entity) {
        for (final EpicEntity e : this.entities.values()) {
            if (e.isDemigod() && e.getCompanion() == entity) {
                return e;
            }
        }
        return null;
    }
    
    public boolean elevateDemigod(final long deityNum) {
        return this.elevateDemigod(deityNum, null);
    }
    
    public boolean elevateDemigod(final long deityNum, @Nullable final String name) {
        final EpicEntity god = this.getEntity(deityNum);
        HexMap.logger.log(Level.INFO, "Checking elev for " + deityNum);
        if (god == null) {
            return false;
        }
        HexMap.logger.log(Level.INFO, "Checking elev at 2 for " + god.getId());
        final EpicEntity e = this.getDemiGodFor(god);
        if (e == null) {
            return false;
        }
        HexMap.logger.log(Level.INFO, "Found entity demigod " + e.getName() + ". Number is " + e.getId() + ".");
        if (name == null || e.getName().toLowerCase().equals(name.toLowerCase())) {
            final Deity d = Deities.getDeity((int)e.getId());
            HexMap.logger.log(Level.INFO, "Setting deity power " + d.getName() + " id=" + d.number);
            d.setPower((byte)3);
            e.setType(0);
            float rest = e.getInitialAttack() - 6.0f;
            float att = Math.min(6.0f, e.getInitialAttack());
            if (rest > 0.0f) {
                att += rest / 10.0f;
            }
            rest = e.getInitialVitality() - 6.0f;
            float vit = Math.min(6.0f, e.getInitialVitality());
            if (rest > 0.0f) {
                vit += rest / 10.0f;
            }
            Servers.ascend(d.getNumber(), d.name, e.getId(), (byte)deityNum, d.sex, (byte)3, e.getCurrentSkill(102), e.getCurrentSkill(103), e.getCurrentSkill(104), e.getCurrentSkill(100), e.getCurrentSkill(101), e.getCurrentSkill(105), e.getCurrentSkill(106));
            return true;
        }
        return false;
    }
    
    void generateRandomScenario() {
        EpicEntity.toggleXmlDump(false);
        this.destroyCollectables();
        this.destroySources();
        this.respawnEntities();
        final int maxCollectables = 1 + HexMap.rand.nextInt(10);
        final int reasonAndEffect = this.getRandomReason();
        boolean spawnPoint = HexMap.rand.nextBoolean();
        final String firstPartOfName = generateFirstName();
        final String secondPartOfName = generateSecondName();
        final EpicEntity questHolder = this.getRandomEntityMonster();
        int hexNum = 0;
        if (!spawnPoint) {
            hexNum = HexMap.rand.nextInt(this.hexmap.size()) + 1;
        }
        if (reasonAndEffect == 15) {
            hexNum = 5;
            spawnPoint = false;
        }
        String missionName = "";
        final String instigator = this.getRandomInstigator();
        final String hide = this.generateHideWord();
        final String reasonString = this.getReason(reasonAndEffect, maxCollectables > 1);
        String missionDescription = instigator + ' ' + hide + " the " + firstPartOfName + ' ' + secondPartOfName + '.' + ' ' + reasonString;
        if (maxCollectables == 1) {
            missionName = this.generateMissionName(firstPartOfName + ' ' + secondPartOfName, questHolder);
            if (questHolder != null && HexMap.rand.nextBoolean()) {
                missionDescription = instigator + ' ' + hide + ' ' + questHolder.getName() + "'s " + firstPartOfName + ' ' + secondPartOfName + '.' + ' ' + reasonString;
            }
        }
        else {
            if (HexMap.rand.nextBoolean()) {
                missionName = this.generateMissionName(firstPartOfName + ' ' + secondPartOfName + "s", questHolder);
            }
            else {
                missionName = this.generateMissionName(secondPartOfName + "s", questHolder);
            }
            if (HexMap.rand.nextBoolean()) {
                missionDescription = instigator + ' ' + hide + ' ' + questHolder.getName() + "'s " + getNameForNumber(maxCollectables) + ' ' + firstPartOfName + ' ' + secondPartOfName + "s" + '.' + ' ' + reasonString;
            }
            else {
                missionDescription = instigator + ' ' + hide + " the " + firstPartOfName + ' ' + secondPartOfName + "s" + '.' + ' ' + reasonString;
            }
        }
        missionDescription = missionDescription + ' ' + this.getMapSpecialWinEffect();
        final int srcfrags = 2 + HexMap.rand.nextInt(4);
        this.generateCollectables(maxCollectables, firstPartOfName + ' ' + secondPartOfName, 2);
        this.generateCollectables(srcfrags, "Source " + generateSecondName(), 1);
        this.setWinCondition(Math.max(1, maxCollectables / 2), maxCollectables, spawnPoint, hexNum, missionName, missionDescription, reasonAndEffect);
        EpicEntity.toggleXmlDump(true);
        EpicXmlWriter.dumpEntities(this);
    }
    
    String getMapSpecialWinEffect() {
        return "";
    }
    
    int getRandomReason() {
        int num = HexMap.rand.nextInt(21);
        if (num == 20) {
            num = 20 + HexMap.rand.nextInt(5);
        }
        return num;
    }
    
    String getReason(final int reasonId, final boolean many) {
        return "Those are dangerous!";
    }
    
    String getRandomInstigator() {
        final int r = HexMap.rand.nextInt(20);
        String firstPart = null;
        switch (r) {
            case 0: {
                firstPart = "The Morbid One";
                break;
            }
            case 1: {
                firstPart = "The vengeful Sea Spirits";
                break;
            }
            case 2: {
                firstPart = "The mischievous Forest Spirits";
                break;
            }
            case 3: {
                firstPart = "The immobile Frozen One";
                break;
            }
            case 4: {
                firstPart = "The unfathomable Stargazer";
                break;
            }
            case 5: {
                firstPart = "The mysterious Drakespirit";
                break;
            }
            case 6: {
                firstPart = "The evil Deathcrawler";
                break;
            }
            case 7: {
                firstPart = "Ethereal thunderstorms";
                break;
            }
            case 8: {
                firstPart = "An emissary from the void";
                break;
            }
            case 9: {
                firstPart = "A deadly starburst";
                break;
            }
            case 10: {
                firstPart = "A heavy chaos eruption";
                break;
            }
            case 11: {
                firstPart = "An unnatural meteor storm";
                break;
            }
            case 12: {
                firstPart = "A sudden surge in source energy";
                break;
            }
            case 13: {
                firstPart = "A physical storm of emotions";
                break;
            }
            case 14: {
                firstPart = "A quake of world-shattering proportions";
                break;
            }
            case 15: {
                firstPart = "The Shift";
                break;
            }
            case 16: {
                firstPart = "An eruption of Fire Spirits from Firejaw";
                break;
            }
            case 17: {
                firstPart = "Uttacha who left her depths in desperation";
                break;
            }
            case 18: {
                firstPart = "A portal to Seris opened. The dead souls";
                break;
            }
            case 19: {
                firstPart = "Demons from Sol";
                break;
            }
            default: {
                firstPart = "";
                HexMap.logger.warning("Somehow rand.nextInt(20) returned an int that was not between 0 and 19");
                break;
            }
        }
        return firstPart;
    }
    
    EpicEntity getRandomEntityMonster() {
        final EpicEntity[] allArr = this.getAllEntities();
        final LinkedList<EpicEntity> mons = new LinkedList<EpicEntity>();
        for (final EpicEntity ep : allArr) {
            if (ep.isWurm() || ep.isDeity() || ep.isSentinelMonster() || ep.isAlly()) {
                mons.add(ep);
            }
        }
        if (mons.size() > 0) {
            return mons.get(HexMap.rand.nextInt(mons.size()));
        }
        return null;
    }
    
    String generateHideWord() {
        final int r = HexMap.rand.nextInt(14);
        String secondPart = null;
        switch (r) {
            case 0: {
                secondPart = "hid";
                break;
            }
            case 1: {
                secondPart = "scattered";
                break;
            }
            case 2: {
                secondPart = "dispersed";
                break;
            }
            case 3: {
                secondPart = "dug down";
                break;
            }
            case 4: {
                secondPart = "brought";
                break;
            }
            case 5: {
                secondPart = "stole";
                break;
            }
            case 6: {
                secondPart = "dropped";
                break;
            }
            case 7: {
                secondPart = "misplaced";
                break;
            }
            case 8: {
                secondPart = "invented";
                break;
            }
            case 9: {
                secondPart = "created";
                break;
            }
            case 10: {
                secondPart = "spread out";
                break;
            }
            case 11: {
                secondPart = "revealed the existance of";
                break;
            }
            case 12: {
                secondPart = "rained";
                break;
            }
            case 13: {
                secondPart = "separated";
                break;
            }
            default: {
                secondPart = "";
                HexMap.logger.warning("Somehow rand.nextInt(14) returned an int that was not between 0 and 13");
                break;
            }
        }
        final int prep = HexMap.rand.nextInt(4);
        if (prep == 0) {
            return "has " + secondPart;
        }
        if (prep == 1) {
            return "just " + secondPart;
        }
        if (prep == 2) {
            return "recently " + secondPart;
        }
        return secondPart;
    }
    
    String generateMissionName(final String firstPart, final EpicEntity questHolder) {
        String monsterName;
        if (questHolder != null) {
            monsterName = questHolder.getName();
        }
        else {
            monsterName = "The Spirits";
        }
        final int r = HexMap.rand.nextInt(16);
        String secondPart = null;
        switch (r) {
            case 0: {
                secondPart = "Hunt for the " + firstPart;
                break;
            }
            case 1: {
                secondPart = "Looking for " + firstPart;
                break;
            }
            case 2: {
                secondPart = "The lost " + firstPart;
                break;
            }
            case 3: {
                secondPart = "Quest of the " + firstPart;
                break;
            }
            case 4: {
                secondPart = "Revenge of " + monsterName;
                break;
            }
            case 5: {
                secondPart = monsterName + "'s hunt";
                break;
            }
            case 6: {
                secondPart = monsterName + " lost";
                break;
            }
            case 7: {
                secondPart = firstPart + " lost";
                break;
            }
            case 8: {
                secondPart = monsterName + " in peril";
                break;
            }
            case 9: {
                secondPart = monsterName + "'s mystery";
                break;
            }
            case 10: {
                secondPart = monsterName + " fall";
                break;
            }
            case 11: {
                secondPart = "The missing " + firstPart;
                break;
            }
            case 12: {
                secondPart = "Lost the " + firstPart;
                break;
            }
            case 13: {
                secondPart = "Who hid the " + firstPart;
                break;
            }
            case 14: {
                secondPart = monsterName + "'s " + firstPart;
                break;
            }
            case 15: {
                secondPart = monsterName + " and the " + firstPart;
                break;
            }
            default: {
                secondPart = "";
                HexMap.logger.warning("Somehow rand.nextInt(16) returned an int that was not between 0 and 15");
                break;
            }
        }
        return secondPart;
    }
    
    final void setWinCondition(final int collectiblesRequired, final int collectiblesRequiredForWurm, final boolean atSpawnPointRequired, final int hexNumRequired, final String newScenarioName, final String newScenarioQuest, final int reasonAndEffect) {
        this.currentScenario.setCollectiblesToWin(collectiblesRequired);
        this.currentScenario.setCollectiblesForWurmToWin(collectiblesRequiredForWurm);
        this.currentScenario.setSpawnPointRequiredToWin(atSpawnPointRequired);
        if (this.currentScenario.isSpawnPointRequiredToWin()) {
            this.currentScenario.setHexNumRequiredToWin(0);
        }
        else {
            this.currentScenario.setHexNumRequiredToWin(hexNumRequired);
        }
        this.currentScenario.setScenarioName(newScenarioName);
        this.currentScenario.setScenarioQuest(newScenarioQuest);
        this.currentScenario.setReasonPlusEffect(reasonAndEffect);
        HexMap.logger.log(Level.INFO, this.currentScenario.getScenarioName() + ':');
        HexMap.logger.log(Level.INFO, this.currentScenario.getScenarioQuest());
        this.currentScenario.saveScenario(true);
    }
    
    public final void broadCast(final String event) {
        for (final EpicMapListener listener : this.eventListeners.values()) {
            listener.broadCastEpicEvent(event);
        }
        HexMap.logger.log(Level.INFO, event);
    }
    
    final EpicScenario getCurrentScenario() {
        return this.currentScenario;
    }
    
    final String getScenarioQuestString() {
        return this.currentScenario.getScenarioQuest();
    }
    
    final String getScenarioName() {
        return this.currentScenario.getScenarioName();
    }
    
    final void addAttackTo(final long entityId, final float addedValue) {
        final EpicEntity entity = this.entities.get(entityId);
        if (entity != null) {
            entity.setAttack(entity.getAttack() + addedValue);
        }
    }
    
    final void addVitalityTo(final long entityId, final float addedValue) {
        final EpicEntity entity = this.entities.get(entityId);
        if (entity != null) {
            entity.setVitality(entity.getVitality() + addedValue);
        }
    }
    
    public final EpicEntity getEntity(final long eid) {
        return this.entities.get(eid);
    }
    
    final void addEntity(final String newEntityName, final long newid, final float attack, final float vitality, final long masterId, final int deityType) {
        final EpicEntity newent = new EpicEntity(this, newid, newEntityName, deityType, attack, vitality);
        final EpicEntity masterEntity = this.entities.get(masterId);
        if (masterEntity != null) {
            final MapHex mh = this.getSpawnHex(masterEntity);
            final MapHex newSpawn = this.getMapHex(mh.getId() + ((masterId == 3L) ? 2 : 1));
            newSpawn.setSpawnEntityId(newid);
            newent.setCompanion(masterEntity);
            this.broadCast(newEntityName + " has joined the side of " + masterEntity.getName() + " on " + this.name + '.');
            this.broadCast(newEntityName + " set up home " + newSpawn.getPrepositionString() + newSpawn.getName() + '.');
        }
        else {
            boolean searching = true;
            while (searching) {
                HexMap.logger.log(Level.INFO, "Looking for free spawnpoint for " + newEntityName);
                final MapHex mh2 = this.getRandomHex();
                if (!mh2.isSpawn()) {
                    mh2.setSpawnEntityId(newid);
                    searching = false;
                    this.broadCast(newEntityName + " has entered " + this.name + '.');
                    this.broadCast(newEntityName + " set up home " + mh2.getPrepositionString() + mh2.getName() + '.');
                }
            }
        }
    }
    
    final void broadCastEpicWinCondition(final String _scenarioname, final String _scenarioQuest) {
        for (final EpicMapListener listener : this.eventListeners.values()) {
            listener.broadCastEpicWinCondition(_scenarioname, _scenarioQuest);
        }
    }
    
    public final void removeListener(final EpicMapListener listener) {
        this.eventListeners.remove(listener.hashCode());
    }
    
    public final void addListener(final EpicMapListener listener) {
        this.eventListeners.put(listener.hashCode(), listener);
    }
    
    public static final String generateFirstName(final int randId) {
        HexMap.nameRand.setSeed(randId);
        return getFirstNameForNumber(HexMap.nameRand.nextInt(20));
    }
    
    public static final String generateFirstName() {
        final int r = HexMap.rand.nextInt(20);
        return getFirstNameForNumber(r);
    }
    
    static final String getFirstNameForNumber(final int r) {
        String firstPart = null;
        switch (r) {
            case 0: {
                firstPart = "Golden";
                break;
            }
            case 1: {
                firstPart = "Frozen";
                break;
            }
            case 2: {
                firstPart = "Silvery";
                break;
            }
            case 3: {
                firstPart = "Ornamented";
                break;
            }
            case 4: {
                firstPart = "Shiny";
                break;
            }
            case 5: {
                firstPart = "Beautiful";
                break;
            }
            case 6: {
                firstPart = "Burning";
                break;
            }
            case 7: {
                firstPart = "Fire";
                break;
            }
            case 8: {
                firstPart = "Glowing";
                break;
            }
            case 9: {
                firstPart = "Lustrous";
                break;
            }
            case 10: {
                firstPart = "Charming";
                break;
            }
            case 11: {
                firstPart = "Deadly";
                break;
            }
            case 12: {
                firstPart = "Wild";
                break;
            }
            case 13: {
                firstPart = "Soulstruck";
                break;
            }
            case 14: {
                firstPart = "Black";
                break;
            }
            case 15: {
                firstPart = "Shadow";
                break;
            }
            case 16: {
                firstPart = "Rotten";
                break;
            }
            case 17: {
                firstPart = "Marble";
                break;
            }
            case 18: {
                firstPart = "Powerful";
                break;
            }
            case 19: {
                firstPart = "Holy";
                break;
            }
            default: {
                firstPart = "";
                HexMap.logger.warning("Method argument was an int that was not between 0 and 19");
                break;
            }
        }
        return firstPart;
    }
    
    static final String getNameForNumber(final int number) {
        String numString = null;
        switch (number) {
            case 0: {
                numString = "zero";
                break;
            }
            case 1: {
                numString = "one";
                break;
            }
            case 2: {
                numString = "two";
                break;
            }
            case 3: {
                numString = "three";
                break;
            }
            case 4: {
                numString = "four";
                break;
            }
            case 5: {
                numString = "five";
                break;
            }
            case 6: {
                numString = "six";
                break;
            }
            case 7: {
                numString = "seven";
                break;
            }
            case 8: {
                numString = "eight";
                break;
            }
            case 9: {
                numString = "nine";
                break;
            }
            case 10: {
                numString = "ten";
                break;
            }
            case 11: {
                numString = "eleven";
                break;
            }
            case 12: {
                numString = "twelve";
                break;
            }
            case 13: {
                numString = "thirteen";
                break;
            }
            case 14: {
                numString = "fourteen";
                break;
            }
            case 15: {
                numString = "fifteen";
                break;
            }
            case 16: {
                numString = "sixteen";
                break;
            }
            case 17: {
                numString = "seventeen";
                break;
            }
            case 18: {
                numString = "eighteen";
                break;
            }
            case 19: {
                numString = "nineteen";
                break;
            }
            case 20: {
                numString = "twenty";
                break;
            }
            default: {
                numString = number + "";
                break;
            }
        }
        return numString;
    }
    
    public static final String generateSecondName(final int randId) {
        HexMap.nameRand.setSeed(randId);
        return getSecondNameForNumber(HexMap.nameRand.nextInt(20));
    }
    
    public static final String generateSecondName() {
        final int r = HexMap.rand.nextInt(20);
        return getSecondNameForNumber(r);
    }
    
    static final String getSecondNameForNumber(final int r) {
        String secondPart = null;
        switch (r) {
            case 0: {
                secondPart = "Feather";
                break;
            }
            case 1: {
                secondPart = "Token";
                break;
            }
            case 2: {
                secondPart = "Totem";
                break;
            }
            case 3: {
                secondPart = "Crystal";
                break;
            }
            case 4: {
                secondPart = "Shard";
                break;
            }
            case 5: {
                secondPart = "Opal";
                break;
            }
            case 6: {
                secondPart = "Diamond";
                break;
            }
            case 7: {
                secondPart = "Fragment";
                break;
            }
            case 8: {
                secondPart = "Jar";
                break;
            }
            case 9: {
                secondPart = "Quill";
                break;
            }
            case 10: {
                secondPart = "Harp";
                break;
            }
            case 11: {
                secondPart = "Orb";
                break;
            }
            case 12: {
                secondPart = "Sceptre";
                break;
            }
            case 13: {
                secondPart = "Spirit";
                break;
            }
            case 14: {
                secondPart = "Jewel";
                break;
            }
            case 15: {
                secondPart = "Corpse";
                break;
            }
            case 16: {
                secondPart = "Eye";
                break;
            }
            case 17: {
                secondPart = "Circlet";
                break;
            }
            case 18: {
                secondPart = "Band";
                break;
            }
            case 19: {
                secondPart = "Strand";
                break;
            }
            default: {
                secondPart = "";
                HexMap.logger.warning("Method argument was an int that was not between 0 and 19");
                break;
            }
        }
        return secondPart;
    }
    
    final void destroyCollectables() {
        final EpicEntity[] allEntities;
        final EpicEntity[] entityArr = allEntities = this.getAllEntities();
        for (final EpicEntity e : allEntities) {
            if (e.isCollectable()) {
                this.destroyEntity(e);
            }
        }
    }
    
    final void destroySources() {
        final EpicEntity[] allEntities;
        final EpicEntity[] entityArr = allEntities = this.getAllEntities();
        for (final EpicEntity e : allEntities) {
            if (e.isSource()) {
                this.destroyEntity(e);
            }
        }
    }
    
    final void respawnEntities() {
        final EpicEntity[] allEntities;
        final EpicEntity[] entityArr = allEntities = this.getAllEntities();
        for (final EpicEntity e : allEntities) {
            if (!e.isCollectable() && !e.isSource()) {
                final int numSteps = e.resetSteps();
                HexMap.logger.log(Level.INFO, e.getName() + " took " + numSteps + " steps.");
                e.spawn();
            }
        }
    }
    
    final void generateCollectables(final int nums, final String cname, final int type) {
        for (int x = -1; x >= -nums; --x) {
            int id = x;
            if (type == 1) {
                id = -100 - x;
            }
            final EpicEntity collectable = new EpicEntity(this, id, cname, type);
            final MapHex hex = this.getRandomHex();
            collectable.createEntity(0);
            hex.addEntity(collectable);
        }
    }
    
    boolean doesEntityExist(final int entityId) {
        return this.getEntities().containsKey((long)entityId);
    }
    
    void setImpossibleWinConditions() {
        this.currentScenario.setCollectiblesToWin(100);
        this.currentScenario.setCollectiblesForWurmToWin(100);
        this.currentScenario.setSpawnPointRequiredToWin(false);
        this.currentScenario.setHexNumRequiredToWin(0);
        this.currentScenario.setScenarioName("");
        this.currentScenario.setScenarioQuest("");
    }
    
    public int getCollictblesRequiredToWin() {
        return this.currentScenario.getCollectiblesToWin();
    }
    
    public int getCollictblesRequiredForWurmToWin() {
        return this.currentScenario.getCollectiblesForWurmToWin();
    }
    
    boolean isSpawnPointRequiredToWin() {
        return this.currentScenario.isSpawnPointRequiredToWin();
    }
    
    int getHexNumRequiredToWin() {
        return this.currentScenario.getHexNumRequiredToWin();
    }
    
    int getScenarioNumber() {
        return this.currentScenario.getScenarioNumber();
    }
    
    void incrementScenarioNumber() {
        this.currentScenario.incrementScenarioNumber();
    }
    
    Map<Long, EpicEntity> getEntities() {
        return this.entities;
    }
    
    void sendDemigodRequest(final long deityNum, final String dname) {
        Servers.requestDemigod((byte)deityNum, dname);
    }
    
    boolean spawnCreatures(final EpicEntity entity) {
        boolean delayedSpawn = false;
        final LinkedList<Integer> creatureTemplates = this.controllers.get(entity.getId());
        if (creatureTemplates != null && creatureTemplates.size() > 0) {
            final Integer toSpawn = creatureTemplates.get(HexMap.rand.nextInt(creatureTemplates.size()));
            try {
                final CreatureTemplate ct = CreatureTemplateFactory.getInstance().getTemplate(toSpawn);
                String summonString = null;
                switch (Server.rand.nextInt(5)) {
                    case 0: {
                        summonString = "sends forth";
                        break;
                    }
                    case 1: {
                        summonString = "summons";
                        break;
                    }
                    case 2: {
                        summonString = "commands";
                        break;
                    }
                    case 3: {
                        summonString = "brings";
                        break;
                    }
                    case 4: {
                        summonString = "lets loose";
                        break;
                    }
                    default: {
                        summonString = "summons";
                        break;
                    }
                }
                if (toSpawn == 75) {
                    delayedSpawn = true;
                }
                final String effectDesc = entity.getName() + " " + summonString + " the " + ct.getName() + "s.";
                final WcEpicEvent wce = new WcEpicEvent(WurmId.getNextWCCommandId(), 0, entity.getId(), toSpawn, 5, effectDesc, false);
                wce.sendFromLoginServer();
                wce.sendToServer(3);
                this.broadCast(effectDesc);
            }
            catch (NoSuchCreatureTemplateException nst) {
                HexMap.logger.log(Level.WARNING, nst.getMessage());
            }
        }
        return delayedSpawn;
    }
    
    final void loadControllers() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM CONTROLLERS");
            rs = ps.executeQuery();
            int found = 0;
            while (rs.next()) {
                final int creatureTemplateId = rs.getInt("CREATURE");
                final long controller = rs.getLong("CONTROLLER");
                LinkedList<Integer> list = this.controllers.get(controller);
                if (list == null) {
                    list = new LinkedList<Integer>();
                }
                list.add(creatureTemplateId);
                this.controllers.put(controller, list);
                ++found;
            }
            if (found == 0) {
                this.createControlledCreatures();
            }
        }
        catch (SQLException sqx) {
            HexMap.logger.log(Level.WARNING, "Problem loading entity controllers due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void createControlledCreatures() {
        this.initializeCreatureController(72);
        this.initializeCreatureController(73);
        this.initializeCreatureController(74);
        this.initializeCreatureController(75);
        this.initializeCreatureController(76);
        this.initializeCreatureController(77);
    }
    
    final void initializeCreatureController(final int creatureTemplateId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("INSERT INTO CONTROLLERS (CREATURE) VALUES (?)");
            ps.setInt(1, creatureTemplateId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            HexMap.logger.log(Level.WARNING, "Problem creating entity controller for creature template " + creatureTemplateId + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    final void setCreatureController(final int creatureTemplateId, final long controller) {
        for (final Map.Entry<Long, LinkedList<Integer>> me : this.controllers.entrySet()) {
            final LinkedList<Integer> creatures = me.getValue();
            if (creatures.contains(creatureTemplateId)) {
                if (me.getKey() == controller) {
                    return;
                }
                creatures.remove((Object)creatureTemplateId);
                break;
            }
        }
        LinkedList<Integer> list = this.controllers.get(controller);
        if (list == null) {
            list = new LinkedList<Integer>();
        }
        list.add(creatureTemplateId);
        this.controllers.put(controller, list);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("UPDATE CONTROLLERS SET CONTROLLER=? WHERE CREATURE=?");
            ps.setLong(1, controller);
            ps.setInt(2, creatureTemplateId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            HexMap.logger.log(Level.WARNING, "Problem updating entity controller for creature template " + creatureTemplateId + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    final void loadVisitedHexes() {
        HexMap.logger.info("Starting to load visited hexes for " + this.name);
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int found = 0;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM VISITED");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int hexid = rs.getInt("HEXID");
                final long entityId = rs.getLong("ENTITYID");
                final MapHex h = this.getMapHex(hexid);
                final EpicEntity e = this.getEntity(entityId);
                if (e != null) {
                    h.addVisitedBy(e, true);
                }
                ++found;
            }
        }
        catch (SQLException sqx) {
            HexMap.logger.log(Level.WARNING, "Problem loading visited hexes due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            HexMap.logger.info("Loading " + found + " visited hexes took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    static {
        logger = Logger.getLogger(HexMap.class.getName());
        rand = new Random();
        HexMap.nameRand = new Random();
    }
}
