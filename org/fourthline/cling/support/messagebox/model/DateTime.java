// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.fourthline.cling.support.messagebox.parser.MessageElement;

public class DateTime implements ElementAppender
{
    private final String date;
    private final String time;
    
    public DateTime() {
        this(getCurrentDate(), getCurrentTime());
    }
    
    public DateTime(final String date, final String time) {
        this.date = date;
        this.time = time;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public String getTime() {
        return this.time;
    }
    
    @Override
    public void appendMessageElements(final MessageElement parent) {
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Date").setContent(this.getDate());
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Time").setContent(this.getTime());
    }
    
    public static String getCurrentDate() {
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(new Date());
    }
    
    public static String getCurrentTime() {
        final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        return fmt.format(new Date());
    }
}
