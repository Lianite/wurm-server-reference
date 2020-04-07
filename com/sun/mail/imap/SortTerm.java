// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.imap;

public final class SortTerm
{
    public static final SortTerm ARRIVAL;
    public static final SortTerm CC;
    public static final SortTerm DATE;
    public static final SortTerm FROM;
    public static final SortTerm REVERSE;
    public static final SortTerm SIZE;
    public static final SortTerm SUBJECT;
    public static final SortTerm TO;
    private String term;
    
    private SortTerm(final String term) {
        this.term = term;
    }
    
    public String toString() {
        return this.term;
    }
    
    static {
        ARRIVAL = new SortTerm("ARRIVAL");
        CC = new SortTerm("CC");
        DATE = new SortTerm("DATE");
        FROM = new SortTerm("FROM");
        REVERSE = new SortTerm("REVERSE");
        SIZE = new SortTerm("SIZE");
        SUBJECT = new SortTerm("SUBJECT");
        TO = new SortTerm("TO");
    }
}
