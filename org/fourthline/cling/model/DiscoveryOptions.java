// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

public class DiscoveryOptions
{
    protected boolean advertised;
    protected boolean byeByeBeforeFirstAlive;
    private static String simpleName;
    
    public DiscoveryOptions(final boolean advertised) {
        this.advertised = advertised;
    }
    
    public DiscoveryOptions(final boolean advertised, final boolean byeByeBeforeFirstAlive) {
        this.advertised = advertised;
        this.byeByeBeforeFirstAlive = byeByeBeforeFirstAlive;
    }
    
    public boolean isAdvertised() {
        return this.advertised;
    }
    
    public boolean isByeByeBeforeFirstAlive() {
        return this.byeByeBeforeFirstAlive;
    }
    
    @Override
    public String toString() {
        return "(" + DiscoveryOptions.simpleName + ")" + " advertised: " + this.isAdvertised() + " byebyeBeforeFirstAlive: " + this.isByeByeBeforeFirstAlive();
    }
    
    static {
        DiscoveryOptions.simpleName = DiscoveryOptions.class.getSimpleName();
    }
}
