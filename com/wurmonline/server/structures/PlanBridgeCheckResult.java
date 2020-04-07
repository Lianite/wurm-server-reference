// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

public class PlanBridgeCheckResult
{
    final boolean failed;
    String pMsg;
    String qMsg;
    
    public PlanBridgeCheckResult(final boolean fail, final String PMsg, final String QMsg) {
        this.pMsg = "";
        this.qMsg = "";
        this.failed = fail;
        this.pMsg = PMsg;
        this.qMsg = QMsg;
    }
    
    public PlanBridgeCheckResult(final boolean fail) {
        this.pMsg = "";
        this.qMsg = "";
        this.failed = fail;
    }
    
    public boolean failed() {
        return this.failed;
    }
    
    public String pMsg() {
        return this.pMsg;
    }
    
    public String qMsg() {
        return this.qMsg;
    }
}
