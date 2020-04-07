// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import com.sun.tools.jxc.NGCCRuntimeEx;
import java.io.File;

public class Schema extends NGCCHandler
{
    private File baseDir;
    private String loc;
    protected final NGCCRuntimeEx $runtime;
    private int $_ngcc_current_state;
    protected String $uri;
    protected String $localName;
    protected String $qname;
    private File location;
    private String namespace;
    
    public final NGCCRuntime getRuntime() {
        return this.$runtime;
    }
    
    public Schema(final NGCCHandler parent, final NGCCEventSource source, final NGCCRuntimeEx runtime, final int cookie, final File _baseDir) {
        super(source, parent, cookie);
        this.$runtime = runtime;
        this.baseDir = _baseDir;
        this.$_ngcc_current_state = 10;
    }
    
    public Schema(final NGCCRuntimeEx runtime, final File _baseDir) {
        this(null, runtime, runtime, -1, _baseDir);
    }
    
    private void action0() throws SAXException {
        this.location = new File(this.baseDir, this.loc);
    }
    
    public void enterElement(final String $__uri, final String $__local, final String $__qname, final Attributes $attrs) throws SAXException {
        this.$uri = $__uri;
        this.$localName = $__local;
        this.$qname = $__qname;
        switch (this.$_ngcc_current_state) {
            case 10: {
                if ($__uri == "" && $__local == "schema") {
                    this.$runtime.onEnterElementConsumed($__uri, $__local, $__qname, $attrs);
                    this.$_ngcc_current_state = 6;
                    break;
                }
                this.unexpectedEnterElement($__qname);
                break;
            }
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 0: {
                this.revertToParentFromEnterElement(this, super._cookie, $__uri, $__local, $__qname, $attrs);
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendEnterElement(super._cookie, $__uri, $__local, $__qname, $attrs);
                    break;
                }
                this.$_ngcc_current_state = 1;
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
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveElement(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 1: {
                if ($__uri == "" && $__local == "schema") {
                    this.$runtime.onLeaveElementConsumed($__uri, $__local, $__qname);
                    this.$_ngcc_current_state = 0;
                    break;
                }
                this.unexpectedLeaveElement($__qname);
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendLeaveElement(super._cookie, $__uri, $__local, $__qname);
                    break;
                }
                this.$_ngcc_current_state = 1;
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
            case 6: {
                if ($__uri == "" && $__local == "namespace") {
                    this.$_ngcc_current_state = 8;
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendEnterAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromEnterAttribute(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                if ($__uri == "" && $__local == "location") {
                    this.$_ngcc_current_state = 4;
                    break;
                }
                this.$_ngcc_current_state = 1;
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
            case 6: {
                this.$_ngcc_current_state = 2;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 0: {
                this.revertToParentFromLeaveAttribute(this, super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 2: {
                this.$_ngcc_current_state = 1;
                this.$runtime.sendLeaveAttribute(super._cookie, $__uri, $__local, $__qname);
                break;
            }
            case 7: {
                if ($__uri == "" && $__local == "namespace") {
                    this.$_ngcc_current_state = 2;
                    break;
                }
                this.unexpectedLeaveAttribute($__qname);
                break;
            }
            case 3: {
                if ($__uri == "" && $__local == "location") {
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
            case 6: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "namespace")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 2;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 0: {
                this.revertToParentFromText(this, super._cookie, $value);
                break;
            }
            case 8: {
                this.namespace = $value;
                this.$_ngcc_current_state = 7;
                break;
            }
            case 2: {
                final int $ai;
                if (($ai = this.$runtime.getAttributeIndex("", "location")) >= 0) {
                    this.$runtime.consumeAttribute($ai);
                    this.$runtime.sendText(super._cookie, $value);
                    break;
                }
                this.$_ngcc_current_state = 1;
                this.$runtime.sendText(super._cookie, $value);
                break;
            }
            case 4: {
                this.loc = $value;
                this.$_ngcc_current_state = 3;
                this.action0();
                break;
            }
        }
    }
    
    public void onChildCompleted(final Object $__result__, final int $__cookie__, final boolean $__needAttCheck__) throws SAXException {
    }
    
    public boolean accepted() {
        return this.$_ngcc_current_state == 0;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public File getLocation() {
        return this.location;
    }
}
