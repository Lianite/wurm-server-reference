// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

public final class WurmPermissions implements MiscConstants
{
    public static boolean mayCreateItems(final Creature performer) {
        return Servers.isThisATestServer() || performer.getPower() >= 2 || Players.isArtist(performer.getWurmId(), false, false);
    }
    
    public static boolean mayChangeTile(final Creature performer) {
        return performer.getPower() >= 3 || Players.isArtist(performer.getWurmId(), false, false);
    }
    
    public static boolean mayUseDeityWand(final Creature performer) {
        return performer.getPower() >= 2 || Players.isArtist(performer.getWurmId(), false, false);
    }
    
    public static boolean mayUseGMWand(final Creature performer) {
        return performer.getPower() >= 2 || Players.isArtist(performer.getWurmId(), false, false);
    }
    
    public static boolean maySetFaith(final Creature performer) {
        return performer.getPower() >= 3 || (Servers.isThisATestServer() && Players.isArtist(performer.getWurmId(), false, false));
    }
}
