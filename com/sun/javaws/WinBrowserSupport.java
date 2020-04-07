// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.net.URL;
import com.sun.deploy.config.Config;

public class WinBrowserSupport extends BrowserSupport
{
    public String getNS6MailCapInfo() {
        return null;
    }
    
    public OperaSupport getOperaSupport() {
        return new WinOperaSupport(Config.getBooleanProperty("deployment.mime.types.use.default"));
    }
    
    public boolean isWebBrowserSupportedImpl() {
        return true;
    }
    
    public boolean showDocumentImpl(final URL url) {
        return url != null && this.showDocument(url.toString());
    }
    
    public String getDefaultHandler(final URL url) {
        return Config.getInstance().getBrowserPath();
    }
    
    public boolean showDocument(final String s) {
        return Config.getInstance().showDocument(s);
    }
}
