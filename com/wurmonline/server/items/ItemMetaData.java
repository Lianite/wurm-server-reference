// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class ItemMetaData implements MiscConstants
{
    private static final Logger logger;
    private static final String INSERT_ITEMKEYS;
    public final long itemId;
    public final boolean locked;
    public final long lockid;
    public final long[] keys;
    public final long lastowner;
    public final int data1;
    public final int data2;
    public final int extra1;
    public final int extra2;
    public final String itname;
    public final String desc;
    public final long ownerId;
    public final long parentId;
    public final long lastmaintained;
    public final float ql;
    public final float itemdam;
    public final float origQl;
    public final int itemtemplateId;
    public final int weight;
    public final int sizex;
    public final int sizey;
    public final int sizez;
    public final byte bless;
    public final byte enchantment;
    public final byte material;
    public final int price;
    public final short temp;
    public final boolean banked;
    public final byte auxbyte;
    public final long creationDate;
    public final byte creationState;
    public final int realTemplate;
    public final boolean wornAsArmour;
    public final int color;
    public final int color2;
    public final short place;
    public final float posx;
    public final float posy;
    public final float posz;
    public final String creator;
    public DbStrings instance;
    public final boolean female;
    public final boolean mailed;
    public final byte mailTimes;
    public final byte rarity;
    public final long onBridge;
    public final int settings;
    public final boolean hasInscription;
    
    public ItemMetaData(final boolean aLocked, final long aLockId, final long aItemId, final long[] aKeyIds, final long aLastOwner, final int aData1, final int aData2, final int aExtra1, final int aExtra2, final String aName, final String aDescription, final long aOwnerId, final long aParentId, final long aLastMaintained, final float aQualityLevel, final float aDamage, final float aOriginalQualityLevel, final int aTemplateId, final int aWeight, final int aSizeX, final int aSizeY, final int aSizeZ, final int aBless, final byte aEnchantment, final byte aMaterial, final int aPrice, final short aTemp, final boolean aBanked, final byte aAuxData, final long aCreated, final byte aCreationState, final int aRealTemplate, final boolean aWornArmour, final int aColor, final int aColor2, final short aPlace, final float aPosX, final float aPosY, final float aPosZ, final String aCreator, final boolean aFemale, final boolean aMailed, final byte mailedTimes, final byte rarebyte, final long bridgeId, final boolean inscriptionFlag, final int aSettings, final boolean frozen) {
        this.itemId = aItemId;
        this.locked = (aLocked || aLockId != -10L);
        this.lockid = aLockId;
        this.keys = aKeyIds;
        this.lastowner = aLastOwner;
        this.data1 = aData1;
        this.data2 = aData2;
        this.extra1 = aExtra1;
        this.extra2 = aExtra2;
        this.desc = aDescription.substring(0, Math.min(99, aDescription.length()));
        this.ownerId = aOwnerId;
        this.parentId = aParentId;
        this.lastmaintained = aLastMaintained;
        this.ql = aQualityLevel;
        this.itemdam = aDamage;
        this.origQl = aOriginalQualityLevel;
        this.itemtemplateId = aTemplateId;
        this.instance = Item.getDbStrings(this.itemtemplateId);
        if (frozen && this.instance == ItemDbStrings.getInstance()) {
            this.instance = FrozenItemDbStrings.getInstance();
        }
        this.weight = aWeight;
        this.sizex = aSizeX;
        this.sizey = aSizeY;
        this.sizez = aSizeZ;
        this.bless = (byte)aBless;
        this.enchantment = aEnchantment;
        this.material = aMaterial;
        this.price = aPrice;
        this.temp = aTemp;
        this.banked = aBanked;
        this.auxbyte = aAuxData;
        this.rarity = rarebyte;
        this.onBridge = bridgeId;
        this.settings = aSettings;
        this.hasInscription = inscriptionFlag;
        this.creationDate = aCreated;
        this.creationState = aCreationState;
        this.realTemplate = aRealTemplate;
        this.wornAsArmour = aWornArmour;
        this.color = aColor;
        this.color2 = aColor2;
        this.place = aPlace;
        this.posx = aPosX;
        this.posy = aPosY;
        this.posz = aPosZ;
        this.creator = aCreator;
        this.female = aFemale;
        this.mailed = aMailed;
        this.mailTimes = mailedTimes;
        String itsName = aName.substring(0, Math.min(39, aName.length()));
        try {
            final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(aTemplateId);
            if (template.isVehicle()) {
                itsName = ItemFactory.generateName(template, this.material);
            }
        }
        catch (NoSuchTemplateException ex) {}
        this.itname = itsName;
    }
    
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement(this.instance.transferItem());
            ps.setLong(1, this.itemId);
            ps.setInt(2, this.itemtemplateId);
            ps.setString(3, this.itname);
            ps.setFloat(4, this.ql);
            ps.setFloat(5, this.origQl);
            ps.setLong(6, this.lastmaintained);
            ps.setLong(7, this.ownerId);
            ps.setInt(8, this.sizex);
            ps.setInt(9, this.sizey);
            ps.setInt(10, this.sizez);
            ps.setInt(11, -10);
            ps.setFloat(12, this.itemdam);
            ps.setFloat(13, 1.0f);
            ps.setLong(14, this.parentId);
            ps.setInt(15, this.weight);
            ps.setByte(16, this.material);
            ps.setLong(17, this.lockid);
            ps.setString(18, this.desc);
            ps.setByte(19, this.bless);
            ps.setByte(20, this.enchantment);
            ps.setShort(21, this.temp);
            ps.setInt(22, this.price);
            ps.setBoolean(23, this.banked);
            ps.setByte(24, this.auxbyte);
            ps.setLong(25, this.creationDate);
            ps.setByte(26, this.creationState);
            ps.setInt(27, this.realTemplate);
            ps.setBoolean(28, this.wornAsArmour);
            ps.setInt(29, this.color);
            ps.setInt(30, this.color2);
            ps.setShort(31, this.place);
            ps.setFloat(32, this.posx);
            ps.setFloat(33, this.posy);
            ps.setFloat(34, this.posz);
            ps.setString(35, this.creator);
            ps.setBoolean(36, this.female);
            ps.setBoolean(37, this.mailed);
            ps.setByte(38, this.mailTimes);
            ps.setByte(39, this.rarity);
            ps.setLong(40, this.onBridge);
            ps.setLong(41, this.lastowner);
            ps.setInt(42, this.settings);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (this.keys != null && this.keys.length > 0) {
                this.saveKeys(this.locked);
            }
            if (Servers.isThisATestServer()) {
                ItemMetaData.logger.log(Level.INFO, "Saving " + this.itname + ", " + this.itemId);
            }
        }
        catch (SQLException sqex) {
            throw new IOException(this.itemId + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void saveKeys(final boolean aLocked) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            if (!this.lockExists(dbcon)) {
                final String string = this.instance.createLock();
                ps = dbcon.prepareStatement(string);
                ps.setLong(1, this.itemId);
                ps.setBoolean(2, aLocked);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            ps = dbcon.prepareStatement(this.instance.setLocked());
            ps.setBoolean(1, aLocked);
            ps.setLong(2, this.itemId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            DbUtilities.closeDatabaseObjects(ps, null);
            for (int x = 0; x < this.keys.length; ++x) {
                try {
                    ps = dbcon.prepareStatement(ItemMetaData.INSERT_ITEMKEYS);
                    ps.setLong(1, this.itemId);
                    ps.setLong(2, this.keys[x]);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                }
                catch (SQLException ex) {
                    ItemMetaData.logger.log(Level.INFO, "Failed to insert key id " + this.keys[x] + ": " + ex.getMessage(), ex);
                }
            }
        }
        catch (SQLException ex2) {
            throw new IOException(this.itemId + " " + ex2.getMessage(), ex2);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean lockExists(final Connection dbcon) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement(this.instance.getLock());
            ps.setLong(1, this.itemId);
            rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException ex) {
            ItemMetaData.logger.log(Level.WARNING, "Failed to check if lock exists:", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(ItemMetaData.class.getName());
        INSERT_ITEMKEYS = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO ITEMKEYS (LOCKID,KEYID)VALUES(?,?)" : "INSERT IGNORE INTO ITEMKEYS (LOCKID,KEYID)VALUES(?,?)");
    }
}
