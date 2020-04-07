// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.config;

public class DefaultConfiguration implements IConfiguration
{
    @Override
    public int getTimeout() {
        return 3000;
    }
    
    @Override
    public String getTestUrl() {
        return "www.google.com";
    }
    
    @Override
    public int getTestPort() {
        return 80;
    }
}
