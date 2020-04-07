// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

import java.util.Vector;
import java.util.Hashtable;

public class CatalogEntry
{
    protected static int nextEntry;
    protected static Hashtable entryTypes;
    protected static Vector entryArgs;
    protected int entryType;
    protected Vector args;
    
    public static int addEntryType(final String s, final int n) {
        CatalogEntry.entryTypes.put(s, new Integer(CatalogEntry.nextEntry));
        CatalogEntry.entryArgs.add(CatalogEntry.nextEntry, new Integer(n));
        ++CatalogEntry.nextEntry;
        return CatalogEntry.nextEntry - 1;
    }
    
    public static int getEntryType(final String s) throws CatalogException {
        if (!CatalogEntry.entryTypes.containsKey(s)) {
            throw new CatalogException(3);
        }
        final Integer n = CatalogEntry.entryTypes.get(s);
        if (n == null) {
            throw new CatalogException(3);
        }
        return n;
    }
    
    public static int getEntryArgCount(final String s) throws CatalogException {
        return getEntryArgCount(getEntryType(s));
    }
    
    public static int getEntryArgCount(final int n) throws CatalogException {
        try {
            return CatalogEntry.entryArgs.get(n);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new CatalogException(3);
        }
    }
    
    public CatalogEntry() {
        this.entryType = 0;
        this.args = null;
    }
    
    public CatalogEntry(final String s, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        final Integer n = CatalogEntry.entryTypes.get(s);
        if (n == null) {
            throw new CatalogException(3);
        }
        final int intValue = n;
        try {
            if ((int)CatalogEntry.entryArgs.get(intValue) != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new CatalogException(3);
        }
        this.entryType = intValue;
        this.args = args;
    }
    
    public CatalogEntry(final int entryType, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        try {
            if (CatalogEntry.entryArgs.get(entryType) != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new CatalogException(3);
        }
        this.entryType = entryType;
        this.args = args;
    }
    
    public int getEntryType() {
        return this.entryType;
    }
    
    public String getEntryArg(final int n) {
        try {
            return this.args.get(n);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
    
    public void setEntryArg(final int n, final String s) throws ArrayIndexOutOfBoundsException {
        this.args.set(n, s);
    }
    
    static {
        CatalogEntry.nextEntry = 0;
        CatalogEntry.entryTypes = new Hashtable();
        CatalogEntry.entryArgs = new Vector();
    }
}
