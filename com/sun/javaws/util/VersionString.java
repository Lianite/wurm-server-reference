// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import java.util.StringTokenizer;
import java.util.ArrayList;

public class VersionString
{
    private ArrayList _versionIds;
    
    public VersionString(final String s) {
        this._versionIds = new ArrayList();
        if (s != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " ", false);
            while (stringTokenizer.hasMoreElements()) {
                this._versionIds.add(new VersionID(stringTokenizer.nextToken()));
            }
        }
    }
    
    public boolean isSimpleVersion() {
        return this._versionIds.size() == 1 && this._versionIds.get(0).isSimpleVersion();
    }
    
    public boolean contains(final VersionID versionID) {
        for (int i = 0; i < this._versionIds.size(); ++i) {
            if (((VersionID)this._versionIds.get(i)).match(versionID)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final String s) {
        return this.contains(new VersionID(s));
    }
    
    public boolean containsGreaterThan(final VersionID versionID) {
        for (int i = 0; i < this._versionIds.size(); ++i) {
            if (((VersionID)this._versionIds.get(i)).isGreaterThan(versionID)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsGreaterThan(final String s) {
        return this.containsGreaterThan(new VersionID(s));
    }
    
    public static boolean contains(final String s, final String s2) {
        return new VersionString(s).contains(s2);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this._versionIds.size(); ++i) {
            sb.append(this._versionIds.get(i).toString());
            sb.append(' ');
        }
        return sb.toString();
    }
}
