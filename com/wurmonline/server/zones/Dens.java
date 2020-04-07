// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.HashMap;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.Constants;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public final class Dens implements CreatureTemplateIds
{
    private static final String GET_DENS = "select * from DENS";
    private static final String DELETE_DEN = "DELETE FROM DENS  where TEMPLATEID=?";
    private static final String CREATE_DEN = "insert into DENS(TEMPLATEID,TILEX, TILEY, SURFACED) values(?,?,?,?)";
    private static final Logger logger;
    private static final Map<Integer, Den> dens;
    
    private static void addDen(final Den den) {
        Dens.dens.put(den.getTemplateId(), den);
    }
    
    private static void removeDen(final int templateId) {
        Dens.dens.remove(templateId);
    }
    
    public static Den getDen(final int templateId) {
        return Dens.dens.get(templateId);
    }
    
    public static Map<Integer, Den> getDens() {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends Den>)Dens.dens);
    }
    
    public static Den getDen(final int tilex, final int tiley) {
        for (final Den d : Dens.dens.values()) {
            if (d.getTilex() == tilex && d.getTiley() == tiley) {
                return d;
            }
        }
        return null;
    }
    
    public static void loadDens() {
        Dens.logger.info("Loading dens");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("select * from DENS");
            rs = ps.executeQuery();
            int tid = -1;
            int tilex = 0;
            int tiley = 0;
            boolean surfaced = false;
            while (rs.next()) {
                tid = rs.getInt("TEMPLATEID");
                tilex = rs.getInt("TILEX");
                tiley = rs.getInt("TILEY");
                surfaced = rs.getBoolean("SURFACED");
                if (tid > 0) {
                    final Den den = new Den(tid, tilex, tiley, surfaced);
                    addDen(den);
                    if (!Dens.logger.isLoggable(Level.FINE)) {
                        continue;
                    }
                    Dens.logger.fine("Loaded Den: " + den);
                }
            }
            checkDens(false);
        }
        catch (SQLException sqx) {
            Dens.logger.log(Level.WARNING, "Problem loading Dens - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Dens.logger.info("Loaded " + Dens.dens.size() + " dens from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static void checkDens(final boolean whileRunning) {
        checkTemplate(16, whileRunning);
        checkTemplate(89, whileRunning);
        checkTemplate(91, whileRunning);
        checkTemplate(90, whileRunning);
        checkTemplate(92, whileRunning);
        checkTemplate(17, whileRunning);
        checkTemplate(18, whileRunning);
        checkTemplate(19, whileRunning);
        checkTemplate(104, whileRunning);
        checkTemplate(103, whileRunning);
        checkTemplate(20, whileRunning);
        checkTemplate(22, whileRunning);
        checkTemplate(27, whileRunning);
        checkTemplate(11, whileRunning);
        checkTemplate(26, whileRunning);
        checkTemplate(23, whileRunning);
        Constants.respawnUniques = false;
    }
    
    private static final Den getDragonSpawnTop(final int templateId) {
        switch (templateId) {
            case 16: {
                return Zones.getNorthTop(templateId);
            }
            case 89: {
                return Zones.getWestTop(templateId);
            }
            case 91: {
                return Zones.getSouthTop(templateId);
            }
            case 90: {
                return Zones.getNorthTop(templateId);
            }
            case 92: {
                return Zones.getEastTop(templateId);
            }
            case 17: {
                return Zones.getSouthTop(templateId);
            }
            case 18: {
                return Zones.getEastTop(templateId);
            }
            case 19: {
                return Zones.getWestTop(templateId);
            }
            case 103: {
                return Zones.getSouthTop(templateId);
            }
            case 104: {
                return Zones.getNorthTop(templateId);
            }
            default: {
                return Zones.getRandomTop();
            }
        }
    }
    
    private static void checkTemplate(final int templateId, final boolean whileRunning) {
        try {
            final CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(templateId);
            final boolean creatureExists = Creatures.getInstance().creatureWithTemplateExists(templateId);
            if (!Constants.respawnUniques && !whileRunning) {
                return;
            }
            if (whileRunning && Server.rand.nextInt(300) > 0) {
                return;
            }
            if (!creatureExists) {
                final Den d = Dens.dens.get(templateId);
                if (d != null) {
                    deleteDen(templateId);
                }
            }
            if (!Dens.dens.containsKey(templateId)) {
                Den den = null;
                if (CreatureTemplate.isDragon(templateId)) {
                    if (!Servers.localServer.isChallengeServer()) {
                        if (Constants.respawnUniques) {
                            den = getDragonSpawnTop(templateId);
                        }
                        else if (Server.rand.nextBoolean()) {
                            den = Zones.getRandomTop();
                        }
                        else {
                            den = Zones.getRandomForest(templateId);
                        }
                        if (den != null) {
                            den = createDen(den.getTemplateId(), den.getTilex(), den.getTiley(), den.isSurfaced());
                        }
                    }
                }
                else {
                    if (template.getLeaderTemplateId() > 0) {
                        den = getDen(template.getLeaderTemplateId());
                        if (den != null) {
                            den.setTemplateId(templateId);
                        }
                    }
                    else {
                        den = Zones.getRandomForest(templateId);
                    }
                    if (den != null) {
                        den = createDen(den.getTemplateId(), den.getTilex(), den.getTiley(), den.isSurfaced());
                    }
                }
                if (den != null) {
                    if (template.isUnique()) {
                        final VolaTile villtile = Zones.getOrCreateTile(den.getTilex(), den.getTiley(), den.isSurfaced());
                        final Village vill = villtile.getVillage();
                        if (vill != null) {
                            Dens.logger.log(Level.INFO, "Unique spawn " + template.getName() + ", on deed " + vill.getName() + ".");
                            removeDen(templateId);
                            return;
                        }
                    }
                    if (!template.isUnique()) {
                        try {
                            final Zone zone = Zones.getZone(den.getTilex(), den.getTiley(), den.isSurfaced());
                            zone.den = den;
                            Dens.logger.log(Level.INFO, "Zone at " + den.getTilex() + ", " + den.getTiley() + " now spawning " + template.getName() + " (" + den.getTemplateId() + ")");
                        }
                        catch (NoSuchZoneException nsz) {
                            Dens.logger.log(Level.WARNING, "Den at " + den.getTilex() + ", " + den.getTiley() + " surf=" + den.isSurfaced() + " - zone does not exist.");
                        }
                    }
                    else if (!creatureExists) {
                        byte ctype = (byte)Math.max(0, Server.rand.nextInt(22) - 10);
                        if (Server.rand.nextInt(3) < 2) {
                            ctype = 0;
                        }
                        if (Server.rand.nextInt(40) == 0) {
                            ctype = 99;
                        }
                        try {
                            Creature.doNew(templateId, ctype, (den.getTilex() << 2) + 2, (den.getTiley() << 2) + 2, 180.0f, den.isSurfaced() ? 0 : -1, template.getName(), template.getSex());
                            Dens.logger.log(Level.INFO, "Created " + template.getName() + " at " + den.getTilex() + "," + den.getTiley() + "!");
                        }
                        catch (Exception ex) {
                            Dens.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                    addDen(den);
                }
            }
            else if (!template.isUnique()) {
                final Den den = getDen(templateId);
                try {
                    final Zone zone = Zones.getZone(den.getTilex(), den.getTiley(), den.isSurfaced());
                    zone.den = den;
                    Dens.logger.log(Level.INFO, "Zone at " + den.getTilex() + ", " + den.getTiley() + " now spawning " + template.getName() + " (" + den.getTemplateId() + ")");
                }
                catch (NoSuchZoneException nsz) {
                    Dens.logger.log(Level.WARNING, "Den at " + den.getTilex() + ", " + den.getTiley() + " surf=" + den.isSurfaced() + " - zone does not exist.");
                }
            }
        }
        catch (NoSuchCreatureTemplateException nst) {
            Dens.logger.log(Level.WARNING, templateId + ":" + nst.getMessage(), nst);
        }
    }
    
    public static void deleteDen(final int templateId) {
        Dens.logger.log(Level.INFO, "Deleting den for " + templateId);
        final Den d = getDen(templateId);
        if (d != null) {
            Dens.logger.log(Level.INFO, "Den for " + templateId + " was at " + d.getTilex() + "," + d.getTiley());
        }
        removeDen(templateId);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM DENS  where TEMPLATEID=?");
            ps.setInt(1, templateId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Dens.logger.log(Level.WARNING, templateId + ":" + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static Den createDen(final int templateId, final int tilex, final int tiley, final boolean surfaced) {
        final Den den = new Den(templateId, tilex, tiley, surfaced);
        addDen(den);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("insert into DENS(TEMPLATEID,TILEX, TILEY, SURFACED) values(?,?,?,?)");
            ps.setInt(1, templateId);
            ps.setInt(2, tilex);
            ps.setInt(3, tiley);
            ps.setBoolean(4, surfaced);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Dens.logger.log(Level.WARNING, templateId + ":" + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return den;
    }
    
    static {
        logger = Logger.getLogger(Dens.class.getName());
        dens = new HashMap<Integer, Den>();
    }
}
