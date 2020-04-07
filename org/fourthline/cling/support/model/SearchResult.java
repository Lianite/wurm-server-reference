// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SearchResult
{
    protected String result;
    protected UnsignedIntegerFourBytes count;
    protected UnsignedIntegerFourBytes totalMatches;
    protected UnsignedIntegerFourBytes containerUpdateID;
    
    public SearchResult(final String result, final UnsignedIntegerFourBytes count, final UnsignedIntegerFourBytes totalMatches, final UnsignedIntegerFourBytes containerUpdateID) {
        this.result = result;
        this.count = count;
        this.totalMatches = totalMatches;
        this.containerUpdateID = containerUpdateID;
    }
    
    public SearchResult(final String result, final long count, final long totalMatches) {
        this(result, count, totalMatches, 0L);
    }
    
    public SearchResult(final String result, final long count, final long totalMatches, final long updateID) {
        this(result, new UnsignedIntegerFourBytes(count), new UnsignedIntegerFourBytes(totalMatches), new UnsignedIntegerFourBytes(updateID));
    }
    
    public String getResult() {
        return this.result;
    }
    
    public UnsignedIntegerFourBytes getCount() {
        return this.count;
    }
    
    public long getCountLong() {
        return this.count.getValue();
    }
    
    public UnsignedIntegerFourBytes getTotalMatches() {
        return this.totalMatches;
    }
    
    public long getTotalMatchesLong() {
        return this.totalMatches.getValue();
    }
    
    public UnsignedIntegerFourBytes getContainerUpdateID() {
        return this.containerUpdateID;
    }
    
    public long getContainerUpdateIDLong() {
        return this.containerUpdateID.getValue();
    }
}
