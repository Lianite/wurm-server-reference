// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import org.fourthline.cling.support.messagebox.parser.MessageElement;

public class MessageSMS extends Message
{
    private final DateTime receiveTime;
    private final NumberName receiver;
    private final NumberName sender;
    private final String body;
    
    public MessageSMS(final NumberName receiver, final NumberName sender, final String body) {
        this(new DateTime(), receiver, sender, body);
    }
    
    public MessageSMS(final DateTime receiveTime, final NumberName receiver, final NumberName sender, final String body) {
        this(DisplayType.MAXIMUM, receiveTime, receiver, sender, body);
    }
    
    public MessageSMS(final DisplayType displayType, final DateTime receiveTime, final NumberName receiver, final NumberName sender, final String body) {
        super(Category.SMS, displayType);
        this.receiveTime = receiveTime;
        this.receiver = receiver;
        this.sender = sender;
        this.body = body;
    }
    
    public DateTime getReceiveTime() {
        return this.receiveTime;
    }
    
    public NumberName getReceiver() {
        return this.receiver;
    }
    
    public NumberName getSender() {
        return this.sender;
    }
    
    public String getBody() {
        return this.body;
    }
    
    @Override
    public void appendMessageElements(final MessageElement parent) {
        this.getReceiveTime().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("ReceiveTime"));
        this.getReceiver().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("Receiver"));
        this.getSender().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("Sender"));
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Body").setContent(this.getBody());
    }
}
