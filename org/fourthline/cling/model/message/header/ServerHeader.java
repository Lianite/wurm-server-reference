// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.ServerClientTokens;

public class ServerHeader extends UpnpHeader<ServerClientTokens>
{
    public ServerHeader() {
        this.setValue(new ServerClientTokens());
    }
    
    public ServerHeader(final ServerClientTokens tokens) {
        this.setValue(tokens);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        final ServerClientTokens serverClientTokens = new ServerClientTokens();
        serverClientTokens.setOsName("UNKNOWN");
        serverClientTokens.setOsVersion("UNKNOWN");
        serverClientTokens.setProductName("UNKNOWN");
        serverClientTokens.setProductVersion("UNKNOWN");
        if (s.contains("UPnP/1.1")) {
            serverClientTokens.setMinorVersion(1);
        }
        else if (!s.contains("UPnP/1.")) {
            throw new InvalidHeaderException("Missing 'UPnP/1.' in server information: " + s);
        }
        try {
            int numberOfSpaces = 0;
            for (int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) == ' ') {
                    ++numberOfSpaces;
                }
            }
            String[] osNameVersion;
            String[] productNameVersion;
            if (s.contains(",")) {
                final String[] productTokens = s.split(",");
                osNameVersion = productTokens[0].split("/");
                productNameVersion = productTokens[2].split("/");
            }
            else if (numberOfSpaces > 2) {
                final String beforeUpnpToken = s.substring(0, s.indexOf("UPnP/1.")).trim();
                final String afterUpnpToken = s.substring(s.indexOf("UPnP/1.") + 8).trim();
                osNameVersion = beforeUpnpToken.split("/");
                productNameVersion = afterUpnpToken.split("/");
            }
            else {
                final String[] productTokens = s.split(" ");
                osNameVersion = productTokens[0].split("/");
                productNameVersion = productTokens[2].split("/");
            }
            serverClientTokens.setOsName(osNameVersion[0].trim());
            if (osNameVersion.length > 1) {
                serverClientTokens.setOsVersion(osNameVersion[1].trim());
            }
            serverClientTokens.setProductName(productNameVersion[0].trim());
            if (productNameVersion.length > 1) {
                serverClientTokens.setProductVersion(productNameVersion[1].trim());
            }
        }
        catch (Exception ex) {
            serverClientTokens.setOsName("UNKNOWN");
            serverClientTokens.setOsVersion("UNKNOWN");
            serverClientTokens.setProductName("UNKNOWN");
            serverClientTokens.setProductVersion("UNKNOWN");
        }
        this.setValue(serverClientTokens);
    }
    
    @Override
    public String getString() {
        return this.getValue().getHttpToken();
    }
}
