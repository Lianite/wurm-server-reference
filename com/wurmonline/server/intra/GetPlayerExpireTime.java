// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.io.IOException;
import com.wurmonline.server.Servers;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

final class GetPlayerExpireTime extends IntraCommand implements MiscConstants
{
    private static final Logger logger;
    private boolean done;
    private IntraClient client;
    private final long wurmId;
    
    private GetPlayerExpireTime(final long playerId) {
        this.done = false;
        this.wurmId = playerId;
    }
    
    @Override
    public boolean poll() {
        if (Servers.isThisLoginServer()) {
            return true;
        }
        if (this.client == null) {
            try {
                (this.client = new IntraClient(Servers.loginServer.INTRASERVERADDRESS, Integer.parseInt(Servers.loginServer.INTRASERVERPORT), this)).login(Servers.loginServer.INTRASERVERPASSWORD, true);
            }
            catch (IOException iox) {
                this.done = true;
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                this.done = true;
            }
            if (this.client.loggedIn) {
                try {
                    this.client.executeRequestPlayerPaymentExpire(this.wurmId);
                }
                catch (IOException iox) {
                    GetPlayerExpireTime.logger.log(Level.WARNING, iox.getMessage(), iox);
                    this.done = true;
                }
            }
            if (!this.done) {
                try {
                    this.client.update();
                }
                catch (Exception ex) {
                    this.done = true;
                }
            }
        }
        if (this.done && this.client != null) {
            this.client.disconnect("Done");
            this.client = null;
        }
        return this.done;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        final long expireTime = buffer.getLong();
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(this.wurmId);
        final long oldExpire = info.getPaymentExpire();
        try {
            if (expireTime > oldExpire) {
                info.setPaymentExpire(expireTime);
                try {
                    final Player p = Players.getInstance().getPlayer(this.wurmId);
                    p.getCommunicator().sendNormalServerMessage("Your payment expiration date is updated to " + WurmCalendar.formatGmt(expireTime));
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
        catch (IOException iox) {
            GetPlayerExpireTime.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        this.done = true;
    }
    
    static {
        logger = Logger.getLogger(GetPlayerExpireTime.class.getName());
    }
}
