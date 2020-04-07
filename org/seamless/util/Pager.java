// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.io.Serializable;

public class Pager implements Serializable
{
    private Long numOfRecords;
    private Integer page;
    private Long pageSize;
    
    public Pager() {
        this.numOfRecords = 0L;
        this.page = 1;
        this.pageSize = 15L;
    }
    
    public Pager(final Long numOfRecords) {
        this.numOfRecords = 0L;
        this.page = 1;
        this.pageSize = 15L;
        this.numOfRecords = numOfRecords;
    }
    
    public Pager(final Long numOfRecords, final Integer page) {
        this.numOfRecords = 0L;
        this.page = 1;
        this.pageSize = 15L;
        this.numOfRecords = numOfRecords;
        this.page = page;
    }
    
    public Pager(final Long numOfRecords, final Integer page, final Long pageSize) {
        this.numOfRecords = 0L;
        this.page = 1;
        this.pageSize = 15L;
        this.numOfRecords = numOfRecords;
        this.page = page;
        this.pageSize = pageSize;
    }
    
    public Long getNumOfRecords() {
        return this.numOfRecords;
    }
    
    public void setNumOfRecords(final Long numOfRecords) {
        this.numOfRecords = numOfRecords;
    }
    
    public Integer getPage() {
        return this.page;
    }
    
    public void setPage(final Integer page) {
        if (page != null) {
            this.page = page;
        }
    }
    
    public Long getPageSize() {
        return this.pageSize;
    }
    
    public void setPageSize(final Long pageSize) {
        if (pageSize != null) {
            this.pageSize = pageSize;
        }
    }
    
    public int getNextPage() {
        return this.page + 1;
    }
    
    public int getPreviousPage() {
        return this.page - 1;
    }
    
    public int getFirstPage() {
        return 1;
    }
    
    public long getIndexRangeBegin() {
        final long retval = (this.getPage() - 1) * this.getPageSize();
        return Math.max(Math.min(this.getNumOfRecords() - 1L, (retval >= 0L) ? retval : 0L), 0L);
    }
    
    public long getIndexRangeEnd() {
        final long firstIndex = this.getIndexRangeBegin();
        final long pageIndex = this.getPageSize() - 1L;
        final long lastIndex = this.getNumOfRecords() - 1L;
        return Math.min(firstIndex + pageIndex, lastIndex);
    }
    
    public long getLastPage() {
        long lastPage = this.numOfRecords / this.pageSize;
        if (this.numOfRecords % this.pageSize == 0L) {
            --lastPage;
        }
        return lastPage + 1L;
    }
    
    public boolean isPreviousPageAvailable() {
        return this.getIndexRangeBegin() + 1L > this.getPageSize();
    }
    
    public boolean isNextPageAvailable() {
        return this.numOfRecords - 1L > this.getIndexRangeEnd();
    }
    
    public boolean isSeveralPages() {
        return this.getNumOfRecords() != 0L && this.getNumOfRecords() > this.getPageSize();
    }
    
    public String toString() {
        return "Pager - Records: " + this.getNumOfRecords() + " Page size: " + this.getPageSize();
    }
}
