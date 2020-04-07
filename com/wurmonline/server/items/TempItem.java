// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Features;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.Server;
import java.util.Iterator;
import com.wurmonline.server.creatures.Creature;
import java.util.HashSet;
import java.util.Set;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.logging.Level;
import java.io.IOException;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public final class TempItem extends Item
{
    private static final Logger logger;
    
    TempItem() {
    }
    
    @Override
    public void bless(final int blesser) {
        if (this.bless == 0) {
            this.bless = (byte)blesser;
        }
    }
    
    @Override
    public void setOwnerStuff(final ItemTemplate templ) {
    }
    
    @Override
    public void enchant(final byte ench) {
        if (this.enchantment != ench) {
            this.enchantment = ench;
        }
    }
    
    @Override
    public void setColor(final int _color) {
        this.color = _color;
    }
    
    @Override
    public void setColor2(final int _color) {
        this.color2 = _color;
    }
    
    @Override
    public void setLastOwnerId(final long oid) {
        this.lastOwner = oid;
    }
    
    public TempItem(final String aName, final ItemTemplate aTemplate, final float qLevel, @Nullable final String aCreator) throws IOException {
        super(-10L, aName, aTemplate, qLevel, (byte)0, (byte)0, -10L, aCreator);
    }
    
    public TempItem(final long wurmId, final short aPlace, final String aName, final ItemTemplate aTemplate, final float qLevel, @Nullable final String aCreator) throws IOException {
        super(wurmId, aName, aTemplate, qLevel, (byte)1, (byte)0, -10L, aCreator);
        this.setPlace(aPlace);
    }
    
    public TempItem(final String aName, final short aPlace, final ItemTemplate aTemplate, final float aQualityLevel, final String aCreator) throws IOException {
        super(aName, aPlace, aTemplate, aQualityLevel, (byte)0, (byte)0, -10L, aCreator);
    }
    
    public TempItem(final String aName, final ItemTemplate aTemplate, final float aQualityLevel, final float aPosX, final float aPosY, final float aPosZ, final float aRotation, final long bridgeId, final String aCreator) throws IOException {
        super(aName, aTemplate, aQualityLevel, aPosX, aPosY, aPosZ, aRotation, (byte)0, (byte)0, bridgeId, aCreator);
    }
    
    @Override
    void create(final float aQualityLevel, final long aCreationDate) throws IOException {
        this.qualityLevel = aQualityLevel;
        this.lastMaintained = aCreationDate;
    }
    
    @Override
    void load() throws Exception {
    }
    
    @Override
    public void loadEffects() {
    }
    
    @Override
    void setPlace(final short aPlace) {
        this.place = aPlace;
    }
    
    @Override
    public short getPlace() {
        return this.place;
    }
    
    @Override
    public void setLastMaintained(final long time) {
        this.lastMaintained = time;
    }
    
    @Override
    public long getLastMaintained() {
        return this.lastMaintained;
    }
    
    @Override
    public boolean setQualityLevel(final float newLevel) {
        this.qualityLevel = newLevel;
        return false;
    }
    
    @Override
    public long getOwnerId() {
        return this.ownerId;
    }
    
    @Override
    public boolean setOwnerId(final long aOwnerId) {
        this.ownerId = aOwnerId;
        return true;
    }
    
    @Override
    public boolean getLocked() {
        return this.locked;
    }
    
    @Override
    public void setLocked(final boolean aLocked) {
        this.locked = aLocked;
    }
    
    @Override
    public int getTemplateId() {
        return this.template.getTemplateId();
    }
    
    @Override
    public void setTemplateId(final int aId) {
        try {
            this.template = ItemTemplateFactory.getInstance().getTemplate(aId);
        }
        catch (NoSuchTemplateException nst) {
            TempItem.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
    }
    
    @Override
    public void setZoneId(final int aId, final boolean isOnSurface) {
        this.surfaced = isOnSurface;
        this.zoneId = aId;
    }
    
    @Override
    public int getZoneId() {
        if (this.parentId != -10L && Items.isItemLoaded(this.parentId)) {
            try {
                final Item parent = Items.getItem(this.parentId);
                return parent.getZoneId();
            }
            catch (NoSuchItemException nsi) {
                TempItem.logger.log(Level.WARNING, "This REALLY shouldn't happen! parentId: " + this.parentId, nsi);
            }
        }
        return this.zoneId;
    }
    
    @Override
    public boolean setDescription(final String desc) {
        this.description = desc;
        return false;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public void setName(final String newname) {
        this.name = newname;
    }
    
    @Override
    public void setName(final String newname, final boolean sendUpdate) {
        this.setName(newname);
    }
    
    @Override
    public boolean setInscription(final String aInscription, final String aInscriber) {
        return this.setInscription(aInscription, aInscriber, 0);
    }
    
    @Override
    public boolean setInscription(final String aInscription, final String aInscriber, final int penColour) {
        this.inscription.setInscription(aInscription);
        this.inscription.setInscriber(aInscriber);
        this.inscription.setPenColour(penColour);
        return true;
    }
    
    @Override
    public float getRotation() {
        return this.rotation;
    }
    
    @Override
    public void setPos(final float aPosX, final float aPosY, final float aPosZ, final float aRotation, final long bridgeId) {
        this.posX = aPosX;
        this.posY = aPosY;
        this.posZ = aPosZ;
        this.rotation = aRotation;
        this.onBridge = bridgeId;
    }
    
    @Override
    public void setPosXYZRotation(final float _posX, final float _posY, final float _posZ, final float _rot) {
        this.posX = _posX;
        this.posY = _posY;
        this.posZ = _posZ;
        this.rotation = _rot;
    }
    
    @Override
    public void setPosXYZ(final float _posX, final float _posY, final float _posZ) {
        this.posX = _posX;
        this.posY = _posY;
        this.posZ = _posZ;
    }
    
    @Override
    public void setPosXY(final float _posX, final float _posY) {
        this.posX = _posX;
        this.posY = _posY;
    }
    
    @Override
    public void setPosX(final float aPosX) {
        this.posX = aPosX;
    }
    
    @Override
    public void setPosY(final float aPosY) {
        this.posY = aPosY;
    }
    
    @Override
    public void setPosZ(final float aPosZ) {
        this.posZ = aPosZ;
    }
    
    @Override
    public void setRotation(final float aRotation) {
        this.rotation = aRotation;
    }
    
    @Override
    public float getQualityLevel() {
        return this.qualityLevel;
    }
    
    @Override
    public float getDamage() {
        return this.damage;
    }
    
    @Override
    public Set<Item> getItems() {
        if (this.items == null) {
            this.items = new HashSet<Item>();
        }
        return this.items;
    }
    
    @Override
    public Item[] getItemsAsArray() {
        if (this.items == null) {
            return new Item[0];
        }
        return this.items.toArray(new Item[this.items.size()]);
    }
    
    @Override
    public void setParentId(final long pid, final boolean isOnSurface) {
        this.surfaced = isOnSurface;
        if (this.parentId != pid) {
            if (pid == -10L) {
                if (this.watchers != null) {
                    for (final Creature watcher : this.watchers) {
                        watcher.getCommunicator().sendRemoveFromInventory(this);
                    }
                }
                this.watchers = null;
            }
            else {
                try {
                    final Item parent = Items.getItem(pid);
                    if (this.ownerId != parent.getOwnerId() && (parent.getPosX() != this.getPosX() || parent.getPosY() != this.getPosY())) {
                        this.setPosXYZ(this.getPosX(), this.getPosY(), this.getPosZ());
                    }
                }
                catch (NoSuchItemException nsi) {
                    TempItem.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                }
            }
            this.parentId = pid;
        }
    }
    
    @Override
    public long getParentId() {
        return this.parentId;
    }
    
    @Override
    void setSizeX(final int sizex) {
        this.sizeX = sizex;
    }
    
    @Override
    void setSizeY(final int sizey) {
        this.sizeY = sizey;
    }
    
    @Override
    void setSizeZ(final int sizez) {
        this.sizeZ = sizez;
    }
    
    @Override
    public int getSizeX() {
        if (this.sizeX > 0) {
            return this.sizeX;
        }
        return this.template.getSizeX();
    }
    
    @Override
    public int getSizeY() {
        if (this.sizeY > 0) {
            return this.sizeY;
        }
        return this.template.getSizeY();
    }
    
    @Override
    public int getSizeZ() {
        if (this.sizeZ > 0) {
            return this.sizeZ;
        }
        return this.template.getSizeZ();
    }
    
    @Override
    public void setOriginalQualityLevel(final float qlevel) {
    }
    
    @Override
    public float getOriginalQualityLevel() {
        return this.originalQualityLevel;
    }
    
    @Override
    public boolean setDamage(final float dam) {
        float modifier = 1.0f;
        float difference = dam - this.damage;
        if (difference > 0.0f && this.getSpellEffects() != null) {
            modifier = this.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_DAMAGETAKEN);
            difference *= modifier;
        }
        return this.setDamage(this.damage + difference, false);
    }
    
    @Override
    public boolean setDamage(final float dam, final boolean override) {
        this.damage = dam;
        if (dam >= 100.0f) {
            this.setQualityLevel(0.0f);
            this.checkDecay();
            return true;
        }
        return false;
    }
    
    @Override
    public void setData1(final int data1) {
        if (this.data == null) {
            this.data = new ItemData(this.id, data1, -1, -1, -1);
        }
        this.data.data1 = data1;
    }
    
    @Override
    public void setData2(final int data2) {
        if (this.data == null) {
            this.data = new ItemData(this.id, -1, data2, -1, -1);
        }
        this.data.data2 = data2;
    }
    
    @Override
    public void setData(final int data1, final int data2) {
        if (this.data == null) {
            this.data = new ItemData(this.id, data1, data2, -1, -1);
        }
        this.data.data1 = data1;
        this.data.data2 = data2;
    }
    
    @Override
    public int getData1() {
        if (this.data != null) {
            return this.data.data1;
        }
        return -1;
    }
    
    @Override
    public int getData2() {
        if (this.data != null) {
            return this.data.data2;
        }
        return -1;
    }
    
    @Override
    public int getWeightGrams() {
        if (this.getSpellEffects() == null) {
            return this.weight;
        }
        final float modifier = this.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_WEIGHT);
        return (int)(this.weight * modifier);
    }
    
    @Override
    public boolean setWeight(final int w, final boolean destroyOnWeightZero) {
        return this.setWeight(w, destroyOnWeightZero, false);
    }
    
    @Override
    public boolean setWeight(final int w, final boolean destroyOnWeightZero, final boolean sameOwner) {
        if (this.weight <= 0) {
            Items.destroyItem(this.id);
            return true;
        }
        this.weight = w;
        if (this.parentId != -10L) {
            this.updateParents();
        }
        return false;
    }
    
    @Override
    public byte getMaterial() {
        return this.material;
    }
    
    @Override
    public void setMaterial(final byte mat) {
        this.material = mat;
    }
    
    @Override
    public long getLockId() {
        return this.lockid;
    }
    
    @Override
    public void setLockId(final long lid) {
        this.lockid = lid;
    }
    
    @Override
    void addItem(@Nullable final Item item, final boolean loading) {
        if (item != null) {
            this.getItems().add(item);
            if (this.parentId != -10L) {
                this.updateParents();
            }
        }
        else {
            TempItem.logger.warning("Ignored attempt to add a null item to " + this);
        }
    }
    
    @Override
    void removeItem(final Item item) {
        if (this.items != null) {
            this.items.remove(item);
        }
        if (item.wornAsArmour) {
            item.setWornAsArmour(false, this.getOwnerId());
        }
        if (this.parentId != -10L) {
            this.updateParents();
        }
    }
    
    @Override
    public void setPrice(final int newPrice) {
        this.price = newPrice;
    }
    
    @Override
    public void setTemperature(final short temp) {
        this.temperature = temp;
    }
    
    @Override
    public void setBanked(final boolean bank) {
        this.banked = bank;
    }
    
    @Override
    public void setAuxData(final byte auxdata) {
        this.auxbyte = auxdata;
    }
    
    @Override
    public void setCreationState(final byte newState) {
        this.creationState = newState;
    }
    
    @Override
    public void setRealTemplate(final int rTemplate) {
        this.realTemplate = rTemplate;
    }
    
    @Override
    void setWornAsArmour(final boolean wornArmour, final long newOwner) {
        if (this.wornAsArmour != wornArmour) {
            this.wornAsArmour = wornArmour;
            if (this.wornAsArmour) {
                try {
                    final Creature creature = Server.getInstance().getCreature(newOwner);
                    final ArmourTemplate armour = ArmourTemplate.getArmourTemplate(this.template.templateId);
                    if (armour != null) {
                        float moveModChange = armour.getMoveModifier();
                        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
                            moveModChange *= ArmourTemplate.getMaterialMovementModifier(this.getMaterial());
                        }
                        else if (Servers.localServer.isChallengeOrEpicServer()) {
                            if (this.getMaterial() == 57 || this.getMaterial() == 67) {
                                moveModChange *= 0.9f;
                            }
                            else if (this.getMaterial() == 56) {
                                moveModChange *= 0.95f;
                            }
                        }
                        creature.getMovementScheme().armourMod.setModifier(creature.getMovementScheme().armourMod.getModifier() - moveModChange);
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    TempItem.logger.log(Level.WARNING, "Worn armour on unknown player: ", nsp);
                }
                catch (NoSuchCreatureException cnf) {
                    TempItem.logger.log(Level.WARNING, "Worn armour on unknown creature: ", cnf);
                }
            }
            else {
                try {
                    final Creature creature = Server.getInstance().getCreature(this.getOwnerId());
                    final ArmourTemplate armour = ArmourTemplate.getArmourTemplate(this.template.templateId);
                    if (armour != null) {
                        float moveModChange = armour.getMoveModifier();
                        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
                            moveModChange *= ArmourTemplate.getMaterialMovementModifier(this.getMaterial());
                        }
                        else if (Servers.localServer.isChallengeOrEpicServer()) {
                            if (this.getMaterial() == 57 || this.getMaterial() == 67) {
                                moveModChange *= 0.9f;
                            }
                            else if (this.getMaterial() == 56) {
                                moveModChange *= 0.95f;
                            }
                        }
                        creature.getMovementScheme().armourMod.setModifier(creature.getMovementScheme().armourMod.getModifier() + moveModChange);
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    TempItem.logger.log(Level.WARNING, "Worn armour on unknown player: ", nsp);
                }
                catch (NoSuchCreatureException cnf) {
                    TempItem.logger.log(Level.WARNING, "Worn armour on unknown creature: ", cnf);
                }
            }
        }
    }
    
    @Override
    public void savePosition() {
    }
    
    @Override
    public void setFemale(final boolean _female) {
        this.female = _female;
    }
    
    @Override
    public void setTransferred(final boolean trans) {
        this.transferred = trans;
    }
    
    @Override
    void addNewKey(final long keyId) {
    }
    
    @Override
    void removeNewKey(final long keyId) {
    }
    
    @Override
    public void setMailed(final boolean _mailed) {
        this.mailed = _mailed;
    }
    
    @Override
    public void setCreator(final String _creator) {
        this.creator = _creator;
    }
    
    @Override
    public void setHidden(final boolean _hidden) {
        this.hidden = _hidden;
    }
    
    @Override
    public void setDbStrings(final DbStrings dbStrings) {
    }
    
    @Override
    public DbStrings getDbStrings() {
        return null;
    }
    
    @Override
    void clear(final long wurmId, final String _creator, final float posx, final float posy, final float posz, final float _rot, final String _desc, final String _name, final float _qualitylevel, final byte _material, final byte aRarity, final long bridgeId) {
        this.id = wurmId;
        this.creator = _creator;
        this.posX = posx;
        this.posY = posy;
        this.posZ = posz;
        this.description = _desc;
        this.name = _name;
        this.qualityLevel = _qualitylevel;
        this.originalQualityLevel = this.qualityLevel;
        this.rotation = _rot;
        this.zoneId = -10;
        this.parentId = -10L;
        this.sizeX = this.template.getSizeX();
        this.sizeY = this.template.getSizeY();
        this.sizeZ = this.template.getSizeZ();
        this.weight = this.template.getWeightGrams();
        this.lastMaintained = WurmCalendar.currentTime;
        this.creationDate = WurmCalendar.currentTime;
        this.creationState = 0;
        this.banked = false;
        this.damage = 0.0f;
        this.enchantment = 0;
        this.material = _material;
        this.rarity = aRarity;
        this.onBridge = bridgeId;
        this.creationState = 0;
    }
    
    @Override
    public void setMailTimes(final byte times) {
    }
    
    @Override
    public void returnFromFreezer() {
    }
    
    @Override
    public void moveToFreezer() {
    }
    
    @Override
    public void deleteInDatabase() {
    }
    
    @Override
    public boolean setRarity(final byte newRarity) {
        if (newRarity != this.rarity) {
            this.rarity = newRarity;
            return true;
        }
        return false;
    }
    
    @Override
    public void savePermissions() {
    }
    
    @Override
    boolean saveInscription() {
        return false;
    }
    
    @Override
    public void setExtra1(final int extra1) {
        if (this.data == null) {
            this.data = new ItemData(this.id, -1, -1, -1, -1);
        }
        this.data.extra1 = extra1;
    }
    
    @Override
    public void setExtra2(final int extra2) {
        if (this.data == null) {
            this.data = new ItemData(this.id, -1, -1, -1, -1);
        }
        this.data.extra2 = extra2;
    }
    
    @Override
    public void setExtra(final int extra1, final int extra2) {
        if (this.data == null) {
            this.data = new ItemData(this.id, -1, -1, -1, -1);
        }
        this.data.extra1 = extra1;
        this.data.extra2 = extra2;
    }
    
    @Override
    public int getExtra1() {
        if (this.data != null) {
            return this.data.extra1;
        }
        return -1;
    }
    
    @Override
    public int getExtra2() {
        if (this.data != null) {
            return this.data.extra2;
        }
        return -1;
    }
    
    @Override
    public void setAllData(final int data1, final int data2, final int extra1, final int extra2) {
        if (this.data == null) {
            this.data = new ItemData(this.id, -1, -1, -1, -1);
        }
        this.data.data1 = data1;
        this.data.data2 = data2;
        this.data.extra1 = extra1;
        this.data.extra2 = extra2;
    }
    
    @Override
    public void setPlacedOnParent(final boolean onParent) {
        this.placedOnParent = onParent;
    }
    
    @Override
    public boolean isItem() {
        return true;
    }
    
    static {
        logger = Logger.getLogger(TempItem.class.getName());
    }
}
