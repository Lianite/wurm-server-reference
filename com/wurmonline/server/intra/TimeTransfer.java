// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.util.HashMap;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class TimeTransfer extends IntraCommand implements MiscConstants
{
    private static final Logger logger;
    private static final Logger moneylogger;
    private final String name;
    private final long wurmid;
    private final int monthsadded;
    private final int daysadded;
    private final boolean dealItems;
    private final String detail;
    public static final List<TimeTransfer> transfers;
    private boolean done;
    private IntraClient client;
    private boolean started;
    public boolean deleted;
    private boolean sentTransfer;
    private static final Map<Long, Set<TimeTransfer>> batchTransfers;
    
    public TimeTransfer(final String aName, final long playerId, final int months, final boolean _dealItems, final int days, final String transactionDetail, final boolean load) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.monthsadded = months;
        this.daysadded = days;
        this.dealItems = _dealItems;
        this.detail = transactionDetail.substring(0, Math.min(19, transactionDetail.length()));
        if (!load) {
            this.save();
        }
        TimeTransfer.transfers.add(this);
    }
    
    public TimeTransfer(final String aName, final long playerId, final int months, final boolean _dealItems, final int days, final String transactionDetail) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.monthsadded = months;
        this.daysadded = days;
        this.dealItems = _dealItems;
        this.detail = transactionDetail.substring(0, Math.min(19, transactionDetail.length()));
        this.saveProcessed();
    }
    
    public TimeTransfer(final long playerId, final String aName, final int months, final boolean _dealItems, final int days, final String transactionDetail) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.monthsadded = months;
        this.daysadded = days;
        this.dealItems = _dealItems;
        this.detail = transactionDetail.substring(0, Math.min(19, transactionDetail.length()));
    }
    
    private void saveProcessed() {
    }
    
    private void save() {
    }
    
    private void process() {
        this.deleted = true;
    }
    
    public static final Set<TimeTransfer> getTimeTransfersFor(final long id) {
        return TimeTransfer.batchTransfers.get(id);
    }
    
    @Override
    public boolean poll() {
        ++this.pollTimes;
        final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(this.name);
        try {
            info.load();
        }
        catch (Exception eex) {
            TimeTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ".", eex);
        }
        if (info.wurmId <= 0L) {
            TimeTransfer.logger.log(Level.WARNING, "Failed to load info for wurmid " + this.wurmid + ". No info available.");
            this.done = true;
        }
        else if (info.currentServer == Servers.localServer.id) {
            this.process();
            this.done = true;
        }
        else if (this.client == null && (System.currentTimeMillis() > this.timeOutAt || !this.started)) {
            final ServerEntry entry = Servers.getServerWithId(info.currentServer);
            if (entry != null && entry.isAvailable(5, true)) {
                try {
                    this.started = true;
                    this.startTime = System.currentTimeMillis();
                    this.timeOutAt = this.startTime + this.timeOutTime;
                    (this.client = new IntraClient(entry.INTRASERVERADDRESS, Integer.parseInt(entry.INTRASERVERPORT), this)).login(entry.INTRASERVERPASSWORD, true);
                    this.done = false;
                }
                catch (IOException iox2) {
                    this.done = true;
                }
            }
            else {
                this.timeOutAt = this.startTime + this.timeOutTime;
                this.done = true;
                if (entry == null) {
                    TimeTransfer.logger.log(Level.WARNING, "No server entry for server with id " + info.currentServer);
                }
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                this.done = true;
            }
            if (this.client.loggedIn && !this.done && !this.sentTransfer) {
                try {
                    this.timeOutAt = this.startTime + this.timeOutTime;
                    this.client.executeExpireUpdate(this.wurmid, info.getPaymentExpire(), this.detail, this.daysadded, this.monthsadded, this.dealItems);
                    this.sentTransfer = true;
                }
                catch (IOException iox) {
                    TimeTransfer.logger.log(Level.WARNING, this + ", " + iox.getMessage(), iox);
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
            this.sentTransfer = false;
            this.client.disconnect("Done");
            this.client = null;
        }
        return this.done;
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
    public void commandExecuted(final IntraClient aClient) {
        this.process();
        TimeTransfer.logger2.log(Level.INFO, "TT accepted " + TimeTransfer.num);
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.done = true;
        TimeTransfer.logger2.log(Level.INFO, "TT rejected " + TimeTransfer.num + " for " + this.wurmid + " " + this.name);
        this.deleted = true;
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        this.done = true;
    }
    
    public final int getDaysAdded() {
        return this.daysadded;
    }
    
    public final int getMonthsAdded() {
        return this.monthsadded;
    }
    
    @Override
    public String toString() {
        return "TimeTransfer [num: " + TimeTransfer.num + ", name: " + this.name + ", ID: " + this.wurmid + ", Months Added: " + this.monthsadded + ", Days Added: " + this.daysadded + ", detail: " + this.detail + ", done: " + this.done + ", started: " + this.started + ", deleted: " + this.deleted + ", sentTransfer: " + this.sentTransfer + ", IntraClient: " + this.client + ']';
    }
    
    static {
        logger = Logger.getLogger(TimeTransfer.class.getName());
        moneylogger = Logger.getLogger("Money");
        transfers = new LinkedList<TimeTransfer>();
        batchTransfers = new HashMap<Long, Set<TimeTransfer>>();
    }
}
