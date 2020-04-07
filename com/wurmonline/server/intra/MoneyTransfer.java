// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.util.HashMap;
import java.nio.ByteBuffer;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class MoneyTransfer extends IntraCommand implements MiscConstants
{
    private static final Logger logger;
    private static final Logger moneylogger;
    private final String name;
    private final long wurmid;
    private final long newMoney;
    private final long moneyAdded;
    private final String detail;
    private boolean done;
    private IntraClient client;
    private boolean started;
    public boolean deleted;
    private boolean sentTransfer;
    public static final byte EXECUTOR_NONE = 0;
    public static final byte EXECUTOR_BLVDMEDIA = 1;
    public static final byte EXECUTOR_SUPERREWARDS = 2;
    public static final byte EXECUTOR_INGAME_BONUS = 3;
    public static final byte EXECUTOR_PAYPAL = 4;
    public static final byte EXECUTOR_INGAME_REFER = 5;
    public static final byte EXECUTOR_INGAME_SHOP = 6;
    public static final byte EXECUTOR_ALLOPASS = 7;
    public static final byte EXECUTOR_COINLAB = 8;
    public static final byte EXECUTOR_XSOLLA = 9;
    private final byte paymentExecutor;
    private final String campaignId;
    public static final ConcurrentLinkedDeque<MoneyTransfer> transfers;
    private static final Map<Long, Set<MoneyTransfer>> batchTransfers;
    
    public MoneyTransfer(final String aName, final long playerId, final long money, final long _moneyAdded, final String transactionDetail, final byte executor, final String campid, final boolean load) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.newMoney = money;
        this.detail = transactionDetail.substring(0, Math.min(39, transactionDetail.length()));
        this.paymentExecutor = executor;
        this.campaignId = campid;
        this.moneyAdded = _moneyAdded;
        if (!load) {
            this.save();
        }
        MoneyTransfer.transfers.add(this);
    }
    
    public MoneyTransfer(final long playerId, final String aName, final long money, final long _moneyAdded, final String transactionDetail, final byte executor, final String campid) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.newMoney = money;
        this.detail = transactionDetail.substring(0, Math.min(39, transactionDetail.length()));
        this.paymentExecutor = executor;
        this.campaignId = campid;
        this.moneyAdded = _moneyAdded;
    }
    
    public MoneyTransfer(final String aName, final long playerId, final long money, final long _moneyAdded, final String transactionDetail, final byte executor, final String campId) {
        this.done = false;
        this.client = null;
        this.started = false;
        this.deleted = false;
        this.sentTransfer = false;
        this.name = aName;
        this.wurmid = playerId;
        this.newMoney = money;
        this.moneyAdded = _moneyAdded;
        this.detail = transactionDetail.substring(0, Math.min(39, transactionDetail.length()));
        this.campaignId = campId;
        this.paymentExecutor = executor;
        this.saveProcessed();
    }
    
    private void saveProcessed() {
        this.deleted = true;
    }
    
    private void save() {
    }
    
    public void process() {
        this.deleted = true;
    }
    
    public static final Set<MoneyTransfer> getMoneyTransfersFor(final long id) {
        return MoneyTransfer.batchTransfers.get(id);
    }
    
    @Override
    public boolean poll() {
        ++this.pollTimes;
        final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(this.name);
        try {
            info.load();
        }
        catch (Exception eex) {
            MoneyTransfer.logger.log(Level.WARNING, "Failed to load player info for wurmid " + this.wurmid + ".", eex);
        }
        if (info.wurmId <= 0L) {
            MoneyTransfer.logger.log(Level.WARNING, "Failed to load player info for wurmid " + this.wurmid + ". No info available.");
            this.done = true;
        }
        else if (info.currentServer == Servers.localServer.id) {
            MoneyTransfer.logger2.log(Level.INFO, "MT Processing " + MoneyTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", money: " + this.newMoney);
            this.process();
            this.done = true;
        }
        else if (this.client == null && (System.currentTimeMillis() > this.timeOutAt || !this.started)) {
            final ServerEntry entry = Servers.getServerWithId(info.currentServer);
            if (entry != null && entry.isAvailable(5, true)) {
                try {
                    this.startTime = System.currentTimeMillis();
                    this.timeOutAt = this.startTime + this.timeOutTime;
                    this.started = true;
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
                    MoneyTransfer.logger.log(Level.WARNING, "No available server entry for server with id " + info.currentServer);
                }
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                MoneyTransfer.logger2.log(Level.INFO, "MT timeout " + MoneyTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", money: " + this.newMoney);
                this.done = true;
            }
            if (this.client.loggedIn && !this.done && !this.sentTransfer) {
                try {
                    this.client.executeMoneyUpdate(this.wurmid, this.newMoney, this.moneyAdded, this.detail);
                    this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                    this.sentTransfer = true;
                }
                catch (IOException iox) {
                    MoneyTransfer.logger2.log(Level.WARNING, "Problem calling IntraClient.executeMoneyUpdate() for " + this + " due to " + iox.getMessage(), iox);
                    this.done = true;
                }
            }
            if (!this.done) {
                try {
                    this.client.update();
                }
                catch (Exception ex) {
                    this.done = true;
                    if (MoneyTransfer.logger.isLoggable(Level.FINE)) {
                        MoneyTransfer.logger.log(Level.FINE, "Problem calling IntraClient.update() but hopefully not serious", ex);
                    }
                }
            }
        }
        if (this.done && this.client != null) {
            this.sentTransfer = false;
            this.client.disconnect("Done");
            this.client = null;
            MoneyTransfer.logger2.log(Level.INFO, "MT Disconnected " + MoneyTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", money: " + this.newMoney);
        }
        return this.done;
    }
    
    public final long getMoneyAdded() {
        return this.moneyAdded;
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
        MoneyTransfer.logger2.log(Level.INFO, "MT accepted " + MoneyTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", money: " + this.newMoney);
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.done = true;
        MoneyTransfer.logger2.log(Level.INFO, "MT rejected " + MoneyTransfer.num + ", name: " + this.name + ", wurmid: " + this.wurmid + ", money: " + this.newMoney);
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
    
    @Override
    public String toString() {
        return "MoneyTransfer [num: " + MoneyTransfer.num + ", wurmid: " + this.wurmid + ", name: " + this.name + ", detail: " + this.detail + ", newMoney: " + this.newMoney + ", moneyAdded: " + this.moneyAdded + ']';
    }
    
    static {
        logger = Logger.getLogger(MoneyTransfer.class.getName());
        moneylogger = Logger.getLogger("Money");
        transfers = new ConcurrentLinkedDeque<MoneyTransfer>();
        batchTransfers = new HashMap<Long, Set<MoneyTransfer>>();
    }
}
