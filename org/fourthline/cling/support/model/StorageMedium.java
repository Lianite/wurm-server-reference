// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.util.HashMap;
import org.fourthline.cling.model.ModelUtil;
import java.util.Map;

public enum StorageMedium
{
    UNKNOWN, 
    DV, 
    MINI_DV("MINI-DV"), 
    VHS, 
    W_VHS("W-VHS"), 
    S_VHS("S-VHS"), 
    D_VHS("D-VHS"), 
    VHSC, 
    VIDEO8, 
    HI8, 
    CD_ROM("CD-ROM"), 
    CD_DA("CD-DA"), 
    CD_R("CD-R"), 
    CD_RW("CD-RW"), 
    VIDEO_CD("VIDEO-CD"), 
    SACD, 
    MD_AUDIO("M-AUDIO"), 
    MD_PICTURE("MD-PICTURE"), 
    DVD_ROM("DVD-ROM"), 
    DVD_VIDEO("DVD-VIDEO"), 
    DVD_R("DVD-R"), 
    DVD_PLUS_RW("DVD+RW"), 
    DVD_MINUS_RW("DVD-RW"), 
    DVD_RAM("DVD-RAM"), 
    DVD_AUDIO("DVD-AUDIO"), 
    DAT, 
    LD, 
    HDD, 
    MICRO_MV("MICRO_MV"), 
    NETWORK, 
    NONE, 
    NOT_IMPLEMENTED, 
    VENDOR_SPECIFIC;
    
    private static Map<String, StorageMedium> byProtocolString;
    private String protocolString;
    
    private StorageMedium() {
        this(null);
    }
    
    private StorageMedium(final String protocolString) {
        this.protocolString = ((protocolString == null) ? this.name() : protocolString);
    }
    
    @Override
    public String toString() {
        return this.protocolString;
    }
    
    public static StorageMedium valueOrExceptionOf(final String s) {
        final StorageMedium sm = StorageMedium.byProtocolString.get(s);
        if (sm != null) {
            return sm;
        }
        throw new IllegalArgumentException("Invalid storage medium string: " + s);
    }
    
    public static StorageMedium valueOrVendorSpecificOf(final String s) {
        final StorageMedium sm = StorageMedium.byProtocolString.get(s);
        return (sm != null) ? sm : StorageMedium.VENDOR_SPECIFIC;
    }
    
    public static StorageMedium[] valueOfCommaSeparatedList(final String s) {
        final String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) {
            return new StorageMedium[0];
        }
        final StorageMedium[] result = new StorageMedium[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            result[i] = valueOrVendorSpecificOf(strings[i]);
        }
        return result;
    }
    
    static {
        StorageMedium.byProtocolString = new HashMap<String, StorageMedium>() {
            {
                for (final StorageMedium e : StorageMedium.values()) {
                    this.put(e.protocolString, e);
                }
            }
        };
    }
}
