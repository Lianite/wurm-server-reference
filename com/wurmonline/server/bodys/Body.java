// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import java.io.IOException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.items.NoSpaceException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class Body implements CounterTypes, CombatConstants, MiscConstants
{
    private Wounds wounds;
    private final Creature owner;
    private short centimetersHigh;
    private short centimetersLong;
    private short centimetersWide;
    private final BodyTemplate template;
    private static final Logger logger;
    private static final String GET_WOUNDS = "SELECT * FROM WOUNDS WHERE OWNER=?";
    private final Item[] spaces;
    private boolean initialized;
    
    Body(final BodyTemplate aTemplate, final Creature aOwner, final short aCentimetersHigh, final short aCentimetersLong, final short aCentimetersWide) {
        this.initialized = false;
        this.template = aTemplate;
        this.owner = aOwner;
        this.centimetersHigh = aCentimetersHigh;
        this.centimetersLong = aCentimetersLong;
        this.centimetersWide = aCentimetersWide;
        this.spaces = new Item[48];
    }
    
    public byte getType() {
        return this.template.type;
    }
    
    public final void loadWounds() {
        if (this.owner instanceof Player) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM WOUNDS WHERE OWNER=?");
                ps.setLong(1, this.owner.getWurmId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    final DbWound wound = new DbWound(rs.getLong("ID"), rs.getByte("TYPE"), rs.getByte("LOCATION"), rs.getFloat("SEVERITY"), this.owner.getWurmId(), rs.getFloat("POISONSEVERITY"), rs.getFloat("INFECTIONSEVERITY"), rs.getLong("LASTPOLLED"), rs.getBoolean("BANDAGED"), rs.getByte("HEALEFF"));
                    this.addWound(wound);
                }
            }
            catch (SQLException sqx) {
                Body.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public void sendWounds() {
        if (this.owner instanceof Player && this.wounds != null) {
            final Wound[] w = this.wounds.getWounds();
            for (int x = 0; x < w.length; ++x) {
                try {
                    this.owner.getCommunicator().sendAddWound(w[x], this.getBodyPartForWound(w[x]));
                }
                catch (NoSpaceException nsp) {
                    Body.logger.log(Level.INFO, nsp.getMessage(), nsp);
                }
            }
        }
    }
    
    public Wounds getWounds() {
        return this.wounds;
    }
    
    public boolean addWound(final Wound wound) {
        if (this.owner.isInvulnerable() && !this.owner.isPlayer()) {
            if (wound.getType() != 7) {
                Body.logger.log(Level.INFO, "Invulnerable " + this.owner.getName() + " receiving wound. Ignoring.", new Exception());
            }
            wound.delete();
            return false;
        }
        if (this.wounds == null) {
            this.wounds = new Wounds();
        }
        this.wounds.addWound(wound);
        this.owner.setWounded();
        return this.owner.getStatus().modifyWounds((int)wound.getSeverity());
    }
    
    public boolean isWounded() {
        return this.wounds != null && this.wounds.hasWounds();
    }
    
    public void removeWound(final Wound wound) {
        if (this.wounds != null) {
            this.wounds.remove(wound);
        }
    }
    
    public long getId() {
        if (this.spaces[0] != null) {
            return this.spaces[0].getWurmId();
        }
        Body.logger.log(Level.INFO, "This should be looked into:", new Exception());
        return -10L;
    }
    
    public long getOwnerId() {
        if (this.owner == null) {
            return -10L;
        }
        return this.owner.getWurmId();
    }
    
    public String getWoundLocationString(final int location) {
        return this.template.typeString[location];
    }
    
    public Item getBodyPartForWound(final Wound wound) throws NoSpaceException {
        final int pos = wound.getLocation();
        if (this.spaces == null || pos < 0) {
            throw new NoSpaceException(String.valueOf(pos));
        }
        Item toReturn = null;
        if (pos == 1 || pos == 17) {
            toReturn = this.spaces[1];
        }
        else if (pos == 29 || pos == 18 || pos == 19 || pos == 20) {
            toReturn = this.spaces[29];
        }
        else if (pos == 36) {
            toReturn = this.spaces[1];
        }
        else if (pos == 2 || pos == 21 || pos == 27 || pos == 26 || pos == 32 || pos == 23 || pos == 24 || pos == 25 || pos == 22 || pos == 46 || pos == 47 || pos == 45 || pos == 42 || pos == 35 || pos == 0) {
            toReturn = this.spaces[2];
        }
        else if (pos == 3 || pos == 5 || pos == 9 || pos == 44) {
            toReturn = this.spaces[3];
        }
        else if (pos == 4 || pos == 6 || pos == 10) {
            toReturn = this.spaces[4];
        }
        else if (pos == 30 || pos == 7 || pos == 11 || pos == 31 || pos == 8 || pos == 12 || pos == 43 || pos == 41) {
            toReturn = this.spaces[34];
        }
        else if (pos == 37 || pos == 39) {
            toReturn = this.spaces[13];
        }
        else if (pos == 38 || pos == 40) {
            toReturn = this.spaces[14];
        }
        else if (pos < this.spaces.length) {
            toReturn = this.spaces[pos];
        }
        if (toReturn == null) {
            throw new NoSpaceException("No space for " + this.getWoundLocationString(pos));
        }
        return toReturn;
    }
    
    public void healFully() {
        if (this.wounds != null) {
            final Wound[] w = this.wounds.getWounds();
            for (int x = 0; x < w.length; ++x) {
                this.wounds.remove(w[x]);
            }
        }
        this.owner.setDisease((byte)0);
        this.owner.getStatus().removeWounds();
    }
    
    public short getCentimetersLong() {
        return this.centimetersLong;
    }
    
    public short getCentimetersHigh() {
        return this.centimetersHigh;
    }
    
    public short getCentimetersWide() {
        return this.centimetersWide;
    }
    
    public float getWeight(final byte weightLevel) {
        float modifier = 1.0f;
        if (weightLevel >= 50) {
            modifier = 1.0f + (weightLevel - 50) / 100.0f;
        }
        else {
            modifier = 0.5f * (1.0f + Math.max(1, weightLevel) / 50.0f);
        }
        return this.centimetersHigh * this.centimetersLong * this.centimetersWide / 1.4f * modifier;
    }
    
    public void setCentimetersLong(final short aCentimetersLong) {
        this.centimetersLong = aCentimetersLong;
        this.owner.calculateSize();
    }
    
    public void setCentimetersHigh(final short aCentimetersHigh) {
        this.centimetersHigh = aCentimetersHigh;
        this.owner.calculateSize();
    }
    
    public void setCentimetersWide(final short aCentimetersWide) {
        this.centimetersWide = aCentimetersWide;
        this.owner.calculateSize();
    }
    
    public Item getBodyPart(final int pos) throws NoSpaceException {
        if (this.spaces == null || pos < 0) {
            throw new NoSpaceException(String.valueOf(pos));
        }
        Item toReturn = null;
        if (pos == 1 || pos == 17) {
            toReturn = this.spaces[1];
        }
        else if (pos == 29 || pos == 18 || pos == 19 || pos == 20) {
            toReturn = this.spaces[29];
        }
        else if (pos == 2 || pos == 21 || pos == 27 || pos == 26 || pos == 32 || pos == 23 || pos == 24 || pos == 25 || pos == 22) {
            toReturn = this.spaces[2];
        }
        else if (pos == 3 || pos == 5 || pos == 9) {
            toReturn = this.spaces[3];
        }
        else if (pos == 4 || pos == 6 || pos == 10) {
            toReturn = this.spaces[4];
        }
        else if (pos == 30 || pos == 7 || pos == 11 || pos == 31 || pos == 8 || pos == 12) {
            toReturn = this.spaces[34];
        }
        else if (pos == 38 || pos == 40) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[14];
                }
            }
            else {
                toReturn = this.spaces[14];
            }
        }
        else if (pos == 37 || pos == 39) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[13];
                }
            }
            else {
                toReturn = this.spaces[13];
            }
        }
        else if (pos == 44) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[3];
                }
            }
            else {
                toReturn = this.spaces[3];
            }
        }
        else if (pos == 43 || pos == 41) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[34];
                }
            }
            else {
                toReturn = this.spaces[34];
            }
        }
        else if (pos == 36) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[1];
                }
            }
            else {
                toReturn = this.spaces[1];
            }
        }
        else if (pos == 35 || pos == 42 || pos == 45 || pos == 46 || pos == 47) {
            if (this.template.type == 0) {
                toReturn = this.spaces[pos];
                if (toReturn == null) {
                    toReturn = this.spaces[2];
                }
            }
            else {
                toReturn = this.spaces[2];
            }
        }
        else if (pos < this.spaces.length) {
            toReturn = this.spaces[pos];
        }
        if (toReturn == null) {
            throw new NoSpaceException("No space for " + this.getWoundLocationString(pos));
        }
        return toReturn;
    }
    
    public Item[] getSpaces() {
        return this.spaces;
    }
    
    public Item[] getAllItems() {
        final Set<Item> items = new HashSet<Item>();
        for (int x = 0; x < this.spaces.length; ++x) {
            if (this.spaces[x] != null) {
                final Item[] itemarr = this.spaces[x].getAllItems(false);
                for (int y = 0; y < itemarr.length; ++y) {
                    if (!itemarr[y].isBodyPart()) {
                        items.add(itemarr[y]);
                    }
                }
            }
        }
        Item[] toReturn = new Item[items.size()];
        toReturn = items.toArray(toReturn);
        return toReturn;
    }
    
    public Item[] getContainersAndWornItems() {
        final Set<Item> items = this.getContainersAndWornItems(this.getBodyItem());
        return items.toArray(new Item[items.size()]);
    }
    
    public Set<Item> getContainersAndWornItems(final Item item) {
        final Set<Item> items = item.getItems();
        final Set<Item> newItems = new HashSet<Item>();
        for (final Item next : items) {
            if (next.isBodyPart()) {
                newItems.addAll(this.getContainersAndWornItems(next));
            }
            else {
                newItems.add(next);
            }
        }
        return newItems;
    }
    
    public byte getRandomWoundPos() throws Exception {
        return this.template.getRandomWoundPos();
    }
    
    public byte getRandomWoundPos(final byte attackerStance) throws Exception {
        if (attackerStance == 7) {
            return this.template.getHighWoundPos();
        }
        if (attackerStance == 10) {
            return this.template.getLowWoundPos();
        }
        if (attackerStance == 6) {
            return this.template.getUpperLeftWoundPos();
        }
        if (attackerStance == 1) {
            return this.template.getUpperRightWoundPos();
        }
        if (attackerStance == 5) {
            return this.template.getMidLeftWoundPos();
        }
        if (attackerStance == 2) {
            return this.template.getMidRightWoundPos();
        }
        if (attackerStance == 3) {
            return this.template.getLowerRightWoundPos();
        }
        if (attackerStance == 4) {
            return this.template.getLowerLeftWoundPos();
        }
        return this.template.getCenterWoundPos();
    }
    
    public byte getCenterWoundPos() throws Exception {
        return this.template.getCenterWoundPos();
    }
    
    private void createBodyPart(final byte bodyConstant, final int tempalteId, final String partName, final byte constData) throws FailedException, NoSuchTemplateException {
        (this.spaces[bodyConstant] = ItemFactory.createBodyPart(this, bodyConstant, tempalteId, partName, 50.0f)).setAuxData(constData);
    }
    
    public void createBodyParts() throws FailedException, NoSuchTemplateException {
        if (this.initialized) {
            return;
        }
        this.createBodyPart((byte)0, 16, this.template.bodyS, (byte)24);
        this.createBodyPart((byte)1, 12, this.template.headS, (byte)2);
        this.createBodyPart((byte)13, 14, this.template.leftHandS, (byte)7);
        this.createBodyPart((byte)14, 14, this.template.rightHandS, (byte)8);
        this.createBodyPart((byte)15, 15, this.template.leftFootS, (byte)9);
        this.createBodyPart((byte)16, 15, this.template.rightFootS, (byte)10);
        this.createBodyPart((byte)2, 13, this.template.torsoS, (byte)3);
        this.createBodyPart((byte)29, 17, this.template.faceS, (byte)25);
        this.createBodyPart((byte)3, 11, this.template.leftArmS, (byte)5);
        this.createBodyPart((byte)4, 11, this.template.rightArmS, (byte)6);
        this.createBodyPart((byte)34, 19, this.template.legsS, (byte)4);
        if (this.template.type == 4) {
            this.spaces[28] = ItemFactory.createBodyPart(this, (short)28, 12, this.template.secondHeadS, 50.0f);
        }
        if (this.template.type == 8) {
            this.spaces[31] = ItemFactory.createBodyPart(this, (short)31, 10, this.template.rightLegS, 50.0f);
            this.spaces[30] = ItemFactory.createBodyPart(this, (short)30, 10, this.template.leftLegS, 50.0f);
        }
        if (this.template.type == 0 && this.owner.isPlayer()) {
            this.spaces[40] = this.createEquipmentSlot((byte)40, "right ring", (byte)16);
            this.spaces[14].insertItem(this.spaces[40]);
            final Item rHeld = this.createEquipmentSlot((byte)38, "right held item", (byte)1);
            rHeld.setDescription("main weapon");
            this.spaces[14].insertItem(rHeld);
            this.spaces[13].insertItem(this.createEquipmentSlot((byte)39, "left ring", (byte)17));
            final Item lHeld = this.createEquipmentSlot((byte)37, "left held item", (byte)0);
            lHeld.setDescription("off-hand weapon");
            this.spaces[13].insertItem(lHeld);
            this.spaces[3].insertItem(this.createEquipmentSlot((byte)44, "shield slot", (byte)11));
            this.spaces[2].insertItem(this.createEquipmentSlot((byte)45, "cape", (byte)14));
            this.spaces[2].insertItem(this.createEquipmentSlot((byte)46, "left shoulder", (byte)18));
            this.spaces[2].insertItem(this.createEquipmentSlot((byte)47, "right shoulder", (byte)19));
            this.spaces[2].insertItem(this.createEquipmentSlot((byte)42, "back", (byte)20));
            this.spaces[1].insertItem(this.createEquipmentSlot((byte)36, "neck", (byte)21));
            this.spaces[34].insertItem(this.createEquipmentSlot((byte)43, "belt", (byte)22));
            this.spaces[34].insertItem(this.createEquipmentSlot((byte)41, "hip slot", (byte)23));
            this.spaces[2].insertItem(this.createEquipmentSlot((byte)35, "tabard", (byte)15));
        }
        this.buildBody();
        this.initialized = true;
    }
    
    private Item createEquipmentSlot(final byte space, final String name, final byte slotConstant) throws FailedException, NoSuchTemplateException {
        final Item item = ItemFactory.createBodyPart(this, space, 823, name, 50.0f);
        item.setAuxData(slotConstant);
        item.setOwnerId(this.owner.getWurmId());
        return this.spaces[space] = item;
    }
    
    private void buildBody() {
        this.template.buildBody(this.spaces, this.owner);
    }
    
    public Item getBodyItem() {
        return this.spaces[0];
    }
    
    public void load() throws Exception {
        this.createBodyParts();
    }
    
    public void sleep(final Creature sleeper, final boolean epicServer) throws IOException {
        this.spaces[0].sleep(sleeper, epicServer);
    }
    
    public void poll() {
        if (this.wounds != null) {
            this.wounds.poll(this.owner);
        }
    }
    
    static {
        logger = Logger.getLogger(Body.class.getName());
    }
}
