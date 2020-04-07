// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.net.URL;
import java.io.IOException;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Component;

public class CacheUtilities
{
    private static CacheUtilities _instance;
    private Component _component;
    static /* synthetic */ Class class$com$sun$javaws$cache$CacheUtilities;
    
    public static CacheUtilities getSharedInstance() {
        if (CacheUtilities._instance == null) {
            Class class$;
            Class class$com$sun$javaws$cache$CacheUtilities;
            if (CacheUtilities.class$com$sun$javaws$cache$CacheUtilities == null) {
                class$com$sun$javaws$cache$CacheUtilities = (CacheUtilities.class$com$sun$javaws$cache$CacheUtilities = (class$ = class$("com.sun.javaws.cache.CacheUtilities")));
            }
            else {
                class$ = (class$com$sun$javaws$cache$CacheUtilities = CacheUtilities.class$com$sun$javaws$cache$CacheUtilities);
            }
            final Class clazz = class$com$sun$javaws$cache$CacheUtilities;
            synchronized (class$) {
                if (CacheUtilities._instance == null) {
                    CacheUtilities._instance = new CacheUtilities();
                }
            }
        }
        return CacheUtilities._instance;
    }
    
    public Image loadImage(final String s) throws IOException {
        final Image image = Toolkit.getDefaultToolkit().createImage(s);
        if (image != null) {
            final MediaTracker mediaTracker = new MediaTracker(this.getComponent());
            mediaTracker.addImage(image, 0);
            try {
                mediaTracker.waitForID(0, 5000L);
            }
            catch (InterruptedException ex) {
                throw new IOException("Failed to load");
            }
            return image;
        }
        return null;
    }
    
    public Image loadImage(final URL url) throws IOException {
        final Image image = Toolkit.getDefaultToolkit().createImage(url);
        if (image != null) {
            final MediaTracker mediaTracker = new MediaTracker(this.getComponent());
            mediaTracker.addImage(image, 0);
            try {
                mediaTracker.waitForID(0, 5000L);
            }
            catch (InterruptedException ex) {
                throw new IOException("Failed to load");
            }
            return image;
        }
        return null;
    }
    
    private Component getComponent() {
        if (this._component == null) {
            synchronized (this) {
                if (this._component == null) {
                    this._component = new Component() {};
                }
            }
        }
        return this._component;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        CacheUtilities._instance = null;
    }
}
