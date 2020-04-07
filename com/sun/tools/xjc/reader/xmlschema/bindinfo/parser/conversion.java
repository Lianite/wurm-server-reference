// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;

class conversion extends NGCCHandler
{
    private BIConversion r;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public conversion(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 3;
    }
    
    public conversion(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 0: {
                this.revertToParentFromEnterElement((Object)this.r, super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 3: {
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 2: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0 || ($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromEnterElement((NGCCEventReceiver)h, $__uri, $__local, $__qname, $attrs);
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
                if ($__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)this.r, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                int $ai;
                if ((($ai = this.$runtime.getAttributeIndex("", "name")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") || (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType") || (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0 && $__uri == "http://java.sun.com/xml/ns/jaxb" && $__local == "javaType")) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromLeaveElement((NGCCEventReceiver)h, $__uri, $__local, $__qname);
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
                this.revertToParentFromEnterAttribute((Object)this.r, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                if (($__uri == "" && $__local == "name") || ($__uri == "" && $__local == "parseMethod") || ($__uri == "" && $__local == "printMethod")) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromEnterAttribute((NGCCEventReceiver)h, $__uri, $__local, $__qname);
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
                this.revertToParentFromLeaveAttribute((Object)this.r, super._cookie, $__uri, $__local, $__qname);
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
                this.revertToParentFromText((Object)this.r, super._cookie, $value);
                break;
            }
            case 2: {
                int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "printMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "parseMethod")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                if (($ai = this.$runtime.getAttributeIndex("", "name")) >= 0) {
                    final NGCCHandler h = (NGCCHandler)new conversionBody((NGCCHandler)this, super._source, this.$runtime, 421);
                    this.spawnChildFromText((NGCCEventReceiver)h, $value);
                    break;
                }
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 421: {
                this.r = (BIConversion)$__result__;
                this.$_ngcc_current_state = 1;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
}
