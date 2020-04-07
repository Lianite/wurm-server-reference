// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public class KingdomIp implements TimeConstants, MiscConstants
{
    private long lastLogout;
    private final String ipaddress;
    private byte currentKingdom;
    private final long timeBetweenKingdomSwitches = 600000L;
    private static final Map<String, KingdomIp> ips;
    private static int pruneCounter;
    private static final Logger logger;
    
    public KingdomIp(final String ipAddress, final byte activeKingdom) {
        this.ipaddress = ipAddress;
        this.currentKingdom = activeKingdom;
    }
    
    public final void logoff() {
        if (!Players.existsPlayerWithIp(this.ipaddress)) {
            this.lastLogout = System.currentTimeMillis();
        }
    }
    
    public static final KingdomIp[] getAllKips() {
        return KingdomIp.ips.values().toArray(new KingdomIp[KingdomIp.ips.size()]);
    }
    
    public final void logon(final byte newKingdom) {
        this.lastLogout = 0L;
        this.currentKingdom = newKingdom;
    }
    
    public final String getIpAddress() {
        return this.ipaddress;
    }
    
    public final byte getKingdom() {
        return this.currentKingdom;
    }
    
    public final void setKingdom(final byte newKingdom) {
        this.currentKingdom = newKingdom;
    }
    
    public static final KingdomIp getKIP(final String ipAddress) {
        if (ipAddress == null) {
            return null;
        }
        return KingdomIp.ips.get(ipAddress.replace("/", ""));
    }
    
    public final long getLastLogout() {
        return this.lastLogout;
    }
    
    public final long mayLogonKingdom(final byte kingdomChecked) {
        if (kingdomChecked == this.currentKingdom) {
            return 1L;
        }
        if (this.lastLogout == 0L) {
            return -1L;
        }
        if (System.currentTimeMillis() - this.lastLogout > 600000L) {
            return 1L;
        }
        return System.currentTimeMillis() - this.lastLogout;
    }
    
    public static final KingdomIp getKIP(final String ipAddress, final byte kingdom) {
        ++KingdomIp.pruneCounter;
        if (KingdomIp.pruneCounter == 300) {
            KingdomIp.pruneCounter = 0;
            for (final KingdomIp kp : KingdomIp.ips.values()) {
                if (kp.lastLogout > 0L) {
                    if (System.currentTimeMillis() - kp.lastLogout <= 3600000L) {
                        continue;
                    }
                    KingdomIp.logger.log(Level.INFO, "Pruning kip " + kp.getIpAddress());
                    KingdomIp.ips.remove(kp.getIpAddress());
                }
                else {
                    if (Players.existsPlayerWithIp(kp.getIpAddress())) {
                        continue;
                    }
                    KingdomIp.logger.log(Level.INFO, "Detected non existing address for logged on ip when pruning kip " + kp.getIpAddress());
                    KingdomIp.ips.remove(kp.getIpAddress());
                }
            }
        }
        KingdomIp kip = KingdomIp.ips.get(ipAddress.replace("/", ""));
        if (kip == null && kingdom != 0) {
            kip = new KingdomIp(ipAddress.replace("/", ""), kingdom);
            KingdomIp.ips.put(ipAddress.replace("/", ""), kip);
        }
        return kip;
    }
    
    static {
        ips = new ConcurrentHashMap<String, KingdomIp>();
        KingdomIp.pruneCounter = 0;
        logger = Logger.getLogger(KingdomIp.class.getName());
    }
}
