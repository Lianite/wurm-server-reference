// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo.parser;

import org.xml.sax.SAXException;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import org.xml.sax.Attributes;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.NGCCRuntimeEx;

class nameXmlTransformRule extends NGCCHandler
{
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private String prefix;
    private String suffix;
    
    public final NGCCRuntime getRuntime() {
        return (NGCCRuntime)this.$runtime;
    }
    
    public nameXmlTransformRule(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.prefix = "";
        this.suffix = "";
        this.$runtime = runtime;
        this.$_ngcc_current_state = 5;
    }
    
    public nameXmlTransformRule(final NGCCRuntimeEx runtime) {
        this(null, (NGCCEventSource)runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement((Object)new BISchemaBinding.NamingRule(this.prefix, this.suffix), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
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
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement((Object)new BISchemaBinding.NamingRule(this.prefix, this.suffix), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
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
            case 5: {
                if ($__uri == "" && $__local == "prefix") {
                    this.$_ngcc_current_state = 7;
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute((Object)new BISchemaBinding.NamingRule(this.prefix, this.suffix), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "" && $__local == "suffix") {
                    this.$_ngcc_current_state = 3;
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
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
            case 2: {
                if ($__uri == "" && $__local == "suffix") {
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 5: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute((Object)new BISchemaBinding.NamingRule(this.prefix, this.suffix), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                if ($__uri == "" && $__local == "prefix") {
                    this.$_ngcc_current_state = 1;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
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
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "prefix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 0: {
                this.revertToParentFromText((Object)new BISchemaBinding.NamingRule(this.prefix, this.suffix), super._cookie, $value);
                break;
            }
            case 3: {
                this.suffix = $value;
                this.$_ngcc_current_state = 2;
                break;
            }
            case 1: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "suffix")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 7: {
                this.prefix = $value;
                this.$_ngcc_current_state = 6;
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 1 || this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 5;
    }
}
