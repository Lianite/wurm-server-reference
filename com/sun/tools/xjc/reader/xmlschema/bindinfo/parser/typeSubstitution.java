// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class typeSubstitution extends NGCCHandler
{
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public typeSubstitution(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 5;
    }
    
    public typeSubstitution(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 4: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)new Boolean(true), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 5: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "typeSubstitution") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            default: {
                this.unexpectedEnterElement($__qname);
                break;
            }
        }
    }
    
    public void leaveElement(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb/xjc" && $__local == "typeSubstitution") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 4: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)new Boolean(true), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            default: {
                this.unexpectedLeaveElement($__qname);
                break;
            }
        }
    }
    
    public void enterAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 4: {
                if ($__uri == "" && $__local == "type") {
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)new Boolean(true), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            default: {
                this.unexpectedEnterAttribute($__qname);
                break;
            }
        }
    }
    
    public void leaveAttribute(final String $__uri, final String $__local, final String $__qname) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)new Boolean(true), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                if ($__uri == "" && $__local == "type") {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            default: {
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
        }
    }
    
    public void text(final String $value) throws SAXException {
        switch (this.$_ngcc_current_state) {
            case 3: {
                if ($value.equals("complex")) {
                    this.$_ngcc_current_state = 2;
                    break;
                }
                break;
            }
            case 4: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "type")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)new Boolean(true), super._cookie, $value);
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
}
