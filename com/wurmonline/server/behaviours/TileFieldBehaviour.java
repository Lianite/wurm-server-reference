// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.Servers;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.logging.Level;
import java.util.Random;
import com.wurmonline.server.Players;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.Server;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.Trap;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class TileFieldBehaviour extends TileBehaviour
{
    private static final Logger logger;
    private static final String[] harvestStrings;
    
    TileFieldBehaviour() {
        super((short)17);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, subject, tilex, tiley, onSurface, tile));
        final int data = Tiles.decodeData(tile) & 0xFF;
        final int tileState = data >> 4;
        final int tileAge = tileState & 0x7;
        final boolean farmed = tileState >> 3 == 1;
        final int crop = data & 0xF;
        if (!farmed && subject.isFieldTool() && !Zones.protectedTiles[tilex][tiley]) {
            toReturn.add(Actions.actionEntrys[151]);
        }
        if (tileAge != 0 && tileAge != 7 && !Zones.protectedTiles[tilex][tiley]) {
            if (subject.getTemplateId() == 268) {
                toReturn.add(Actions.actionEntrys[152]);
            }
            else if (crop > 3 || Tiles.decodeType(tile) == Tiles.Tile.TILE_FIELD2.id) {
                toReturn.add(Actions.actionEntrys[152]);
            }
        }
        if (tileAge < 7 && subject.getTemplateId() == 176 && performer.getPower() >= 2) {
            toReturn.add(Actions.actionEntrys[188]);
        }
        if (subject.getTemplateId() == 526) {
            toReturn.add(Actions.actionEntrys[118]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, tilex, tiley, onSurface, tile));
        final int data = Tiles.decodeData(tile) & 0xFF;
        final int tileState = data >> 4;
        final int tileAge = tileState & 0x7;
        final int crop = data & 0xF;
        if (tileAge != 0 && tileAge != 7 && !Zones.protectedTiles[tilex][tiley] && (crop > 3 || Tiles.decodeType(tile) == Tiles.Tile.TILE_FIELD2.id)) {
            toReturn.add(Actions.actionEntrys[152]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final short action, final float counter) {
        boolean done = true;
        if (action == 1) {
            final byte data = Tiles.decodeData(tile);
            final byte type = Tiles.decodeType(tile);
            final int tileAge = Crops.decodeFieldAge(data);
            final boolean farmed = Crops.decodeFieldState(data);
            final int crop = Crops.getCropNumber(type, data);
            final Communicator comm = performer.getCommunicator();
            final Trap t = Trap.getTrap(tilex, tiley, performer.getLayer());
            if (performer.getPower() > 2) {
                comm.sendNormalServerMessage("data=" + data + ", age=" + tileAge + ", farmed=" + farmed + ", crop=" + crop + ".");
                if (t != null) {
                    String villageName = "none";
                    if (t.getVillage() > 0) {
                        try {
                            villageName = Villages.getVillage(t.getVillage()).getName();
                        }
                        catch (NoSuchVillageException ex) {}
                    }
                    comm.sendNormalServerMessage("A " + t.getName() + ", ql=" + t.getQualityLevel() + " kingdom=" + Kingdoms.getNameFor(t.getKingdom()) + ", vill=" + villageName + ", rotdam=" + t.getRotDamage() + " firedam=" + t.getFireDamage() + " speed=" + t.getSpeedBon());
                }
            }
            else if (t != null && (t.getKingdom() == performer.getKingdomId() || performer.getDetectDangerBonus() > 0.0f)) {
                String qlString = "average";
                if (t.getQualityLevel() < 20) {
                    qlString = "low";
                }
                else if (t.getQualityLevel() > 80) {
                    qlString = "deadly";
                }
                else if (t.getQualityLevel() > 50) {
                    qlString = "high";
                }
                String villageName2 = ".";
                if (t.getVillage() > 0) {
                    try {
                        villageName2 = " of " + Villages.getVillage(t.getVillage()).getName() + ".";
                    }
                    catch (NoSuchVillageException ex2) {}
                }
                String rotDam = "";
                if (t.getRotDamage() > 0) {
                    rotDam = " It has ugly black-green speckles.";
                }
                String fireDam = "";
                if (t.getFireDamage() > 0) {
                    fireDam = " It has the rune of fire.";
                }
                final StringBuilder buf = new StringBuilder();
                buf.append("You detect a ");
                buf.append(t.getName());
                buf.append(" here, of ");
                buf.append(qlString);
                buf.append(" quality.");
                buf.append(" It has been set by people from ");
                buf.append(Kingdoms.getNameFor(t.getKingdom()));
                buf.append(villageName2);
                buf.append(rotDam);
                buf.append(fireDam);
                comm.sendNormalServerMessage(buf.toString());
            }
            String growString = "The crops grow steadily.";
            if (!farmed) {
                growString = "It could use a touch from the rake or some other farming tool.";
            }
            if (tileAge != 0 && tileAge != 7) {
                final Skill farming = performer.getSkills().getSkillOrLearn(10049);
                final String cropString = Crops.getCropName(crop);
                final double skill = farming.getKnowledge(0.0);
                if (skill < 10.0) {
                    comm.sendNormalServerMessage("You see a farmer's field. " + growString);
                }
                else if (skill < 20.0) {
                    comm.sendNormalServerMessage("You see a farmer's field growing " + cropString + ". " + growString);
                }
                else if (skill < 60.0) {
                    comm.sendNormalServerMessage("You see a farmer's field growing " + cropString + ". " + TileFieldBehaviour.harvestStrings[tileAge] + " " + growString);
                }
                else if (skill <= 100.0) {
                    float ageYieldFactor = 0.0f;
                    if (tileAge >= 3) {
                        if (tileAge < 4) {
                            ageYieldFactor = 0.5f;
                        }
                        else if (tileAge < 5) {
                            ageYieldFactor = 0.7f;
                        }
                        else if (tileAge < 7) {
                            ageYieldFactor = 1.0f;
                        }
                    }
                    final float realKnowledge = (float)farming.getKnowledge(0.0);
                    final int worldResource = Server.getWorldResource(tilex, tiley);
                    final int farmedCount = worldResource >>> 11;
                    final int farmedChance = worldResource & 0x7FF;
                    final short resource = (short)(farmedChance + Math.min(5, farmedCount) * 50);
                    final float div = 100.0f - realKnowledge / 15.0f;
                    final short bonusYield = (short)(resource / div / 1.5f);
                    final float baseYield = realKnowledge / 15.0f;
                    int quantity = (int)((baseYield + bonusYield) * ageYieldFactor);
                    if (quantity <= 1 && farmedCount > 0 && (tileAge == 5 || tileAge == 6)) {
                        if (farmedCount > 2) {
                            ++quantity;
                        }
                        if (farmedCount > 4) {
                            ++quantity;
                        }
                    }
                    if (quantity == 0 && (tileAge == 5 || tileAge == 6)) {
                        quantity = 1;
                    }
                    final String measureString = Crops.getMeasure(crop);
                    String amountString = "about " + quantity + " " + cropString + "s.";
                    if (measureString.length() > 0) {
                        amountString = "about " + quantity + " " + measureString + " of " + cropString + ".";
                    }
                    comm.sendNormalServerMessage("You see a farmer's field, which would render " + amountString + " " + TileFieldBehaviour.harvestStrings[tileAge] + " " + growString);
                }
            }
            else if (tileAge == 0) {
                comm.sendNormalServerMessage("You see a patch of freshly sown field. " + growString);
            }
            else if (tileAge == 7) {
                comm.sendNormalServerMessage("You see a patch of soil containing old rotten weeds.");
            }
            TileBehaviour.sendVillageString(performer, tilex, tiley, true);
        }
        else {
            if (action == 152 && !Zones.protectedTiles[tilex][tiley]) {
                return Terraforming.harvest(performer, tilex, tiley, onSurface, tile, counter, null);
            }
            if (action == 109) {
                done = super.action(act, performer, tilex, tiley, onSurface, tile, action, counter);
            }
            else if (action == 56) {
                performer.getCommunicator().sendNormalServerMessage("You can't plan a building on a field.");
            }
            else {
                done = super.action(act, performer, tilex, tiley, onSurface, tile, action, counter);
            }
        }
        return done;
    }
    
    public static void graze(final int tilex, final int tiley, final int tile) {
        final byte data = Tiles.decodeData(tile);
        final byte type = Tiles.decodeType(tile);
        final int tileState = data >> 4;
        int tileAge = tileState & 0x7;
        final int crop = Crops.getCropNumber(type, data);
        if (tileAge == 7 || tileAge <= 1) {
            Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0);
            Server.setWorldResource(tilex, tiley, 0);
            Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            return;
        }
        --tileAge;
        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Crops.getTileType(crop), Crops.encodeFieldData(false, tileAge, crop));
        final int worldResource = Server.getWorldResource(tilex, tiley);
        int farmedCount = worldResource >>> 11;
        int farmedChance = worldResource & 0x7FF;
        final Random rand = new Random();
        if (rand.nextBoolean()) {
            if (farmedCount == 0) {
                ++farmedCount;
            }
            farmedChance = Math.min(farmedChance + 150, 2047);
        }
        Server.setWorldResource(tilex, tiley, (farmedCount << 11) + farmedChance);
        Players.getInstance().sendChangedTile(tilex, tiley, true, false);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final short action, final float counter) {
        boolean done = true;
        if (action == 151 && source.isFieldTool() && !Zones.protectedTiles[tilex][tiley]) {
            if (Tiles.decodeType(tile) != Tiles.Tile.TILE_FIELD.id && Tiles.decodeType(tile) != Tiles.Tile.TILE_FIELD2.id) {
                performer.getCommunicator().sendNormalServerMessage("The ground cannot be farmed any more.", (byte)3);
                return true;
            }
            try {
                if (!Terraforming.isFlat(tilex, tiley, onSurface, 4)) {
                    performer.getCommunicator().sendNormalServerMessage("The ground is not flat enough for crops to grow. You need to flatten it first.", (byte)3);
                    return true;
                }
            }
            catch (IllegalArgumentException iae) {
                performer.getCommunicator().sendNormalServerMessage("The water will eat away the field in no time. You cannot farm there.", (byte)3);
                return true;
            }
            final byte data = Tiles.decodeData(tile);
            final byte type = Tiles.decodeType(tile);
            final int tileAge = Crops.decodeFieldAge(data);
            final boolean farmed = Crops.decodeFieldState(data);
            final int crop = Crops.getCropNumber(type, data);
            final double difficulty = Crops.getDifficultyFor(crop);
            final Skills skills = performer.getSkills();
            final Skill farming = skills.getSkillOrLearn(10049);
            Skill tool = null;
            try {
                tool = skills.getSkill(source.getPrimarySkill());
            }
            catch (Exception ex) {
                try {
                    tool = skills.learn(source.getPrimarySkill(), 1.0f);
                }
                catch (NoSuchSkillException nss) {
                    TileFieldBehaviour.logger.log(Level.INFO, source.getName() + " has no skill related for farming.");
                }
            }
            int time = 100;
            if (tileAge == 7) {
                done = false;
                if (counter == 1.0f) {
                    time = Actions.getStandardActionTime(performer, farming, source, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start preparing the field for sowing.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts preparing the field for sowing.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[151].getVerbString(), true, time);
                    source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    performer.getStatus().modifyStamina(-1500.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.mayPlaySound()) {
                        Methods.sendSound(performer, "sound.work.farming.rake");
                    }
                    if (act.currentSecond() % 5 == 0) {
                        source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                        performer.getStatus().modifyStamina(-10000.0f);
                    }
                    if (counter * 10.0f > time) {
                        final double power = farming.skillCheck(difficulty, source, 0.0, false, counter);
                        if (tool != null) {
                            tool.skillCheck(difficulty, source, 0.0, false, counter);
                        }
                        done = true;
                        try {
                            final int seedTemplate = Crops.getItemTemplate(crop);
                            final double ql = (farming.getKnowledge(0.0) + (100.0 - farming.getKnowledge(0.0)) * ((float)power / 500.0f)) * 0.5;
                            final Item result = ItemFactory.createItem(seedTemplate, (float)Math.max(Math.min(ql, 100.0), 1.0), null);
                            performer.getCommunicator().sendNormalServerMessage("You find " + (result.getName().endsWith("s") ? "some " : "a ") + result.getName() + " in amongst the weeds.");
                            if (!performer.getInventory().insertItem(result, true)) {
                                performer.getCommunicator().sendNormalServerMessage("You can't carry the " + result.getName() + ". It falls to the ground and is ruined!");
                            }
                        }
                        catch (FailedException ex2) {}
                        catch (NoSuchTemplateException ex3) {}
                        Server.getInstance().broadCastAction(performer.getName() + " has prepared the field for sowing.", performer, 5);
                        performer.getCommunicator().sendNormalServerMessage("The field is now prepared for sowing.");
                        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0);
                        Players.getInstance().sendChangedTile(tilex, tiley, onSurface, true);
                    }
                }
            }
            else if (!farmed) {
                double power = 0.0;
                double bonus = 0.0;
                done = false;
                if (counter == 1.0f) {
                    time = Actions.getStandardActionTime(performer, farming, source, 0.0);
                    act.setTimeLeft(time);
                    Server.getInstance().broadCastAction(performer.getName() + " starts tending the field.", performer, 5);
                    performer.getCommunicator().sendNormalServerMessage("You start removing weeds and otherwise put the field in good order.");
                    performer.sendActionControl(Actions.actionEntrys[151].getVerbString(), true, time);
                    source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                    performer.getStatus().modifyStamina(-1500.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.currentSecond() % 5 == 0) {
                        Methods.sendSound(performer, "sound.work.farming.rake");
                        source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                        performer.getStatus().modifyStamina(-10000.0f);
                    }
                }
                if (counter * 10.0f > time) {
                    if (act.getRarity() != 0) {
                        performer.playPersonalSound("sound.fx.drumroll");
                    }
                    if (tool != null) {
                        bonus = tool.skillCheck(difficulty, source, 0.0, false, counter) / 10.0;
                    }
                    power = Math.max(0.0, farming.skillCheck(difficulty, source, bonus, false, counter));
                    done = true;
                    if (power <= 0.0) {
                        performer.getCommunicator().sendNormalServerMessage("The field is tended.");
                    }
                    else if (power < 25.0) {
                        performer.getCommunicator().sendNormalServerMessage("The field is now tended.");
                    }
                    else if (power < 50.0) {
                        performer.getCommunicator().sendNormalServerMessage("The field looks better after your tending.");
                    }
                    else if (power < 75.0) {
                        performer.getCommunicator().sendNormalServerMessage("The field is now groomed.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The field is now nicely groomed.");
                    }
                    Server.getInstance().broadCastAction(performer.getName() + " is pleased as the field is now in order.", performer, 5);
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Crops.getTileType(crop), Crops.encodeFieldData(true, tileAge, crop));
                    final int worldResource = Server.getWorldResource(tilex, tiley);
                    int farmedCount = worldResource >>> 11;
                    int farmedChance = worldResource & 0x7FF;
                    if (farmedCount < 5) {
                        ++farmedCount;
                        farmedChance = (int)Math.min(farmedChance + power * 2.0 + act.getRarity() * 110 + source.getRarity() * 10, 2047.0);
                    }
                    if (source.getSpellEffects() != null) {
                        final float extraChance = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1.0f;
                        if (extraChance > 0.0f && Server.rand.nextFloat() < extraChance) {
                            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " seems to have an extra effect on the field.");
                            farmedChance = Math.min(farmedChance + 100, 2047);
                            if (farmedCount < 5) {
                                ++farmedCount;
                            }
                        }
                    }
                    Server.setWorldResource(tilex, tiley, (farmedCount << 11) + farmedChance);
                    if (Servers.isThisATestServer()) {
                        performer.getCommunicator().sendNormalServerMessage("farmedCount is:" + farmedCount + " farmedChance is:" + farmedChance);
                    }
                    Players.getInstance().sendChangedTile(tilex, tiley, onSurface, false);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The crops are growing nicely and the field is in order already.");
            }
        }
        else if (action == 1) {
            done = this.action(act, performer, tilex, tiley, onSurface, tile, action, counter);
        }
        else if (action == 152 && !Zones.protectedTiles[tilex][tiley]) {
            if (source.getTemplateId() == 268) {
                return Terraforming.harvest(performer, tilex, tiley, onSurface, tile, counter, source);
            }
            return Terraforming.harvest(performer, tilex, tiley, onSurface, tile, counter, null);
        }
        else if (action == 188) {
            if (source.getTemplateId() == 176 && performer.getPower() >= 2) {
                done = Terraforming.growFarm(performer, tile, tilex, tiley, onSurface);
            }
        }
        else if (action == 118 && source.getTemplateId() == 526) {
            performer.getCommunicator().sendNormalServerMessage("You draw a circle in the air in front of you with " + source.getNameWithGenus() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " draws a circle in the air in front of " + performer.getHimHerItString() + " with " + source.getNameWithGenus() + ".", performer, 5);
            done = true;
            if (source.getAuxData() > 0) {
                final byte data = Tiles.decodeData(tile);
                final byte type = Tiles.decodeType(tile);
                final int crop2 = Crops.getCropNumber(type, data);
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Crops.getTileType(crop2), Crops.encodeFieldData(false, 5, crop2));
                Players.getInstance().sendChangedTile(tilex, tiley, onSurface, false);
                source.setAuxData((byte)(source.getAuxData() - 1));
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
            }
        }
        else {
            done = super.action(act, performer, source, tilex, tiley, onSurface, heightOffset, tile, action, counter);
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(TileFieldBehaviour.class.getName());
        harvestStrings = new String[] { "The ground is freshly sown and the seeds are evolving.", "A few green blades pop out of the ground.", "Small sprouts with many blades grow here.", "The sprouts are growing, a bit above half their mature height.", "The field is almost at full height.", "The field is at full height and ready to harvest!", "The field is at full height and ready to harvest!", "The field is barren, with weeds rotting in the dirt." };
    }
}
