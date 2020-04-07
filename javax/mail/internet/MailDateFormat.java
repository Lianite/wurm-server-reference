// 
// Decompiled by Procyon v0.5.30
// 

package javax.mail.internet;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import com.sun.mail.util.MailLogger;
import java.text.SimpleDateFormat;

public class MailDateFormat extends SimpleDateFormat
{
    private static final long serialVersionUID = -8148227605210628779L;
    static boolean debug;
    private static MailLogger logger;
    private static final Calendar cal;
    static /* synthetic */ Class class$javax$mail$internet$MailDateFormat;
    
    public MailDateFormat() {
        super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
    }
    
    public StringBuffer format(final Date date, final StringBuffer dateStrBuf, final FieldPosition fieldPosition) {
        final int start = dateStrBuf.length();
        super.format(date, dateStrBuf, fieldPosition);
        int pos;
        for (pos = 0, pos = start + 25; dateStrBuf.charAt(pos) != 'X'; ++pos) {}
        this.calendar.clear();
        this.calendar.setTime(date);
        int offset = this.calendar.get(15) + this.calendar.get(16);
        if (offset < 0) {
            dateStrBuf.setCharAt(pos++, '-');
            offset = -offset;
        }
        else {
            dateStrBuf.setCharAt(pos++, '+');
        }
        final int rawOffsetInMins = offset / 60 / 1000;
        final int offsetInHrs = rawOffsetInMins / 60;
        final int offsetInMins = rawOffsetInMins % 60;
        dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs / 10, 10));
        dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs % 10, 10));
        dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins / 10, 10));
        dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins % 10, 10));
        return dateStrBuf;
    }
    
    public Date parse(final String text, final ParsePosition pos) {
        return parseDate(text.toCharArray(), pos, this.isLenient());
    }
    
    private static Date parseDate(final char[] orig, final ParsePosition pos, final boolean lenient) {
        try {
            int day = -1;
            int month = -1;
            int year = -1;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            int offset = 0;
            final MailDateParser p = new MailDateParser(orig, pos.getIndex());
            p.skipUntilNumber();
            day = p.parseNumber();
            if (!p.skipIfChar('-')) {
                p.skipWhiteSpace();
            }
            month = p.parseMonth();
            if (!p.skipIfChar('-')) {
                p.skipWhiteSpace();
            }
            year = p.parseNumber();
            if (year < 50) {
                year += 2000;
            }
            else if (year < 100) {
                year += 1900;
            }
            p.skipWhiteSpace();
            hours = p.parseNumber();
            p.skipChar(':');
            minutes = p.parseNumber();
            if (p.skipIfChar(':')) {
                seconds = p.parseNumber();
            }
            try {
                p.skipWhiteSpace();
                offset = p.parseTimeZone();
            }
            catch (ParseException pe) {
                if (MailDateFormat.logger.isLoggable(Level.FINE)) {
                    MailDateFormat.logger.log(Level.FINE, "No timezone? : '" + new String(orig) + "'", pe);
                }
            }
            pos.setIndex(p.getIndex());
            final Date d = ourUTC(year, month, day, hours, minutes, seconds, offset, lenient);
            return d;
        }
        catch (Exception e) {
            if (MailDateFormat.logger.isLoggable(Level.FINE)) {
                MailDateFormat.logger.log(Level.FINE, "Bad date: '" + new String(orig) + "'", e);
            }
            pos.setIndex(1);
            return null;
        }
    }
    
    private static synchronized Date ourUTC(final int year, final int mon, final int mday, final int hour, final int min, final int sec, final int tzoffset, final boolean lenient) {
        MailDateFormat.cal.clear();
        MailDateFormat.cal.setLenient(lenient);
        MailDateFormat.cal.set(1, year);
        MailDateFormat.cal.set(2, mon);
        MailDateFormat.cal.set(5, mday);
        MailDateFormat.cal.set(11, hour);
        MailDateFormat.cal.set(12, min);
        MailDateFormat.cal.add(12, tzoffset);
        MailDateFormat.cal.set(13, sec);
        return MailDateFormat.cal.getTime();
    }
    
    public void setCalendar(final Calendar newCalendar) {
        throw new RuntimeException("Method setCalendar() shouldn't be called");
    }
    
    public void setNumberFormat(final NumberFormat newNumberFormat) {
        throw new RuntimeException("Method setNumberFormat() shouldn't be called");
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        MailDateFormat.debug = false;
        MailDateFormat.logger = new MailLogger((MailDateFormat.class$javax$mail$internet$MailDateFormat == null) ? (MailDateFormat.class$javax$mail$internet$MailDateFormat = class$("javax.mail.internet.MailDateFormat")) : MailDateFormat.class$javax$mail$internet$MailDateFormat, "DEBUG", MailDateFormat.debug, System.out);
        cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    }
}
