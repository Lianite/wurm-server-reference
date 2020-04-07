// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.server.Servers;
import java.util.logging.Logger;

public abstract class IntraCommand implements IntraServerConnectionListener
{
    long startTime;
    long timeOutAt;
    long timeOutTime;
    private static int nums;
    static int num;
    public int pollTimes;
    static final Logger logger2;
    
    IntraCommand() {
        this.timeOutTime = 10000L;
        this.pollTimes = 0;
        IntraCommand.num = IntraCommand.nums++;
        this.startTime = System.currentTimeMillis();
        this.timeOutAt = this.startTime + this.timeOutTime;
    }
    
    public abstract boolean poll();
    
    @Override
    public abstract void commandExecuted(final IntraClient p0);
    
    @Override
    public abstract void commandFailed(final IntraClient p0);
    
    @Override
    public abstract void dataReceived(final IntraClient p0);
    
    boolean isThisLoginServer() {
        return Servers.isThisLoginServer();
    }
    
    String getLoginServerIntraServerAddress() {
        return Servers.loginServer.INTRASERVERADDRESS;
    }
    
    String getLoginServerIntraServerPort() {
        return Servers.loginServer.INTRASERVERPORT;
    }
    
    String getLoginServerIntraServerPassword() {
        return Servers.loginServer.INTRASERVERPASSWORD;
    }
    
    static {
        IntraCommand.nums = 0;
        IntraCommand.num = 0;
        logger2 = Logger.getLogger("IntraServer");
    }
}
