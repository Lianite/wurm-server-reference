// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PendingAward
{
    private static Logger logger;
    long wurmid;
    String name;
    int days;
    int months;
    private static final ConcurrentHashMap<String, PendingAward> pendingAwards;
    
    public PendingAward(final long _wurmid, final String _name, final int _days, final int _months) {
        this.wurmid = _wurmid;
        this.name = _name;
        this.days = _days;
        this.months = _months;
    }
    
    public final void award() {
        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(this.wurmid);
        if (pinf != null) {
            if (pinf.currentServer == Servers.localServer.id) {
                if (pinf.awards == null) {
                    pinf.awards = new Awards(this.wurmid, this.days, 0, 0, 0, 0, 0L, 0, 0, true);
                    for (int m = 0; m < this.months; ++m) {
                        pinf.awards.setMonthsPaidSinceReset(pinf.awards.getMonthsPaidSinceReset() + 1);
                        pinf.awards.setMonthsPaidInARow(pinf.awards.getMonthsPaidInARow() + 1);
                        AwardLadder.award(pinf, true);
                    }
                    pinf.awards.setLastTickedDay(System.currentTimeMillis());
                    pinf.awards.update();
                }
                else {
                    final int monthsMissed = this.months - pinf.awards.getMonthsPaidSinceReset();
                    if (monthsMissed > 0) {
                        for (int i = 0; i < monthsMissed; ++i) {
                            pinf.awards.setMonthsPaidSinceReset(pinf.awards.getMonthsPaidSinceReset() + 1);
                            pinf.awards.setMonthsPaidInARow(pinf.awards.getMonthsPaidInARow() + 1);
                            AwardLadder.award(pinf, true);
                        }
                        pinf.awards.setLastTickedDay(System.currentTimeMillis());
                        pinf.awards.update();
                    }
                }
            }
            else {
                PendingAward.logger.log(Level.INFO, this.wurmid + " " + this.name + " is on server " + pinf.currentServer + " and not here when being awarded " + this.months + " months, " + this.days + " days.");
            }
        }
        else {
            PendingAward.logger.log(Level.INFO, this.wurmid + " " + this.name + " no PlayerInfo when being awarded " + this.months + " months, " + this.days + " days.");
        }
    }
    
    static {
        PendingAward.logger = Logger.getLogger(PendingAward.class.getName());
        pendingAwards = new ConcurrentHashMap<String, PendingAward>();
    }
}
