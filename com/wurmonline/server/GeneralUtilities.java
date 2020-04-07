// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.behaviours.Vehicles;
import java.util.BitSet;
import java.util.Date;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.behaviours.ItemBehaviour;
import com.wurmonline.server.items.CreationMatrix;
import com.wurmonline.server.items.CreationEntry;
import java.util.Map;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.items.Item;

public final class GeneralUtilities implements MiscConstants
{
    public static boolean isValidTileLocation(final int tilex, final int tiley) {
        return tilex >= 0 && tilex < 1 << Constants.meshSize && tiley >= 0 && tiley < 1 << Constants.meshSize;
    }
    
    public static float calcOreRareQuality(final double power, final int actionBonus, final int toolBonus) {
        return calcRareQuality(power, actionBonus, toolBonus, 0, 2, 108.428f);
    }
    
    public static float calcRareQuality(final double power, final int actionBonus, final int toolBonus) {
        return calcRareQuality(power, actionBonus, toolBonus, 0, 2, 100.0f);
    }
    
    public static float calcRareQuality(final double power, final int actionBonus, final int toolBonus, final int targetBonus) {
        return calcRareQuality(power, actionBonus, toolBonus, targetBonus, 3, 100.0f);
    }
    
    public static float calcRareQuality(final double power, final int actionBonus, final int toolBonus, final int targetBonus, final int numbBonus, final float fiddleFactor) {
        final float rPower = (float)power;
        final int totalBonus = toolBonus + targetBonus + actionBonus;
        float bonus = 0.0f;
        if (totalBonus > 0) {
            final float val = fiddleFactor - rPower;
            final float square = val * val;
            final float n = square / 1000.0f;
            final float mod = Math.min(n * 1.25f, 1.0f);
            bonus = totalBonus * 3.0f / numbBonus * mod;
        }
        return Math.max(Math.min(99.999f, rPower + bonus), 1.0f);
    }
    
    public static final Map<String, Map<CreationEntry, Integer>> getCreationList(final Item source, final Item target, final Player player) {
        final CreationEntry[] entries = CreationMatrix.getInstance().getCreationOptionsFor(source, target);
        final Map<String, Map<CreationEntry, Integer>> map = ItemBehaviour.generateMapfromOptions(player, source, target, entries);
        return map;
    }
    
    public static String toGMTString(final long aDate) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return sdf.format(new Date(aDate));
    }
    
    public static void setSettingsBits(final BitSet bits, final int value) {
        for (int x = 0; x < 32; ++x) {
            bits.set(x, (value & 0x1) == 0x1);
        }
    }
    
    public static int getIntSettingsFrom(final BitSet bits) {
        int ret = 0;
        for (int x = 0; x < 32; ++x) {
            if (bits.get(x)) {
                ret += (int)(1L << x);
            }
        }
        return ret;
    }
    
    public static boolean isOnSameLevel(final Creature creature1, final Creature creature2) {
        final float difference = Math.abs(creature1.getStatus().getPositionZ() - creature2.getStatus().getPositionZ()) * 10.0f;
        return difference < 30.0f;
    }
    
    public static boolean mayAttackSameLevel(final Creature creature1, final Creature creature2) {
        final float difference = Math.abs(creature1.getStatus().getPositionZ() - creature2.getStatus().getPositionZ()) * 10.0f;
        return difference < 29.7f;
    }
    
    public static boolean isOnSameLevel(final Creature creature, final Item item) {
        float pz = creature.getStatus().getPositionZ();
        if (creature.getVehicle() != -10L) {
            final Vehicle vehicle = Vehicles.getVehicleForId(creature.getVehicle());
            if (vehicle != null) {
                pz = vehicle.getPosZ();
            }
        }
        final float difference = Math.abs(Math.max(0.0f, pz) - Math.max(0.0f, item.getPosZ())) * 10.0f;
        return difference < 30.0f;
    }
    
    public static short getHeight(final int tilex, final int tiley, final boolean onSurface) {
        if (onSurface) {
            return Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley));
        }
        return Tiles.decodeHeight(Server.caveMesh.getTile(tilex, tiley));
    }
}
