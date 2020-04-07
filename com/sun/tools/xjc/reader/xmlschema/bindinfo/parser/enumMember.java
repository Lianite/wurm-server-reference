// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnumMember;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class enumMember extends NGCCHandler
{
    private String name;
    private String javadoc;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private Locator loc;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public enumMember(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 7;
    }
    
    public enumMember(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    private void action0() throws SAXException {
        this.loc = this.$runtime.copyLocator();
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 7: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 6;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 2: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                    final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.$runtime, 96);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
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
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "typesafeEnumMember") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
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
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 5;
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
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
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 4: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 2;
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
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 5: {
                this.name = $value;
                this.$_ngcc_current_state = 4;
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 96: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    private BIEnumMember makeResult() {
        return new BIEnumMember(this.loc, this.name, this.javadoc);
    }
}
