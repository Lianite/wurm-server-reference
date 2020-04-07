// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import org.fourthline.cling.support.messagebox.parser.MessageElement;

public class MessageScheduleReminder extends Message
{
    private final DateTime startTime;
    private final NumberName owner;
    private final String subject;
    private final DateTime endTime;
    private final String location;
    private final String body;
    
    public MessageScheduleReminder(final DateTime startTime, final NumberName owner, final String subject, final DateTime endTime, final String location, final String body) {
        this(DisplayType.MAXIMUM, startTime, owner, subject, endTime, location, body);
    }
    
    public MessageScheduleReminder(final DisplayType displayType, final DateTime startTime, final NumberName owner, final String subject, final DateTime endTime, final String location, final String body) {
        super(Category.SCHEDULE_REMINDER, displayType);
        this.startTime = startTime;
        this.owner = owner;
        this.subject = subject;
        this.endTime = endTime;
        this.location = location;
        this.body = body;
    }
    
    public DateTime getStartTime() {
        return this.startTime;
    }
    
    public NumberName getOwner() {
        return this.owner;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public DateTime getEndTime() {
        return this.endTime;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public String getBody() {
        return this.body;
    }
    
    @Override
    public void appendMessageElements(final MessageElement parent) {
        this.getStartTime().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("StartTime"));
        this.getOwner().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("Owner"));
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Subject").setContent(this.getSubject());
        this.getEndTime().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("EndTime"));
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Location").setContent(this.getLocation());
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Body").setContent(this.getBody());
    }
}
