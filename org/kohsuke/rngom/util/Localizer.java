// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer
{
    private final Class cls;
    private ResourceBundle bundle;
    private final Localizer parent;
    
    public Localizer(final Class cls) {
        this(null, cls);
    }
    
    public Localizer(final Localizer parent, final Class cls) {
        this.parent = parent;
        this.cls = cls;
    }
    
    private String getString(final String key) {
        try {
            return this.getBundle().getString(key);
        }
        catch (MissingResourceException e) {
            if (this.parent != null) {
                return this.parent.getString(key);
            }
            throw e;
        }
    }
    
    public String message(final String key) {
        return MessageFormat.format(this.getString(key), new Object[0]);
    }
    
    public String message(final String key, final Object arg) {
        return MessageFormat.format(this.getString(key), arg);
    }
    
    public String message(final String key, final Object arg1, final Object arg2) {
        return MessageFormat.format(this.getString(key), arg1, arg2);
    }
    
    public String message(final String key, final Object[] args) {
        return MessageFormat.format(this.getString(key), args);
    }
    
    private ResourceBundle getBundle() {
        if (this.bundle == null) {
            String s = this.cls.getName();
            final int i = s.lastIndexOf(46);
            if (i > 0) {
                s = s.substring(0, i + 1);
            }
            else {
                s = "";
            }
            this.bundle = ResourceBundle.getBundle(s + "Messages");
        }
        return this.bundle;
    }
}
