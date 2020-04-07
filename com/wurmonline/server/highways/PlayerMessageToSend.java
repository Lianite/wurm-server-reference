// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.items.Item;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

public class PlayerMessageToSend
{
    private static final Logger logger;
    private final Player player;
    private final String text;
    
    PlayerMessageToSend(final Player player, final String text) {
        this.player = player;
        this.text = text;
    }
    
    void send() {
        this.player.getCommunicator().sendNormalServerMessage(this.text);
        for (final Item waystone : Items.getWaystones()) {
            final VolaTile vt = Zones.getTileOrNull(waystone.getTileX(), waystone.getTileY(), waystone.isOnSurface());
            if (vt != null) {
                for (final VirtualZone vz : vt.getWatchers()) {
                    try {
                        if (vz.getWatcher().getWurmId() == this.player.getWurmId()) {
                            this.player.getCommunicator().sendWaystoneData(waystone);
                            break;
                        }
                    }
                    catch (Exception e) {
                        PlayerMessageToSend.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(PlayerMessageToSend.class.getName());
    }
}
