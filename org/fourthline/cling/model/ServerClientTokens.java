// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

public class ServerClientTokens
{
    public static final String UNKNOWN_PLACEHOLDER = "UNKNOWN";
    private int majorVersion;
    private int minorVersion;
    private String osName;
    private String osVersion;
    private String productName;
    private String productVersion;
    
    public ServerClientTokens() {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = "Cling";
        this.productVersion = "2.0";
    }
    
    public ServerClientTokens(final int majorVersion, final int minorVersion) {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = "Cling";
        this.productVersion = "2.0";
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }
    
    public ServerClientTokens(final String productName, final String productVersion) {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = "Cling";
        this.productVersion = "2.0";
        this.productName = productName;
        this.productVersion = productVersion;
    }
    
    public ServerClientTokens(final int majorVersion, final int minorVersion, final String osName, final String osVersion, final String productName, final String productVersion) {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = "Cling";
        this.productVersion = "2.0";
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.osName = osName;
        this.osVersion = osVersion;
        this.productName = productName;
        this.productVersion = productVersion;
    }
    
    public int getMajorVersion() {
        return this.majorVersion;
    }
    
    public void setMajorVersion(final int majorVersion) {
        this.majorVersion = majorVersion;
    }
    
    public int getMinorVersion() {
        return this.minorVersion;
    }
    
    public void setMinorVersion(final int minorVersion) {
        this.minorVersion = minorVersion;
    }
    
    public String getOsName() {
        return this.osName;
    }
    
    public void setOsName(final String osName) {
        this.osName = osName;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public void setProductName(final String productName) {
        this.productName = productName;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
    
    public void setProductVersion(final String productVersion) {
        this.productVersion = productVersion;
    }
    
    @Override
    public String toString() {
        return this.getOsName() + "/" + this.getOsVersion() + " UPnP/" + this.getMajorVersion() + "." + this.getMinorVersion() + " " + this.getProductName() + "/" + this.getProductVersion();
    }
    
    public String getHttpToken() {
        final StringBuilder sb = new StringBuilder(256);
        sb.append((this.osName.indexOf(32) != -1) ? this.osName.replace(' ', '_') : this.osName);
        sb.append('/');
        sb.append((this.osVersion.indexOf(32) != -1) ? this.osVersion.replace(' ', '_') : this.osVersion);
        sb.append(" UPnP/");
        sb.append(this.majorVersion);
        sb.append('.');
        sb.append(this.minorVersion);
        sb.append(' ');
        sb.append((this.productName.indexOf(32) != -1) ? this.productName.replace(' ', '_') : this.productName);
        sb.append('/');
        sb.append((this.productVersion.indexOf(32) != -1) ? this.productVersion.replace(' ', '_') : this.productVersion);
        return sb.toString();
    }
    
    public String getOsToken() {
        return this.getOsName().replaceAll(" ", "_") + "/" + this.getOsVersion().replaceAll(" ", "_");
    }
    
    public String getProductToken() {
        return this.getProductName().replaceAll(" ", "_") + "/" + this.getProductVersion().replaceAll(" ", "_");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ServerClientTokens that = (ServerClientTokens)o;
        return this.majorVersion == that.majorVersion && this.minorVersion == that.minorVersion && this.osName.equals(that.osName) && this.osVersion.equals(that.osVersion) && this.productName.equals(that.productName) && this.productVersion.equals(that.productVersion);
    }
    
    @Override
    public int hashCode() {
        int result = this.majorVersion;
        result = 31 * result + this.minorVersion;
        result = 31 * result + this.osName.hashCode();
        result = 31 * result + this.osVersion.hashCode();
        result = 31 * result + this.productName.hashCode();
        result = 31 * result + this.productVersion.hashCode();
        return result;
    }
}
