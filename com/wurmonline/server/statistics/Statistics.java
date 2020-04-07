// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.questions.Questions;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.TimerTask;

public final class Statistics extends TimerTask
{
    private static Logger log;
    private long totalBytesIn;
    private long totalBytesOut;
    private long currentBytesIn;
    private long currentBytesOut;
    private long playerCount;
    private static final long fivemin = 300000L;
    private static final long Onemeg = 1024000L;
    private static Statistics instance;
    private long creationTime;
    
    private Statistics() {
        this.totalBytesIn = 0L;
        this.totalBytesOut = 0L;
        this.currentBytesIn = 0L;
        this.currentBytesOut = 0L;
        this.playerCount = 0L;
        this.creationTime = System.currentTimeMillis();
    }
    
    public static Statistics getInstance() {
        if (Statistics.instance == null) {
            Statistics.instance = new Statistics();
        }
        return Statistics.instance;
    }
    
    public void startup(final Logger logger) {
        Statistics.log = logger;
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 300000L, 300000L);
    }
    
    @Override
    public void run() {
        final Runtime rt = Runtime.getRuntime();
        this.countBytes();
        Statistics.log.info("current mem in use: " + rt.totalMemory() / 1024000L + "M free mem: " + rt.freeMemory() / 1024000L + "M Max mem: " + rt.maxMemory() / 1024000L + "M\nplayer count: " + this.playerCount + "\nbytes in: " + this.currentBytesIn + " bytes out: " + this.currentBytesOut + " total in: " + this.totalBytesIn + " total out: " + this.totalBytesOut + '\n' + "Server uptime: " + (System.currentTimeMillis() - this.creationTime) / 1000L + " seconds. Unanswered questions:" + Questions.getNumUnanswered());
    }
    
    private void countBytes() {
        long bytesIn = 0L;
        long bytesOut = 0L;
        final Player[] players = Players.getInstance().getPlayers();
        this.playerCount = players.length;
        for (int x = 0; x != players.length; ++x) {
            if (players[x].hasLink()) {
                bytesIn += players[x].getCommunicator().getConnection().getReadBytes();
                bytesOut += players[x].getCommunicator().getConnection().getSentBytes();
            }
        }
        this.currentBytesIn = bytesIn - this.totalBytesIn;
        this.currentBytesOut = bytesOut - this.totalBytesOut;
        if (this.currentBytesIn < 0L) {
            this.currentBytesIn = 0L;
        }
        if (this.currentBytesOut < 0L) {
            this.currentBytesOut = 0L;
        }
        this.totalBytesIn += this.currentBytesIn;
        this.totalBytesOut += this.currentBytesOut;
    }
    
    static {
        Statistics.log = null;
        Statistics.instance = null;
    }
}
