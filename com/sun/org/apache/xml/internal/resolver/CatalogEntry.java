// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver;

import java.util.Vector;
import java.util.Hashtable;

public class CatalogEntry
{
    protected static int nextEntry;
    protected static Hashtable entryTypes;
    protected static Vector entryArgs;
    protected int entryType;
    protected Vector args;
    
    public static int addEntryType(final String name, final int numArgs) {
        CatalogEntry.entryTypes.put(name, new Integer(CatalogEntry.nextEntry));
        CatalogEntry.entryArgs.add(CatalogEntry.nextEntry, new Integer(numArgs));
        ++CatalogEntry.nextEntry;
        return CatalogEntry.nextEntry - 1;
    }
    
    public static int getEntryType(final String name) throws CatalogException {
        if (!CatalogEntry.entryTypes.containsKey(name)) {
            throw new CatalogException(3);
        }
        final Integer iType = CatalogEntry.entryTypes.get(name);
        if (iType == null) {
            throw new CatalogException(3);
        }
        return iType;
    }
    
    public static int getEntryArgCount(final String name) throws CatalogException {
        return getEntryArgCount(getEntryType(name));
    }
    
    public static int getEntryArgCount(final int type) throws CatalogException {
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            return iArgs;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
    }
    
    public CatalogEntry() {
        this.entryType = 0;
        this.args = null;
    }
    
    public CatalogEntry(final String name, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        final Integer iType = CatalogEntry.entryTypes.get(name);
        if (iType == null) {
            throw new CatalogException(3);
        }
        final int type = iType;
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            if (iArgs != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
        this.entryType = type;
        this.args = args;
    }
    
    public CatalogEntry(final int type, final Vector args) throws CatalogException {
        this.entryType = 0;
        this.args = null;
        try {
            final Integer iArgs = CatalogEntry.entryArgs.get(type);
            if (iArgs != args.size()) {
                throw new CatalogException(2);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new CatalogException(3);
        }
        this.entryType = type;
        this.args = args;
    }
    
    public int getEntryType() {
        return this.entryType;
    }
    
    public String getEntryArg(final int argNum) {
        try {
            final String arg = this.args.get(argNum);
            return arg;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public void setEntryArg(final int argNum, final String newspec) throws ArrayIndexOutOfBoundsException {
        this.args.set(argNum, newspec);
    }
    
    static {
        CatalogEntry.nextEntry = 0;
        CatalogEntry.entryTypes = new Hashtable();
        CatalogEntry.entryArgs = new Vector();
    }
}
