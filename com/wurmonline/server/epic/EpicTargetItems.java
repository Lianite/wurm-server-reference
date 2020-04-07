// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import javax.annotation.Nullable;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.behaviours.Terraforming;
import java.util.Random;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class EpicTargetItems implements MiscConstants
{
    private final long[] epicTargetItems;
    private static final String LOAD_ALL_TARGET_ITEMS = "SELECT * FROM EPICTARGETITEMS WHERE KINGDOM=?";
    private static final String UPDATE_TARGET_ITEMS = "UPDATE EPICTARGETITEMS SET PILLARONE=?,PILLARTWO=?,PILLARTHREE=?,OBELISQUEONE=?,OBELISQUETWO=?,OBELISQUETHREE=?,PYLONONE=?,PYLONTWO=?,PYLONTHREE=?,TEMPLEONE=?,TEMPLETWO=?,TEMPLETHREE=?,SHRINEONE=?,SHRINETWO=?,SHRINETHREE=?,SPIRITGATEONE=?,SPIRITGATETWO=?,SPIRITGATETHREE=? WHERE KINGDOM=?";
    private static final String INSERT_TARGET_ITEMS = "INSERT INTO EPICTARGETITEMS (KINGDOM) VALUES(?)";
    private final byte kingdomId;
    static final int PILLAR_ONE = 0;
    static final int PILLAR_TWO = 1;
    static final int PILLAR_THREE = 2;
    static final int OBELISK_ONE = 3;
    static final int OBELISK_TWO = 4;
    static final int OBELISK_THREE = 5;
    static final int PYLON_ONE = 6;
    static final int PYLON_TWO = 7;
    static final int PYLON_THREE = 8;
    static final int TEMPLE_ONE = 9;
    static final int TEMPLE_TWO = 10;
    static final int TEMPLE_THREE = 11;
    static final int SHRINE_ONE = 12;
    static final int SHRINE_TWO = 13;
    static final int SHRINE_THREE = 14;
    static final int SPIRIT_GATE_ONE = 15;
    static final int SPIRIT_GATE_TWO = 16;
    static final int SPIRIT_GATE_THREE = 17;
    private static final Logger logger;
    private static final Map<Byte, EpicTargetItems> KINGDOM_ITEMS;
    private static final ArrayList<Item> ritualTargetItems;
    
    public EpicTargetItems(final byte kingdomTemplateId) {
        this.epicTargetItems = new long[18];
        this.kingdomId = kingdomTemplateId;
        this.loadAll();
        MissionHelper.loadAll();
    }
    
    public static void removeRitualTargetItem(final Item ritualItem) {
        if (EpicTargetItems.ritualTargetItems.contains(ritualItem)) {
            EpicTargetItems.ritualTargetItems.remove(ritualItem);
        }
    }
    
    public static void addRitualTargetItem(final Item ritualItem) {
        if (ritualItem == null) {
            return;
        }
        if (!ritualItem.isEpicTargetItem()) {
            return;
        }
        if (ritualItem.isUnfinished()) {
            return;
        }
        if (EpicTargetItems.ritualTargetItems.contains(ritualItem)) {
            return;
        }
        EpicTargetItems.ritualTargetItems.add(ritualItem);
    }
    
    public static Item getRandomRitualTarget() {
        if (EpicTargetItems.ritualTargetItems.isEmpty()) {
            return null;
        }
        return EpicTargetItems.ritualTargetItems.get(Server.rand.nextInt(EpicTargetItems.ritualTargetItems.size()));
    }
    
    public static final EpicTargetItems getEpicTargets(final byte kingdomTemplateId) {
        EpicTargetItems toReturn = EpicTargetItems.KINGDOM_ITEMS.get(kingdomTemplateId);
        if (toReturn == null) {
            toReturn = new EpicTargetItems(kingdomTemplateId);
            EpicTargetItems.KINGDOM_ITEMS.put(kingdomTemplateId, toReturn);
        }
        return toReturn;
    }
    
    public static final boolean isItemAlreadyEpic(final Item itemChecked) {
        for (final EpicTargetItems etis : EpicTargetItems.KINGDOM_ITEMS.values()) {
            for (final long item : etis.epicTargetItems) {
                if (item == itemChecked.getWurmId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final byte getKingdomTemplateId() {
        return this.kingdomId;
    }
    
    public final void loadAll() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean found = false;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM EPICTARGETITEMS WHERE KINGDOM=?");
            ps.setByte(1, this.kingdomId);
            rs = ps.executeQuery();
            while (rs.next()) {
                rs.getByte(1);
                for (int x = 0; x <= 17; ++x) {
                    this.epicTargetItems[x] = rs.getLong(x + 2);
                }
                found = true;
            }
        }
        catch (SQLException sqx) {
            EpicTargetItems.logger.log(Level.WARNING, "Failed to load epic target items.", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        if (!found) {
            this.initialize();
        }
    }
    
    private final void initialize() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO EPICTARGETITEMS (KINGDOM) VALUES(?)");
            ps.setByte(1, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicTargetItems.logger.log(Level.WARNING, "Failed to save epic target status for kingdom " + this.kingdomId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void testSetCounter(final int toSet, final long wid) {
        this.epicTargetItems[toSet] = wid;
        this.update();
    }
    
    public static final boolean isEpicItemWithMission(final Item epicItem) {
        for (final EpicMission m : EpicServerStatus.getCurrentEpicMissions()) {
            boolean correctItem = false;
            switch (m.getMissionType()) {
                case 101: {
                    if (epicItem.getTemplateId() == 717 || epicItem.getTemplateId() == 712) {
                        correctItem = true;
                        break;
                    }
                    break;
                }
                case 102: {
                    if (epicItem.getTemplateId() == 715 || epicItem.getTemplateId() == 714) {
                        correctItem = true;
                        break;
                    }
                    break;
                }
                case 103: {
                    if (epicItem.getTemplateId() == 713 || epicItem.getTemplateId() == 716) {
                        correctItem = true;
                        break;
                    }
                    break;
                }
            }
            if (correctItem) {
                final int placementLocation = getTargetItemPlacement(m.getMissionId());
                final int itemLocation = epicItem.getGlobalMapPlacement();
                if (itemLocation == placementLocation) {
                    return true;
                }
                if (placementLocation == 0 && epicItem.isInTheNorth()) {
                    return true;
                }
                if (placementLocation == 2 && epicItem.isInTheEast()) {
                    return true;
                }
                if (placementLocation == 4 && epicItem.isInTheSouth()) {
                    return true;
                }
                if (placementLocation == 6 && epicItem.isInTheWest()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final boolean addEpicItem(final Item epicItem, final Creature performer) {
        if (epicItem.isEpicTargetItem()) {
            if (mayBuildEpicItem(epicItem.getTemplateId(), epicItem.getTileX(), epicItem.getTileY(), epicItem.isOnSurface(), performer, performer.getKingdomTemplateId())) {
                if (epicItem.getGlobalMapPlacement() == this.getGlobalMapPlacementRequirement(epicItem.getTemplateId())) {
                    EpicTargetItems.logger.log(Level.INFO, performer.getName() + " Correct placement for " + epicItem);
                    performer.sendToLoggers("Correct placement for " + epicItem, (byte)2);
                    final int toSet = this.getCurrentCounter(epicItem.getTemplateId());
                    this.epicTargetItems[toSet] = epicItem.getWurmId();
                    this.update();
                    return true;
                }
                EpicTargetItems.logger.log(Level.INFO, performer.getName() + " Not proper map placement " + epicItem.getGlobalMapPlacement() + " for " + epicItem.getName() + " here at " + epicItem.getTileX() + "," + epicItem.getTileY() + ": Required " + this.getGlobalMapPlacementRequirement(epicItem.getTemplateId()));
                performer.sendToLoggers("Not proper map placement " + epicItem.getGlobalMapPlacement() + " for " + epicItem.getName() + " here at " + epicItem.getTileX() + "," + epicItem.getTileY() + ": Required " + this.getGlobalMapPlacementRequirement(epicItem.getTemplateId()), (byte)2);
            }
            else {
                performer.sendToLoggers("May not build " + epicItem.getName() + " here at " + epicItem.getTileX() + "," + epicItem.getTileY(), (byte)2);
            }
        }
        return false;
    }
    
    public final int testGetCurrentCounter(final int templateId) {
        return this.getCurrentCounter(templateId);
    }
    
    public final int getCurrentCounter(final int itemTemplateId) {
        int toReturn = -1;
        switch (itemTemplateId) {
            case 717: {
                if (this.epicTargetItems[0] == 0L) {
                    return 0;
                }
                if (this.epicTargetItems[1] == 0L) {
                    return 1;
                }
                if (this.epicTargetItems[2] == 0L) {
                    return 2;
                }
                break;
            }
            case 714: {
                if (this.epicTargetItems[3] == 0L) {
                    return 3;
                }
                if (this.epicTargetItems[4] == 0L) {
                    return 4;
                }
                if (this.epicTargetItems[5] == 0L) {
                    return 5;
                }
                break;
            }
            case 713: {
                if (this.epicTargetItems[6] == 0L) {
                    return 6;
                }
                if (this.epicTargetItems[7] == 0L) {
                    return 7;
                }
                if (this.epicTargetItems[8] == 0L) {
                    return 8;
                }
                break;
            }
            case 712: {
                if (this.epicTargetItems[12] == 0L) {
                    return 12;
                }
                if (this.epicTargetItems[13] == 0L) {
                    return 13;
                }
                if (this.epicTargetItems[14] == 0L) {
                    return 14;
                }
                break;
            }
            case 715: {
                if (this.epicTargetItems[9] == 0L) {
                    return 9;
                }
                if (this.epicTargetItems[10] == 0L) {
                    return 10;
                }
                if (this.epicTargetItems[11] == 0L) {
                    return 11;
                }
                break;
            }
            case 716: {
                if (this.epicTargetItems[15] == 0L) {
                    return 15;
                }
                if (this.epicTargetItems[16] == 0L) {
                    return 16;
                }
                if (this.epicTargetItems[17] == 0L) {
                    return 17;
                }
                break;
            }
            default: {
                toReturn = -1;
                break;
            }
        }
        return toReturn;
    }
    
    public static final String getSymbolNamePartString(final Creature performer) {
        String toReturn = "Faith";
        final int rand = Server.rand.nextInt(50);
        final byte kingdomId = performer.getKingdomTemplateId();
        switch (rand) {
            case 0: {
                toReturn = "Secrets";
                break;
            }
            case 1: {
                if (kingdomId == 3) {
                    toReturn = "Libila";
                    break;
                }
                if (kingdomId == 2) {
                    toReturn = "Magranon";
                    break;
                }
                if (performer.getDeity() != null && performer.getDeity().number == 1) {
                    toReturn = "Fo";
                    break;
                }
                toReturn = "Vynora";
                break;
            }
            case 2: {
                if (kingdomId == 3) {
                    toReturn = "Hate";
                    break;
                }
                if (kingdomId == 2) {
                    toReturn = "Fire";
                    break;
                }
                if (performer.getDeity() != null && performer.getDeity().number == 1) {
                    toReturn = "Love";
                    break;
                }
                toReturn = "Mysteries";
                break;
            }
            case 3: {
                if (kingdomId == 3) {
                    toReturn = "Revenge";
                    break;
                }
                if (kingdomId == 2) {
                    toReturn = "Power";
                    break;
                }
                if (performer.getDeity() != null && performer.getDeity().number == 1) {
                    toReturn = "Compassion";
                    break;
                }
                toReturn = "Wisdom";
                break;
            }
            case 4: {
                if (kingdomId == 3) {
                    toReturn = "Death";
                    break;
                }
                if (kingdomId == 2) {
                    toReturn = "Sand";
                    break;
                }
                if (performer.getDeity() != null && performer.getDeity().number == 1) {
                    toReturn = "Tree";
                    break;
                }
                toReturn = "Water";
                break;
            }
            case 5: {
                toReturn = "Spirit";
                break;
            }
            case 6: {
                toReturn = "Soul";
                break;
            }
            case 7: {
                toReturn = "Hope";
                break;
            }
            case 8: {
                toReturn = "Despair";
                break;
            }
            case 9: {
                toReturn = "Luck";
                break;
            }
            case 10: {
                toReturn = "Heaven";
                break;
            }
            case 11: {
                toReturn = "Valrei";
                break;
            }
            case 12: {
                toReturn = "Strength";
                break;
            }
            case 13: {
                toReturn = "Sleep";
                break;
            }
            case 14: {
                toReturn = "Tongue";
                break;
            }
            case 15: {
                toReturn = "Dreams";
                break;
            }
            case 16: {
                toReturn = "Enlightened";
                break;
            }
            case 17: {
                toReturn = "Fool";
                break;
            }
            case 18: {
                toReturn = "Cat";
                break;
            }
            case 19: {
                toReturn = "Troll";
                break;
            }
            case 20: {
                toReturn = "Dragon";
                break;
            }
            case 21: {
                toReturn = "Deep";
                break;
            }
            case 22: {
                toReturn = "Square";
                break;
            }
            case 23: {
                toReturn = "Song";
                break;
            }
            case 24: {
                toReturn = "Jump";
                break;
            }
            case 25: {
                toReturn = "High";
                break;
            }
            case 26: {
                toReturn = "Low";
                break;
            }
            case 27: {
                toReturn = "Inbetween";
                break;
            }
            case 28: {
                toReturn = "One";
                break;
            }
            case 29: {
                toReturn = "Many";
                break;
            }
            case 30: {
                toReturn = "Sorrow";
                break;
            }
            case 31: {
                toReturn = "Pain";
                break;
            }
            case 32: {
                toReturn = "Oracle";
                break;
            }
            case 33: {
                toReturn = "Slithering";
                break;
            }
            case 34: {
                toReturn = "Roundabout";
                break;
            }
            case 35: {
                toReturn = "Winter";
                break;
            }
            case 36: {
                toReturn = "Summer";
                break;
            }
            case 37: {
                toReturn = "Fallen";
                break;
            }
            case 38: {
                toReturn = "Cherry";
                break;
            }
            case 39: {
                toReturn = "Innocent";
                break;
            }
            case 40: {
                toReturn = "Demon";
                break;
            }
            case 41: {
                toReturn = "Left";
                break;
            }
            case 42: {
                toReturn = "Shard";
                break;
            }
            case 43: {
                toReturn = "Mantra";
                break;
            }
            case 44: {
                toReturn = "Island";
                break;
            }
            case 45: {
                toReturn = "Seafarer";
                break;
            }
            case 46: {
                toReturn = "Ascendant";
                break;
            }
            case 47: {
                toReturn = "Shame";
                break;
            }
            case 48: {
                toReturn = "Running";
                break;
            }
            case 49: {
                toReturn = "Lamentation";
                break;
            }
            default: {
                toReturn = "Figure";
                break;
            }
        }
        return toReturn;
    }
    
    public static final String getTypeNamePartString(final int itemTemplateId) {
        String toReturn = "Focus";
        final int rand = Server.rand.nextInt(10);
        toReturn = getTypeNamePartStringWithPart(itemTemplateId, rand);
        return toReturn;
    }
    
    static final String getTypeNamePartStringWithPart(final int itemTemplateId, final int partId) {
        String toReturn = null;
        Label_0765: {
            switch (itemTemplateId) {
                case 717: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Pillar";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Foundation";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Ram";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "Symbol";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Tower";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Post";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Column";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Backbone";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Menhir";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Last Stand";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Pillar";
                            break Label_0765;
                        }
                    }
                    break;
                }
                case 714: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Needle";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Fist";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Obelisk";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "Charge";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Mantra";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Testimonial";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Trophy";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Stand";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Spear";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Challenge";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Obelisk";
                            break Label_0765;
                        }
                    }
                    break;
                }
                case 713: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Memento";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Monument";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Path";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "Way";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Door";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Victorial";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Shield";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Passage";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Rest";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Gate";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Pylon";
                            break Label_0765;
                        }
                    }
                    break;
                }
                case 712: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Shrine";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Barrow";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Vault";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "Long Home";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Mausoleum";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Chamber";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Reliquary";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Remembrance";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Sacrarium";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Sanctum";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Shrine";
                            break Label_0765;
                        }
                    }
                    break;
                }
                case 715: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Church";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Temple";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Hand";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "House";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Sanctuary";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Chapel";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Abode";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Walls";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Sign";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Fist";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Temple";
                            break Label_0765;
                        }
                    }
                    break;
                }
                case 716: {
                    switch (partId) {
                        case 0: {
                            toReturn = "Pathway";
                            break Label_0765;
                        }
                        case 1: {
                            toReturn = "Mirror";
                            break Label_0765;
                        }
                        case 2: {
                            toReturn = "Mystery";
                            break Label_0765;
                        }
                        case 3: {
                            toReturn = "Gate";
                            break Label_0765;
                        }
                        case 4: {
                            toReturn = "Shimmer";
                            break Label_0765;
                        }
                        case 5: {
                            toReturn = "Route";
                            break Label_0765;
                        }
                        case 6: {
                            toReturn = "Run";
                            break Label_0765;
                        }
                        case 7: {
                            toReturn = "Trail";
                            break Label_0765;
                        }
                        case 8: {
                            toReturn = "Wake";
                            break Label_0765;
                        }
                        case 9: {
                            toReturn = "Secret";
                            break Label_0765;
                        }
                        default: {
                            toReturn = "Gate";
                            break Label_0765;
                        }
                    }
                    break;
                }
                default: {
                    toReturn = "Monument";
                    break;
                }
            }
        }
        return toReturn;
    }
    
    public final String getInstructionString(final int itemTemplateId) {
        return getInstructionStringForKingdom(itemTemplateId, this.kingdomId);
    }
    
    public static final String getInstructionStringForKingdom(final int itemTemplateId, final byte aKingdomId) {
        String toReturn = null;
        switch (itemTemplateId) {
            case 717: {
                toReturn = "This should be built in the darkness of a cave with sufficient ceiling height, not inside a settlement, and on a flat surface.";
                break;
            }
            case 714: {
                toReturn = "This must be constructed on a 3x3 slabbed area, not inside a settlement, and on a flat surface.";
                break;
            }
            case 713: {
                toReturn = "This must be constructed on a 7x7 slabbed area close to water, not inside a settlement, and on a flat surface.";
                break;
            }
            case 712: {
                toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. A couple of fruit trees or bushes must be within 5 tiles.";
                break;
            }
            case 715: {
                if (aKingdomId == 3) {
                    toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. It must be within 5 tiles of marsh or mycelium.";
                    break;
                }
                toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. It must be built higher up than 100 steps.";
                break;
            }
            case 716: {
                if (aKingdomId != 3) {
                    toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. It must be built higher up than 100 steps.";
                    break;
                }
                if (Servers.localServer.PVPSERVER) {
                    toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. It must be within 5 tiles of marsh as well as mycelium.";
                    break;
                }
                toReturn = "This must be constructed on a 5x5 slabbed area, not inside a settlement, and on a flat surface. It must be within 5 tiles of marsh as well as moss.";
                break;
            }
            default: {
                toReturn = "It is not the right time to build this now.";
                break;
            }
        }
        return toReturn;
    }
    
    public final String getGlobalMapPlacementRequirementString(final int itemTemplateId) {
        final int placement = this.getGlobalMapPlacementRequirement(itemTemplateId);
        String toReturn = null;
        switch (placement) {
            case 3: {
                toReturn = "This must be built in the south east.";
                break;
            }
            case 5: {
                toReturn = "This must be built in the south west.";
                break;
            }
            case 7: {
                toReturn = "This must be built in the north west.";
                break;
            }
            case 1: {
                toReturn = "This must be built in the north east.";
                break;
            }
            default: {
                toReturn = "It is not the right time to build this now.";
                break;
            }
        }
        return toReturn;
    }
    
    public final int getGlobalMapPlacementRequirement(final int itemTemplateId) {
        final int counter = this.getCurrentCounter(itemTemplateId);
        int toReturn = 0;
        if (counter <= -1) {
            return toReturn;
        }
        toReturn = getGlobalMapPlacementRequirementWithCounter(itemTemplateId, counter, this.kingdomId);
        return toReturn;
    }
    
    public static final String getTargetItemPlacementString(final int placementLocation) {
        switch (placementLocation) {
            case 0: {
                return "This must be built in the north.";
            }
            case 1: {
                return "This must be built in the northeast.";
            }
            case 2: {
                return "This must be built in the east.";
            }
            case 3: {
                return "This must be built in the southeast.";
            }
            case 4: {
                return "This must be built in the south.";
            }
            case 5: {
                return "This must be built in the southwest.";
            }
            case 6: {
                return "This must be built in the west.";
            }
            case 7: {
                return "This must be built in the northwest.";
            }
            default: {
                return "It is not the right time to build this now.";
            }
        }
    }
    
    public static final int getTargetItemPlacement(final int missionId) {
        final Random r = new Random(missionId);
        return r.nextInt(8);
    }
    
    static final int getGlobalMapPlacementRequirementWithCounter(final int aItemTemplateId, final int aCounter, final byte aKingdomId) {
        int toReturn = 0;
        Label_1124: {
            switch (aItemTemplateId) {
                case 717: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 0: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                case 1: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 2: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 0: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 1: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 2: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 0: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 1: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 2: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                case 714: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 3: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 4: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 5: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 3: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 4: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 5: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 3: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 4: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 5: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                case 713: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 6: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 7: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 8: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 6: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                case 7: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 8: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 6: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 7: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 8: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                case 712: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 12: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 13: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 14: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 12: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 13: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 14: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 12: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 13: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 14: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                case 715: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 9: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 10: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 11: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 9: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 10: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                case 11: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 9: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 10: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 11: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                case 716: {
                    switch (aKingdomId) {
                        case 2: {
                            switch (aCounter) {
                                case 15: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 16: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 17: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 3: {
                            switch (aCounter) {
                                case 15: {
                                    toReturn = 1;
                                    break Label_1124;
                                }
                                case 16: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                case 17: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        case 1:
                        case 4: {
                            switch (aCounter) {
                                case 15: {
                                    toReturn = 5;
                                    break Label_1124;
                                }
                                case 16: {
                                    toReturn = 3;
                                    break Label_1124;
                                }
                                case 17: {
                                    toReturn = 7;
                                    break Label_1124;
                                }
                                default: {
                                    toReturn = 0;
                                    break Label_1124;
                                }
                            }
                            break;
                        }
                        default: {
                            toReturn = 0;
                            break Label_1124;
                        }
                    }
                    break;
                }
                default: {
                    toReturn = 0;
                    break;
                }
            }
        }
        return toReturn;
    }
    
    public static final boolean mayBuildEpicItem(final int itemTemplateId, final int tilex, final int tiley, final boolean surfaced, final Creature performer, final byte kingdomTemplateId) {
        if (!Terraforming.isFlat(tilex, tiley, surfaced, 4)) {
            performer.sendToLoggers("The tile is not flat", (byte)2);
            return false;
        }
        if (Villages.getVillage(tilex, tiley, surfaced) != null) {
            return false;
        }
        boolean toReturn = true;
        switch (itemTemplateId) {
            case 717: {
                toReturn = false;
                if (!surfaced) {
                    toReturn = true;
                    final int cornerNorthW = Server.caveMesh.getTile(tilex, tiley);
                    final short ceilHeight = (short)(Tiles.decodeData(cornerNorthW) & 0xFF);
                    if (ceilHeight < 50) {
                        performer.sendToLoggers("The NW corner is too low " + ceilHeight, (byte)2);
                        toReturn = false;
                    }
                    final int cornerNorthE = Server.caveMesh.getTile(tilex + 1, tiley);
                    final short ceilHeightNE = (short)(Tiles.decodeData(Tiles.decodeTileData(cornerNorthE)) & 0xFF);
                    if (ceilHeightNE < 50) {
                        performer.sendToLoggers("The NE corner is too low " + ceilHeightNE, (byte)2);
                        toReturn = false;
                    }
                    final int cornerSE = Server.caveMesh.getTile(tilex + 1, tiley + 1);
                    final short ceilHeightSE = (short)(Tiles.decodeData(Tiles.decodeTileData(cornerSE)) & 0xFF);
                    if (ceilHeightSE < 50) {
                        performer.sendToLoggers("The SE corner is too low " + ceilHeightSE, (byte)2);
                        toReturn = false;
                    }
                    final int cornerSW = Server.caveMesh.getTile(tilex, tiley + 1);
                    final short ceilHeightSW = (short)(Tiles.decodeData(Tiles.decodeTileData(cornerSW)) & 0xFF);
                    if (ceilHeightSW < 50) {
                        performer.sendToLoggers("The SW corner is too low " + ceilHeightSW, (byte)2);
                        toReturn = false;
                    }
                    break;
                }
                performer.sendToLoggers("The pillar is on the surface!", (byte)2);
                break;
            }
            case 714: {
                toReturn = true;
                for (int x = Zones.safeTileX(tilex - 1); x <= Zones.safeTileX(tilex + 1); ++x) {
                    for (int y = Zones.safeTileY(tiley - 1); y <= Zones.safeTileY(tiley + 1); ++y) {
                        if (!Terraforming.isFlat(x, y, true, 4)) {
                            toReturn = false;
                        }
                        if (!Tiles.isRoadType(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                            toReturn = false;
                        }
                    }
                }
                break;
            }
            case 713: {
                toReturn = true;
                for (int x = Zones.safeTileX(tilex - 3); x <= Zones.safeTileX(tilex + 3); ++x) {
                    for (int y = Zones.safeTileY(tiley - 3); y <= Zones.safeTileY(tiley + 3); ++y) {
                        if (!Terraforming.isFlat(x, y, true, 4)) {
                            toReturn = false;
                            break;
                        }
                        if (!Tiles.isRoadType(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                            toReturn = false;
                            break;
                        }
                    }
                }
                if (toReturn) {
                    toReturn = false;
                    for (int x = Zones.safeTileX(tilex - 10); x <= Zones.safeTileX(tilex + 10); x += 5) {
                        for (int y = Zones.safeTileY(tiley - 10); y <= Zones.safeTileY(tiley + 10); y += 5) {
                            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) < 0) {
                                toReturn = true;
                                break;
                            }
                        }
                    }
                    break;
                }
                break;
            }
            case 712: {
                for (int x = Zones.safeTileX(tilex - 2); x <= Zones.safeTileX(tilex + 2); ++x) {
                    for (int y = Zones.safeTileY(tiley - 2); y <= Zones.safeTileY(tiley + 2); ++y) {
                        if (!Terraforming.isFlat(x, y, true, 4)) {
                            toReturn = false;
                            break;
                        }
                        if (!Tiles.isRoadType(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                            toReturn = false;
                            break;
                        }
                    }
                }
                if (toReturn) {
                    toReturn = false;
                    int numSmalltrees = 0;
                    for (int x2 = Zones.safeTileX(tilex - 5); x2 <= Zones.safeTileX(tilex + 5); ++x2) {
                        for (int y2 = Zones.safeTileY(tiley - 5); y2 <= Zones.safeTileY(tiley + 5); ++y2) {
                            final int t = Server.surfaceMesh.getTile(x2, y2);
                            final Tiles.Tile theTile = Tiles.getTile(Tiles.decodeType(t));
                            final byte data = Tiles.decodeData(t);
                            if (theTile.isNormalTree()) {
                                if (theTile.getTreeType(data).isFruitTree()) {
                                    ++numSmalltrees;
                                }
                            }
                            else if (theTile.isMyceliumTree() && kingdomTemplateId == 3 && theTile.getTreeType(data).isFruitTree()) {
                                ++numSmalltrees;
                            }
                        }
                    }
                    if (numSmalltrees > 3) {
                        toReturn = true;
                    }
                    break;
                }
                break;
            }
            case 715: {
                for (int x = Zones.safeTileX(tilex - 2); x <= Zones.safeTileX(tilex + 2); ++x) {
                    for (int y = Zones.safeTileY(tiley - 2); y <= Zones.safeTileY(tiley + 2); ++y) {
                        if (!Terraforming.isFlat(x, y, true, 4)) {
                            toReturn = false;
                            break;
                        }
                        if (!Tiles.isRoadType(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                            toReturn = false;
                            break;
                        }
                    }
                }
                if (!toReturn) {
                    break;
                }
                toReturn = false;
                if (kingdomTemplateId == 3) {
                    for (int x = Zones.safeTileX(tilex - 5); x <= Zones.safeTileX(tilex + 5); ++x) {
                        for (int y = Zones.safeTileY(tiley - 5); y <= Zones.safeTileY(tiley + 5); ++y) {
                            final int t2 = Server.surfaceMesh.getTile(x, y);
                            final byte type = Tiles.decodeType(t2);
                            if (Tiles.decodeType(t2) == Tiles.Tile.TILE_MARSH.id || type == Tiles.Tile.TILE_MYCELIUM.id || Tiles.getTile(type).isMyceliumTree()) {
                                toReturn = true;
                                break;
                            }
                        }
                    }
                    break;
                }
                final int t3 = Server.surfaceMesh.getTile(tilex, tiley);
                if (Tiles.decodeHeight(t3) > 1000) {
                    toReturn = true;
                }
                break;
            }
            case 716: {
                for (int x = Zones.safeTileX(tilex - 2); x <= Zones.safeTileX(tilex + 2); ++x) {
                    for (int y = Zones.safeTileY(tiley - 2); y <= Zones.safeTileY(tiley + 2); ++y) {
                        if (!Terraforming.isFlat(x, y, true, 4)) {
                            toReturn = false;
                            break;
                        }
                        if (!Tiles.isRoadType(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                            toReturn = false;
                            break;
                        }
                    }
                }
                if (!toReturn) {
                    break;
                }
                toReturn = false;
                if (kingdomTemplateId == 3) {
                    boolean foundMycel = false;
                    boolean foundMarsh = false;
                    for (int x3 = Zones.safeTileX(tilex - 5); x3 <= Zones.safeTileX(tilex + 5); ++x3) {
                        for (int y3 = Zones.safeTileY(tiley - 5); y3 <= Zones.safeTileY(tiley + 5); ++y3) {
                            final int t4 = Server.surfaceMesh.getTile(x3, y3);
                            if (Servers.localServer.PVPSERVER) {
                                final byte type2 = Tiles.decodeType(t4);
                                if (type2 == Tiles.Tile.TILE_MYCELIUM.id || Tiles.getTile(type2).isMyceliumTree()) {
                                    foundMycel = true;
                                    continue;
                                }
                            }
                            else if (Tiles.decodeType(t4) == Tiles.Tile.TILE_MOSS.id) {
                                foundMycel = true;
                                continue;
                            }
                            if (Tiles.decodeType(t4) == Tiles.Tile.TILE_MARSH.id) {
                                foundMarsh = true;
                            }
                        }
                    }
                    if (foundMycel && foundMarsh) {
                        toReturn = true;
                    }
                    break;
                }
                final int t3 = Server.surfaceMesh.getTile(tilex, tiley);
                if (Tiles.decodeHeight(t3) > 1000) {
                    toReturn = true;
                }
                break;
            }
            default: {
                toReturn = false;
                break;
            }
        }
        return toReturn;
    }
    
    private final void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE EPICTARGETITEMS SET PILLARONE=?,PILLARTWO=?,PILLARTHREE=?,OBELISQUEONE=?,OBELISQUETWO=?,OBELISQUETHREE=?,PYLONONE=?,PYLONTWO=?,PYLONTHREE=?,TEMPLEONE=?,TEMPLETWO=?,TEMPLETHREE=?,SHRINEONE=?,SHRINETWO=?,SHRINETHREE=?,SPIRITGATEONE=?,SPIRITGATETWO=?,SPIRITGATETHREE=? WHERE KINGDOM=?");
            for (int x = 0; x <= 17; ++x) {
                ps.setLong(x + 1, this.epicTargetItems[x]);
            }
            ps.setByte(19, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicTargetItems.logger.log(Level.WARNING, "Failed to save epic target status for kingdom " + this.kingdomId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    final long getRandomTarget() {
        return this.getRandomTarget(0, 0, null);
    }
    
    private final long getRandomTarget(final int attempts, final int targetTemplate, @Nullable ArrayList<Long> itemList) {
        long itemFound = -1L;
        if (Servers.localServer.PVPSERVER) {
            int numsExisting = 0;
            for (int x = 0; x < 17; ++x) {
                if (this.epicTargetItems[x] > 0L) {
                    ++numsExisting;
                }
            }
            if (numsExisting > 0) {
                for (int x = 0; x < 17; ++x) {
                    if (this.epicTargetItems[x] > 0L) {
                        try {
                            final Item eti = Items.getItem(this.epicTargetItems[x]);
                            final Village v = Villages.getVillage(eti.getTilePos(), eti.isOnSurface());
                            if (v == null) {
                                if (itemFound == -1L) {
                                    itemFound = this.epicTargetItems[x];
                                }
                                else if (Server.rand.nextInt(numsExisting) == 0) {
                                    itemFound = this.epicTargetItems[x];
                                }
                            }
                            else {
                                EpicTargetItems.logger.info("Disqualified Epic Mission Target item due to being in village " + v.getName() + ": Name: " + eti.getName() + " | WurmID: " + eti.getWurmId() + " | TileX: " + eti.getTileX() + " | TileY: " + eti.getTileY());
                            }
                        }
                        catch (NoSuchItemException nsie) {
                            EpicTargetItems.logger.warning("Epic mission item could not be found when loaded, maybe it was wrongfully deleted? WurmID:" + this.epicTargetItems[x] + ". " + nsie);
                        }
                    }
                }
            }
        }
        else {
            if (EpicTargetItems.logger.isLoggable(Level.FINE)) {
                EpicTargetItems.logger.fine("Entering Freedom Version of Valrei Mission Target Structure selection.");
            }
            Connection dbcon = null;
            PreparedStatement ps1 = null;
            ResultSet rs = null;
            final int structureType = Server.rand.nextInt(6);
            int templateId = 0;
            if (targetTemplate > 0) {
                templateId = targetTemplate;
            }
            else {
                switch (structureType) {
                    case 0: {
                        templateId = 717;
                        break;
                    }
                    case 1: {
                        templateId = 714;
                        break;
                    }
                    case 2: {
                        templateId = 713;
                        break;
                    }
                    case 3: {
                        templateId = 715;
                        break;
                    }
                    case 4: {
                        templateId = 712;
                        break;
                    }
                    case 5: {
                        templateId = 716;
                        break;
                    }
                    default: {
                        templateId = 713;
                        break;
                    }
                }
            }
            if (EpicTargetItems.logger.isLoggable(Level.FINE)) {
                EpicTargetItems.logger.fine("Selected template with id=" + templateId);
            }
            if (itemList == null) {
                itemList = new ArrayList<Long>();
                try {
                    final String dbQueryString = "SELECT WURMID FROM ITEMS WHERE TEMPLATEID=?";
                    if (EpicTargetItems.logger.isLoggable(Level.FINER)) {
                        EpicTargetItems.logger.finer("Query String [ SELECT WURMID FROM ITEMS WHERE TEMPLATEID=? ]");
                    }
                    dbcon = DbConnector.getItemDbCon();
                    ps1 = dbcon.prepareStatement("SELECT WURMID FROM ITEMS WHERE TEMPLATEID=?");
                    ps1.setInt(1, templateId);
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        final long currentLong = rs.getLong("WURMID");
                        if (currentLong > 0L) {
                            itemList.add(currentLong);
                        }
                        if (EpicTargetItems.logger.isLoggable(Level.FINEST)) {
                            EpicTargetItems.logger.finest(rs.toString());
                        }
                    }
                }
                catch (SQLException ex) {
                    EpicTargetItems.logger.log(Level.WARNING, "Failed to locate mission items with templateid=" + templateId, ex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps1, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
            if (itemList.size() > 0) {
                final int randomIndex = Server.rand.nextInt(itemList.size());
                if (itemList.get(randomIndex) != null) {
                    final long selectedTarget = itemList.get(randomIndex);
                    try {
                        final Item eti2 = Items.getItem(selectedTarget);
                        final Village v2 = Villages.getVillage(eti2.getTilePos(), eti2.isOnSurface());
                        if (v2 == null) {
                            EpicTargetItems.logger.info("Selected mission target with wurmid=" + selectedTarget);
                            return selectedTarget;
                        }
                        EpicTargetItems.logger.info("Disqualified Epic Mission Target item due to being in village " + v2.getName() + ": Name: " + eti2.getName() + " | WurmID: " + eti2.getWurmId() + " | TileX: " + eti2.getTileX() + " | TileY: " + eti2.getTileY());
                        final int ATTEMPT_NUMBER_OF_TIMES = 25;
                        if (attempts < 25) {
                            EpicTargetItems.logger.fine("Failing roll number " + attempts + "/" + 25 + " and trying again.");
                            return this.getRandomTarget(attempts + 1, templateId, itemList);
                        }
                        EpicTargetItems.logger.info("Failing roll of finding structure with templateID=" + templateId + " completely,  could not find any mission structure not in a village in " + 25 + " tries.");
                        return -1L;
                    }
                    catch (NoSuchItemException ex2) {
                        return itemFound;
                    }
                }
                EpicTargetItems.logger.warning("WURMID was null for item with templateId=" + templateId);
                return -1L;
            }
            EpicTargetItems.logger.info("Couldn't find any items with itemtemplate=" + templateId + " failing, the roll.");
            return -1L;
        }
        return itemFound;
    }
    
    final int getNextBuildTarget(int difficulty) {
        difficulty = Math.min(5, difficulty);
        final int start = difficulty * 3;
        int templateFound = -1;
        for (int x = start; x < 17; ++x) {
            if (this.epicTargetItems[x] <= 0L) {
                templateFound = x;
                break;
            }
        }
        if (templateFound == -1) {
            for (int x = start; x > 0; --x) {
                if (this.epicTargetItems[x] <= 0L) {
                    templateFound = x;
                    break;
                }
            }
        }
        if (templateFound <= -1) {
            return -1;
        }
        if (templateFound < 3) {
            return 717;
        }
        if (templateFound < 6) {
            return 714;
        }
        if (templateFound < 9) {
            return 713;
        }
        if (templateFound < 12) {
            return 715;
        }
        if (templateFound < 15) {
            return 712;
        }
        return 716;
    }
    
    static {
        logger = Logger.getLogger(EpicTargetItems.class.getName());
        KINGDOM_ITEMS = new ConcurrentHashMap<Byte, EpicTargetItems>();
        ritualTargetItems = new ArrayList<Item>();
        getEpicTargets((byte)4);
        getEpicTargets((byte)1);
        getEpicTargets((byte)2);
        getEpicTargets((byte)3);
        getEpicTargets((byte)0);
    }
}
