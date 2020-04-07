// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import com.wurmonline.server.webinterface.WCValreiMapUpdater;
import com.wurmonline.server.Servers;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.webinterface.WcEpicEvent;
import com.wurmonline.server.WurmId;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deities;
import java.util.LinkedList;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public class Valrei extends HexMap
{
    private static Logger logger;
    private static final String LOAD_ENTITYDATA = "SELECT * FROM ENTITIES";
    private static final String LOAD_ENTITYSKILLS = "SELECT * FROM ENTITYSKILLS";
    
    private final void loadEntityData() {
        Valrei.logger.info("Starting to load Epic Entity Data for Valrei");
        final long start = System.nanoTime();
        this.loadControllers();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ENTITIES");
            rs = ps.executeQuery();
            int found = 0;
            final Map<Long, Long> comps = new HashMap<Long, Long>();
            final Map<Long, Long> carrs = new HashMap<Long, Long>();
            while (rs.next()) {
                final long number = rs.getLong("ID");
                final String name = rs.getString("NAME");
                final long companionId = rs.getInt("COMPANION");
                final byte demigodApps = rs.getByte("DEMIGODPLUS");
                final int spawnPoint = rs.getInt("SPAWNPOINT");
                final float attack = rs.getFloat("ATTACK");
                final float vitality = rs.getFloat("VITALITY");
                final float initialAttack = rs.getFloat("INATTACK");
                final float initialVitality = rs.getFloat("INVITALITY");
                final int type = rs.getInt("ENTITYTYPE");
                final long carrier = rs.getLong("CARRIER");
                final int currentHex = rs.getInt("CURRENTHEX");
                final boolean helped = rs.getBoolean("HELPED");
                final long entered = rs.getLong("ENTERED");
                final long leaving = rs.getLong("LEAVING");
                final int targetHex = rs.getInt("TARGETHEX");
                if (companionId > 0L) {
                    comps.put(number, companionId);
                }
                final EpicEntity e = new EpicEntity(this, number, name, type, initialAttack, initialVitality, helped, entered, leaving, targetHex);
                e.setAttack(attack, true);
                e.setVitality(vitality, true);
                if (e != null) {
                    e.setDemigodsToAppoint(demigodApps);
                    final MapHex mh = this.getMapHex(spawnPoint);
                    if (mh != null && !mh.isSpawnFor(number) && !mh.isSpawn()) {
                        mh.setSpawnEntityId(number);
                    }
                    if (carrier > 0L) {
                        final EpicEntity carr = this.getEntity(carrier);
                        if (carr != null) {
                            e.setCarrier(carr, true, true, false);
                        }
                        else {
                            carrs.put(number, carrier);
                        }
                    }
                    if (currentHex > 0 && !e.isPlayerGod()) {
                        final MapHex hex = this.getMapHex(currentHex);
                        if (hex != null) {
                            e.setMapHex(hex, true);
                        }
                    }
                }
                ++found;
            }
            for (final Map.Entry<Long, Long> carr2 : carrs.entrySet()) {
                final EpicEntity e2 = this.getEntity(carr2.getKey());
                if (e2 != null) {
                    final EpicEntity carrier2 = this.getEntity(carr2.getValue());
                    if (carrier2 == null) {
                        continue;
                    }
                    e2.setCarrier(carrier2, true, true, false);
                }
            }
            for (final Map.Entry<Long, Long> coma : comps.entrySet()) {
                final EpicEntity e2 = this.getEntity(coma.getKey());
                if (e2 != null) {
                    final EpicEntity companion = this.getEntity(coma.getValue());
                    if (companion == null) {
                        continue;
                    }
                    Valrei.logger.log(Level.INFO, e2.getName() + " setting companion " + companion.getName());
                    e2.setCompanion(companion, true);
                }
            }
            if (found == 0) {
                this.createEntities();
            }
        }
        catch (SQLException sqx) {
            Valrei.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Valrei.logger.info("Loading Epic Entity data for Valrei took " + (end - start) / 1000000.0f + " ms");
        }
        this.loadVisitedHexes();
    }
    
    private final void loadEntitySkills() {
        Valrei.logger.info("Starting to load Epic Entity Skill Data");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ENTITYSKILLS");
            rs = ps.executeQuery();
            int found = 0;
            while (rs.next()) {
                final long entityId = rs.getLong("ENTITYID");
                final int skillId = rs.getInt("SKILLID");
                final float defaultVal = rs.getFloat("DEFAULTVAL");
                final float currentVal = rs.getFloat("CURRENTVAL");
                final EpicEntity e = this.getEntity(entityId);
                if (e != null) {
                    e.setSkill(skillId, defaultVal, currentVal);
                    ++found;
                }
            }
            if (found == 0) {
                this.setEntityDefaultSkills();
                for (final EpicEntity e2 : this.getAllEntities()) {
                    e2.createAndSaveSkills();
                }
            }
        }
        catch (SQLException sqx) {
            Valrei.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Valrei.logger.info("Loading Epic Entity Skill Data took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    private final void createEntities() {
        Valrei.logger.info("Starting to create Epic Entities for Valrei");
        final long start = System.nanoTime();
        final EpicEntity Fo = new EpicEntity(this, 1L, "Fo", 0, 6.0f, 7.0f);
        final EpicEntity Vynora = new EpicEntity(this, 3L, "Vynora", 6, 6.0f, 7.0f);
        Fo.setCompanion(Vynora);
        Vynora.setCompanion(Fo);
        final EpicEntity Magranon = new EpicEntity(this, 2L, "Magranon", 0, 6.0f, 7.0f);
        final EpicEntity Libila = new EpicEntity(this, 4L, "Libila", 0, 6.0f, 7.0f);
        final EpicEntity Nogump = new EpicEntity(this, 6L, "Nogump", 6, 4.0f, 4.0f);
        Nogump.setCompanion(Vynora);
        final EpicEntity Walnut = new EpicEntity(this, 7L, "Walnut", 6, 6.0f, 4.0f);
        Walnut.setCompanion(Fo);
        final EpicEntity Pharmakos = new EpicEntity(this, 8L, "Pharmakos", 6, 3.0f, 3.0f);
        Pharmakos.setCompanion(Libila);
        final EpicEntity Jackal = new EpicEntity(this, 9L, "Jackal", 6, 5.0f, 3.0f);
        Jackal.setCompanion(Magranon);
        final EpicEntity DeathCrawler = new EpicEntity(this, 10L, "The Deathcrawler", 5, 6.0f, 3.0f);
        final EpicEntity Scavenger = new EpicEntity(this, 11L, "The Scavenger", 5, 2.0f, 6.0f);
        final EpicEntity DirtMaw = new EpicEntity(this, 12L, "The Dirtmaw Giant", 5, 5.0f, 4.0f);
        this.setEntityDefaultSkills();
        final EpicEntity[] allEntities;
        final EpicEntity[] allents = allEntities = this.getAllEntities();
        for (final EpicEntity e : allEntities) {
            final MapHex mh = this.getSpawnHex(e);
            int sp = 0;
            if (mh != null) {
                sp = mh.getId();
            }
            e.createEntity(sp);
            final EpicEntity comp = e.getCompanion();
            long cid = 0L;
            if (comp != null) {
                cid = comp.getId();
            }
            e.setCompanionForEntity(cid);
        }
        final long end = System.nanoTime();
        Valrei.logger.info("Creating Epic Entities for Valrei took " + (end - start) / 1000000.0f + " ms");
    }
    
    public void setEntityDefaultSkills(final EpicEntity entity) {
        if (entity == null) {
            return;
        }
        if (entity.getId() == 1L) {
            entity.addSkill(102, 60.0f);
            entity.addSkill(103, 45.0f);
            entity.addSkill(104, 55.0f);
            entity.addSkill(100, 45.0f);
            entity.addSkill(101, 50.0f);
            entity.addSkill(105, 40.0f);
            entity.addSkill(106, 65.0f);
        }
        else if (entity.getId() == 3L) {
            entity.addSkill(102, 40.0f);
            entity.addSkill(103, 50.0f);
            entity.addSkill(104, 45.0f);
            entity.addSkill(100, 65.0f);
            entity.addSkill(101, 45.0f);
            entity.addSkill(105, 60.0f);
            entity.addSkill(106, 55.0f);
        }
        else if (entity.getId() == 2L) {
            entity.addSkill(102, 65.0f);
            entity.addSkill(103, 60.0f);
            entity.addSkill(104, 50.0f);
            entity.addSkill(100, 40.0f);
            entity.addSkill(101, 45.0f);
            entity.addSkill(105, 55.0f);
            entity.addSkill(106, 45.0f);
        }
        else if (entity.getId() == 4L) {
            entity.addSkill(102, 50.0f);
            entity.addSkill(103, 65.0f);
            entity.addSkill(104, 45.0f);
            entity.addSkill(100, 60.0f);
            entity.addSkill(101, 40.0f);
            entity.addSkill(105, 45.0f);
            entity.addSkill(106, 55.0f);
        }
        else if (entity.getId() == 6L) {
            entity.addSkill(102, 45.0f);
            entity.addSkill(103, 30.0f);
            entity.addSkill(104, 40.0f);
            entity.addSkill(100, 25.0f);
            entity.addSkill(101, 30.0f);
            entity.addSkill(105, 50.0f);
            entity.addSkill(106, 35.0f);
        }
        else if (entity.getId() == 7L) {
            entity.addSkill(102, 50.0f);
            entity.addSkill(103, 40.0f);
            entity.addSkill(104, 45.0f);
            entity.addSkill(100, 30.0f);
            entity.addSkill(101, 25.0f);
            entity.addSkill(105, 35.0f);
            entity.addSkill(106, 30.0f);
        }
        else if (entity.getId() == 8L) {
            entity.addSkill(102, 30.0f);
            entity.addSkill(103, 35.0f);
            entity.addSkill(104, 30.0f);
            entity.addSkill(100, 45.0f);
            entity.addSkill(101, 40.0f);
            entity.addSkill(105, 50.0f);
            entity.addSkill(106, 25.0f);
        }
        else if (entity.getId() == 9L) {
            entity.addSkill(102, 40.0f);
            entity.addSkill(103, 30.0f);
            entity.addSkill(104, 50.0f);
            entity.addSkill(100, 35.0f);
            entity.addSkill(101, 45.0f);
            entity.addSkill(105, 25.0f);
            entity.addSkill(106, 30.0f);
        }
        else if (entity.getId() == 10L) {
            entity.addSkill(102, 60.0f);
            entity.addSkill(103, 25.0f);
            entity.addSkill(104, 45.0f);
            entity.addSkill(100, 20.0f);
            entity.addSkill(101, 30.0f);
            entity.addSkill(105, 50.0f);
            entity.addSkill(106, 40.0f);
        }
        else if (entity.getId() == 11L) {
            entity.addSkill(102, 40.0f);
            entity.addSkill(103, 60.0f);
            entity.addSkill(104, 30.0f);
            entity.addSkill(100, 50.0f);
            entity.addSkill(101, 25.0f);
            entity.addSkill(105, 45.0f);
            entity.addSkill(106, 20.0f);
        }
        else if (entity.getId() == 12L) {
            entity.addSkill(102, 50.0f);
            entity.addSkill(103, 40.0f);
            entity.addSkill(104, 45.0f);
            entity.addSkill(100, 25.0f);
            entity.addSkill(101, 20.0f);
            entity.addSkill(105, 60.0f);
            entity.addSkill(106, 30.0f);
        }
        else if (entity.getId() == 5L) {
            entity.addSkill(102, 70.0f);
            entity.addSkill(103, 70.0f);
            entity.addSkill(104, 70.0f);
            entity.addSkill(100, 60.0f);
            entity.addSkill(101, 60.0f);
            entity.addSkill(105, 50.0f);
            entity.addSkill(106, 50.0f);
        }
    }
    
    private final void setEntityDefaultSkills() {
        final EpicEntity foEntity = this.getEntity(1L);
        final EpicEntity vynEntity = this.getEntity(3L);
        final EpicEntity magEntity = this.getEntity(2L);
        final EpicEntity libEntity = this.getEntity(4L);
        final EpicEntity nogumpEntity = this.getEntity(6L);
        final EpicEntity walnutEntity = this.getEntity(7L);
        final EpicEntity pharEntity = this.getEntity(8L);
        final EpicEntity jackalEntity = this.getEntity(9L);
        final EpicEntity deathEntity = this.getEntity(10L);
        final EpicEntity scavEntity = this.getEntity(11L);
        final EpicEntity dirtEntity = this.getEntity(12L);
        final EpicEntity wurmEntity = this.getEntity(5L);
        this.setEntityDefaultSkills(foEntity);
        this.setEntityDefaultSkills(vynEntity);
        this.setEntityDefaultSkills(magEntity);
        this.setEntityDefaultSkills(libEntity);
        this.setEntityDefaultSkills(nogumpEntity);
        this.setEntityDefaultSkills(walnutEntity);
        this.setEntityDefaultSkills(pharEntity);
        this.setEntityDefaultSkills(jackalEntity);
        this.setEntityDefaultSkills(deathEntity);
        this.setEntityDefaultSkills(scavEntity);
        this.setEntityDefaultSkills(dirtEntity);
        this.setEntityDefaultSkills(wurmEntity);
    }
    
    public Valrei() {
        super("Valrei");
        Valrei.logger.info("Creating Valrei");
        final MapHex v1 = new MapHex(this, 1, "Faltersteps", 2.0f, 0);
        v1.addNearHexes(2, 3, 4, 54, 53, 55);
        v1.setPresenceStringOne(" is traversing the ");
        v1.setPrepositionString(" at the ");
        v1.setHomeEntityId(3L);
        final MapHex v2 = new MapHex(this, 2, "Shaded Depths of Uttacha", 1.0f, 0);
        v2.addNearHexes(55, 3, 1, 5, 6, 54);
        v2.setPresenceStringOne(" is wallowing in the ");
        v2.setPrepositionString(" down in the ");
        v2.setHomeEntityId(3L);
        final MapHex v3 = new MapHex(this, 3, "The Drown", 1.0f, 0);
        v3.addNearHexes(1, 2, 4, 6, 7, 8);
        v3.setSpawnEntityId(3L);
        v3.setPresenceStringOne(" is trying to survive in ");
        v3.setPrepositionString(" deep down in ");
        v3.setHomeEntityId(3L);
        final MapHex v4 = new MapHex(this, 4, "Nogump The Dirty", 1.0f, 0);
        v4.addNearHexes(1, 3, 8, 9, 52, 53);
        v4.setPresenceStringOne(" is visiting ");
        v4.setPrepositionString(" at ");
        v4.setHomeEntityId(3L);
        v4.setSpawnEntityId(6L);
        final MapHex v5 = new MapHex(this, 5, "Altars Of Contemplation", 1.0f, 4);
        v5.addNearHexes(2, 10, 11, 6, 55, 56);
        v5.setPresenceStringOne(" marvels at the ");
        v5.setPrepositionString(" at the ");
        v5.setHomeEntityId(3L);
        final MapHex v6 = new MapHex(this, 6, "Brokeneyes", 3.0f, 0);
        v6.addNearHexes(2, 5, 11, 12, 7, 3);
        v6.setPresenceStringOne(" is climbing the ");
        v6.setPrepositionString(" at the ");
        v6.setHomeEntityId(3L);
        final MapHex v7 = new MapHex(this, 7, "Firejaw", 3.0f, 2);
        v7.addNearHexes(3, 6, 12, 13, 14, 8);
        v7.setPresenceStringOne(" is trying to survive the ");
        v7.setPrepositionString(" at the ");
        v7.setHomeEntityId(3L);
        final MapHex v8 = new MapHex(this, 8, "The Shift", 1.0f, 5);
        v8.addNearHexes(4, 3, 7, 14, 15, 9);
        v8.setPresenceStringOne(" is exploring ");
        v8.setHomeEntityId(3L);
        final MapHex v9 = new MapHex(this, 9, "Valreis Worried Brow", 1.0f, 0);
        v9.addNearHexes(4, 8, 15, 16, 51, 52);
        v9.setPresenceStringOne(" lingers at ");
        v9.setHomeEntityId(3L);
        final MapHex v10 = new MapHex(this, 10, "Plains Of Hidden Thoughts", 1.0f, 0);
        v10.addNearHexes(5, 11, 17, 55, 54, 56);
        v10.setPresenceStringOne(" wanders the ");
        v10.setPrepositionString(" on the ");
        final MapHex v11 = new MapHex(this, 11, "Windswept Heights", 1.0f, 0);
        v11.addNearHexes(5, 10, 17, 18, 12, 6);
        v11.setPresenceStringOne(" crosses the ");
        v11.setPrepositionString(" on the ");
        final MapHex v12 = new MapHex(this, 12, "Scary Old Trees", 1.0f, 0);
        v12.addNearHexes(6, 11, 18, 19, 13, 7);
        v12.setPresenceStringOne(" walks among the ");
        v12.setPrepositionString(" under the ");
        final MapHex v13 = new MapHex(this, 13, "Really Bad Lands", 2.0f, 1);
        v13.addNearHexes(7, 12, 19, 20, 21, 14);
        v13.setPresenceStringOne(" is struggling in the ");
        v13.setPrepositionString(" in the ");
        v13.setSpawnEntityId(5L);
        v13.setHomeEntityId(5L);
        final MapHex v14 = new MapHex(this, 14, "Beastwatch Range", 2.0f, 0);
        v14.addNearHexes(8, 7, 13, 21, 22, 15);
        v14.setPresenceStringOne(" is surveying at ");
        v14.setPrepositionString(" at ");
        final MapHex v15 = new MapHex(this, 15, "The Nobody", 2.0f, 0);
        v15.addNearHexes(9, 8, 14, 22, 23, 16);
        v15.setPresenceStringOne(" visits ");
        v15.setPrepositionString(" at ");
        final MapHex v16 = new MapHex(this, 16, "Who's There Forest", 1.0f, 0);
        v16.addNearHexes(9, 15, 23, 43, 51, 42);
        v16.setPresenceStringOne(" is wandering ");
        v16.setPrepositionString(" in ");
        final MapHex v17 = new MapHex(this, 17, "Diamond Mines", 2.0f, 1);
        v17.addNearHexes(10, 24, 25, 18, 11, 50);
        v17.setPresenceStringOne(" is searching the ");
        v17.setHomeEntityId(2L);
        final MapHex v18 = new MapHex(this, 18, "Jeopardy Hunt", 1.0f, 0);
        v18.addNearHexes(11, 17, 25, 26, 19, 12);
        v18.setPresenceStringOne(" is hunting at the ");
        v18.setHomeEntityId(2L);
        final MapHex v19 = new MapHex(this, 19, "Dying Plateau", 1.0f, 0);
        v19.addNearHexes(12, 18, 26, 27, 20, 13);
        v19.setPresenceStringOne(" is traversing the ");
        final MapHex v20 = new MapHex(this, 20, "Skyrisen Range", 2.0f, 0);
        v20.addNearHexes(13, 19, 27, 28, 29, 21);
        v20.setPresenceStringOne(" is climbing the ");
        final MapHex v21 = new MapHex(this, 21, "Brittlerock Mountains", 2.0f, 0);
        v21.addNearHexes(14, 13, 20, 29, 30, 22);
        v21.setPresenceStringOne(" defies the thunderstorms at ");
        final MapHex v22 = new MapHex(this, 22, "Loft Despair", 1.0f, 0);
        v22.addNearHexes(15, 14, 21, 30, 31, 23);
        v22.setPresenceStringOne(" stands at ");
        v22.setHomeEntityId(4L);
        v22.setSpawnEntityId(8L);
        final MapHex v23 = new MapHex(this, 23, "The Dark Songs Forest", 1.0f, 0);
        v23.addNearHexes(16, 15, 22, 31, 32, 43);
        v23.setPresenceStringOne(" listens at ");
        v23.setHomeEntityId(4L);
        final MapHex v24 = new MapHex(this, 24, "Jackal's Sanctuary", 1.0f, 0);
        v24.addNearHexes(17, 33, 25, 49, 50, 57);
        v24.setPresenceStringOne(" visits ");
        v24.setHomeEntityId(2L);
        v24.setSpawnEntityId(9L);
        final MapHex v25 = new MapHex(this, 25, "Castle Glittercrown", 1.0f, 0);
        v25.addNearHexes(17, 24, 33, 34, 26, 18);
        v25.setPresenceStringOne(" visits ");
        v25.setSpawnEntityId(2L);
        v25.setHomeEntityId(2L);
        final MapHex v26 = new MapHex(this, 26, "Spiritgathers", 1.0f, 3);
        v26.addNearHexes(18, 25, 34, 35, 27, 19);
        v26.setPresenceStringOne(" listens at ");
        v26.setHomeEntityId(2L);
        final MapHex v27 = new MapHex(this, 27, "Weirdpeaks Fall", 2.0f, 0);
        v27.addNearHexes(19, 26, 35, 36, 28, 20);
        v27.setPresenceStringOne(" is traversing the ");
        final MapHex v28 = new MapHex(this, 28, "Spring Valleys", 1.0f, 0);
        v28.addNearHexes(20, 27, 36, 37, 38, 29);
        v28.setPresenceStringOne(" wanders the ");
        final MapHex v29 = new MapHex(this, 29, "Eaglespirit Glacier", 2.0f, 3);
        v29.addNearHexes(21, 20, 28, 38, 39, 30);
        v29.setPresenceStringOne(" is stuck in the ");
        final MapHex v30 = new MapHex(this, 30, "Wintertree Hills", 1.0f, 0);
        v30.addNearHexes(22, 21, 29, 39, 40, 31);
        v30.setPresenceStringOne(" hunts the ");
        v30.setHomeEntityId(4L);
        v30.setSpawnEntityId(11L);
        final MapHex v31 = new MapHex(this, 31, "Bloodsucker March", 2.0f, 2);
        v31.addNearHexes(23, 22, 30, 40, 41, 32);
        v31.setPresenceStringOne(" is stuck in the ");
        v31.setHomeEntityId(4L);
        final MapHex v32 = new MapHex(this, 32, "Den Of The Deathcrawler", 1.0f, 0);
        v32.addNearHexes(23, 31, 41, 42, 43, 33);
        v32.setPresenceStringOne(" explores the ");
        v32.setHomeEntityId(4L);
        v32.setSpawnEntityId(10L);
        final MapHex v33 = new MapHex(this, 33, "Saltwalk", 2.0f, 2);
        v33.addNearHexes(32, 24, 42, 34, 25, 41);
        v33.setPresenceStringOne(" survives the ");
        v33.setHomeEntityId(2L);
        final MapHex v34 = new MapHex(this, 34, "Mount Creation", 2.0f, 0);
        v34.addNearHexes(33, 42, 43, 35, 26, 25);
        v34.setPresenceStringOne(" climbs ");
        v34.setHomeEntityId(2L);
        final MapHex v35 = new MapHex(this, 35, "Golden Jungle", 2.0f, 2);
        v35.addNearHexes(34, 43, 44, 36, 27, 26);
        v35.setPresenceStringOne(" explores the ");
        v35.setHomeEntityId(2L);
        final MapHex v36 = new MapHex(this, 36, "Mount Assami", 2.0f, 0);
        v36.addNearHexes(35, 44, 45, 37, 28, 27);
        v36.setPresenceStringOne(" avoids the rockfalls at ");
        final MapHex v37 = new MapHex(this, 37, "Humid Hills", 1.0f, 0);
        v37.addNearHexes(36, 45, 46, 47, 38, 28);
        v37.setPresenceStringOne(" travels through the ");
        v37.setHomeEntityId(1L);
        final MapHex v38 = new MapHex(this, 38, "Deadends", 2.0f, 1);
        v38.addNearHexes(28, 37, 47, 48, 39, 29);
        v38.setPresenceStringOne(" looks for a way through the ");
        final MapHex v39 = new MapHex(this, 39, "Foulwater Ices", 2.0f, 0);
        v39.addNearHexes(29, 38, 48, 49, 40, 30);
        v39.setPresenceStringOne(" swims through the ");
        final MapHex v40 = new MapHex(this, 40, "Rusty Daggers", 2.0f, 0);
        v40.addNearHexes(30, 39, 49, 50, 41, 31);
        v40.setPresenceStringOne(" walks the ");
        v40.setSpawnEntityId(4L);
        v40.setHomeEntityId(4L);
        final MapHex v41 = new MapHex(this, 41, "Broken Fingernails", 2.0f, 0);
        v41.addNearHexes(31, 40, 50, 32, 24, 33);
        v41.setPresenceStringOne(" traverses the ");
        v41.setHomeEntityId(4L);
        final MapHex v42 = new MapHex(this, 42, "Flamestrike Desert", 1.0f, 0);
        v42.addNearHexes(16, 23, 32, 33, 34, 43);
        v42.setPresenceStringOne(" explores the ");
        v42.setHomeEntityId(2L);
        final MapHex v43 = new MapHex(this, 43, "Western Spurs", 2.0f, 0);
        v43.addNearHexes(42, 23, 51, 44, 35, 34);
        v43.setPresenceStringOne(" climbs the ");
        final MapHex v44 = new MapHex(this, 44, "Drakespirit Gardens", 2.0f, 4);
        v44.addNearHexes(43, 51, 52, 45, 36, 35);
        v44.setPresenceStringOne(" visits the ");
        final MapHex v45 = new MapHex(this, 45, "Jagged Rise", 1.0f, 2);
        v45.addNearHexes(44, 52, 53, 46, 37, 36);
        v45.setPresenceStringOne(" is traversing the ");
        v45.setHomeEntityId(1L);
        final MapHex v46 = new MapHex(this, 46, "The Myriad", 1.0f, 0);
        v46.addNearHexes(45, 53, 54, 55, 47, 37);
        v46.setPresenceStringOne(" lingers in ");
        v46.setSpawnEntityId(1L);
        v46.setHomeEntityId(1L);
        final MapHex v47 = new MapHex(this, 47, "Forest Of The Dreadwalkers", 1.0f, 0);
        v47.addNearHexes(37, 46, 55, 56, 48, 38);
        v47.setPresenceStringOne(" explores the ");
        v47.setHomeEntityId(1L);
        final MapHex v48 = new MapHex(this, 48, "Misthollow Flats", 1.0f, 0);
        v48.addNearHexes(38, 47, 56, 57, 49, 39);
        v48.setPresenceStringOne(" is in the ");
        final MapHex v49 = new MapHex(this, 49, "Home Of The Treekeeper", 1.0f, 0);
        v49.addNearHexes(39, 48, 57, 50, 40, 24);
        v49.setPresenceStringOne(" is visiting the ");
        v49.setSpawnEntityId(7L);
        final MapHex v50 = new MapHex(this, 50, "The Fence", 2.0f, 1);
        v50.addNearHexes(40, 49, 41, 24, 33, 17);
        v50.setPresenceStringOne(" is trapped at ");
        v50.setPrepositionString(" at ");
        v50.setHomeEntityId(4L);
        final MapHex v51 = new MapHex(this, 51, "Bleak Plains", 1.0f, 0);
        v51.addNearHexes(43, 44, 52, 16, 23, 9);
        v51.setPresenceStringOne(" is walking the ");
        final MapHex v52 = new MapHex(this, 52, "Deforestation", 1.0f, 0);
        v52.addNearHexes(44, 51, 45, 53, 4, 9);
        v52.setPresenceStringOne(" is passing the ");
        v52.setHomeEntityId(1L);
        final MapHex v53 = new MapHex(this, 53, "The Mawpits", 2.0f, 2);
        v53.addNearHexes(52, 45, 46, 54, 1, 4);
        v53.setPresenceStringOne(" is exploring the ");
        v53.setHomeEntityId(1L);
        final MapHex v54 = new MapHex(this, 54, "Stompinggrounds", 1.0f, 0);
        v54.addNearHexes(1, 2, 53, 46, 55, 4);
        v54.setPresenceStringOne(" runs through ");
        v54.setHomeEntityId(1L);
        v54.setSpawnEntityId(12L);
        final MapHex v55 = new MapHex(this, 55, "Glowing Shrubs", 1.0f, 0);
        v55.addNearHexes(46, 54, 47, 56, 5, 2);
        v55.setPresenceStringOne(" walks among the ");
        v55.setHomeEntityId(1L);
        v55.setPrepositionString(" among the ");
        final MapHex v56 = new MapHex(this, 56, "Stargazers' Hollows", 1.0f, 0);
        v56.addNearHexes(47, 55, 48, 57, 5, 10);
        v56.setPresenceStringOne(" visits the ");
        v56.setHomeEntityId(1L);
        final MapHex v57 = new MapHex(this, 57, "Withering Marble", 1.0f, 0);
        v57.addNearHexes(49, 48, 56, 10, 24, 5);
        v57.setPresenceStringOne(" walks the ");
    }
    
    public void generateEntities() {
        this.loadEntityData();
        this.loadEntitySkills();
        if (!this.getCurrentScenario().loadCurrentScenario()) {
            this.nextScenario();
        }
    }
    
    @Override
    String getMapSpecialWinEffect() {
        if (this.doesEntityExist(5)) {
            return "";
        }
        if (this.getCollictblesRequiredToWin() > 0) {
            return "If Libila collects these, she will awake the Wurm!";
        }
        return "If Libila acquires it, she will awake the Wurm!";
    }
    
    @Override
    void checkSpecialMapWinCases(final EpicEntity winner) {
        if (winner.getId() == 4L && !this.isWurmAwake()) {
            final EpicEntity Wurm = new EpicEntity(this, 5L, "Wurm", 4, 10.0f, 10.0f);
            final MapHex mh = this.getSpawnHex(Wurm);
            int sp = 0;
            if (mh != null) {
                sp = mh.getId();
            }
            Wurm.createEntity(sp);
            this.broadCast("Terror strikes the hearts of mortals as Libila has awoken the Wurm!");
        }
    }
    
    boolean isWurmAwake() {
        return this.doesEntityExist(5);
    }
    
    @Override
    final void nextScenario() {
        this.getCurrentScenario().saveScenario(false);
        this.setImpossibleWinConditions();
        this.incrementScenarioNumber();
        switch (this.getScenarioNumber()) {
            case 1: {
                this.generateRandomScenario();
                break;
            }
            default: {
                this.generateRandomScenario();
                break;
            }
        }
    }
    
    @Override
    int getRandomReason() {
        return Valrei.rand.nextInt(20);
    }
    
    EpicEntity charmEnemyEntity(final EpicEntity entity) {
        final EpicEntity[] allents = this.getAllEntities();
        final LinkedList<EpicEntity> allies = new LinkedList<EpicEntity>();
        for (final EpicEntity e : allents) {
            if (e.isAlly() && e.getCompanion() != entity) {
                allies.add(e);
            }
        }
        EpicEntity stolen = null;
        if (allies.size() > 0) {
            stolen = allies.get(Valrei.rand.nextInt(allies.size()));
            stolen.setCompanion(entity);
        }
        return stolen;
    }
    
    EpicEntity returnCharmedEnemyEntity(final EpicEntity entity) {
        EpicEntity stolen = null;
        switch ((int)entity.getId()) {
            case 1: {
                stolen = this.getEntity(7L);
                break;
            }
            case 3: {
                stolen = this.getEntity(6L);
                break;
            }
            case 2: {
                stolen = this.getEntity(9L);
                break;
            }
            case 4: {
                stolen = this.getEntity(8L);
                break;
            }
        }
        if (stolen != null && stolen.getCompanion() != entity) {
            stolen.setCompanion(entity);
        }
        else {
            stolen = null;
        }
        return stolen;
    }
    
    public void setWinEffects(final EpicEntity entity, final String collName, final int nums) {
        for (final EpicEntity e : this.getAllEntities()) {
            if (e.isDeity() && e != entity) {
                final HashMap<Integer, EpicEntity.SkillVal> skills = e.getAllSkills();
                for (final int id : skills.keySet()) {
                    final EpicEntity.SkillVal sv = skills.get(id);
                    if (sv.getCurrentVal() > sv.getDefaultVal()) {
                        e.setSkill(id, sv.getCurrentVal() - (100.0f - sv.getCurrentVal()) / 100.0f);
                    }
                }
            }
        }
        MapHex gatherPlace = this.getSpawnHex(entity);
        if (this.getHexNumRequiredToWin() > 0) {
            gatherPlace = this.getMapHex(this.getHexNumRequiredToWin());
        }
        String gatherPlaceS = "Home";
        if (gatherPlace != null) {
            gatherPlaceS = gatherPlace.getName();
        }
        String collMultipleName = collName;
        if (nums > 1) {
            collMultipleName = collName + "s";
        }
        final String toBroadCast = entity.getName() + " has gathered the " + collMultipleName + " required at " + gatherPlaceS + ".";
        this.broadCast(entity.getName() + " has gathered the " + collMultipleName + " required at " + gatherPlaceS + ".");
        this.applyDeityScenarioReward(entity, toBroadCast, collMultipleName);
        if (!entity.isWurm()) {
            final Deity deity = Deities.getDeity((int)entity.getId());
            if (deity != null) {
                final ConcurrentHashMap<Long, Float> allHelpers = deity.getHelpers();
                float maxHelperVal = 0.0f;
                for (final Float f : allHelpers.values()) {
                    if (f > maxHelperVal) {
                        maxHelperVal = f;
                    }
                }
                final long tierOneWinner = this.getWinningHelper(allHelpers, 0.8f, 300);
                if (tierOneWinner > -10L) {
                    final float winnerVal = allHelpers.get(tierOneWinner);
                    int randomTomeId = 795 + Server.rand.nextInt(16);
                    if (winnerVal >= maxHelperVal * 0.95f && Server.rand.nextInt(50) == 0) {
                        randomTomeId = ((Server.rand.nextInt(5) == 0) ? 794 : 465);
                    }
                    this.sendWinnerItem(deity, tierOneWinner, randomTomeId, false);
                    allHelpers.replace(tierOneWinner, 0.0f);
                    if (randomTomeId == 794 || randomTomeId == 465) {
                        return;
                    }
                }
                long tierTwoWinner = -10L;
                for (int i = 0; i < 3; ++i) {
                    tierTwoWinner = this.getWinningHelper(allHelpers, 0.5f, 300);
                    if (tierTwoWinner > -10L) {
                        final int randomTomeId2 = 795 + Server.rand.nextInt(16);
                        this.sendWinnerItem(deity, tierTwoWinner, randomTomeId2, true);
                        allHelpers.replace(tierTwoWinner, 0.0f);
                    }
                }
                long tierThreeWinner = -10L;
                for (int j = 0; j < 5; ++j) {
                    tierThreeWinner = this.getWinningHelper(allHelpers, 0.0f, 300);
                    if (tierThreeWinner > -10L) {
                        final boolean fragment = Server.rand.nextBoolean();
                        final int randomTomeId3 = fragment ? (795 + Server.rand.nextInt(16)) : 837;
                        this.sendWinnerItem(deity, tierThreeWinner, randomTomeId3, fragment);
                        allHelpers.replace(tierThreeWinner, 0.0f);
                    }
                }
            }
        }
    }
    
    private void applyDeityScenarioReward(final EpicEntity entity, final String toBroadCast, final String collMultipleName) {
        String rest = "";
        final HashMap<Integer, EpicEntity.SkillVal> skills = entity.getAllSkills();
        switch (this.getReasonAndEffectInt()) {
            case 0: {
                if (entity.isWurm()) {
                    rest = "The Wurm punishes you all!";
                }
                else {
                    rest = entity.getName() + " uses the " + collMultipleName + " aggressively.";
                }
                this.broadCast(rest);
                break;
            }
            case 1: {
                if (entity.isWurm()) {
                    rest = "The Wurm swallows the " + collMultipleName + " whole!";
                }
                else {
                    rest = entity.getName() + " stores the " + collMultipleName + " in a secret place.";
                }
                this.broadCast(rest);
                break;
            }
            case 2: {
                if (entity.isWurm()) {
                    rest = "The Wurm relishes in your pain!";
                }
                else {
                    rest = entity.getName() + " makes good use of the " + collMultipleName + ".";
                }
                this.broadCast(rest);
                break;
            }
            case 3:
            case 4: {
                if (entity.isWurm()) {
                    rest = "The Wurm punishes you all!";
                }
                else {
                    rest = entity.getName() + " will use the " + collMultipleName + " instead.";
                }
                this.broadCast(rest);
                final EpicEntity wasStolen1 = this.returnCharmedEnemyEntity(entity);
                if (wasStolen1 != null) {
                    final String nrest = wasStolen1.getName() + " is convinced to return to the side of " + entity.getName() + ".";
                    this.broadCast(nrest);
                    rest = rest + " " + nrest;
                    break;
                }
                break;
            }
            case 5: {
                if (entity.isWurm()) {
                    rest = "Wurm releases aggravated souls all over the lands.";
                }
                else {
                    rest = entity.getName() + " directs aggravated souls at the enemy.";
                }
                this.broadCast(rest);
                this.setCreatureController(74, entity.getId());
                break;
            }
            case 6:
            case 9: {
                if (entity.isWurm()) {
                    rest = "Wurm uses the power gained to influence Valrei!";
                }
                else {
                    rest = entity.getName() + " tries to gain influence on Valrei!";
                }
                this.broadCast(rest);
                final EpicEntity stolen = this.charmEnemyEntity(entity);
                if (stolen != null) {
                    final String nrest2 = stolen.getName() + " is charmed by the power of " + entity.getName() + " and switches sides!";
                    this.broadCast(nrest2);
                    rest = rest + " " + nrest2;
                    break;
                }
                break;
            }
            case 7: {
                if (entity.isWurm()) {
                    rest = "The Wurm increases its power even more!";
                }
                else {
                    rest = "The power of the " + collMultipleName + " is used by " + entity.getName() + " to grow in power";
                    if (this.isWurmAwake()) {
                        rest += " and weaken the Wurm.";
                    }
                    else {
                        rest += ".";
                    }
                }
                this.broadCast(rest);
                for (final int id : skills.keySet()) {
                    final EpicEntity.SkillVal sv = skills.get(id);
                    entity.setSkill(id, sv.getCurrentVal() + (100.0f - sv.getCurrentVal()) / 200.0f);
                }
                if (this.isWurmAwake() && !entity.isWurm()) {
                    final EpicEntity wurm = this.getEntity(5L);
                    if (wurm != null) {
                        for (final int id2 : skills.keySet()) {
                            final EpicEntity.SkillVal sv2 = skills.get(id2);
                            wurm.setSkill(id2, sv2.getCurrentVal() - (100.0f - sv2.getCurrentVal()) / 25.0f);
                        }
                    }
                    rest = "Wurm is weakened.";
                    this.broadCast(rest);
                    break;
                }
                break;
            }
            case 8: {
                if (entity.isWurm()) {
                    rest = "Those were needed to keep the Demons from Sol at bay!";
                }
                else {
                    rest = entity.getName() + " now controls the Demons from Sol!";
                }
                this.broadCast(rest);
                this.setCreatureController(72, entity.getId());
                break;
            }
            case 10: {
                rest = entity.getName() + " receives significant knowledge from using the " + collMultipleName + ".";
                this.broadCast(rest);
                entity.increaseRandomSkill(100.0f);
                entity.increaseRandomSkill(100.0f);
                break;
            }
            case 11: {
                rest = entity.getName() + " now controls the Eagle Spirits!";
                this.broadCast(rest);
                this.setCreatureController(77, entity.getId());
                break;
            }
            case 12:
            case 14: {
                rest = "The deities are weakened as Valrei is struck with a mysterious disease.";
                this.broadCast(rest);
                this.diseaseAllBut(entity);
                break;
            }
            case 13: {
                rest = entity.getName() + " now commands the Deathcrawler's minions!";
                this.broadCast(rest);
                this.setCreatureController(73, entity.getId());
                break;
            }
            case 15: {
                rest = entity.getName() + " now commands the sons of Nogump!";
                this.broadCast(rest);
                this.setCreatureController(75, entity.getId());
                break;
            }
            case 16: {
                rest = entity.getName() + " grows from the immense power of the " + collMultipleName + ".";
                this.broadCast(rest);
                for (int i = 0; i < 4; ++i) {
                    entity.increaseRandomSkill(100.0f);
                }
                break;
            }
            case 17: {
                rest = entity.getName() + " now controls the Spirit Drakes!";
                this.broadCast(rest);
                this.setCreatureController(76, entity.getId());
                break;
            }
            case 18: {
                rest = entity.getName() + " attempts to disrupt Valrei with the newfound power from the " + collMultipleName + ".";
                this.broadCast(rest);
                for (int i = 0; i < 4; ++i) {
                    entity.increaseRandomSkill(100.0f);
                }
                final EpicEntity charmed = this.charmEnemyEntity(entity);
                if (charmed != null) {
                    final String nrest3 = charmed.getName() + " is charmed by the power of " + entity.getName() + " and switches sides!";
                    this.broadCast(nrest3);
                    rest = rest + " " + nrest3;
                    break;
                }
                break;
            }
            case 19: {
                final int additionalRoll = Valrei.rand.nextInt(5);
                switch (additionalRoll) {
                    case 0:
                    case 1: {
                        rest = entity.getName() + " unlocks their hidden potential from using the " + collMultipleName + ".";
                        this.broadCast(rest);
                        for (int j = 0; j < 6; ++j) {
                            entity.increaseRandomSkill(75.0f);
                        }
                        break;
                    }
                    case 2:
                    case 3: {
                        rest = entity.getName() + " reveals some true power from within the " + collMultipleName + ".";
                        this.broadCast(rest);
                        for (int j = 0; j < 6; ++j) {
                            entity.increaseRandomSkill(50.0f);
                        }
                        break;
                    }
                    case 4: {
                        rest = entity.getName() + " will rule both heaven and earth and may promote an ally!";
                        this.broadCast(rest);
                        for (int j = 0; j < 6; ++j) {
                            entity.increaseRandomSkill(50.0f);
                        }
                        this.crushAllBut(entity);
                        Effectuator.promoteImmortal(entity.getId());
                        break;
                    }
                }
                break;
            }
        }
        final WcEpicEvent wce = new WcEpicEvent(WurmId.getNextWCCommandId(), this.getReasonAndEffectInt(), entity.getId(), 0, 0, toBroadCast + " " + rest, true);
        wce.sendFromLoginServer();
    }
    
    private void sendWinnerItem(final Deity deity, final long winnerId, final int itemId, final boolean fragment) {
        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(winnerId);
        Valrei.logger.log(Level.INFO, winnerId + " won the prize of " + itemId + " from " + deity.getName());
        if (pinf != null) {
            if (!pinf.loaded) {
                try {
                    pinf.load();
                }
                catch (IOException iox) {
                    Valrei.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
            final WcEpicEvent wcwin = new WcEpicEvent(WurmId.getNextWCCommandId(), 0, pinf.wurmId, itemId, fragment ? 5 : 4, deity.getName(), false);
            wcwin.sendFromLoginServer();
        }
    }
    
    private long getWinningHelper(final ConcurrentHashMap<Long, Float> allHelpers, final float tierPercentage, final int ticketCost) {
        final HashMap<Long, Integer> ticketCounts = new HashMap<Long, Integer>();
        float maxHelperVal = 0.0f;
        for (final Float f : allHelpers.values()) {
            if (f > maxHelperVal) {
                maxHelperVal = f;
            }
        }
        int totalTickets = 0;
        for (final Long l : allHelpers.keySet()) {
            if (allHelpers.get(l) >= maxHelperVal * tierPercentage) {
                final float totalTier = allHelpers.get(l) - maxHelperVal * tierPercentage;
                final int ticketCount = (int)(totalTier / ticketCost);
                totalTickets += ticketCount;
                ticketCounts.put(l, ticketCount);
            }
        }
        if (totalTickets > 0) {
            int currentTicket = 0;
            final long[] tickets = new long[totalTickets];
            for (final Long i : ticketCounts.keySet()) {
                for (int j = 0; j < ticketCounts.get(i); ++j) {
                    tickets[currentTicket++] = i;
                }
            }
            return tickets[Server.rand.nextInt(totalTickets)];
        }
        return -10L;
    }
    
    private void diseaseAllBut(final EpicEntity winner) {
        Valrei.logger.info("Disease all but the winning epic entity: " + winner.getName());
        final EpicEntity[] allEntities;
        final EpicEntity[] entits = allEntities = this.getAllEntities();
        for (final EpicEntity entity : allEntities) {
            if (entity != winner) {
                if ((entity.isDeity() || entity.isWurm()) && !entity.isFriend(winner)) {
                    final HashMap<Integer, EpicEntity.SkillVal> skills = entity.getAllSkills();
                    for (final int id : skills.keySet()) {
                        final EpicEntity.SkillVal sv = skills.get(id);
                        entity.setSkill(id, sv.getCurrentVal() - (100.0f - sv.getCurrentVal()) / 100.0f);
                    }
                }
                if (entity.isDemigod()) {
                    final HashMap<Integer, EpicEntity.SkillVal> skills = entity.getAllSkills();
                    for (final int id : skills.keySet()) {
                        final EpicEntity.SkillVal sv = skills.get(id);
                        entity.setSkill(id, sv.getCurrentVal() - (100.0f - sv.getCurrentVal()) / 100.0f);
                    }
                }
            }
        }
    }
    
    private void crushAllBut(final EpicEntity winner) {
        Valrei.logger.info("Crushing all but the winning epic entity: " + winner.getName());
        this.setCreatureController(74, winner.getId());
        this.setCreatureController(76, winner.getId());
        this.setCreatureController(77, winner.getId());
        this.setCreatureController(75, winner.getId());
        this.setCreatureController(73, winner.getId());
        this.setCreatureController(72, winner.getId());
        final EpicEntity[] allEntities;
        final EpicEntity[] entits = allEntities = this.getAllEntities();
        for (final EpicEntity entity : allEntities) {
            if (entity != winner) {
                if ((entity.isDeity() || entity.isWurm()) && !entity.isFriend(winner)) {
                    final HashMap<Integer, EpicEntity.SkillVal> skills = entity.getAllSkills();
                    for (final int id : skills.keySet()) {
                        final EpicEntity.SkillVal sv = skills.get(id);
                        entity.setSkill(id, sv.getCurrentVal() - (100.0f - sv.getCurrentVal()) / 50.0f);
                    }
                }
                if (entity.isDemigod() && !entity.isFriend(winner)) {
                    if (Valrei.rand.nextInt(15) == 0) {
                        this.broadCast(entity.getName() + " is put to eternal sleep and dismissed to the void!");
                        this.destroyEntity(entity);
                    }
                    else {
                        if (entity.getMapHex() != null) {
                            this.broadCast(entity.getName() + " is spared and will stay in " + entity.getMapHex().getName() + " for now.");
                        }
                        else {
                            this.broadCast(entity.getName() + " is spared and will stay on Valrei for now.");
                        }
                        final HashMap<Integer, EpicEntity.SkillVal> skills = entity.getAllSkills();
                        for (final int id : skills.keySet()) {
                            final EpicEntity.SkillVal sv = skills.get(id);
                            entity.setSkill(id, sv.getCurrentVal() - (100.0f - sv.getCurrentVal()) / 50.0f);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    String getReason(final int reasonId, final boolean many) {
        String firstPart = null;
        switch (reasonId) {
            case 0: {
                if (many) {
                    firstPart = "If we acquire these we will gain immense strength!";
                    break;
                }
                firstPart = "If we acquire this we will gain immense strength!";
                break;
            }
            case 1: {
                if (many) {
                    firstPart = "These can be used to deal a severe blow to our enemies!";
                    break;
                }
                firstPart = "It can be used to deal a severe blow to our enemies!";
                break;
            }
            case 2: {
                if (many) {
                    firstPart = "If we aquire these we will gain considerable power!";
                    break;
                }
                firstPart = "If we aquire this we will gain considerable power!";
                break;
            }
            case 3: {
                if (many) {
                    firstPart = "These must not fall into the claws of the Wurm!";
                    break;
                }
                firstPart = "This must not fall into the claws of the Wurm!";
                break;
            }
            case 4: {
                if (many) {
                    firstPart = "The power of these are not to be underestimated!";
                    break;
                }
                firstPart = "The power of this one is not to be underestimated!";
                break;
            }
            case 5: {
                if (many) {
                    firstPart = "They will contain soul traces of utter importance!";
                    break;
                }
                firstPart = "It will contain soul traces of utter importance!";
                break;
            }
            case 6: {
                if (many) {
                    firstPart = "Woe befalls our enemies if we lay our hands on them!";
                    break;
                }
                firstPart = "Woe befalls our enemies if we lay our hands on it!";
                break;
            }
            case 7: {
                if (many) {
                    firstPart = "Apparently, these can be used to diminish Wurm's power.";
                    break;
                }
                firstPart = "Apparently, this can be used to diminish Wurm's power.";
                break;
            }
            case 8: {
                if (many) {
                    firstPart = "These are needed to keep the Demons from Sol at bay!";
                    break;
                }
                firstPart = "This is needed to keep the Demons from Sol at bay!";
                break;
            }
            case 9: {
                if (many) {
                    firstPart = "We can use these to erupt our enemy lands!";
                    break;
                }
                firstPart = "We can use it to erupt our enemy lands!";
                break;
            }
            case 10: {
                if (many) {
                    firstPart = "These are said to be truly worth dying for.";
                    break;
                }
                firstPart = "It is said that this one is truly worth dying for.";
                break;
            }
            case 11: {
                if (many) {
                    firstPart = "These may cause mayhem if used by the wrong hands!";
                    break;
                }
                firstPart = "It may cause mayhem if used by the wrong hands!";
                break;
            }
            case 12: {
                if (many) {
                    firstPart = "Meteors will rain down on our enemies if we get hold of these!";
                    break;
                }
                firstPart = "Meteors will rain down on our enemies if we get hold of this!";
                break;
            }
            case 13: {
                if (many) {
                    firstPart = "These are used to protect us from the Deathcrawler's minions!";
                    break;
                }
                firstPart = "It is used to protect us from the Deathcrawler's minions!";
                break;
            }
            case 14: {
                if (many) {
                    firstPart = "Unless we recover these, we will be severely weakened!";
                    break;
                }
                firstPart = "Unless we recover this, we will be severely weakened!";
                break;
            }
            case 15: {
                if (many) {
                    firstPart = "We can create world-changing things with these!";
                    break;
                }
                firstPart = "We can create world-changing things with this one!";
                break;
            }
            case 16: {
                if (many) {
                    firstPart = "The insights gained from these now will be magnificent!";
                    break;
                }
                firstPart = "The insights gained from this now will be magnificent!";
                break;
            }
            case 17: {
                if (many) {
                    firstPart = "Great damage would befall us if they end up in enemy hands.";
                    break;
                }
                firstPart = "Great damage would befall us if it ends up in enemy hands.";
                break;
            }
            case 18: {
                if (many) {
                    firstPart = "Whoever finds these will rise in power!";
                    break;
                }
                firstPart = "Whoever finds this one will rise in power!";
                break;
            }
            case 19: {
                if (many) {
                    firstPart = "Immense power awaits for those who find these!";
                    break;
                }
                firstPart = "Immense power awaits for those who find this!";
                break;
            }
            default: {
                if (many) {
                    firstPart = "We cannot allow our enemies to get their hands on these!";
                    break;
                }
                firstPart = "We cannot allow our enemies to get their hands on this!";
                break;
            }
        }
        return firstPart;
    }
    
    public void testValreiFight(final Creature performer) {
        EpicEntity fighter1;
        EpicEntity fighter2;
        for (fighter1 = this.getEntity(Server.rand.nextInt(4) + 1), fighter2 = this.getEntity(Server.rand.nextInt(4) + 1); fighter2 == fighter1; fighter2 = this.getEntity(Server.rand.nextInt(4) + 1)) {}
        int wins1 = 0;
        int wins2 = 0;
        for (int i = 0; i < 1000; ++i) {
            final ValreiFight vFight = new ValreiFight(this.getSpawnHex(fighter1), fighter1, fighter2);
            final ValreiFightHistory fightHistory = vFight.completeFight(true);
            if (fightHistory.getFightWinner() == fighter1.getId()) {
                ++wins1;
            }
            else {
                ++wins2;
            }
        }
        performer.getCommunicator().sendNormalServerMessage("1000 fights completed between " + fighter1.getName() + " and " + fighter2.getName() + ".");
        performer.getCommunicator().sendNormalServerMessage(fighter1.getName() + " won " + wins1 / 10.0f + "% of fights, " + fighter2.getName() + " won the remaining " + wins2 / 10.0f + "%.");
    }
    
    public void testSingleValreiFight(final Creature performer, final Item source) {
        final EpicEntity fighter1 = this.getEntity(source.getData1());
        final EpicEntity fighter2 = this.getEntity(source.getData2());
        if (fighter1 == null) {
            performer.getCommunicator().sendNormalServerMessage("Invalid entity id for fighter 1: " + source.getData1() + ". Set a valid entity in Data1 of the " + source.getName() + ".");
        }
        else if (fighter2 == null) {
            performer.getCommunicator().sendNormalServerMessage("Invalid entity id for fighter 2: " + source.getData2() + ". Set a valid entity in Data2 of the " + source.getName() + ".");
        }
        else if (fighter1 == fighter2) {
            performer.getCommunicator().sendNormalServerMessage("Cannot fight two entities that are the same. Pick a different second entity by setting the Data2 of the " + source.getName() + ".");
        }
        if (fighter1 == null || fighter2 == null || fighter1 == fighter2) {
            return;
        }
        final ValreiFight vFight = new ValreiFight(this.getSpawnHex(fighter1), fighter1, fighter2);
        final ValreiFightHistory fightHistory = vFight.completeFight(false);
        ValreiFightHistoryManager.getInstance().addFight(fightHistory.getFightId(), fightHistory);
        if (Servers.localServer.LOGINSERVER) {
            final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)5);
            updater.sendFromLoginServer();
        }
        performer.getCommunicator().sendNormalServerMessage("Fight complete between " + fighter1.getName() + " and " + fighter2.getName() + ".");
    }
    
    static {
        Valrei.logger = Logger.getLogger(Valrei.class.getName());
    }
}