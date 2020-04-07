// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.bodys.Body;
import com.wurmonline.server.epic.HexMap;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmId;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nonnull;
import com.wurmonline.server.FailedException;
import javax.annotation.Nullable;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public final class ItemFactory implements MiscConstants, ItemTypes, ItemMaterials
{
    private static final Logger logger;
    private static final String deleteItemData = "delete from ITEMDATA where WURMID=?";
    private static DbStrings dbstrings;
    public static int[] metalLumpList;
    
    @Nonnull
    public static Item createItem(final int templateId, final float qualityLevel, final byte material, final byte aRarity, @Nullable final String creator) throws FailedException, NoSuchTemplateException {
        return createItem(templateId, qualityLevel, material, aRarity, -10L, creator);
    }
    
    public static Optional<Item> createItemOptional(final int templateId, final float qualityLevel, final byte material, final byte aRarity, @Nullable final String creator) {
        try {
            return Optional.of(createItem(templateId, qualityLevel, material, aRarity, creator));
        }
        catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public static void createContainerRestrictions(final Item item) {
        final ItemTemplate template = item.getTemplate();
        if (template.getContainerRestrictions() != null && !template.isNoPut()) {
            for (final ContainerRestriction cRest : template.getContainerRestrictions()) {
                boolean skipAdd = false;
                for (final Item i : item.getItems()) {
                    if (i.getTemplateId() == 1392 && cRest.contains(i.getRealTemplateId())) {
                        skipAdd = true;
                    }
                    else {
                        if (!cRest.contains(i.getTemplateId())) {
                            continue;
                        }
                        skipAdd = true;
                    }
                }
                if (!skipAdd) {
                    try {
                        final Item tempSlotItem = createItem(1392, 100.0f, item.getCreatorName());
                        tempSlotItem.setRealTemplate(cRest.getEmptySlotTemplateId());
                        tempSlotItem.setName(cRest.getEmptySlotName());
                        item.insertItem(tempSlotItem, true);
                    }
                    catch (FailedException ex) {}
                    catch (NoSuchTemplateException ex2) {}
                }
            }
        }
    }
    
    @Nonnull
    public static Item createItem(final int templateId, final float qualityLevel, byte material, final byte aRarity, final long bridgeId, @Nullable final String creator) throws FailedException, NoSuchTemplateException {
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(templateId);
        if (material == 0) {
            material = template.getMaterial();
        }
        final String name = generateName(template, material);
        Item toReturn = null;
        Label_0277: {
            if (template.isTemporary()) {
                try {
                    toReturn = new TempItem(name, template, qualityLevel, creator);
                    if (ItemFactory.logger.isLoggable(Level.FINEST)) {
                        ItemFactory.logger.finest("Creating tempitem: " + toReturn);
                    }
                    break Label_0277;
                }
                catch (IOException ex) {
                    throw new FailedException(ex);
                }
            }
            try {
                if (template.isRecycled) {
                    toReturn = Itempool.getRecycledItem(templateId, qualityLevel);
                    if (toReturn != null) {
                        if (toReturn.isTemporary()) {
                            toReturn.clear(WurmId.getNextTempItemId(), creator, 0.0f, 0.0f, 0.0f, 1.0f, "", name, qualityLevel, material, aRarity, bridgeId);
                        }
                        else {
                            toReturn.clear(toReturn.id, creator, 0.0f, 0.0f, 0.0f, 1.0f, "", name, qualityLevel, material, aRarity, bridgeId);
                        }
                        return toReturn;
                    }
                }
                toReturn = new DbItem(-10L, name, template, qualityLevel, material, aRarity, bridgeId, creator);
                if (template.isCoin()) {
                    Server.getInstance().transaction(toReturn.getWurmId(), -10L, bridgeId, "new " + toReturn.getName(), template.getValue());
                }
            }
            catch (IOException iox) {
                throw new FailedException(iox);
            }
        }
        if (template.getInitialContainers() != null) {
            for (final InitialContainer ic : template.getInitialContainers()) {
                final byte icMaterial = (ic.getMaterial() == 0) ? material : ic.getMaterial();
                final Item subItem = createItem(ic.getTemplateId(), Math.max(1.0f, qualityLevel), icMaterial, aRarity, creator);
                subItem.setName(ic.getName());
                toReturn.insertItem(subItem, true);
            }
        }
        if (toReturn != null) {
            createContainerRestrictions(toReturn);
        }
        return toReturn;
    }
    
    public static Item createItem(final int templateId, final float qualityLevel, final byte aRarity, @Nullable final String creator) throws FailedException, NoSuchTemplateException {
        return createItem(templateId, qualityLevel, (byte)0, aRarity, creator);
    }
    
    public static Optional<Item> createItemOptional(final int templateId, final float qualityLevel, final byte aRarity, @Nullable final String creator) {
        return createItemOptional(templateId, qualityLevel, (byte)0, aRarity, creator);
    }
    
    @Nonnull
    public static Item createItem(final int templateId, final float qualityLevel, @Nullable final String creator) throws FailedException, NoSuchTemplateException {
        return createItem(templateId, qualityLevel, (byte)0, (byte)0, creator);
    }
    
    public static String generateName(final ItemTemplate template, final byte material) {
        String name = template.sizeString + template.getName();
        if (template.getTemplateId() == 683) {
            name = HexMap.generateFirstName() + " " + HexMap.generateSecondName();
        }
        if (template.unique) {
            name = template.getName();
        }
        return name;
    }
    
    public static Item createBodyPart(final Body body, final short place, final int templateId, final String name, final float qualityLevel) throws FailedException, NoSuchTemplateException {
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(templateId);
        Item toReturn = null;
        try {
            final long wurmId = WurmId.getNextBodyPartId(body.getOwnerId(), (byte)place, WurmId.getType(body.getOwnerId()) == 0);
            if (template.isRecycled) {
                toReturn = Itempool.getRecycledItem(templateId, qualityLevel);
                if (toReturn != null) {
                    toReturn.clear(-10L, "", 0.0f, 0.0f, 0.0f, 0.0f, "", name, qualityLevel, template.getMaterial(), (byte)0, -10L);
                    toReturn.setPlace(place);
                }
            }
            if (toReturn == null) {
                toReturn = new TempItem(wurmId, place, name, template, qualityLevel, "");
            }
        }
        catch (IOException ex) {
            throw new FailedException(ex);
        }
        return toReturn;
    }
    
    @Nullable
    public static Item createInventory(final long ownerId, final short place, final float qualityLevel) throws FailedException, NoSuchTemplateException {
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(0);
        Item toReturn = null;
        try {
            final long wurmId = WurmId.getNextBodyPartId(ownerId, (byte)place, WurmId.getType(ownerId) == 0);
            if (template.isRecycled) {
                toReturn = Itempool.getRecycledItem(0, qualityLevel);
                if (toReturn != null) {
                    toReturn.clear(wurmId, "", 0.0f, 0.0f, 0.0f, 0.0f, "", "inventory", qualityLevel, template.getMaterial(), (byte)0, -10L);
                }
            }
            if (toReturn == null) {
                toReturn = new TempItem(wurmId, place, "inventory", template, qualityLevel, "");
            }
        }
        catch (IOException ex) {
            throw new FailedException(ex);
        }
        return toReturn;
    }
    
    public static Item loadItem(final long id) throws NoSuchItemException, Exception {
        Item item = null;
        if (WurmId.getType(id) == 2 || WurmId.getType(id) == 19 || WurmId.getType(id) == 20) {
            item = new DbItem(id);
            return item;
        }
        throw new NoSuchItemException("Temporary item.");
    }
    
    public static void decay(final long id, @Nullable final DbStrings dbStrings) {
        ItemFactory.dbstrings = dbStrings;
        if (ItemFactory.dbstrings == null) {
            ItemFactory.dbstrings = Item.getDbStringsByWurmId(id);
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(ItemFactory.dbstrings.deleteItem());
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("delete from ITEMDATA where WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM ITEMKEYS WHERE LOCKID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemFactory.logger.log(Level.WARNING, "Failed to decay item with id " + id, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void clearData(final long id) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("delete from ITEMDATA where WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM ITEMKEYS WHERE LOCKID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemFactory.logger.log(Level.WARNING, "Failed to decay item with id " + id, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static Item createItem(final int templateId, final float qualityLevel, final float posX, final float posY, final float rot, final boolean onSurface, final byte rarity, final long bridgeId, @Nullable final String creator) throws NoSuchTemplateException, FailedException {
        return createItem(templateId, qualityLevel, posX, posY, rot, onSurface, (byte)0, rarity, bridgeId, creator);
    }
    
    public static Item createItem(final int templateId, final float qualityLevel, final float posX, final float posY, final float rot, final boolean onSurface, final byte material, final byte aRarity, final long bridgeId, @Nullable final String creator) throws NoSuchTemplateException, FailedException {
        return createItem(templateId, qualityLevel, posX, posY, rot, onSurface, material, aRarity, bridgeId, creator, (byte)0);
    }
    
    public static Item createItem(final int templateId, final float qualityLevel, final float posX, final float posY, final float rot, final boolean onSurface, byte material, final byte aRarity, final long bridgeId, @Nullable final String creator, final byte initialAuxData) throws NoSuchTemplateException, FailedException {
        float height = 0.0f;
        try {
            height = Zones.calculateHeight(posX, posY, onSurface);
        }
        catch (NoSuchZoneException nsz) {
            ItemFactory.logger.log(Level.WARNING, "Could not calculate height for position: " + posX + ", " + posY + ", surfaced: " + onSurface + " due to " + nsz.getMessage(), nsz);
        }
        if (ItemFactory.logger.isLoggable(Level.FINER)) {
            ItemFactory.logger.finer("Factory trying to create item with id " + templateId + " at " + posX + ", " + posY + ", " + height + ".");
        }
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(templateId);
        if (material == 0) {
            material = template.getMaterial();
        }
        final String name = generateName(template, material);
        Item toReturn = null;
        try {
            if (template.isRecycled) {
                toReturn = Itempool.getRecycledItem(templateId, qualityLevel);
                if (toReturn != null) {
                    if (toReturn.isTemporary()) {
                        toReturn.clear(WurmId.getNextTempItemId(), creator, posX, posY, height, rot, "", name, qualityLevel, material, aRarity, bridgeId);
                    }
                    else {
                        toReturn.clear(toReturn.id, creator, posX, posY, height, rot, "", name, qualityLevel, material, aRarity, bridgeId);
                    }
                }
            }
            if (toReturn == null) {
                if (template.isTemporary()) {
                    toReturn = new TempItem(name, template, qualityLevel, posX, posY, height, rot, bridgeId, creator);
                }
                else {
                    toReturn = new DbItem(name, template, qualityLevel, posX, posY, height, rot, material, aRarity, bridgeId, creator);
                }
            }
            try {
                if (toReturn.getTemplateId() == 385 || toReturn.getTemplateId() == 731) {
                    toReturn.setAuxData((byte)(100 + initialAuxData));
                }
                final Zone zone = Zones.getZone((int)posX >> 2, (int)posY >> 2, onSurface);
                zone.addItem(toReturn);
                if (toReturn.getTemplateId() == 385 || toReturn.getTemplateId() == 731) {
                    toReturn.setAuxData(initialAuxData);
                }
            }
            catch (NoSuchZoneException sex) {
                ItemFactory.logger.log(Level.WARNING, "Could not get Zone for position: " + posX + ", " + posY + ", surfaced: " + onSurface + " due to " + sex.getMessage(), sex);
            }
        }
        catch (IOException ex) {
            throw new FailedException(ex);
        }
        toReturn.setOwner(-10L, true);
        if (toReturn.isFire()) {
            toReturn.setTemperature((short)20000);
            final Effect effect = EffectFactory.getInstance().createFire(toReturn.getWurmId(), toReturn.getPosX(), toReturn.getPosY(), toReturn.getPosZ(), toReturn.isOnSurface());
            toReturn.addEffect(effect);
        }
        return toReturn;
    }
    
    public static boolean isMetalLump(final int itemTemplateId) {
        for (final int lumpId : ItemFactory.metalLumpList) {
            if (lumpId == itemTemplateId) {
                return true;
            }
        }
        return false;
    }
    
    public static Optional<Item> createItemOptional(final int itemTemplateId, final float qualityLevel, final String creator) {
        try {
            return Optional.of(createItem(itemTemplateId, qualityLevel, creator));
        }
        catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    static {
        logger = Logger.getLogger(ItemFactory.class.getName());
        ItemFactory.metalLumpList = new int[] { 46, 221, 223, 205, 47, 220, 49, 44, 45, 48, 837, 698, 694, 1411 };
    }
}
