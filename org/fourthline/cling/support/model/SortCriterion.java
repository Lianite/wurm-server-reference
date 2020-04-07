// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class SortCriterion
{
    protected final boolean ascending;
    protected final String propertyName;
    
    public SortCriterion(final boolean ascending, final String propertyName) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }
    
    public SortCriterion(final String criterion) {
        this(criterion.startsWith("+"), criterion.substring(1));
        if (!criterion.startsWith("-") && !criterion.startsWith("+")) {
            throw new IllegalArgumentException("Missing sort prefix +/- on criterion: " + criterion);
        }
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public static SortCriterion[] valueOf(final String s) {
        if (s == null || s.length() == 0) {
            return new SortCriterion[0];
        }
        final List<SortCriterion> list = new ArrayList<SortCriterion>();
        final String[] split;
        final String[] criteria = split = s.split(",");
        for (final String criterion : split) {
            list.add(new SortCriterion(criterion.trim()));
        }
        return list.toArray(new SortCriterion[list.size()]);
    }
    
    public static String toString(final SortCriterion[] criteria) {
        if (criteria == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final SortCriterion sortCriterion : criteria) {
            sb.append(sortCriterion.toString()).append(",");
        }
        if (sb.toString().endsWith(",")) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.ascending ? "+" : "-");
        sb.append(this.propertyName);
        return sb.toString();
    }
}
