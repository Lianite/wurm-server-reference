// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.net.URL;
import java.util.List;

public class CallbackHeader extends UpnpHeader<List<URL>>
{
    private static final Logger log;
    
    public CallbackHeader() {
        ((UpnpHeader<ArrayList<URL>>)this).setValue(new ArrayList<URL>());
    }
    
    public CallbackHeader(final List<URL> urls) {
        this();
        ((UpnpHeader<List>)this).getValue().addAll(urls);
    }
    
    public CallbackHeader(final URL url) {
        this();
        this.getValue().add(url);
    }
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() == 0) {
            return;
        }
        if (!s.contains("<") || !s.contains(">")) {
            throw new InvalidHeaderException("URLs not in brackets: " + s);
        }
        s = s.replaceAll("<", "");
        final String[] split = s.split(">");
        try {
            final List<URL> urls = new ArrayList<URL>();
            for (String sp : split) {
                sp = sp.trim();
                Label_0213: {
                    if (!sp.startsWith("http://")) {
                        CallbackHeader.log.warning("Discarding non-http callback URL: " + sp);
                    }
                    else {
                        final URL url = new URL(sp);
                        try {
                            url.toURI();
                        }
                        catch (URISyntaxException ex) {
                            CallbackHeader.log.log(Level.WARNING, "Discarding callback URL, not a valid URI on this platform: " + url, ex);
                            break Label_0213;
                        }
                        urls.add(url);
                    }
                }
            }
            this.setValue(urls);
        }
        catch (MalformedURLException ex2) {
            throw new InvalidHeaderException("Can't parse callback URLs from '" + s + "': " + ex2);
        }
    }
    
    @Override
    public String getString() {
        final StringBuilder s = new StringBuilder();
        for (final URL url : this.getValue()) {
            s.append("<").append(url.toString()).append(">");
        }
        return s.toString();
    }
    
    static {
        log = Logger.getLogger(CallbackHeader.class.getName());
    }
}
