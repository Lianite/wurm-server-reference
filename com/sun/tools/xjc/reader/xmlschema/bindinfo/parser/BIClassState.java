// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIClass;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class BIClassState extends NGCCHandler
{
    private String implClass;
    private String javadoc;
    private String name;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private Locator loc;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public BIClassState(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 12;
    }
    
    public BIClassState(final NGCCRuntimeEx runtime) {
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
            case 12: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.action0();
                    this.$_ngcc_current_state = 10;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 10: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javadoc") {
                    final NGCCHandler h = (NGCCHandler)new javadoc((NGCCHandler)this, super._source, this.$runtime, 414);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 6;
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
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "implClass")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
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
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "class") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 10: {
                this.$_ngcc_current_state = 6;
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
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "implClass")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            case 10: {
                this.$_ngcc_current_state = 6;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 8;
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                if ($__uri == "" && $__local == "implClass") {
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            case 3: {
                if ($__uri == "" && $__local == "implClass") {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 10: {
                this.$_ngcc_current_state = 6;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "" && $__local == "name") {
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)this.makeResult(), super._cookie, $__uri, $__local, $__qname);
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
            case 4: {
                this.implClass = $value;
                this.$_ngcc_current_state = 3;
                break;
            }
            case 10: {
                this.$_ngcc_current_state = 6;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "implClass")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)this.makeResult(), super._cookie, $value);
                break;
            }
            case 8: {
                this.name = $value;
                this.$_ngcc_current_state = 7;
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 414: {
                this.javadoc = (String)$__result__;
                this.$_ngcc_current_state = 6;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public BIClass makeResult() {
        return new BIClass(this.loc, this.name, this.implClass, this.javadoc);
    }
}
