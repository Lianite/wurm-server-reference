// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.model;

import org.seamless.xml.DOMElement;
import org.seamless.xml.ParserException;
import org.seamless.xml.DOM;
import org.fourthline.cling.support.messagebox.parser.MessageElement;
import org.fourthline.cling.support.messagebox.parser.MessageDOM;
import org.fourthline.cling.support.messagebox.parser.MessageDOMParser;
import java.util.Random;

public abstract class Message implements ElementAppender
{
    protected final Random randomGenerator;
    private final int id;
    private final Category category;
    private DisplayType displayType;
    
    public Message(final Category category, final DisplayType displayType) {
        this(0, category, displayType);
    }
    
    public Message(int id, final Category category, final DisplayType displayType) {
        this.randomGenerator = new Random();
        if (id == 0) {
            id = this.randomGenerator.nextInt(Integer.MAX_VALUE);
        }
        this.id = id;
        this.category = category;
        this.displayType = displayType;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public DisplayType getDisplayType() {
        return this.displayType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Message message = (Message)o;
        return this.id == message.id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public String toString() {
        try {
            final MessageDOMParser mp = new MessageDOMParser();
            final MessageDOM dom = mp.createDocument();
            final MessageElement root = dom.createRoot(mp.createXPath(), "Message");
            ((DOMElement<MessageElement, PARENT>)root).createChild("Category").setContent(this.getCategory().text);
            ((DOMElement<MessageElement, PARENT>)root).createChild("DisplayType").setContent(this.getDisplayType().text);
            this.appendMessageElements(root);
            final String s = mp.print(dom, 0, false);
            return s.replaceAll("<Message xmlns=\"urn:samsung-com:messagebox-1-0\">", "").replaceAll("</Message>", "");
        }
        catch (ParserException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public enum Category
    {
        SMS("SMS"), 
        INCOMING_CALL("Incoming Call"), 
        SCHEDULE_REMINDER("Schedule Reminder");
        
        public String text;
        
        private Category(final String text) {
            this.text = text;
        }
    }
    
    public enum DisplayType
    {
        MINIMUM("Minimum"), 
        MAXIMUM("Maximum");
        
        public String text;
        
        private DisplayType(final String text) {
            this.text = text;
        }
    }
}
