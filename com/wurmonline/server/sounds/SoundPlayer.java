// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.sounds;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class SoundPlayer
{
    private static final Logger logger;
    
    public static final void playSound(final String soundName, final Creature creature, final float height) {
        if (soundName.length() > 0) {
            try {
                final VolaTile vtile = Zones.getOrCreateTile(creature.getTileX(), creature.getTileY(), creature.isOnSurface());
                final float offsetx = 4.0f * Server.rand.nextFloat();
                final float offsety = 4.0f * Server.rand.nextFloat();
                final Sound so = new Sound(soundName, creature.getPosX() - 2.0f + offsetx, creature.getPosY() - 2.0f + offsety, Zones.calculateHeight(creature.getPosX(), creature.getPosY(), creature.isOnSurface()) + height, 1.0f, 1.0f, 5.0f);
                vtile.addSound(so);
            }
            catch (NoSuchZoneException nsz) {
                SoundPlayer.logger.log(Level.WARNING, "Can't play sound at " + creature.getPosX() + ", " + creature.getPosY() + " surfaced=" + creature.isOnSurface(), nsz);
            }
        }
    }
    
    public static final void playSound(final String soundName, final Item item, final float height) {
        try {
            final VolaTile vtile = Zones.getOrCreateTile((int)item.getPosX() >> 2, (int)item.getPosY() >> 2, item.isOnSurface());
            final Sound so = new Sound(soundName, item.getPosX(), item.getPosY(), Zones.calculateHeight(item.getPosX(), item.getPosY(), item.isOnSurface()) + height, 1.0f, 1.0f, 5.0f);
            vtile.addSound(so);
        }
        catch (NoSuchZoneException nsz) {
            SoundPlayer.logger.log(Level.WARNING, "Can't play sound at " + item.getPosX() + ", " + item.getPosY() + " surfaced=" + item.isOnSurface(), nsz);
        }
    }
    
    public static final void playSound(final String soundName, final int tilex, final int tiley, final boolean surfaced, final float height) {
        try {
            final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, surfaced);
            final Sound so = new Sound(soundName, (tilex << 2) + 2, (tiley << 2) + 2, Zones.calculateHeight((tilex << 2) + 2, (tiley << 2) + 2, surfaced) + height, 1.0f, 1.0f, 5.0f);
            vtile.addSound(so);
        }
        catch (NoSuchZoneException ex) {}
    }
    
    public static final void playSong(final String songName, final Creature creature) {
        playSong(songName, creature, 1.0f);
    }
    
    public static final void playSong(final String songName, final Creature creature, final float pitch) {
        try {
            final Sound so = new Sound(songName, creature.getPosX(), creature.getPosY(), Zones.calculateHeight(creature.getPosX(), creature.getPosY(), creature.isOnSurface()), 1.0f, pitch, 5.0f);
            creature.getCommunicator().sendMusic(so);
        }
        catch (NoSuchZoneException nsz) {
            SoundPlayer.logger.log(Level.WARNING, "Can't play sound at " + creature.getPosX() + ", " + creature.getPosY() + " surfaced=" + creature.isOnSurface(), nsz);
        }
    }
    
    static {
        logger = Logger.getLogger(SoundPlayer.class.getName());
    }
}
