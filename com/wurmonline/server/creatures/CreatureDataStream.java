// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.skills.SkillsFactory;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import java.util.logging.Level;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.bodys.BodyFactory;
import java.io.DataInputStream;
import java.io.IOException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.intra.PlayerTransfer;
import java.io.DataOutputStream;
import java.util.logging.Logger;

public class CreatureDataStream
{
    private static Logger logger;
    
    public static boolean validateCreature(final Creature animal) {
        return animal.getStatus() != null && animal.getTemplate() != null && animal.getBody() != null;
    }
    
    public static void toStream(final Creature animal, final DataOutputStream outputStream) throws IOException {
        final Offspring baby = animal.getOffspring();
        if (baby != null) {
            outputStream.writeBoolean(true);
            outputStream.writeLong(baby.getMother());
            outputStream.writeLong(baby.getFather());
            outputStream.writeLong(baby.getTraits());
            outputStream.writeByte((byte)baby.getDaysLeft());
        }
        else {
            outputStream.writeBoolean(false);
        }
        outputStream.writeLong(animal.getWurmId());
        outputStream.writeUTF(animal.name);
        outputStream.writeUTF(animal.getTemplate().getName());
        outputStream.writeByte(animal.getSex());
        outputStream.writeShort(animal.getCentimetersHigh());
        outputStream.writeShort(animal.getCentimetersLong());
        outputStream.writeShort(animal.getCentimetersWide());
        outputStream.writeLong(animal.getStatus().inventoryId);
        outputStream.writeLong(animal.getBody().getId());
        outputStream.writeLong(animal.getBuildingId());
        outputStream.writeShort(animal.getStatus().getStamina() & 0xFFFF);
        outputStream.writeShort(animal.getStatus().getHunger() & 0xFFFF);
        outputStream.writeFloat(animal.getStatus().getNutritionlevel());
        outputStream.writeShort(animal.getStatus().getThirst() & 0xFFFF);
        outputStream.writeBoolean(animal.isDead());
        outputStream.writeBoolean(animal.isStealth());
        outputStream.writeByte(0);
        outputStream.writeInt(animal.getStatus().age);
        outputStream.writeLong(animal.getStatus().lastPolledAge);
        outputStream.writeByte(animal.getStatus().fat);
        outputStream.writeLong(animal.getStatus().traits);
        outputStream.writeLong(-10L);
        outputStream.writeLong(animal.getMother());
        outputStream.writeLong(animal.getFather());
        outputStream.writeBoolean(animal.isReborn());
        outputStream.writeFloat(0.0f);
        outputStream.writeLong(animal.getStatus().lastPolledLoyalty);
        outputStream.writeBoolean(animal.isOffline());
        outputStream.writeBoolean(animal.isStayonline());
        outputStream.writeShort(animal.getStatus().detectInvisCounter);
        outputStream.writeByte(animal.getDisease());
        outputStream.writeLong(animal.getLastGroomed());
        outputStream.writeLong(animal.getVehicle());
        outputStream.writeByte(animal.getStatus().modtype);
        outputStream.writeUTF(animal.petName);
        if (animal.getSkills() == null || animal.getSkills().getSkillsNoTemp() == null) {
            outputStream.writeInt(0);
        }
        else {
            final Skill[] animalSkills = animal.getSkills().getSkillsNoTemp();
            final int numSkills = animalSkills.length;
            outputStream.writeInt(numSkills);
            for (final Skill curSkill : animalSkills) {
                if (!curSkill.isTemporary()) {
                    outputStream.writeInt(curSkill.getNumber());
                    outputStream.writeDouble(curSkill.getKnowledge());
                    outputStream.writeDouble(curSkill.getMinimumValue());
                    outputStream.writeLong(curSkill.lastUsed);
                    outputStream.writeLong(curSkill.id);
                }
            }
        }
        final Item[] animalItems = animal.getAllItems();
        int numItems = 0;
        for (final Item curItem : animalItems) {
            if (!curItem.isBodyPart() && !curItem.isInventory() && !curItem.isTemporary()) {
                ++numItems;
            }
        }
        outputStream.writeInt(numItems);
        for (final Item curItem : animalItems) {
            if (!curItem.isBodyPart() && !curItem.isInventory() && !curItem.isTemporary()) {
                PlayerTransfer.sendItem(curItem, outputStream, false);
            }
        }
    }
    
    public static void fromStream(final DataInputStream inputStream) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final boolean hasBaby = inputStream.readBoolean();
        if (hasBaby) {
            final long mother = inputStream.readLong();
            final long father = inputStream.readLong();
            final long traits = inputStream.readLong();
            final byte daysLeft = inputStream.readByte();
            new Offspring(mother, father, traits, daysLeft, false);
        }
        final long creatureId = inputStream.readLong();
        Creature animal = null;
        try {
            animal = new Creature(creatureId);
            animal.setName(inputStream.readUTF());
            animal.getStatus().template = CreatureTemplateFactory.getInstance().getTemplate(inputStream.readUTF());
            animal.template = animal.getStatus().template;
            animal.getStatus().setSex(inputStream.readByte());
            final short centimetersHigh = inputStream.readShort();
            final short centimetersLong = inputStream.readShort();
            final short centimetersWide = inputStream.readShort();
            animal.getStatus().inventoryId = inputStream.readLong();
            animal.getStatus().bodyId = inputStream.readLong();
            animal.getStatus().body = BodyFactory.getBody(animal, animal.getStatus().template.getBodyType(), centimetersHigh, centimetersLong, centimetersWide);
            animal.getStatus().buildingId = inputStream.readLong();
            if (animal.getStatus().buildingId != -10L) {
                try {
                    final Structure struct = Structures.getStructure(animal.getStatus().buildingId);
                    if (!struct.isFinalFinished()) {
                        animal.setStructure(struct);
                    }
                    else {
                        animal.getStatus().buildingId = -10L;
                    }
                }
                catch (NoSuchStructureException nss) {
                    animal.getStatus().buildingId = -10L;
                    CreatureDataStream.logger.log(Level.INFO, "Could not find structure for " + animal.name);
                    animal.setStructure(null);
                }
            }
            animal.getStatus().stamina = inputStream.readShort();
            animal.getStatus().hunger = inputStream.readShort();
            animal.getStatus().nutrition = inputStream.readFloat();
            animal.getStatus().thirst = inputStream.readShort();
            animal.getStatus().dead = inputStream.readBoolean();
            animal.getStatus().stealth = inputStream.readBoolean();
            animal.getStatus().kingdom = inputStream.readByte();
            animal.getStatus().age = inputStream.readInt();
            animal.getStatus().lastPolledAge = inputStream.readLong();
            animal.getStatus().fat = inputStream.readByte();
            animal.getStatus().traits = inputStream.readLong();
            if (animal.getStatus().traits != 0L) {
                animal.getStatus().setTraitBits(animal.getStatus().traits);
            }
            animal.dominator = inputStream.readLong();
            animal.getStatus().mother = inputStream.readLong();
            animal.getStatus().father = inputStream.readLong();
            animal.getStatus().reborn = inputStream.readBoolean();
            animal.getStatus().loyalty = inputStream.readFloat();
            animal.getStatus().lastPolledLoyalty = inputStream.readLong();
            animal.getStatus().offline = inputStream.readBoolean();
            animal.getStatus().stayOnline = inputStream.readBoolean();
            animal.getStatus().detectInvisCounter = inputStream.readShort();
            animal.getStatus().disease = inputStream.readByte();
            animal.getStatus().lastGroomed = inputStream.readLong();
            final long hitchedTo = inputStream.readLong();
            if (hitchedTo > 0L) {
                try {
                    final Item vehicle = Items.getItem(hitchedTo);
                    final Vehicle vehic = Vehicles.getVehicle(vehicle);
                    if (vehic != null && vehic.addDragger(animal)) {
                        animal.setHitched(vehic, true);
                        final Seat driverseat = vehic.getPilotSeat();
                        if (driverseat != null) {
                            final float _r = (-vehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                            final float _s = (float)Math.sin(_r);
                            final float _c = (float)Math.cos(_r);
                            final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
                            final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
                            final float nPosX = animal.getStatus().getPositionX() - xo;
                            final float nPosY = animal.getStatus().getPositionY() - yo;
                            final float nPosZ = animal.getStatus().getPositionZ() - driverseat.offz;
                            animal.getStatus().setPositionX(nPosX);
                            animal.getStatus().setPositionY(nPosY);
                            animal.getStatus().setRotation(-vehicle.getRotation() + 180.0f);
                            animal.getMovementScheme().setPosition(animal.getStatus().getPositionX(), animal.getStatus().getPositionY(), nPosZ, animal.getStatus().getRotation(), animal.getLayer());
                        }
                    }
                }
                catch (NoSuchItemException e) {
                    CreatureDataStream.logger.log(Level.WARNING, "Exception", e);
                }
            }
            animal.getStatus().modtype = inputStream.readByte();
            animal.setPetName(inputStream.readUTF());
            animal.loadTemplate();
            Creatures.getInstance().addCreature(animal, false, false);
        }
        catch (Exception e2) {
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e2);
        }
        try {
            assert animal != null;
            (animal.skills = SkillsFactory.createSkills(animal.getWurmId())).clone(animal.template.getSkills().getSkills());
        }
        catch (Exception e2) {
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e2);
        }
        final int numSkills = inputStream.readInt();
        try {
            for (int skillNo = 0; skillNo < numSkills; ++skillNo) {
                final int curSkillNum = inputStream.readInt();
                final double curSkillValue = inputStream.readDouble();
                final double curSkillMinValue = inputStream.readDouble();
                final long curSkillLastUsed = inputStream.readLong();
                inputStream.readLong();
                animal.skills.learn(curSkillNum, (float)curSkillMinValue, false);
                animal.skills.getSkill(curSkillNum).lastUsed = curSkillLastUsed;
                animal.skills.getSkill(curSkillNum).setKnowledge(curSkillValue, false);
            }
        }
        catch (Exception e3) {
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e3);
        }
        try {
            animal.getBody().createBodyParts();
        }
        catch (FailedException | NoSuchTemplateException ex2) {
            final WurmServerException ex;
            final WurmServerException e4 = ex;
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e4);
        }
        try {
            animal.loadPossessions(animal.getStatus().inventoryId);
        }
        catch (Exception e3) {
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e3);
        }
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("insert into CREATURES (WURMID, NAME, TEMPLATENAME, SEX, CENTIMETERSHIGH, CENTIMETERSLONG, CENTIMETERSWIDE, INVENTORYID, BODYID, BUILDINGID, STAMINA, HUNGER, NUTRITION, THIRST, DEAD, STEALTH, KINGDOM, AGE, LASTPOLLEDAGE, FAT, TRAITS, DOMINATOR, MOTHER, FATHER, REBORN, LOYALTY, LASTPOLLEDLOYALTY, OFFLINE, STAYONLINE, DETECTIONSECS, DISEASE, LASTGROOMED, VEHICLE, TYPE, PETNAME) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setLong(1, animal.getWurmId());
            ps.setString(2, animal.name);
            ps.setString(3, animal.getTemplate().getName());
            ps.setByte(4, animal.getSex());
            ps.setShort(5, animal.getCentimetersHigh());
            ps.setShort(6, animal.getCentimetersLong());
            ps.setShort(7, animal.getCentimetersWide());
            ps.setLong(8, animal.getStatus().inventoryId);
            ps.setLong(9, animal.getBody().getId());
            ps.setLong(10, animal.getBuildingId());
            ps.setShort(11, (short)(animal.getStatus().getStamina() & 0xFFFF));
            ps.setShort(12, (short)(animal.getStatus().getHunger() & 0xFFFF));
            ps.setFloat(13, animal.getStatus().getNutritionlevel());
            ps.setShort(14, (short)(animal.getStatus().getThirst() & 0xFFFF));
            ps.setBoolean(15, animal.isDead());
            ps.setBoolean(16, animal.isStealth());
            ps.setByte(17, animal.getCurrentKingdom());
            ps.setInt(18, animal.getStatus().age);
            ps.setLong(19, animal.getStatus().lastPolledAge);
            ps.setByte(20, animal.getStatus().fat);
            ps.setLong(21, animal.getStatus().traits);
            ps.setLong(22, -10L);
            ps.setLong(23, animal.getMother());
            ps.setLong(24, animal.getFather());
            ps.setBoolean(25, animal.isReborn());
            ps.setFloat(26, animal.getLoyalty());
            ps.setLong(27, animal.getStatus().lastPolledLoyalty);
            ps.setBoolean(28, animal.isOffline());
            ps.setBoolean(29, animal.isStayonline());
            ps.setShort(30, (short)animal.getStatus().detectInvisCounter);
            ps.setByte(31, animal.getDisease());
            ps.setLong(32, animal.getLastGroomed());
            ps.setLong(33, animal.getVehicle());
            ps.setByte(34, animal.getStatus().modtype);
            ps.setString(35, animal.petName);
            ps.execute();
        }
        catch (SQLException e5) {
            CreatureDataStream.logger.log(Level.WARNING, "Exception", e5);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        animal.getStatus().setStatusExists(true);
        inputStream.readInt();
        Items.loadAllItemsForCreature(animal, animal.getStatus().getInventoryId());
    }
    
    static {
        CreatureDataStream.logger = Logger.getLogger(CreatureDataStream.class.getName());
    }
}
