// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser.state;

import com.sun.xml.xsom.impl.XPathImpl;
import org.xml.sax.SAXException;
import com.sun.xml.xsom.parser.AnnotationContext;
import org.xml.sax.Attributes;
import com.sun.xml.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.xsom.impl.AnnotationImpl;
import com.sun.xml.xsom.impl.ForeignAttributesImpl;

class xpath extends NGCCHandler
{
    private String xpath;
    private ForeignAttributesImpl fa;
    private AnnotationImpl ann;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    
    public final NGCCRuntime getRuntime() {
        return this.$runtime;
    }
    
    public xpath(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.$_ngcc_current_state = 6;
    }
    
    public xpath(final NGCCRuntimeEx runtime) {
        this(null, runtime, runtime, -1);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 1: {
                if ($__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
                    final NGCCHandler h = new annotation(this, super._source, this.$runtime, 158, null, AnnotationContext.XPATH);
                    this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0 && $__uri.equals("http://www.w3.org/2001/XMLSchema") && $__local.equals("annotation")) {
                    final NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 163, null);
                    this.spawnChildFromEnterElement(h, $__uri, $__local, $__qname, $attrs);
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
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
                    final NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 163, null);
                    this.spawnChildFromLeaveElement(h, $__uri, $__local, $__qname);
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
            case 5: {
                if ($__uri.equals("") && $__local.equals("xpath")) {
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.unexpectedEnterAttribute($__qname);
                break;
            }
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 6: {
                if ($__uri.equals("") && $__local.equals("xpath")) {
                    final NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 163, null);
                    this.spawnChildFromEnterAttribute(h, $__uri, $__local, $__qname);
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
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute(this.makeResult(), super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 3: {
                if ($__uri.equals("") && $__local.equals("xpath")) {
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
            case 5: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                break;
            }
            case 1: {
                this.$_ngcc_current_state = 0;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 4: {
                this.xpath = $value;
                this.$_ngcc_current_state = 3;
                break;
            }
            case 0: {
                this.revertToParentFromText(this.makeResult(), super._cookie, $value);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "xpath")) >= 0) {
                    final NGCCHandler h = new foreignAttributes(this, super._source, this.$runtime, 163, null);
                    this.spawnChildFromText(h, $value);
                    break;
                }
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
        switch ($__cookie__) {
            case 158: {
                this.ann = (AnnotationImpl)$__result__;
                this.$_ngcc_current_state = 0;
                break;
            }
            case 163: {
                this.fa = (ForeignAttributesImpl)$__result__;
                this.$_ngcc_current_state = 5;
                break;
            }
        }
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0 || this.$_ngcc_current_state == 1;
    }
    
    private XPathImpl makeResult() {
        return new XPathImpl(this.$runtime.document, this.ann, this.$runtime.copyLocator(), this.fa, this.$runtime.createXmlString(this.xpath));
    }
}
