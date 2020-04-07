// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import java.util.Arrays;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.util.ArrayList;

public class VersionID implements Comparable
{
    private String[] _tuple;
    private boolean _usePrefixMatch;
    private boolean _useGreaterThan;
    private boolean _isCompound;
    private VersionID _rest;
    
    public VersionID(String s) {
        this._usePrefixMatch = false;
        this._useGreaterThan = false;
        this._isCompound = false;
        if (s == null && s.length() == 0) {
            this._tuple = new String[0];
            return;
        }
        final int index = s.indexOf("&");
        if (index >= 0) {
            this._isCompound = true;
            final VersionID versionID = new VersionID(s.substring(0, index));
            this._rest = new VersionID(s.substring(index + 1));
            this._tuple = versionID._tuple;
            this._usePrefixMatch = versionID._usePrefixMatch;
            this._useGreaterThan = versionID._useGreaterThan;
        }
        else {
            if (s.endsWith("+")) {
                this._useGreaterThan = true;
                s = s.substring(0, s.length() - 1);
            }
            else if (s.endsWith("*")) {
                this._usePrefixMatch = true;
                s = s.substring(0, s.length() - 1);
            }
            final ArrayList list = new ArrayList<String>();
            int n = 0;
            for (int i = 0; i < s.length(); ++i) {
                if (".-_".indexOf(s.charAt(i)) != -1) {
                    if (n < i) {
                        list.add(s.substring(n, i));
                    }
                    n = i + 1;
                }
            }
            if (n < s.length()) {
                list.add(s.substring(n, s.length()));
            }
            this._tuple = new String[list.size()];
            this._tuple = list.toArray(this._tuple);
        }
        Trace.println("Created version ID: " + this, TraceLevel.NETWORK);
    }
    
    public boolean isSimpleVersion() {
        return !this._useGreaterThan && !this._usePrefixMatch && !this._isCompound;
    }
    
    public boolean match(final VersionID versionID) {
        return (!this._isCompound || this._rest.match(versionID)) && (this._usePrefixMatch ? this.isPrefixMatchTuple(versionID) : (this._useGreaterThan ? versionID.isGreaterThanOrEqualTuple(this) : this.matchTuple(versionID)));
    }
    
    public boolean equals(final Object o) {
        if (this.matchTuple(o)) {
            final VersionID versionID = (VersionID)o;
            if ((this._rest == null || this._rest.equals(versionID._rest)) && this._useGreaterThan == versionID._useGreaterThan && this._usePrefixMatch == versionID._usePrefixMatch) {
                return true;
            }
        }
        return false;
    }
    
    private boolean matchTuple(final Object o) {
        if (o == null || !(o instanceof VersionID)) {
            return false;
        }
        final VersionID versionID = (VersionID)o;
        final String[] normalize = this.normalize(this._tuple, versionID._tuple.length);
        final String[] normalize2 = this.normalize(versionID._tuple, this._tuple.length);
        for (int i = 0; i < normalize.length; ++i) {
            if (!this.getValueAsObject(normalize[i]).equals(this.getValueAsObject(normalize2[i]))) {
                return false;
            }
        }
        return true;
    }
    
    private Object getValueAsObject(final String s) {
        if (s.length() > 0 && s.charAt(0) != '-') {
            try {
                return Integer.valueOf(s);
            }
            catch (NumberFormatException ex) {}
        }
        return s;
    }
    
    public boolean isGreaterThan(final VersionID versionID) {
        return this.isGreaterThanOrEqualHelper(versionID, false, true);
    }
    
    public boolean isGreaterThanOrEqual(final VersionID versionID) {
        return this.isGreaterThanOrEqualHelper(versionID, true, true);
    }
    
    private boolean isGreaterThanOrEqualTuple(final VersionID versionID) {
        return this.isGreaterThanOrEqualHelper(versionID, true, false);
    }
    
    private boolean isGreaterThanOrEqualHelper(final VersionID versionID, final boolean b, final boolean b2) {
        if (b2 && this._isCompound && !this._rest.isGreaterThanOrEqualHelper(versionID, b, true)) {
            return false;
        }
        final String[] normalize = this.normalize(this._tuple, versionID._tuple.length);
        final String[] normalize2 = this.normalize(versionID._tuple, this._tuple.length);
        int i = 0;
        while (i < normalize.length) {
            final Object valueAsObject = this.getValueAsObject(normalize[i]);
            final Object valueAsObject2 = this.getValueAsObject(normalize2[i]);
            if (valueAsObject.equals(valueAsObject2)) {
                ++i;
            }
            else {
                if (valueAsObject instanceof Integer && valueAsObject2 instanceof Integer) {
                    return (int)valueAsObject > (int)valueAsObject2;
                }
                return normalize[i].toString().compareTo(normalize2[i].toString()) > 0;
            }
        }
        return b;
    }
    
    private boolean isPrefixMatchTuple(final VersionID versionID) {
        final String[] normalize = this.normalize(versionID._tuple, this._tuple.length);
        for (int i = 0; i < this._tuple.length; ++i) {
            if (!this._tuple[i].equals(normalize[i])) {
                return false;
            }
        }
        return true;
    }
    
    private String[] normalize(final String[] array, final int n) {
        if (array.length < n) {
            final String[] array2 = new String[n];
            System.arraycopy(array, 0, array2, 0, array.length);
            Arrays.fill(array2, array.length, array2.length, "0");
            return array2;
        }
        return array;
    }
    
    public int compareTo(final Object o) {
        if (o == null || !(o instanceof VersionID)) {
            return -1;
        }
        final VersionID versionID = (VersionID)o;
        return this.equals(versionID) ? 0 : (this.isGreaterThanOrEqual(versionID) ? 1 : -1);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this._tuple.length - 1; ++i) {
            sb.append(this._tuple[i]);
            sb.append('.');
        }
        if (this._tuple.length > 0) {
            sb.append(this._tuple[this._tuple.length - 1]);
        }
        if (this._useGreaterThan) {
            sb.append('+');
        }
        if (this._usePrefixMatch) {
            sb.append('*');
        }
        if (this._isCompound) {
            sb.append("&");
            sb.append(this._rest);
        }
        return sb.toString();
    }
}
