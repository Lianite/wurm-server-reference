// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.time;

import java.util.Date;
import java.io.Serializable;

public class DateRange implements Serializable
{
    protected Date start;
    protected Date end;
    
    public DateRange() {
    }
    
    public DateRange(final Date start) {
        this.start = start;
    }
    
    public DateRange(final Date start, final Date end) {
        this.start = start;
        this.end = end;
    }
    
    public DateRange(final String startUnixtime, final String endUnixtime) throws NumberFormatException {
        if (startUnixtime != null) {
            this.start = new Date(Long.valueOf(startUnixtime));
        }
        if (endUnixtime != null) {
            this.end = new Date(Long.valueOf(endUnixtime));
        }
    }
    
    public Date getStart() {
        return this.start;
    }
    
    public Date getEnd() {
        return this.end;
    }
    
    public boolean isStartAfter(final Date date) {
        return this.getStart() != null && this.getStart().getTime() > date.getTime();
    }
    
    public Date getOneDayBeforeStart() {
        if (this.getStart() == null) {
            throw new IllegalStateException("Can't get day before start date because start date is null");
        }
        return new Date(this.getStart().getTime() - 86400000L);
    }
    
    public static int getCurrentYear() {
        return new Date().getYear();
    }
    
    public static int getCurrentMonth() {
        return new Date().getMonth();
    }
    
    public static int getCurrentDayOfMonth() {
        return new Date().getDate();
    }
    
    public boolean hasStartOrEnd() {
        return this.getStart() != null || this.getEnd() != null;
    }
    
    public static int getDaysInMonth(final Date date) {
        final int month = date.getMonth();
        final int year = date.getYear() + 1900;
        final boolean isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
        final int[] daysInMonth = { 31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        return daysInMonth[month];
    }
    
    public static DateRange getMonthOf(final Date date) {
        return new DateRange(new Date(date.getYear(), date.getMonth(), 1), new Date(date.getYear(), date.getMonth(), getDaysInMonth(date)));
    }
    
    public boolean isInRange(final Date date) {
        return this.getStart() != null && this.getStart().getTime() < date.getTime() && (this.getEnd() == null || this.getEnd().getTime() > date.getTime());
    }
    
    public boolean isValid() {
        return this.getStart() != null && (this.getEnd() == null || this.getStart().getTime() <= this.getEnd().getTime());
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DateRange dateRange = (DateRange)o;
        Label_0062: {
            if (this.end != null) {
                if (this.end.equals(dateRange.end)) {
                    break Label_0062;
                }
            }
            else if (dateRange.end == null) {
                break Label_0062;
            }
            return false;
        }
        if (this.start != null) {
            if (this.start.equals(dateRange.start)) {
                return true;
            }
        }
        else if (dateRange.start == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.start != null) ? this.start.hashCode() : 0;
        result = 31 * result + ((this.end != null) ? this.end.hashCode() : 0);
        return result;
    }
    
    public static DateRange valueOf(final String s) {
        if (!s.contains("dr=")) {
            return null;
        }
        String dr = s.substring(s.indexOf("dr=") + 3);
        dr = dr.substring(0, dr.indexOf(";"));
        final String[] split = dr.split(",");
        if (split.length != 2) {
            return null;
        }
        try {
            return new DateRange(split[0].equals("0") ? null : new Date(Long.valueOf(split[0])), split[1].equals("0") ? null : new Date(Long.valueOf(split[1])));
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("dr=");
        sb.append((this.getStart() != null) ? this.getStart().getTime() : "0");
        sb.append(",");
        sb.append((this.getEnd() != null) ? this.getEnd().getTime() : "0");
        sb.append(";");
        return sb.toString();
    }
    
    public enum Preset
    {
        ALL(new DateRange(null)), 
        YEAR_TO_DATE(new DateRange(new Date(DateRange.getCurrentYear(), 0, 1))), 
        MONTH_TO_DATE(new DateRange(new Date(DateRange.getCurrentYear(), DateRange.getCurrentMonth(), 1))), 
        LAST_MONTH(DateRange.getMonthOf(new Date(DateRange.getCurrentYear(), DateRange.getCurrentMonth() - 1, 1))), 
        LAST_YEAR(new DateRange(new Date(DateRange.getCurrentYear() - 1, 0, 1), new Date(DateRange.getCurrentYear() - 1, 11, 31)));
        
        DateRange dateRange;
        
        private Preset(final DateRange dateRange) {
            this.dateRange = dateRange;
        }
        
        public DateRange getDateRange() {
            return this.dateRange;
        }
    }
}
