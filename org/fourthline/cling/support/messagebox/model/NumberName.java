// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import org.fourthline.cling.support.messagebox.parser.MessageElement;

public class NumberName implements ElementAppender
{
    private String number;
    private String name;
    
    public NumberName(final String number, final String name) {
        this.number = number;
        this.name = name;
    }
    
    public String getNumber() {
        return this.number;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void appendMessageElements(final MessageElement parent) {
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Number").setContent(this.getNumber());
        ((DOMElement<MessageElement, PARENT>)parent).createChild("Name").setContent(this.getName());
    }
}
