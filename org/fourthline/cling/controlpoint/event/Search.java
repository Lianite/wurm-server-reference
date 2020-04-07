// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint.event;

import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

public class Search
{
    protected UpnpHeader searchType;
    protected int mxSeconds;
    
    public Search() {
        this.searchType = new STAllHeader();
        this.mxSeconds = MXHeader.DEFAULT_VALUE;
    }
    
    public Search(final UpnpHeader searchType) {
        this.searchType = new STAllHeader();
        this.mxSeconds = MXHeader.DEFAULT_VALUE;
        this.searchType = searchType;
    }
    
    public Search(final UpnpHeader searchType, final int mxSeconds) {
        this.searchType = new STAllHeader();
        this.mxSeconds = MXHeader.DEFAULT_VALUE;
        this.searchType = searchType;
        this.mxSeconds = mxSeconds;
    }
    
    public Search(final int mxSeconds) {
        this.searchType = new STAllHeader();
        this.mxSeconds = MXHeader.DEFAULT_VALUE;
        this.mxSeconds = mxSeconds;
    }
    
    public UpnpHeader getSearchType() {
        return this.searchType;
    }
    
    public int getMxSeconds() {
        return this.mxSeconds;
    }
}
