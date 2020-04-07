// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import org.fourthline.cling.support.messagebox.parser.MessageElement;

public class MessageIncomingCall extends Message
{
    private final DateTime callTime;
    private final NumberName callee;
    private final NumberName caller;
    
    public MessageIncomingCall(final NumberName callee, final NumberName caller) {
        this(new DateTime(), callee, caller);
    }
    
    public MessageIncomingCall(final DateTime callTime, final NumberName callee, final NumberName caller) {
        this(DisplayType.MAXIMUM, callTime, callee, caller);
    }
    
    public MessageIncomingCall(final DisplayType displayType, final DateTime callTime, final NumberName callee, final NumberName caller) {
        super(Category.INCOMING_CALL, displayType);
        this.callTime = callTime;
        this.callee = callee;
        this.caller = caller;
    }
    
    public DateTime getCallTime() {
        return this.callTime;
    }
    
    public NumberName getCallee() {
        return this.callee;
    }
    
    public NumberName getCaller() {
        return this.caller;
    }
    
    @Override
    public void appendMessageElements(final MessageElement parent) {
        this.getCallTime().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("CallTime"));
        this.getCallee().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("Callee"));
        this.getCaller().appendMessageElements(((DOMElement<MessageElement, PARENT>)parent).createChild("Caller"));
    }
}
