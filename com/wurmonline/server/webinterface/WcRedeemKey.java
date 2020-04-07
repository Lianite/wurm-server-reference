// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;

public class WcRedeemKey extends WebCommand
{
    private static final Logger logger;
    
    public WcRedeemKey(final long playerId, final String coupon, final byte reply) {
        super(WurmId.getNextWCCommandId(), (short)26);
    }
    
    public WcRedeemKey(final long aId, final byte[] aData) {
        super(aId, (short)26, aData);
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    byte[] encode() {
        final byte[] barr = null;
        return barr;
    }
    
    @Override
    public void execute() {
    }
    
    private static final Player getRedeemingPlayer(final long wurmId) {
        try {
            return Players.getInstance().getPlayer(wurmId);
        }
        catch (NoSuchPlayerException e) {
            WcRedeemKey.logger.log(Level.WARNING, e.getMessage(), e);
            return null;
        }
    }
    
    static {
        logger = Logger.getLogger(WcRedeemKey.class.getName());
    }
}
