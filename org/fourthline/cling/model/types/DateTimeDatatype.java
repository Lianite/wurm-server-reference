// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class DateTimeDatatype extends AbstractDatatype<Calendar>
{
    protected String[] readFormats;
    protected String writeFormat;
    
    public DateTimeDatatype(final String[] readFormats, final String writeFormat) {
        this.readFormats = readFormats;
        this.writeFormat = writeFormat;
    }
    
    @Override
    public Calendar valueOf(final String s) throws InvalidValueException {
        if (s.equals("")) {
            return null;
        }
        final Date d = this.getDateValue(s, this.readFormats);
        if (d == null) {
            throw new InvalidValueException("Can't parse date/time from: " + s);
        }
        final Calendar c = Calendar.getInstance(this.getTimeZone());
        c.setTime(d);
        return c;
    }
    
    @Override
    public String getString(final Calendar value) throws InvalidValueException {
        if (value == null) {
            return "";
        }
        final SimpleDateFormat sdt = new SimpleDateFormat(this.writeFormat);
        sdt.setTimeZone(this.getTimeZone());
        return sdt.format(value.getTime());
    }
    
    protected String normalizeTimeZone(String value) {
        if (value.endsWith("Z")) {
            value = value.substring(0, value.length() - 1) + "+0000";
        }
        else if (value.length() > 7 && value.charAt(value.length() - 3) == ':' && (value.charAt(value.length() - 6) == '-' || value.charAt(value.length() - 6) == '+')) {
            value = value.substring(0, value.length() - 3) + value.substring(value.length() - 2);
        }
        return value;
    }
    
    protected Date getDateValue(String value, final String[] formats) {
        value = this.normalizeTimeZone(value);
        Date d = null;
        for (final String format : formats) {
            final SimpleDateFormat sdt = new SimpleDateFormat(format);
            sdt.setTimeZone(this.getTimeZone());
            try {
                d = sdt.parse(value);
            }
            catch (ParseException ex) {}
        }
        return d;
    }
    
    protected TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }
}
